package com.mimeng.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.mimeng.App;
import com.mimeng.R;
import com.mimeng.base.BaseActivity;
import com.mimeng.base.BaseDialog;
import com.mimeng.utils.IOUtils;
import com.mimeng.values.ResourcePackInfo;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ResourceManagementActivity extends BaseActivity {

    private static final String TAG = "ResourceManagementActivity";
    private Handler mHandler;
    private ProgressBar progressBar;
    private TextView progressText;
    private TextView imp_state_text;
    private long size = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        blackParentBar();
        setFullScreen(false);
        setContentView(R.layout.activity_resource_management);

        imp_state_text = findViewById(R.id.item_res_info);
        checkAndSetResourcePackInfo(); // 检测资源包导入情况

        mHandler = new ManagerHandler(this);
        ImageButton backButton = findViewById(R.id.back);
        backButton.setOnClickListener(v -> {
            // 当按钮被点击时，关闭当前Activity
            finish();
        });

        CardView item_res = findViewById(R.id.item_res);
        item_res.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/zip"); // 指定MIME类型为ZIP文件
            intent.addCategory(Intent.CATEGORY_OPENABLE); // 确保Intent可以处理打开文件的操作
            startActivityForResult(Intent.createChooser(intent, "请选择道具图片素材包"), 1);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Uri uri = Objects.requireNonNull(result.getData(), "No URI returned from file picker");
            Log.i(TAG, uri + "  " + uri.getPath());
            new Thread(() -> {
                Message msg = Message.obtain();
                msg.what = ManagerHandler.MSG_TOAST_SHORT;
                // 使用try-with-resources自动管理资源    
                try (InputStream is = getContentResolver().openInputStream(uri);
                     ZipInputStream zis = new ZipInputStream(is)) {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ZipEntry entry;
                    while ((entry = zis.getNextEntry()) != null) {
                        if (entry.getName().equalsIgnoreCase("info.json")) {
                            IOUtils.copy(zis, outputStream);
                            break;
                        }
                    }
                    zis.closeEntry();
                    Log.i(TAG, "Size: " + size);
                    // 将字节流转换为字符串
                    String jsonContent = outputStream.toString();
                    if (!jsonContent.isEmpty()) {
                        ResourcePackInfo resPackInfo =
                                App.GSON.fromJson(jsonContent, ResourcePackInfo.class);
                        // 检查资源类型
                        if ("ItemResourcePack".equals(resPackInfo.getType())) {
                            // 处理找到的ResourcePackInfo
                            msg.obj = "导入成功";
                            assert is != null;
                            getAllResultFile(uri, is.available());
                        } else {
                            msg.obj = "该文件不是道具图片素材包，请重新选择";
                        }
                    } else {
                        msg.obj = "info.json为空";
                    }
                } catch (Exception e) {
                    msg.obj = "读取文件时发生错误";
                    Log.e(TAG, "Error when reading files", e);
                }
                mHandler.sendMessage(msg);
            }, "ResourceReader").start();
        }
    }

    @SuppressLint("SetTextI18n")
    private void checkAndSetResourcePackInfo() {
        File jsonFile = new File(getExternalFilesDir(null), "res/item/info.json");
        Log.d(TAG, "File exists: " + jsonFile.exists() + ", Path: " + jsonFile.getAbsolutePath());
        if (jsonFile.exists()) {
            try (Reader reader = new InputStreamReader(new FileInputStream(jsonFile), StandardCharsets.UTF_8)) {
                ResourcePackInfo resourcePackInfo = App.GSON.fromJson(reader, ResourcePackInfo.class);
                // 显示ResourcePackInfo的内容
                imp_state_text.setText("已导入：" + resourcePackInfo.getName() + " v" + resourcePackInfo.getVersion());
                imp_state_text.setTextColor(ContextCompat.getColor(this, R.color.md_theme_primary)); // 假设正常文本颜色为主题主色
            } catch (IOException e) {
                Log.e(TAG, "Error when reading info.json", e);
                e.printStackTrace();
                imp_state_text.setText("读取info.json失败");
                imp_state_text.setTextColor(ContextCompat.getColor(this, R.color.md_theme_error));
            }
        } else {
            // 文件不存在，设置文本和颜色
            imp_state_text.setText("未导入");
            imp_state_text.setTextColor(ContextCompat.getColor(this, R.color.md_theme_error));
        }
    }


    @SuppressLint("SetTextI18n")
    private void getAllResultFile(Uri uri, long totalSize) {
        Message msg = Message.obtain();
        msg.what = ManagerHandler.MSG_SHOW_PROGRESS;
        mHandler.sendMessage(msg);

        try (ZipInputStream zis = new ZipInputStream(getContentResolver().openInputStream(uri))) {
            ZipEntry zipEntry;

            File privateDir =
                    new File(Objects.requireNonNull(getApplicationContext().getExternalFilesDir("res/item")).getAbsolutePath());
            if (!privateDir.exists()) {
                privateDir.mkdirs();
            }

            while ((zipEntry = zis.getNextEntry()) != null) {
                String entryName = zipEntry.getName();
                if (!zipEntry.isDirectory()) {
                    size += zipEntry.getCompressedSize();
                    File targetFile = new File(privateDir, entryName);
                    targetFile.getParentFile().mkdirs();
                    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(targetFile))) {
                        IOUtils.copy(zis, bos);
                    }
                }

                mHandler.post(() -> {
                    int pro_size = (int) ((size * 100) / totalSize);
                    progressBar.setProgress(pro_size);
                    progressText.setText("导入资源中，请稍等...(" + pro_size + "%)");
                });

            }

            zis.closeEntry();

        } catch (Exception e) {
            Log.e("getAllResultFile: ", "错误：", e);
        }
        size = 0;
        Message dismiss = Message.obtain();
        dismiss.what = ManagerHandler.MSG_DISMISS;
        mHandler.sendMessage(dismiss);
    }

    private static class ManagerHandler extends Handler {
        static final int MSG_TOAST_SHORT = 1;
        static final int MSG_SHOW_PROGRESS = 2;
        static final int MSG_DISMISS = 3;
        private final WeakReference<ResourceManagementActivity> mResRef;
        private BaseDialog dialog;

        ManagerHandler(ResourceManagementActivity ctx) {
            this.mResRef = new WeakReference<>(ctx);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            ResourceManagementActivity ctx = mResRef.get();
            if (ctx == null) return;
            switch (msg.what) {
                case MSG_TOAST_SHORT:
                    Toast.makeText(ctx, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    return;
                case MSG_SHOW_PROGRESS:
                    dialog = new BaseDialog(ctx, R.style.base_dialog, R.layout.alert_progress_bar);
                    dialog.show();
                    ctx.progressBar = dialog.findViewById(R.id.alert_progress_bar);
                    ctx.progressText = dialog.findViewById(R.id.progress_text);
                    return;
                case MSG_DISMISS:
                    Log.i(TAG, "Dismiss dialog");
                    dialog.dismiss();
                    ctx.imp_state_text.setText("已导入");
                    ctx.imp_state_text.setTextColor(ContextCompat.getColor(ctx, R.color.md_theme_primary));
                    return;
            }
            super.handleMessage(msg);
        }
    }
}
