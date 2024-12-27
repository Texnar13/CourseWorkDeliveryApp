package com.texnar13.deliveryapp.view_model;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.texnar13.deliveryapp.R;
import com.texnar13.deliveryapp.model.http.HttpApi;
import com.texnar13.deliveryapp.model.shared_preferences.SPHolder;


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

        // хранилище данных
        SPHolder holder = new SPHolder(context);

        return (T) (new MainViewModel(
                new HttpApi(holder.getServerAddress()),
                holder
                ));
    }
}
