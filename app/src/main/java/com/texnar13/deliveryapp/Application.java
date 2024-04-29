package com.texnar13.deliveryapp;

import io.realm.Realm;

public class Application extends android.app.Application{
    @Override
    public void onCreate() {
        Realm.init(this);
        super.onCreate();
    }
}
