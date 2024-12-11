package com.texnar13.deliveryapp.view_model;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.texnar13.deliveryapp.R;


// фабрика создания MainViewModel
public class MainViewModelFactory implements ViewModelProvider.Factory {

    Context context;
    public MainViewModelFactory(Context context){
        this.context = context;
    }


    // Метод создания ViewModel
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        // инициализация бд
        //Realm.init(context);

        // получение строки с апи ключом
        String apiKey = context.getResources().getString(R.string.mongodb_api_key);

        return (T) (new MainViewModel(apiKey));
        //return ViewModelProvider.Factory.super.create(modelClass);
    }
}
