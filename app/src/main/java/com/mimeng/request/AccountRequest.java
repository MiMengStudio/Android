package com.mimeng.request;

import androidx.annotation.NonNull;

import com.mimeng.ApplicationConfig;
import com.mimeng.request.annotations.RequestBaseURL;
import com.mimeng.request.annotations.WithAccountInfo;
import com.mimeng.request.annotations.WithAction;

import okhttp3.Callback;

@RequestBaseURL(ApplicationConfig.ACCOUNT_SERVICE_URL)
public interface AccountRequest extends AppRequest {

    @WithAccountInfo
    @WithAction
    void signIn(@NonNull Callback callback);

    @WithAccountInfo
    @WithAction
    void validateToken(@NonNull Callback callback);

    @WithAction("isSignedIn")
    @WithAccountInfo
    void updateAccountSignInTime(@NonNull Callback callback);
}
