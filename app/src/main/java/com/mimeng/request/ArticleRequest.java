package com.mimeng.request;

import androidx.annotation.NonNull;

import com.mimeng.ApplicationConfig;
import com.mimeng.request.annotations.GetParams;
import com.mimeng.request.annotations.RequestBaseURL;
import com.mimeng.request.annotations.WithAccountInfo;
import com.mimeng.request.annotations.WithAction;
import com.mimeng.request.annotations.WithDefaultGetParams;

import okhttp3.Callback;

@RequestBaseURL(ApplicationConfig.HOST_API + "/search")
public interface ArticleRequest extends AppRequest {

    /**
     * 搜索文章
     *
     * @param keyword  关键词
     * @param page     页数
     * @param callback 请求回调
     */
    @WithAction
    @WithAccountInfo
    @WithDefaultGetParams(name = "sort", value = "hot")
    @WithDefaultGetParams(name = "reverse", value = "false")
    void searchArticle(@GetParams("keyword") String keyword,
                       @GetParams("page") int page, @NonNull Callback callback);

}
