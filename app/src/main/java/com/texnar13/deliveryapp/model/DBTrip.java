package com.texnar13.deliveryapp.model;

import org.bson.types.ObjectId;
import org.bson.Document;

import java.util.Date;

public class DBTrip {

    public static final String TABLE_NAME = "Trip";

    public static final String TRIP_ID = "_id";
    public static final String TRIP_PRICE = "Price";
    public static final String TRIP_RECEIVING_CITY = "Reciving city";
    public static final String TRIP_RECEIVING_COUNTRY = "Reciving country";
    public static final String TRIP_SEND_CITY = "Send city";
    public static final String TRIP_SEND_COUNTRY = "Send country";
    public static final String TRIP_SEND_DATE = "Sent date";
    public static final String TRIP_TRANSPORT = "Transport mean";

    private final ObjectId _id;
    private final String price;
    private final String receivingCity;
    private final String receivingCountry;
    private final String sendCity;
    private final String sendCountry;
    private final Date sentDate;
    private final String transport;


/*

{
    "_id":{"$oid":"661aeaf92cb807852acaa40e"},
    "Price":"2000 rouble",
    "Reciving city":"Laghouat",
    "Reciving country":"Algeria",

    "Send city":"Moscow",
    "Send country":"Russia",
    "Sent date":{"$date":{"$numberLong":"1709283600000"}},
    "Transport mean":"Самолет"
}

*/

    public DBTrip(Document tripDocument){
        this._id = tripDocument.getObjectId(TRIP_ID);
        this.price = tripDocument.getString(TRIP_PRICE);
        this.receivingCity = tripDocument.getString(TRIP_RECEIVING_CITY);
        this.receivingCountry = tripDocument.getString(TRIP_RECEIVING_COUNTRY);
        this.sendCity = tripDocument.getString(TRIP_SEND_CITY);
        this.sendCountry = tripDocument.getString(TRIP_SEND_COUNTRY);
        this.sentDate = tripDocument.getDate(TRIP_SEND_DATE);
        this.transport = tripDocument.getString(TRIP_TRANSPORT);
    }


    public ObjectId get_id() {
        return _id;
    }

    public String getPrice() {
        return price;
    }

    public String getReceivingCity() {
        return receivingCity;
    }

    public String getReceivingCountry() {
        return receivingCountry;
    }

    public String getSendCity() {
        return sendCity;
    }

    public String getSendCountry() {
        return sendCountry;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public String getTransport() {
        return transport;
    }
}

