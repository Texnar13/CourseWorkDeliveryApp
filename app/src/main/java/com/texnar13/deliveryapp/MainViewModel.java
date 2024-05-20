package com.texnar13.deliveryapp;

import static com.texnar13.deliveryapp.RegisterActivityDeleteMe.MY_LOG;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.texnar13.deliveryapp.model.DBUser;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.io.Closeable;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicReference;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;

public class MainViewModel extends ViewModel {

    private String apiKey;
    private MongoDatabase mongoDatabase = null;

// ------------------------------------------ Связь с активностью ---------------------------------------

    // строка для отправки тостов
    public MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private void sendToast(String message){
        toastMessage.setValue(message);
    }

    // статус подключения
    enum ConnectionStatusValue {
        STATUS_NONE,
        STATUS_ERROR,
        STATUS_CONNECTED,
    }
    private MutableLiveData<ConnectionStatusValue> connectionStatus =
            new MutableLiveData<>(ConnectionStatusValue.STATUS_NONE);
    public LiveData<ConnectionStatusValue> activityConnectionStatus = connectionStatus;


// ------------------------------------------ Загруженные данные ------------------------------------------

    // текущий пользователь
    public MutableLiveData<DBUser> currentUser = new MutableLiveData<>();




// ------------------------------------------ Главные методы ---------------------------------------

    public MainViewModel(String apiKey) {
        this.apiKey = apiKey;
        init();
    }


    // нициализация
    private void init() {
        Log.e("Hello", "VM init");

        // подключение к онлайн бд
        connectDB();


    }

    // уничтожение активности
    @Override
    protected void onCleared() {
        Log.e("Hello", "VM destroy");
        super.onCleared();
    }


// ---------------------------------------------- БД -----------------------------------------------

    // подключение к онлайн бд
    void connectDB() {
        String appID = "application-0-epiwn";
        App realmConnectionApp = new App(new AppConfiguration.Builder(appID).build());
        // авторизация
        Credentials apiKeyCredentials = Credentials.apiKey(apiKey);
        AtomicReference<User> user = new AtomicReference<>();
        realmConnectionApp.loginAsync(apiKeyCredentials, it -> {
            if (it.isSuccess()) {
                Log.e(MY_LOG, "Successfully authenticated using an API Key.");
                user.set(realmConnectionApp.currentUser());

                MongoClient mongoClient = realmConnectionApp.currentUser().getMongoClient("mongodb-atlas");
                // получаем ссылку на БД
                mongoDatabase = mongoClient.getDatabase("DeliveryApplicationDB");

                // уведомляем активность, что подключение установлено
                connectionStatus.setValue(ConnectionStatusValue.STATUS_CONNECTED);
            } else {
                Log.e(MY_LOG, it.getError().toString());

                // уведомляем активность, что подключение не произошло
                connectionStatus.setValue(ConnectionStatusValue.STATUS_ERROR);

                // Повторная попытка
                connectionStatus.setValue(ConnectionStatusValue.STATUS_NONE);
                // отложенный вызов
                (new Handler(Looper.getMainLooper())).postDelayed(this::connectDB, 4000);
            }
        });

    }


    void authUser(String email, String password) {

        if (mongoDatabase == null) return;

        // получаем таблицу пользователей
        MongoCollection<Document> usersCollection = mongoDatabase.getCollection("User");

        // поиск пользователя в бд
        Document query = new Document("Email", email);
        usersCollection.find(query).first().getAsync(result -> {
            if (result.isSuccess()) {
                Document documentUser = result.get();

                // если пользователь найден
                if (documentUser != null) {
                    String pass = documentUser.getString("Password");

                    if (hashPassword(password).equals(pass)) {
                        // вытягиваем данные из пользователя и сохраняем их
                        currentUser.setValue( new DBUser(documentUser));

                        sendToast("Авторизован");
                    }

                } else {
                    sendToast("Пользователь не найден");
                }

            } else {
                sendToast("Ошибка подключения");
            }
        });



        /*
_id: ObjectId('661aeaf92cb807852acaa410')
Password: "dmsdkadCSD1232$$" (1234)
Address: Object
Email: "kd_djb@gmail.com"
Name: "Abdelkader"
Phone number: "+79993243245"
Pictue: "URL"
Rating: 4.5
        */

    }


    public static String hashPassword(String password) {
        try {
            // Создание экземпляра MessageDigest с алгоритмом SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Преобразование пароля в массив байтов и хэширование
            byte[] hashedBytes = md.digest(password.getBytes());

            // Преобразование массива байтов в шестнадцатеричную строку
            BigInteger number = new BigInteger(1, hashedBytes);
            StringBuilder hexString = new StringBuilder(number.toString(16));

            // Обрезка строки до 16 символов для простоты
            String shortHash = hexString.substring(0, 16);

            return shortHash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}


