package com.texnar13.deliveryapp.model;

import androidx.annotation.NonNull;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.Serializable;

public class DBExpedition implements Serializable {
    public static final String TABLE_NAME = "Expedition";

    public static final String EXPEDITION_ID = "_id";
    public static final String EXPEDITION_ADDRESS_RECEIVER = "Address_reciver";
    public static final String EXPEDITION_ADDRESS_SENDER = "Address_sender";
    public static final String EXPEDITION_STATUS = "Status";
    public static final String EXPEDITION_SENDER = "Sender";
    public static final String EXPEDITION_PACKAGE = "Package";

    public static final String EXPEDITION_STATUS_VALUE_WAIT_SEND = "Wait";
    public static final String EXPEDITION_STATUS_VALUE_SENT = "Sent";
    public static final String EXPEDITION_STATUS_VALUE_DONE = "Done";

    private ObjectId _id;
    private DBAddress dbAddressReceiver;
    private DBAddress dbAddressSender;
    private String status;
    private ObjectId sender;
    private DBPackage dbPackage;

/*
{
	"_id":{"$oid":"661aeaf92cb807852acaa40d"},
	"Address_reciver":{
		"country":"Algeria",
		"city":"Lagouat",
		"district":"Central",
		"street":"Kaser El bzaim"
	},
	"Address_sender":{
		"country":"Russia",
		"city":"Moscow",
		"district":"Central",
		"street":"Tverskaya"
	},
	"Status":"Sent",
	"Sender":{"$oid":"661aeaf92cb807852acaa410"},
	"Package":{
		"Category":"Electronics",
		"Description":"The box is sleek and sturdy, designed to ensure the safe delivery of the laptop inside. It s made of durable cardboard with reinforced edges and corners, providing extra protection during transit. The exterior is plain brown, adorned only with a shipping label and fragile stickers for added caution. Inside, the laptop rests snugly within custom-fit foam padding, keeping it secure and cushioned against any bumps or jostles during its journey",
		"Dimension":[
			{"$numberDouble":"35.56"},
			{"$numberDouble":"25.3"},
			{"$numberDouble":"3.2"}
		],
		"Name":"Box",
		"Picture":"URL",
		"Weight":{"$numberDouble":"1.1"}
	}
}
 */

    public DBExpedition(@NonNull Document expeditionDocument) {
        this._id = expeditionDocument.getObjectId(EXPEDITION_ID);
        this.sender = expeditionDocument.getObjectId(EXPEDITION_SENDER);
        this.dbPackage = new DBPackage(expeditionDocument.get(EXPEDITION_PACKAGE, Document.class));
        this.dbAddressSender = new DBAddress(expeditionDocument.get(EXPEDITION_ADDRESS_SENDER, Document.class));
        this.dbAddressReceiver = new DBAddress(expeditionDocument.get(EXPEDITION_ADDRESS_RECEIVER, Document.class));
        this.status = expeditionDocument.getString(EXPEDITION_STATUS);
    }

    public DBExpedition(ObjectId _id, DBAddress dbAddressReceiver, DBAddress dbAddressSender, String status, ObjectId sender, DBPackage dbPackage) {
        this._id = _id;
        this.dbAddressReceiver = dbAddressReceiver;
        this.dbAddressSender = dbAddressSender;
        this.status = status;
        this.sender = sender;
        this.dbPackage = dbPackage;
    }

    public Document getDocument(){
        return new Document()
                .append(EXPEDITION_ID,_id)
                .append(EXPEDITION_ADDRESS_RECEIVER, dbAddressReceiver.getDocument())
                .append(EXPEDITION_ADDRESS_SENDER, dbAddressSender.getDocument())
                .append(EXPEDITION_STATUS, status)
                .append(EXPEDITION_SENDER, sender)
                .append(EXPEDITION_PACKAGE, dbPackage.toDocument());
    }

    public ObjectId get_id() {
        return _id;
    }

    public DBAddress getAddressReceiver() {
        return dbAddressReceiver;
    }

    public DBAddress getAddressSender() {
        return dbAddressSender;
    }

    public String getStatus() {
        return status;
    }

    public ObjectId getSender() {
        return sender;
    }

    public DBPackage getPackage() {
        return dbPackage;
    }
}
