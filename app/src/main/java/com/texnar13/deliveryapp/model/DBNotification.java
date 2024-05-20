package com.texnar13.deliveryapp.model;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Date;

public class DBNotification {
    public static final String TABLE_NAME = "Notification";


    public static final String NOTIFICATION_ID = "_id";
    public static final String NOTIFICATION_USER = "Id_User";
    public static final String NOTIFICATION_DATE_TIME = "Date";
    public static final String NOTIFICATION_MESSAGE = "Message";
    public static final String NOTIFICATION_TYPE = "Type";
    public static final String NOTIFICATION_STATUS = "Status";

    public static final String NOTIFICATION_TYPE_VALUE_ADMIN = "Admin";
    public static final String NOTIFICATION_TYPE_VALUE_SEND_REQUEST = "Request";
    public static final String NOTIFICATION_TYPE_VALUE_EXPEDITION = "Expedition";

    public static final String NOTIFICATION_STATUS_UNREAD = "Unread";
    public static final String NOTIFICATION_STATUS_BEEN_READ = "BeenRead";

    private ObjectId _id;
    private ObjectId userId;
    private Date date;
    private String notificationMessage;
    private String notificationType;
    private String notificationStatus;

    /*
    _id _id 661aeaf92cb807852acaa412
    Id_User _id 661aeaf92cb807852acaa410
    Date 2024-02-01T09:00:00.000+00:00
    Message "Welcome to our application"
    Type "Admin"
    Status "Unread"
*/

    public DBNotification(Document notificationDocument){
        this._id = notificationDocument.getObjectId(NOTIFICATION_ID);
        this.userId = notificationDocument.getObjectId(NOTIFICATION_USER);
        this.date = notificationDocument.getDate(NOTIFICATION_DATE_TIME);
        this.notificationMessage = notificationDocument.getString(NOTIFICATION_MESSAGE);
        this.notificationType = notificationDocument.getString(NOTIFICATION_TYPE);
        this.notificationStatus = notificationDocument.getString(NOTIFICATION_STATUS);
    }


    public ObjectId get_id() {
        return _id;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public Date getDate() {
        return date;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public String getNotificationStatus() {
        return notificationStatus;
    }
}
