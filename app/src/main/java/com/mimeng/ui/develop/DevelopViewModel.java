package com.mimeng.ui.develop;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DevelopViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public DevelopViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is editor fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}