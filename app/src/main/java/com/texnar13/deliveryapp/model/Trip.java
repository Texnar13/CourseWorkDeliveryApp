package com.texnar13.deliveryapp.model;

import org.bson.types.ObjectId;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmField;

public class Trip extends RealmObject {
    @PrimaryKey
    @RealmField("_id")
    private ObjectId _id = new ObjectId();

    @RealmField("Id_trip")
    private String dopId;

    @RealmField("страна_отбытия")
    private String departCountry;

    @RealmField("город_отбытия")
    private String departCity;

    @RealmField("страна_прибытия")
    private String arriveCountry;

    @RealmField("город_прибытия")
    private String arriveCity;

    @RealmField("дата_отбытия")
    private Date departDate;

    @RealmField("транспорт")
    private String transport;

    @RealmField("цена_поездки")
    private String cost;

    // методы доступа

    public Trip() {
    }

    public Trip(ObjectId _id, String dopId, String departCountry, String departCity, String arriveCountry, String arriveCity, Date departDate, String transport, String cost) {
        this._id = _id;
        this.dopId = dopId;
        this.departCountry = departCountry;
        this.departCity = departCity;
        this.arriveCountry = arriveCountry;
        this.arriveCity = arriveCity;
        this.departDate = departDate;
        this.transport = transport;
        this.cost = cost;
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getDopId() {
        return dopId;
    }

    public void setDopId(String dopId) {
        this.dopId = dopId;
    }

    public String getDepartCountry() {
        return departCountry;
    }

    public void setDepartCountry(String departCountry) {
        this.departCountry = departCountry;
    }

    public String getDepartCity() {
        return departCity;
    }

    public void setDepartCity(String departCity) {
        this.departCity = departCity;
    }

    public String getArriveCountry() {
        return arriveCountry;
    }

    public void setArriveCountry(String arriveCountry) {
        this.arriveCountry = arriveCountry;
    }

    public String getArriveCity() {
        return arriveCity;
    }

    public void setArriveCity(String arriveCity) {
        this.arriveCity = arriveCity;
    }

    public Date getDepartDate() {
        return departDate;
    }

    public void setDepartDate(Date departDate) {
        this.departDate = departDate;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    /*
    *
 {"_id":{"$oid":"661aeaf92cb807852acaa40e"},
 * "Id_trip":"1",
 * "страна_отбытия":"Russia",
 * "город_отбытия":"Moscow",
 * "страна_прибытия":"Algeria",
 * "город_прибытия":"Laghouat",
 * "дата_отбытия":{"$date":{"$numberLong":"1709283600000"}},
 * "транспорт":"Самолет",
 * "цена_поездки":"2000 rouble"
 * }
    *
    *
db.getCollection('Trip').insertMany([
{ 'Id_trip': '1',
'страна_отбытия': 'Russia',
'город_отбытия': 'Moscow',
'страна_прибытия': 'Algeria',
'город_прибытия': 'Laghouat',
'дата_отбытия': new Date('2024-03-01T09:00:00Z'),
'транспорт': 'Самолет',
'цена_поездки': '2000 rouble'},
]);
*
*
*
String

UUID

ObjectId

Integer

Long

Short

Byte or byte[]

Boolean

Float

Double

Date

RealmList
    * */


}
