package com.mimeng;

import android.app.Application;
import com.mimeng.user.AccountManager;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        
        AccountManager.tryLoadFromStorage(this);
    }
}
