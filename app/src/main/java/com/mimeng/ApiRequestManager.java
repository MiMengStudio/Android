package com.mimeng;

import android.util.Log;

import com.mimeng.request.AccountRequest;
import com.mimeng.request.AppRequest;
import com.mimeng.request.ArticleRequest;
import com.mimeng.request.ContentRequest;
import com.mimeng.request.GetParamsBuilder;
import com.mimeng.request.annotations.DefaultGetParamList;
import com.mimeng.request.annotations.GetParams;
import com.mimeng.request.annotations.RequestBaseURL;
import com.mimeng.request.annotations.WithAccountInfo;
import com.mimeng.request.annotations.WithAction;
import com.mimeng.request.annotations.WithDefaultGetParams;
import com.mimeng.user.Account;
import com.mimeng.user.AccountManager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public final class ApiRequestManager {
    private static final String TAG = "ApiRequestManager";
    private static final OkHttpClient client = new OkHttpClient();

    private static final ConcurrentMap<Class<?>, Object> CACHE = new ConcurrentHashMap<>();

    public static <T extends AppRequest> T get(Class<T> clazz) {
        return clazz.cast(CACHE.computeIfAbsent(clazz, ApiRequestManager::getProxyInstance));
    }

    private static Object getProxyInstance(Class<?> clazz) {
        return Proxy.newProxyInstance(ApiRequestManager.class.getClassLoader(), new Class[]{clazz}, (proxy, method, args) -> {
            if (method.getReturnType() != void.class) {
                Log.e(TAG, "Method " + method + " shouldn't have return value instead of " + method.getReturnType());
                return null;
            }

            Class<?>[] paramsType = method.getParameterTypes();
            if (paramsType.length == 0 || paramsType[paramsType.length - 1] != Callback.class) {
                Log.e(TAG, "Method " + method + " don't have Callback");
                return null;
            }

            String baseUrl = null;
            boolean withAccountInfo = false;

            GetParamsBuilder builder = new GetParamsBuilder();
            Request.Builder req = new Request.Builder();

            // Class
            for (Annotation annotation : method.getDeclaringClass().getAnnotations()) {
                if (annotation instanceof RequestBaseURL base) {
                    baseUrl = base.value();
                } else if (annotation instanceof WithAccountInfo) {
                    withAccountInfo = true;
                }
            }

            // Method
            for (Annotation annotation : method.getAnnotations()) {
                if (annotation instanceof RequestBaseURL base) {
                    baseUrl = base.value();
                } else if (annotation instanceof WithAction action) {
                    builder.set("act", "".equals(action.value()) ? method.getName() : action.value());
                } else if (annotation instanceof WithDefaultGetParams defaultGetParams) {
                    builder.set(defaultGetParams.name(), defaultGetParams.value());
                } else if (annotation instanceof DefaultGetParamList paramList) {
                    Arrays.stream(paramList.value()).forEach(e -> builder.set(e.name(), e.value()));
                } else if (annotation instanceof WithAccountInfo) {
                    withAccountInfo = true;
                }
            }

            if (baseUrl == null) {
                RequestBaseURL reqUrl = method.getDeclaringClass().getAnnotation(RequestBaseURL.class);
                if (reqUrl == null) {
                    Log.e(TAG, "Method " + method + " and class " + method.getDeclaringClass() + " didn't define RequestBaseURL");
                    return null;
                }
                baseUrl = reqUrl.value();
            }

            for (int i = 0; i < method.getParameterCount() - 1; ++i) {
                for (Annotation ann : method.getParameterAnnotations()[i]) {
                    if (ann instanceof GetParams getParams) {
                        builder.set(getParams.value(), args[i]);
                    }
                }
            }

            if (withAccountInfo) {
                Account account = AccountManager.get();
                builder.setIDAndTokenIfValid(account);
                if (account != null)
                    req.addHeader("Authorization", "Bearer " + account.getToken());
            }
            Log.i(TAG, "Request URL " + (baseUrl + builder));
            client.newCall(req.url(baseUrl + builder).get().build()).enqueue((Callback) args[args.length - 1]);
            return null;
        });
    }

    public static ArticleRequest getArticle() {
        return get(ArticleRequest.class);
    }

    public static ContentRequest getContent() {
        return get(ContentRequest.class);
    }

    public static AccountRequest getAccount() {
        return get(AccountRequest.class);
    }
}
