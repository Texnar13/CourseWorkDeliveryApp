package com.texnar13.deliveryapp;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import java.io.Closeable;

public class MainViewModel extends ViewModel {

    public MainViewModel() {
        init();
    }

    public MainViewModel(@NonNull Closeable... closeables) {
        super(closeables);
        init();
    }

    // нициализация
    private void init(){
        Log.e("Hello", "VM init");

    }

    // уничтожение активности
    @Override
    protected void onCleared() {
        Log.e("Hello", "VM destroy");
        super.onCleared();
    }



}
