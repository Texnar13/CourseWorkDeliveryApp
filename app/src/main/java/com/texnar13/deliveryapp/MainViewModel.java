package com.texnar13.deliveryapp;

import static com.texnar13.deliveryapp.RegisterActivityDeleteMe.MY_LOG;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.texnar13.deliveryapp.model.DBNotification;
import com.texnar13.deliveryapp.model.DBUser;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.mongo.iterable.MongoCursor;

public class MainViewModel extends ViewModel {

    private String apiKey;
    private MongoDatabase mongoDatabase = null;

// ------------------------------------------ Связь с активностью ---------------------------------------

    // строка для отправки тостов
    public MutableLiveData<String> toastMessage = new MutableLiveData<>();

    private void sendToast(String message) {
        toastMessage.setValue(message);
    }

    // статус подключения
    enum ConnectionStatusValue {
        STATUS_NONE,
        STATUS_ERROR,
        STATUS_CONNECTED,
    }

    private final MutableLiveData<ConnectionStatusValue> connectionStatus =
            new MutableLiveData<>(ConnectionStatusValue.STATUS_NONE);
    public final LiveData<ConnectionStatusValue> activityConnectionStatus = connectionStatus;


// ------------------------------------------ Загруженные данные ------------------------------------------

    // текущий пользователь
    public MutableLiveData<DBUser> currentUser = new MutableLiveData<>();

    // уведомления
    public MutableLiveData<List<DBNotification>> currentUserNotifications = new MutableLiveData<>();


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

    // выход из учетной записи пользователя
    void logout() {
        currentUser.setValue(null);
    }


    void tryRegisterUser(String password, String[] address, String email, String name, String phone) {

        // получаем таблицу пользователей
        MongoCollection<Document> usersCollection = mongoDatabase.getCollection(DBUser.TABLE_NAME);

        // поиск пользователя в бд по email
        Document query = new Document(DBUser.USER_EMAIL, email);
        usersCollection.find(query).first().getAsync(result -> {
            if (result.isSuccess()) {
                Document existingUser = result.get();

                // если пользователь найден
                if (existingUser != null) {
                    sendToast("Пользователь с таким E-mail уже существует");
                } else {
                    // создаем пользователя

                    // адрес
                    Document addressDoc = new Document();
                    for (int i = 0; i < DBUser.USER_ADDRESS_LOCALS.length; i++)
                        addressDoc.put(DBUser.USER_ADDRESS_LOCALS[i], address[i]);

                    // создаем новый документ пользователя
                    Document newUser = new Document()
                            .append(DBUser.USER_PASSWORD, hashPassword(password))
                            .append(DBUser.USER_ADDRESS, addressDoc)
                            .append(DBUser.USER_EMAIL, email)
                            .append(DBUser.USER_NAME, name)
                            .append(DBUser.USER_PHONE_NUMBER, phone)
                            .append(DBUser.USER_PICTURE, "URL")
                            .append(DBUser.USER_RATING, 0d);

                    // вставка нового пользователя в коллекцию
                    usersCollection.insertOne(newUser).getAsync(insertResult -> {
                        if (insertResult.isSuccess()) {
                            sendToast("Пользователь успешно создан");

                            // и сразу заходим за этого пользователя
                            authUser(email, password);
                        } else {
                            sendToast("Ошибка при создании пользователя");
                        }
                    });
                }
            } else {
                sendToast("Ошибка при проверке существования пользователя");
            }
        });
    }


    void authUser(String email, String password) {

        if (mongoDatabase == null) return;

        // получаем таблицу пользователей
        MongoCollection<Document> usersCollection = mongoDatabase.getCollection(DBUser.TABLE_NAME);

        // поиск пользователя в бд
        Document query = new Document(DBUser.USER_EMAIL, email);
        usersCollection.find(query).first().getAsync(result -> {
            if (result.isSuccess()) {
                Document documentUser = result.get();

                // если пользователь найден
                if (documentUser != null) {
                    String pass = documentUser.getString(DBUser.USER_PASSWORD);

                    if (hashPassword(password).equals(pass)) {
                        // вытягиваем данные из пользователя и сохраняем их
                        currentUser.setValue(new DBUser(documentUser));
                        sendToast("Авторизован");

                        // Получаем данные пользователя
                        loadUserNotifications();
                    } else {
                        sendToast("Пароль неподходит!");
                    }
                } else
                    sendToast("Пользователь не найден");
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

    // редактирование пользователя
    public void editUser(DBUser editedUserData) {
        Document editedDocument = editedUserData.getDocument();

        // получаем таблицу пользователей
        MongoCollection<Document> usersCollection = mongoDatabase.getCollection(DBUser.TABLE_NAME);

        // Получаем идентификатор пользователя из измененных данных
        ObjectId userId = editedDocument.getObjectId(DBUser.USER_ID);

        // Обновляем документ пользователя новыми данными
        usersCollection.findOneAndReplace(new Document(DBUser.USER_ID, userId), editedDocument).getAsync(result -> {
            if (result.isSuccess()) {
                Document updatedUser = result.get();
                if (updatedUser != null) {
                    // если данные сохранены успешно, обновляем глобальную копию переменной и интерфейс
                    currentUser.setValue(editedUserData);
                    sendToast("Данные пользователя успешно сохранены");
                } else {
                    sendToast("Ошибка, пользователь не найден");
                }
            } else {
                sendToast("Ошибка при сохранении данных пользователя");
            }
        });
    }


    void loadUserNotifications() {
        DBUser user = currentUser.getValue();
        if (user != null) {

            // получаем таблицу уведомлений
            MongoCollection<Document> notificationsCollection = mongoDatabase.getCollection(DBNotification.TABLE_NAME);

            // поиск непрочитанных уведомлений пользователя в бд
            Document query = new Document(DBNotification.NOTIFICATION_USER, user.get_id()).append(
                    DBNotification.NOTIFICATION_STATUS, DBNotification.NOTIFICATION_STATUS_UNREAD);

            notificationsCollection.find(query).iterator().getAsync(result -> {
                if (result.isSuccess()) {
                    // сохраняем все уведомления в лист
                    MongoCursor<Document> cursor = result.get();

                    // пробегаемся по всем уведомлениям
                    List<DBNotification> loadedNotifications = new LinkedList<>();
                    while (cursor.hasNext())
                        loadedNotifications.add(new DBNotification(cursor.next()));

                    // передаем получившийся лист в глобальный отслеживаемый
                    currentUserNotifications.setValue(loadedNotifications);
                }
            });
        }
    }

    public void markReadAdminNotification(ObjectId notificationID) {

        // получаем таблицу уведомлений
        MongoCollection<Document> notificationsCollection = mongoDatabase.getCollection(DBNotification.TABLE_NAME);

        // Создаем запрос для поиска документа по идентификатору уведомления
        Document query = new Document(DBNotification.NOTIFICATION_ID, notificationID);

        // Обновление поля
        Document update = new Document("$set", new Document(
                DBNotification.NOTIFICATION_STATUS, DBNotification.NOTIFICATION_STATUS_BEEN_READ));

        // Выполнение обновления
        notificationsCollection.updateOne(query, update).getAsync(result -> {
            if (result.isSuccess()) {
                loadUserNotifications();
            } else {
                sendToast("Ошибка при обновлении поля");
            }
        });
    }


// -------------------------------------------------------------------------------------------------

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


