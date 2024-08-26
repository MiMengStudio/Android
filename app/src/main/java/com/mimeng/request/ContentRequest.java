package com.mimeng.request;

import androidx.annotation.NonNull;

import com.mimeng.ApplicationConfig;
import com.mimeng.request.annotations.RequestBaseURL;
import com.mimeng.request.annotations.WithAccountInfo;
import com.mimeng.request.annotations.WithAction;

import okhttp3.Callback;

@RequestBaseURL(ApplicationConfig.HOST_API + "/content")
@WithAccountInfo
public interface ContentRequest extends AppRequest {

    /**
     * 获取轮播图
     *
     * @param callback 请求回调
     */
    @WithAction("ad")
    void getBannerApi(@NonNull Callback callback);
}
