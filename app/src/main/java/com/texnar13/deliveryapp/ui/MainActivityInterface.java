package com.texnar13.deliveryapp.ui;

public interface MainActivityInterface {

// ----------------------------------------- login fragment ----------------------------------------

    boolean authoriseUser(String email, String password);

    void gotoRegister();

// --------------------------------------- Register fragment ---------------------------------------

    void registerUser(String email, String name, String password);

// --------------------------------------- User fragment ---------------------------------------

    void logout();

    //

}
