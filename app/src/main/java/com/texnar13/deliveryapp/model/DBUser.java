package com.texnar13.deliveryapp.model;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.Objects;

public class DBUser implements Serializable {
    public static final String TABLE_NAME = "User";

    public static final String USER_ID = "_id";
    public static final String USER_PASSWORD = "Password";
    public static final String USER_ADDRESS = "Address";
    public static final String USER_EMAIL = "Email";
    public static final String USER_NAME = "Name";
    public static final String USER_PHONE_NUMBER = "Phone number";
    public static final String USER_PICTURE = "Pictue";// тут неправильно названо в бд
    public static final String USER_RATING = "Rating";


    private ObjectId _id;
    private String password;
    private DBAddress address;
    private String email;
    private String name;
    private String phoneNumber;
    private String picture;
    private Double rating;

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

    public DBUser(Document userDocument) {
        this._id = userDocument.getObjectId(USER_ID);
        this.password = userDocument.getString(USER_PASSWORD);
        this.address = new DBAddress((Document) userDocument.get(USER_ADDRESS));
        this.email = userDocument.getString(USER_EMAIL);
        this.name = userDocument.getString(USER_NAME);
        this.phoneNumber = userDocument.getString(USER_PHONE_NUMBER);
        this.picture = userDocument.getString(USER_PICTURE);
        this.rating = userDocument.getDouble(USER_RATING);
    }

    public DBUser(ObjectId _id, String password, DBAddress address, String email, String name, String phoneNumber, String picture, Double rating) {
        this._id = _id;
        this.password = password;
        this.address = address;
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.picture = picture;
        this.rating = rating;
    }

    public Document getDocument() {

        Document result = new Document();
        result.put(USER_ID, this._id);
        result.put(USER_PASSWORD, this.password);
        result.put(USER_ADDRESS, address.getDocument());
        result.put(USER_EMAIL, this.email);
        result.put(USER_NAME, this.name);
        result.put(USER_PHONE_NUMBER, this.phoneNumber);
        result.put(USER_PICTURE, this.picture);
        result.put(USER_RATING, this.rating);

        return result;
    }

    public ObjectId get_id() {
        return _id;
    }

    public String getPassword() {
        return password;
    }

    public DBAddress getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPicture() {
        return picture;
    }

    public Double getRating() {
        return rating;
    }
}
