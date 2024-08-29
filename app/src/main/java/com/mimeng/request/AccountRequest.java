package com.mimeng.request;

import androidx.annotation.NonNull;

import com.mimeng.ApplicationConfig;
import com.mimeng.request.annotations.GetParams;
import com.mimeng.request.annotations.RequestBaseURL;
import com.mimeng.request.annotations.WithAccountInfo;
import com.mimeng.request.annotations.WithAction;

import okhttp3.Callback;

@RequestBaseURL(ApplicationConfig.ACCOUNT_SERVICE_URL)
@WithAccountInfo
public interface AccountRequest extends AppRequest {

    @WithAction
    void signIn(@NonNull Callback callback);

    @WithAction
    void validateToken(@NonNull Callback callback);

    @WithAction("isSignedIn")
    void updateAccountSignInTime(@NonNull Callback callback);

    @WithAction
    void follow(@NonNull @GetParams("target") String target, @NonNull Callback callback);

    @WithAction
    void unfollow(@NonNull @GetParams("target") String target, @NonNull Callback callback);
}
