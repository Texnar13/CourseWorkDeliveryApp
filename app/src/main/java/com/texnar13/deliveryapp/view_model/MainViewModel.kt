package com.texnar13.deliveryapp.view_model

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.texnar13.deliveryapp.model.DBAddress
import com.texnar13.deliveryapp.model.DBExpedition
import com.texnar13.deliveryapp.model.DBNotification
import com.texnar13.deliveryapp.model.DBTrip
import com.texnar13.deliveryapp.model.DBUser
import com.texnar13.deliveryapp.model.entities.EntityUser
import com.texnar13.deliveryapp.model.http.HttpApi
import com.texnar13.deliveryapp.model.shared_preferences.SPHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class MainViewModel(
        private val httpClient: HttpApi,
        private val spHolder: SPHolder
) : ViewModel(), HttpApi.HttpResultListener, HttpApi.HttpWorkStatusListener {


// ------------------------------------------- Константы -------------------------------------------

    companion object {

        private const val TAG = "VIEW_MODEL"


        // получение экземпляра ViewModel из фрагмента
        fun getViewModel(activity: FragmentActivity): MainViewModel {
            return ViewModelProvider(
                    activity,
                    MainViewModelFactory(activity)
            )[MainViewModel::class.java]
        }

        fun hashPassword(password: String): String {
            try {
                // Создание экземпляра MessageDigest с алгоритмом SHA-256
                val md = MessageDigest.getInstance("SHA-256")

                // Преобразование пароля в массив байтов и хэширование
                val hashedBytes = md.digest(password.toByteArray())

                // Преобразование массива байтов в шестнадцатеричную строку
                val number = BigInteger(1, hashedBytes)
                val hexString = StringBuilder(number.toString(16))

                // Обрезка строки до 16 символов для простоты
                val shortHash = hexString.substring(0, 16)

                return shortHash
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException(e)
            }
        }

    }


//todo ---------------------------------------------------------------------------------------------
// ----------------------------------------- Общие данные ------------------------------------------
// -------------------------------------------------------------------------------------------------

    var token: MutableLiveData<String?> = MutableLiveData(null)


// ------------------------------------------ Связь с активностью ---------------------------------------


    // строка для отправки тостов
    var toastMessage: MutableLiveData<String> = MutableLiveData()

    private fun sendToast(message: String) {
        toastMessage.postValue(message)
    }

    // статус подключения
    enum class ConnectionStatusValue {
        STATUS_NONE,
        STATUS_ERROR,
        STATUS_CONNECTED,
    }


// -------------------------------------- Загруженные данные ---------------------------------------

    // текущий пользователь
    var currentUser: MutableLiveData<EntityUser?> = MutableLiveData(null)

    // уведомления
    var currentUserNotifications: MutableLiveData<List<DBNotification>> = MutableLiveData()

    // посылки пользователя
    var currentUserExpeditions: MutableLiveData<List<DBExpedition>> = MutableLiveData()

    // загруженные поездки
    var currentLoadedTrips: MutableLiveData<List<DBTrip>> = MutableLiveData()


    // ------------------------------------------ Главные методы ---------------------------------------
    // todo это соответственно переносится во ViewModel,
    //            //   а enableLoadBar() работает через подписку на ViewModel
    //            //   нажатие кнопок и обратня связь от фрагментов делегируется во viewModel
    //            //   LiveData и MutableLiveData :)
    //            //   Можно сделать так, чтобы LiveData следила за MutableLiveData и избежать getter-ов и setter-ов
    //            //   контекст view model не хранит, но он передаётся в методах
    //
    //            //


    // нициализация
    init {
        Log.i(TAG, "init")

        // назначаем слушатель HTTP ответов
        httpClient.setHttpResultListener(this)
        httpClient.setHttpWorkStatusListener(this)
    }


    // ---------------------------------------------- БД -----------------------------------------------



    // выход из учетной записи пользователя
    fun logout() {
        Log.i(TAG, "logout")
        currentUser.value = null
    }

// -------------------------------------------------------------------------------------------------
// ------------------------------------- Прогресс бар загрузки -------------------------------------
// -------------------------------------------------------------------------------------------------

    private val mutableHttpLoadingStatus = MutableLiveData(HttpApi.Companion.HttpClientState.NO_WORK)
    val httpLoadingStatus: LiveData<HttpApi.Companion.HttpClientState> = mutableHttpLoadingStatus

    // Статус HTTP интерфейса
    override fun onHttpStatusUpdated(state: HttpApi.Companion.HttpClientState) {

        viewModelScope.launch {
            // чтобы разметка корректно отрабатывала события
            delay(20)
            mutableHttpLoadingStatus.postValue(state)
        }
    }


//todo ---------------------------------------------------------------------------------------------
// -------------------------- Регистрация пользователя (Создание учётки) ---------------------------
// -------------------------------------------------------------------------------------------------

    fun tryRegisterUser(
            email: String,
            password: String,
            address: Array<String>,//[4]
            name: String,
            phone: String
    ) {
        Log.i(TAG, "tryRegisterUser")

        // пытаемся аутентифицировать пользователя
        httpClient.createNewUser(
                email = "myemail21@email.com",
                password = "1",
                confirmPassword = "1",
                address = address,
                name = name,
                phone = phone
        )

        // в любом случае сохраняем последние введённые поля
        spHolder.setUserLastAuth(
                email,
                password
        )
    }

    // обратная связь от Api
    override fun httpCreateUserFailure(status: String) {
        Log.i(TAG, "httpCreateUserFailure status=$status")
        sendToast("Ошибка при создании пользователя: $status")
    }

    // обратная связь от Api
    override fun httpCreateUserSuccess(token: String) {
        Log.i(TAG, "httpCreateUserSuccess token=$token")
        sendToast("Пользователь успешно создан. Загрузка профиля")
        // сохраняем созданый токен
        this.token.postValue(token)

        viewModelScope.launch {
            // вытягиваем данные из пользователя и сохраняем их
            httpClient.getUserData(token)
        }
    }


//todo ---------------------------------------------------------------------------------------------
// --------------------------------- Аутентификация пользователя -----------------------------------
// -------------------------------------------------------------------------------------------------

    fun authUser(email: String, password: String) {
        Log.i(TAG, "authUser email=$email password=$password")

        // отправляем асинхроннный запрос
        viewModelScope.launch {
            // вход пользователя
            httpClient.loginUser(email, password)
        }
    }

    // обратная связь от Api
    override fun httpLoginUserFailure(status: String) {
        Log.i(TAG, "httpLoginUserFailure status=$status")
        sendToast("Ошибка: $status")
    }

    // обратная связь от Api
    override fun httpLoginUserSuccess(token: String) {
        Log.i(TAG, "httpLoginUserSuccess token=$token")
        // сохраняем полученый токен
        this.token.postValue(token)

        sendToast("Авторизация успешна. Загрузка профиля")

        // отправляем асинхроннный запрос
        viewModelScope.launch {
            // вытягиваем данные из пользователя и сохраняем их
            httpClient.getUserData(token)
        }
    }

    // Получение данных пользователя неуспешно
    override fun httpGetUserDataFailure(status: String) {
        Log.i(TAG, "httpGetUserDataFailure status=$status")
        TODO("Not yet implemented")
    }

    // Данные пользователя получены
    override fun httpGetUserDataSuccess(user: EntityUser) {
        Log.i(TAG, "httpGetUserDataSuccess")
        sendToast("Данные загружены")
        currentUser.postValue(user)
    }


//todo ---------------------------------------------------------------------------------------------
// -----------------------------------  ------------------------------------
// -------------------------------------------------------------------------------------------------


    // редактирование пользователя
    fun editUser(editedUserData: EntityUser) {
//        Document editedDocument = editedUserData.getDocument();
//
//        // получаем таблицу пользователей
//        MongoCollection<Document> usersCollection = mongoDatabase.getCollection(DBUser.TABLE_NAME);
//
//        // Получаем идентификатор пользователя из измененных данных
//        ObjectId userId = editedDocument.getObjectId(DBUser.USER_ID);
//
//        // Обновляем документ пользователя новыми данными
//        usersCollection.findOneAndReplace(new Document(DBUser.USER_ID, userId), editedDocument).getAsync(result -> {
//            if (result.isSuccess()) {
//                Document updatedUser = result.get();
//                if (updatedUser != null) {
//                    // если данные сохранены успешно, обновляем глобальную копию переменной и интерфейс
//                    currentUser.setValue(editedUserData);
//                    sendToast("Данные пользователя успешно сохранены");
//                } else {
//                    sendToast("Ошибка, пользователь не найден");
//                }
//            } else {
//                sendToast("Ошибка при сохранении данных пользователя");
//            }
//        });
    }

    fun loadUserNotifications() {
        val user = currentUser.value
        if (user != null) {
            //            // получаем таблицу уведомлений
//            MongoCollection<Document> notificationsCollection = mongoDatabase.getCollection(DBNotification.TABLE_NAME);
//
//            // поиск непрочитанных уведомлений пользователя в бд
//            Document query = new Document(DBNotification.NOTIFICATION_USER, user.get_id()).append(
//                    DBNotification.NOTIFICATION_STATUS, DBNotification.NOTIFICATION_STATUS_UNREAD);
//
//            notificationsCollection.find(query).iterator().getAsync(result -> {
//                if (result.isSuccess()) {
//                    // сохраняем все уведомления в лист
//                    MongoCursor<Document> cursor = result.get();
//
//                    // пробегаемся по всем уведомлениям
//                    List<DBNotification> loadedNotifications = new LinkedList<>();
//                    while (cursor.hasNext())
//                        loadedNotifications.add(new DBNotification(cursor.next()));
//
//                    // передаем получившийся лист в глобальный отслеживаемый
//                    currentUserNotifications.setValue(loadedNotifications);
//                }
//            });
        }
    }

    // пометить уведомление прочитанным
    fun markReadAdminNotification(notificationID: Long) {
        //        // получаем таблицу уведомлений
//        MongoCollection<Document> notificationsCollection = mongoDatabase.getCollection(DBNotification.TABLE_NAME);
//
//        // Создаем запрос для поиска документа по идентификатору уведомления
//        Document query = new Document(DBNotification.NOTIFICATION_ID, notificationID);
//
//        // Обновление поля
//        Document update = new Document("$set", new Document(
//                DBNotification.NOTIFICATION_STATUS, DBNotification.NOTIFICATION_STATUS_BEEN_READ));
//
//        // Выполнение обновления
//        notificationsCollection.updateOne(query, update).getAsync(result -> {
//            if (result.isSuccess()) {
//                loadUserNotifications();
//            } else {
//                sendToast("Ошибка при обновлении поля");
//            }
//        });
    }

    // загрузить отправления пользователя
    fun loadUserExpeditions() {
        val user = currentUser.value
        if (user != null) {
            //            // получаем таблицу отправлений
//            MongoCollection<Document> expeditionsCollection = mongoDatabase.getCollection(DBExpedition.TABLE_NAME);
//
//            // поиск отправлений пользователя в бд
//            Document query = new Document(DBExpedition.EXPEDITION_SENDER, user.get_id());
//
//            expeditionsCollection.find(query).iterator().getAsync(result -> {
//                if (result.isSuccess()) {
//
//                    // сохраняем все отправления в лист
//                    MongoCursor<Document> cursor = result.get();
//
//                    // пробегаемся по всем посылкам
//                    List<DBExpedition> loadedData = new ArrayList<>();
//                    while (cursor.hasNext())
//                        loadedData.add(new DBExpedition(cursor.next()));
//
//                    // передаем получившийся лист в глобальный отслеживаемый
//                    currentUserExpeditions.setValue(loadedData);
//                }
//            });
        }
    }

    fun createExpedition(expedition: DBExpedition?) {
        val user = currentUser.value
        if (user != null) {
            //            // получаем таблицу отправлений
//            MongoCollection<Document> expeditionsCollection = mongoDatabase.getCollection(DBExpedition.TABLE_NAME);
//
//            Document expeditionDocument = new Document()
//                    .append(DBExpedition.EXPEDITION_ADDRESS_RECEIVER, expedition.getAddressReceiver().getDocument())
//                    .append(DBExpedition.EXPEDITION_ADDRESS_SENDER, expedition.getAddressSender().getDocument())
//                    .append(DBExpedition.EXPEDITION_STATUS, expedition.getStatus())
//                    .append(DBExpedition.EXPEDITION_SENDER, user.get_id())
//                    .append(DBExpedition.EXPEDITION_PACKAGE, expedition.getPackage().toDocument());
//
//            // вставка нового пользователя в коллекцию
//            expeditionsCollection.insertOne(expeditionDocument).getAsync(insertResult -> {
//                if (insertResult.isSuccess()) {
//                    sendToast("Отправление успешно создано");
//
//                    // и сразу загрузка всего заново
//                    loadUserExpeditions();
//
//                } else {
//                    sendToast("Ошибка при создании отправления = " + insertResult.getError());
//                    Log.e("Test", "Ошибка при создании отправления = " + insertResult.getError());
//                }
//            });
        }
    }

    fun editExpedition(editedExpeditionData: DBExpedition?) {
        val user = currentUser.value
        if (user != null) {
            //            // получаем таблицу отправлений
//            MongoCollection<Document> expeditionsCollection = mongoDatabase.getCollection(DBExpedition.TABLE_NAME);
//
//            // документ редактирования
//            Document editedDocument = editedExpeditionData.getDocument();
//
//
//            // Обновляем документ пользователя новыми данными
//            expeditionsCollection.findOneAndReplace(
//                    new Document(DBExpedition.EXPEDITION_ID, editedExpeditionData.get_id()),
//                    editedDocument
//            ).getAsync(result -> {
//                if (result.isSuccess()) {
//                    Document updatedDocument = result.get();
//                    if (updatedDocument != null) {
//                        // если данные сохранены успешно, обновляем глобальную копию переменной и интерфейс
//                        loadUserExpeditions();
//
//                        sendToast("Данные отправления успешно сохранены");
//                    } else {
//                        sendToast("Ошибка, отправление не найдено");
//                    }
//                } else {
//                    sendToast("Ошибка при сохранении данных отправления");
//                }
//            });
        }
    }

    fun loadTrips() {
        //        // получаем таблицу
//        MongoCollection<Document> tripsCollection = mongoDatabase.getCollection(DBTrip.TABLE_NAME);
//
//        // получаем последние 10
//        tripsCollection.find().limit(10).iterator().getAsync(result -> {
//            if (result.isSuccess()) {
//                // сохраняем все в лист
//                MongoCursor<Document> cursor = result.get();
//
//                // пробегаемся по всем
//                List<DBTrip> loadedNotifications = new LinkedList<>();
//                while (cursor.hasNext())
//                    loadedNotifications.add(new DBTrip(cursor.next()));
//
//                // передаем получившийся лист в глобальный отслеживаемый
//                currentLoadedTrips.setValue(loadedNotifications);
//            }
//        });
    }


}

