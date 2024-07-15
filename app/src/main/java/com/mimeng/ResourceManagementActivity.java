package com.mimeng;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.AnyThread;
import androidx.cardview.widget.CardView;

import com.google.gson.Gson;
import com.mimeng.BaseClass.BaseActivity;
import com.mimeng.resourcepack.ResourcePackInfo;

import com.mimeng.utils.IOUtils;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ResourceManagementActivity extends BaseActivity {

  private static final String TAG = "ResourceManagementActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    blackParentBar();
    setFullScreen(false);
    setContentView(R.layout.activity_resource_management);

    ImageButton backButton = findViewById(R.id.back);
    backButton.setOnClickListener(
        v -> {
          // 当按钮被点击时，关闭当前Activity
          finish();
        });

    CardView item_res = findViewById(R.id.item_res);
    item_res.setOnClickListener(
        v -> {
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
      new Thread(() -> {
         // 使用try-with-resources自动管理资源
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
          ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String fileName = entry.getName();
                if (fileName.substring(fileName.lastIndexOf("/") + 1).equalsIgnoreCase("info.json")) {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    IOUtils.copy(zipInputStream, outputStream);
                    // 将字节流转换为字符串
                    String jsonContent = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
                    if (!jsonContent.isEmpty()) {
                        Gson gson = new Gson();
                        ResourcePackInfo resPackInfo = gson.fromJson(jsonContent, ResourcePackInfo.class);
                        // 检查资源类型
                        if ("ItemResourcePack".equals(resPackInfo.getType())) {
                            // 处理找到的ResourcePackInfo
                            Toast.makeText(this, "导入成功", Toast.LENGTH_SHORT).show();
                            getAllResultFile(uri);
                        } else {
                            Toast.makeText(this, "该文件不是道具图片素材包，请重新选择", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "info.json为空", Toast.LENGTH_SHORT).show();
                    }
                    break; // 找到info.json后退出循环
                }
                zipInputStream.closeEntry();
            }
        } catch (IOException e) {
            Toast.makeText(this, "读取文件时发生错误", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error when reading files", e);
        }          
      }, "ResourceReader").start();
    }
  }

  private void getAllResultFile(Uri uri) {
    try (ZipInputStream zis = new ZipInputStream(getContentResolver().openInputStream(uri))) {
      ZipEntry zipEntry = zis.getNextEntry();

      File privateDir =
          new File(getApplicationContext().getExternalFilesDir(null).getAbsoluteFile() + "/");
      if (!privateDir.exists()) {
        privateDir.mkdirs();
      }

      while (zipEntry != null) {
        String entryName = zipEntry.getName();
        File file2 = new File(zipEntry.getName());
        if (entryName.startsWith(file2.getParent())
            && !zipEntry.isDirectory()) {

          String nextPath = "resources" + entryName.replaceAll(".*(?=/[^/]*$)", "");
          File newFile = new File(privateDir, nextPath);

          if (entryName.startsWith("src/urces/res/")) {
            Log.i("getAllResultFile: ", "路径：" + entryName.replaceAll(".*(?=/[^/]*$)", ""));
            new File(newFile.getParent()).mkdirs();
            try (BufferedOutputStream bos =
                new BufferedOutputStream(new FileOutputStream(newFile))) {
              IOUtils.copy(zis, bos);
            }
          }
        }
        zipEntry = zis.getNextEntry();
      }
      zis.closeEntry();
    } catch (Exception e) {
      Log.e("getAllResultFile: ", "错误：", e);
    }
  }
}
