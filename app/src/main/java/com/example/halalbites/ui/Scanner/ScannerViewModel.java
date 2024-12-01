package com.example.halalbites.ui.Scanner;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ScannerViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ScannerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the scanner fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
