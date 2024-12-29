package com.texnar13.deliveryapp.view_model

import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.texnar13.deliveryapp.model.entities.EntityAddress
import com.texnar13.deliveryapp.model.entities.EntityExpedition
import com.texnar13.deliveryapp.model.entities.EntityExpedition.Companion.ExpeditionStatus
import com.texnar13.deliveryapp.model.entities.EntityPackage
import com.texnar13.deliveryapp.model.entities.EntityTrip
import com.texnar13.deliveryapp.model.entities.EntityUser
import com.texnar13.deliveryapp.model.http.HttpApi
import com.texnar13.deliveryapp.model.shared_preferences.SPHolder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Date


class MainViewModel(
    private val httpClient: HttpApi,
    private val spHolder: SPHolder
) : ViewModel(), HttpApi.HttpResultAndStatusListener {


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


// -------------------------------------------------------------------------------------------------
// -------------------------------------- Загруженные данные ---------------------------------------
// -------------------------------------------------------------------------------------------------


    var token: MutableLiveData<String?> = MutableLiveData(null)

    // текущий пользователь
    var currentUser: MutableLiveData<EntityUser?> = MutableLiveData(null)

    // посылки пользователя
    val currentUserExpeditions: MutableLiveData<List<EntityExpedition>?> = MutableLiveData(null)

    val selectedExpedition: MutableLiveData<EntityExpedition?> = MutableLiveData(null)


    // загруженные поездки
    var currentLoadedTrips: MutableLiveData<List<EntityTrip>> = MutableLiveData()


// -------------------------------------------------------------------------------------------------
// ------------------------------------------ Конструктор ------------------------------------------
// -------------------------------------------------------------------------------------------------

    // нициализация
    init {
        Log.i(TAG, "init")

        // назначаем слушатель HTTP ответов и статуса
        httpClient.setHttpResultListener(this)
    }


// -------------------------------------------------------------------------------------------------
// ------------------------------------- Отправка уведомлений --------------------------------------
// -------------------------------------------------------------------------------------------------

    // строка для отправки тостов
    var toastMessage: MutableLiveData<String> = MutableLiveData()

    private fun sendToast(message: String) {
        toastMessage.postValue(message)
    }

// -------------------------------------------------------------------------------------------------
// ------------------------------------- Прогресс бар загрузки -------------------------------------
// -------------------------------------------------------------------------------------------------

    private val mutableHttpLoadingStatus =
        MutableLiveData(HttpApi.Companion.HttpClientState.NO_WORK)
    val httpLoadingStatus: LiveData<HttpApi.Companion.HttpClientState> = mutableHttpLoadingStatus

    // Статус HTTP интерфейса
    override fun onHttpStatusUpdated(state: HttpApi.Companion.HttpClientState) {

        viewModelScope.launch {
            // чтобы разметка корректно отрабатывала события
            delay(100)
            mutableHttpLoadingStatus.postValue(state)
        }
    }


// -------------------------------------------------------------------------------------------------
// ----------------------------------- Назначение адреса сервера -----------------------------------
// -------------------------------------------------------------------------------------------------

    fun getServerAddress(): String {
        return spHolder.getServerAddress()
    }

    fun setServerAddress(address: String) {
        spHolder.setServerAddress(address)
        httpClient.updateServerAddress(address)
        sendToast("Сохранено")
    }

// -------------------------------------------------------------------------------------------------
// -------------------------- Регистрация пользователя (Создание учётки) ---------------------------
// -------------------------------------------------------------------------------------------------

    fun tryRegisterUser(
        email: String,
        password: String,
        address: EntityAddress,//[4]
        name: String,
        phone: String
    ) {
        Log.i(TAG, "tryRegisterUser")

        // в любом случае сохраняем последние введённые поля
        spHolder.setUserLastAuth(
            email,
            password
        )

        viewModelScope.launch {

            // пытаемся аутентифицировать пользователя
            httpClient.createNewUser(
                email = email,
                password = password,
                address = address,
                name = name,
                phone = phone
            )
        }
    }

    // обратная связь от Api
    override fun httpCreateUserFailure(errorCode: HttpApi.Companion.ErrorCode, status: String) {
        Log.i(TAG, "httpCreateUserFailure status=$status")
        sendToast("Ошибка: $status")
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


// -------------------------------------------------------------------------------------------------
// --------------------------------- Аутентификация пользователя -----------------------------------
// -------------------------------------------------------------------------------------------------

    // получение уже введённых ранее логина и пароля
    fun getUserLastAuth(): Array<String> {
        return spHolder.getUserLastAuth()
    }

    fun authUser(email: String, password: String) {
        Log.i(TAG, "authUser email=$email password=$password")

        // отправляем асинхроннный запрос
        viewModelScope.launch {
            // вход пользователя
            httpClient.loginUser(email, password)
        }

        // в любом случае сохраняем последние введённые поля
        spHolder.setUserLastAuth(
            email,
            password
        )
    }

    // обратная связь от Api
    override fun httpLoginUserFailure(errorCode: HttpApi.Companion.ErrorCode, status: String) {
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
    override fun httpGetUserDataFailure(errorCode: HttpApi.Companion.ErrorCode, status: String) {
        Log.i(TAG, "httpGetUserDataFailure status=$status")
        sendToast("Ошибка: $status")

    }

    // Данные пользователя получены
    override fun httpGetUserDataSuccess(user: EntityUser) {
        Log.i(TAG, "httpGetUserDataSuccess")
        sendToast("Данные загружены")
        currentUser.postValue(user)
    }


// -------------------------------------------------------------------------------------------------
// --------------------------------- Страничка пользователя -----------------------------------
// -------------------------------------------------------------------------------------------------


    // выход из учетной записи пользователя
    fun logout() {
        Log.i(TAG, "logout")
        token.postValue(null)
    }

    // редактирование пользователя
    private var tempEditedUserData: EntityUser? = null
    fun editUser(editedUserData: EntityUser) {
        tempEditedUserData = editedUserData

        val token = token.value
        if (token != null) {
            // отправляем асинхроннный запрос
            viewModelScope.launch {
                httpClient.editUserData(token, editedUserData)
            }
        }
    }

    override fun httpEditUserDataFailure(errorCode: HttpApi.Companion.ErrorCode, status: String) {
        when (errorCode) {
            HttpApi.Companion.ErrorCode.TOKEN_EXPIRED -> {
                sendToast("Сессия истекла: $status")
                // Выходим из текущего пользователя
                logout()
            }

            else -> sendToast("Ошибка при сохранении: $status")
        }
    }

    override fun httpEditUserDataSuccess() {
        sendToast("Данные пользователя успешно сохранены")
        currentUser.postValue(tempEditedUserData)
    }


// -------------------------------------------------------------------------------------------------
// -------------------------------------- Страничка маршрутов --------------------------------------
// -------------------------------------------------------------------------------------------------


    fun loadTrips(
        departCountry:String,
        departCity: String,
        destinationCountry:String,
        destinationCity: String,
        departDate: Date,
        freeWeightMin: Float
    ) {// todo


        val token = token.value
        if (token != null) {
            viewModelScope.launch {
                httpClient.loadTrajectoriesByParams(
                    token,
                    departCountry,
                    departCity,
                    destinationCountry,
                    destinationCity,
                    departDate,
                    freeWeightMin
                )
            }
        }
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


    override fun httpLoadTrajectoriesFailure(
        errorCode: HttpApi.Companion.ErrorCode,
        status: String
    ) {
        when (errorCode) {
            HttpApi.Companion.ErrorCode.TOKEN_EXPIRED -> {
                sendToast("Сессия истекла: $status")
                // Выходим из текущего пользователя
                logout()
            }

            else -> sendToast("Ошибка: $status")
        }
    }

    override fun httpLoadTrajectoriesSuccess(trips: List<EntityTrip>) {
        currentLoadedTrips.postValue(trips)
    }


// -------------------------------------------------------------------------------------------------
// ------------------------------------- Страничка отправлений -------------------------------------
// -------------------------------------------------------------------------------------------------


    // загрузить отправления пользователя
    fun loadUserExpeditions() {
        // обнуляем
        currentUserExpeditions.postValue(null)

        val token = token.value
        if (token != null) {
            // Загружаем отправления пользователя
            viewModelScope.launch {
                httpClient.loadUserExpeditions(token)
            }
        }
    }

    // ответ Http загрузчика
    override fun httpLoadUserExpeditionsFailure(
        errorCode: HttpApi.Companion.ErrorCode,
        status: String
    ) {
        when (errorCode) {
            HttpApi.Companion.ErrorCode.TOKEN_EXPIRED -> {
                sendToast("Сессия истекла: $status")
                // Выходим из текущего пользователя
                logout()
            }

            else -> sendToast("Ошибка: $status")
        }
    }

    // ответ Http загрузчика
    override fun httpLoadUserExpeditionsSuccess(expeditions: List<EntityExpedition>) {
        currentUserExpeditions.postValue(expeditions)

    }

    // создание нового отправления
    fun createExpedition(
        id: Long,
        addressReceiver: EntityAddress,
        addressSender: EntityAddress,
        status: ExpeditionStatus,
        expeditionPackage: EntityPackage
    ) {
        val expedition = EntityExpedition(
            id,
            addressReceiver,
            addressSender,
            status,
            currentUser.value!!.id,
            null,
            expeditionPackage
        )

        val token = token.value
        if (token != null) {
            // создание нового отправления
            viewModelScope.launch {
                httpClient.createUserExpedition(token, expedition)
            }
        }
    }


    // ответ создания
    override fun httpCreateExpeditionFailure(
        errorCode: HttpApi.Companion.ErrorCode,
        status: String
    ) {
        when (errorCode) {
            HttpApi.Companion.ErrorCode.TOKEN_EXPIRED -> {
                sendToast("Сессия истекла")
                // Выходим из текущего пользователя
                logout()
            }

            else ->
                sendToast("Ошибка при создании отправления = $status")
        }
    }

    // ответ создания
    override fun httpCreateExpeditionSuccess(expedition: EntityExpedition) {

        sendToast("Отправление успешно создано")

        loadUserExpeditions()
    }

    // выбираем из разметки отправление которое будет редактироваться (для вывода в диалог)
    // может передаваться null тогда это будет диалог создания
    fun selectExpeditionForEdit(editedExpeditionData: EntityExpedition?) {

        selectedExpedition.value = editedExpeditionData
    }

    // Редактируем отправление
    fun editExpedition(
        addressReceiver: EntityAddress,
        addressSender: EntityAddress,
        status: ExpeditionStatus,
        expeditionPackage: EntityPackage
        ) {
        val editedExpeditionData = EntityExpedition(
            selectedExpedition.value!!.id,
            addressReceiver,
            addressSender,
            status,
            selectedExpedition.value!!.senderId,
            selectedExpedition.value!!.courierId,
            expeditionPackage
        )

        val token = token.value
        if (token != null) {
            // Редактируем отправление
            viewModelScope.launch {
                httpClient.editExpedition(token, editedExpeditionData)
            }
        }
    }

    // ответ редактирования
    override fun httpEditExpeditionFailure(errorCode: HttpApi.Companion.ErrorCode, status: String) {
        when (errorCode) {
            HttpApi.Companion.ErrorCode.TOKEN_EXPIRED -> {
                sendToast("Сессия истекла")
                // Выходим из текущего пользователя
                logout()
            }

            else ->
                sendToast("Ошибка при редактировании отправления = $status")
        }
    }

    // ответ редактирования
    override fun httpEditExpeditionSuccess(expedition: EntityExpedition) {
        sendToast("Данные сохранены")
        loadUserExpeditions()
    }


    val trajectoryAndExpeditionData = MutableLiveData<EntityTrip?>(null)

    // Для функции "посмотреть маршрут"
    fun loadTrajectoryDataForExpedition(expedition: EntityExpedition) {
        val token = token.value
        if (token != null) {
            // Редактируем отправление
            viewModelScope.launch {
                httpClient.loadTrajectoryDataForExpedition(token, expedition)
            }
        }
    }

    // ответ "посмотреть маршрут"
    override fun loadTrajectoryDataForExpeditionFailure(
        errorCode: HttpApi.Companion.ErrorCode,
        status: String
    ) {
        TODO("Not yet implemented")
    }

    // ответ "посмотреть маршрут"
    override fun loadTrajectoryDataForExpeditionSuccess(trajectoryAndExpedition: EntityTrip) {
        trajectoryAndExpeditionData.postValue(trajectoryAndExpedition)
    }

//--------------------------------------------------------------

    val selectedTrip = MutableLiveData<EntityTrip?>(null)

    // "Сделать заявку" сопрячь
    fun selectTripForEdit(tripUnit: EntityTrip) {

        // Ставим поездку выбранной
        selectedTrip.value = tripUnit

        // Запускаем загрузку отправлений пользователя
        loadUserExpeditions()

    }

    // в "Сделать заявку" была выбрана посылка
    fun selectPackageDeliveryTrip(expedition: EntityExpedition) {
        val token = token.value
        if (token != null && selectedTrip.value != null) {
            // Редактируем отправление
            viewModelScope.launch {
                httpClient.selectPackageDeliveryTrip(token, expedition, selectedTrip.value!!)
            }
        }

    }

    override fun selectPackageDeliveryTripFailure() {
        TODO("Not yet implemented")
    }

    override fun selectPackageDeliveryTripSuccess() {
        sendToast("API говорит ДА...")
    }
}

