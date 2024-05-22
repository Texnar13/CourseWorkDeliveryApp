package com.texnar13.deliveryapp.ui;

import com.texnar13.deliveryapp.model.DBUser;

public interface MainActivityInterface {

// ----------------------------------------- login fragment ----------------------------------------

    void authoriseUser(String email, String password);

    void gotoRegister();

// --------------------------------------- Register fragment ---------------------------------------

    void registerUser(String password, String[] address, String email, String name, String phone);

// --------------------------------------- User fragment ---------------------------------------

    void logout();

// --------------------------------------- User edit dialog ---------------------------------------

    void editUser(DBUser editedUser);


}
