package com.texnar13.deliveryapp.ui;

import com.texnar13.deliveryapp.model.DBUser;

public interface MainActivityInterface {


// --------------------------------------- User fragment ---------------------------------------

    void logout();

// --------------------------------------- User edit dialog ---------------------------------------

    void editUser(DBUser editedUser);


}
