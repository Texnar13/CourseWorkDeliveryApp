package com.texnar13.deliveryapp.model;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Objects;

public class DBUser {

    public static final String USER_ID = "_id";
    public static final String USER_PASSWORD = "Password";
    public static final String USER_ADDRESS = "Address";
    public static final String[] USER_ADDRESS_LOCALS = {"country", "city", "district", "street"};
    public static final String USER_EMAIL = "Email";
    public static final String USER_NAME = "Name";
    public static final String USER_PHONE_NUMBER = "Phone number";
    public static final String USER_PICTURE = "Pictue";
    public static final String USER_RATING = "Rating";


    ObjectId _id;
    String password;
    String[] address;
    String email;
    String name;
    String phoneNumber;
    String picture;// тут неправильно названо в бд
    Double rating;

        /*
_id: ObjectId('661aeaf92cb807852acaa410')
Password: "dmsdkadCSD1232$$" (1234)
Address: Object
    country "Russia"
    city "Moscow"
    district "Central"
    street "Tverskaya"
Email: "kd_djb@gmail.com"
Name: "Abdelkader"
Phone number: "+79993243245"
Pictue: "URL"
Rating: 4.5   (Double)
        */

    public DBUser(Document userDocument){
        this._id = userDocument.getObjectId(USER_ID);
        this.password = userDocument.getString(USER_PASSWORD);
        this.email = userDocument.getString(USER_EMAIL);
        this.name = userDocument.getString(USER_NAME);
        this.phoneNumber = userDocument.getString(USER_PHONE_NUMBER);
        this.picture = userDocument.getString(USER_PICTURE);
        this.rating = userDocument.getDouble(USER_RATING);

        // кастуем документ в массив адреса
        Document addressDocument = (Document) Objects.requireNonNull(userDocument.get(USER_ADDRESS));
        this.address = new String[4];
        for (int i = 0; i < 4; i++)
            this.address[i] = addressDocument.getString(USER_ADDRESS_LOCALS[i]);
    }
}
