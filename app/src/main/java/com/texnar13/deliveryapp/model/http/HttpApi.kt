package com.texnar13.deliveryapp.model.http

import com.texnar13.deliveryapp.model.entities.EntityAddress
import com.texnar13.deliveryapp.model.entities.EntityExpedition
import com.texnar13.deliveryapp.model.entities.EntityPackage
import com.texnar13.deliveryapp.model.entities.EntityTrip
import com.texnar13.deliveryapp.model.entities.EntityUser
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import java.util.Date

class HttpApi(
        address: String = "192.168.1.66:8080"
) {
    // todo здесь можно сделать переменную статуса HTTPклиента, по которой выводить ProgressBar
    companion object {
        private const val TAG = "HTTP_API"


        enum class HttpClientState {
            NO_WORK,
            IN_PROCESS
        }

        enum class ErrorCode {
            TOKEN_EXPIRED,
            CONNECTION_ERROR
        }
    }


    //private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())

// --------------------------------------------------------
// ------------ Для просмотра статуса запросов ------------
// --------------------------------------------------------


    interface HttpResultAndStatusListener {

        // статус работы (работает или не работает)
        fun onHttpStatusUpdated(state: HttpClientState)

        // -- Обратная связь для слушателей --

        // создание пользователя
        fun httpCreateUserFailure(errorCode: ErrorCode, status: String)
        fun httpCreateUserSuccess(token: String)

        // аутентификация
        fun httpLoginUserFailure(errorCode: ErrorCode, status: String)
        fun httpLoginUserSuccess(token: String)

        // получение данных о пользователе
        fun httpGetUserDataFailure(errorCode: ErrorCode, status: String)
        fun httpGetUserDataSuccess(user: EntityUser)

        // Редактирование профиля пользователя
        fun httpEditUserDataFailure(errorCode: ErrorCode, status: String)
        fun httpEditUserDataSuccess(user: EntityUser)

        // Загрузка отправлений пользователя
        fun httpLoadUserExpeditionsFailure(errorCode: ErrorCode, status: String)
        fun httpLoadUserExpeditionsSuccess(expeditions: List<EntityExpedition>)

        // Создание нового отправления
        fun httpCreateExpeditionFailure(errorCode: ErrorCode, status: String)
        fun httpCreateExpeditionSuccess(expedition: EntityExpedition)

        // Редактирование старого отправления
        fun httpEditExpeditionFailure(errorCode: ErrorCode, status: String)
        fun httpEditExpeditionSuccess(expedition: EntityExpedition)

        // Загрузка списка маршрутов по параметрам
        fun httpLoadTrajectoriesFailure(errorCode: ErrorCode, status: String)
        fun httpLoadTrajectoriesSuccess(trips: List<EntityTrip>)

        // посмотреть данные отправления и маршрута
        fun loadTrajectoryDataForExpeditionFailure(errorCode: ErrorCode, status: String)
        fun loadTrajectoryDataForExpeditionSuccess(trajectoryAndExpedition: EntityTrip)

        // в "Сделать заявку" была выбрана посылка
        fun selectPackageDeliveryTripFailure()
        fun selectPackageDeliveryTripSuccess()

        // ..

    }

    // внутренняя ссылка на слушатель
    private var httpResultAndStatusListener: HttpResultAndStatusListener? = null

    // внешний сеттер
    fun setHttpResultListener(listener: HttpResultAndStatusListener) {
        this.httpResultAndStatusListener = listener
    }


    // отправка статуса
    private fun updateStatus(state: HttpClientState) {
        httpResultAndStatusListener?.onHttpStatusUpdated(state)
    }


// --------------------------------------------------------
// ---------------- Внутренние переменные -----------------
// --------------------------------------------------------

    // Создаём клиент OkHttp
    private val client = OkHttpClient()

    // Адрес сервера относительно которого всё делаем
    private val serverAddress = "http://$address"

// --------------------------------------------------------
// ------------------------ Методы ------------------------
// --------------------------------------------------------

    // POST запрос создания нового пользователя
    suspend fun createNewUser(
            email: String,
            password: String,
            confirmPassword: String,
            address: Array<String>,//[4]
            name: String,
            phone: String
    ) {
        // test
        httpResultAndStatusListener?.httpCreateUserSuccess("token")


// todo работает
//
//        // Создаём тело запроса с медиатипом JSON
//        val requestBody = RequestBody.create(
//                "application/json; charset=utf-8".toMediaType(),
//                // Тело запроса в формате JSON
//                JSONObject().apply {
//                    put("email", email)
//                    put("password", password)
//                    put("confirm_password", confirmPassword)
//                }.toString()
//        )
//
//        // Создаём запрос
//        val request = Request.Builder()
//                .url("$serverAddress/create_new_user")
//                .post(requestBody)
//                .build()
//
//        // Отправляем запрос асинхронно
//        client.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                // Обработка ошибки подключения
//                httpResultAndStatusListener?.httpCreateUserFailure(
//                        "Ошибка подключения: ${e.message}"
//                )
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                // Обработка ответа сервера
//                response.use {
//                    if (!it.isSuccessful) {
//                        httpResultAndStatusListener?.httpCreateUserFailure(
//                                "Ошибка сервера: ${it.code}"
//                        )
//                    } else {
//
//                        // если пришел ответ с пустым телом
//                        if (it.body == null) {
//                            httpResultAndStatusListener?.httpCreateUserFailure(
//                                    "Пустой ответ"
//                            )
//                        } else {
//
//                            // если пришел ответ
//                            val responseBody = it.body!!.string()
//                            Log.i(TAG, "Ответ сервера: $responseBody")
//
//                            // Получаем JSON ответ
//                            val jsonResponse = JSONObject(responseBody)
//
//                            // Если получили HTTP ошибку
//                            if (jsonResponse.has("error")) {
//                                httpResultAndStatusListener?.httpCreateUserFailure("Ошибка: code=" +
//                                        jsonResponse.getString("code") +
//                                        "error=" +
//                                        jsonResponse.getString("error")
//                                )
//                            } else {
//                                httpResultAndStatusListener?.httpCreateUserSuccess(
//                                        jsonResponse.getString("token")
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//        })
    }


    // POST запрос получения токена пользователя и авторизации
    suspend fun loginUser(
            email: String,
            password: String
    ) {// TODO Сделать
        // Выставляем статус
        updateStatus(HttpClientState.IN_PROCESS)

        "$serverAddress/login_user"
        delay(1500)
        httpResultAndStatusListener?.httpLoginUserSuccess("success 123")
        // httpResultAndStatusListener?.httpLoginUserFailure(status: String)
        // httpResultAndStatusListener?.httpLoginUserSuccess(token: String)

    }


    // POST запрос получения токена пользователя и авторизации
    suspend fun getUserData(token: String) {// TODO Сделать
        // Выставляем статус
        updateStatus(HttpClientState.IN_PROCESS)


        delay(1500)
        httpResultAndStatusListener?.httpGetUserDataSuccess(
                EntityUser(
                        "1234L",
                        "password",
                        EntityAddress(
                                "Россия",
                                "Бобруйск",
                                "ул. Левая",
                                "полуторка"
                        ),
                        "email",
                        "Ivan M",
                        "+78005553535",
                        5.4
                )

        )
        //httpResultAndStatusListener?.httpGetUserDataFailure(status: String)
        //httpResultAndStatusListener?.httpGetUserDataSuccess(user: EntityUser)

        // Выставляем статус
        updateStatus(HttpClientState.NO_WORK)
    }

    // редактирование пользователя
    suspend fun editUserData(editedUserData: EntityUser) {
        // Выставляем статус
        updateStatus(HttpClientState.IN_PROCESS)

        delay(1500)
//        httpResultAndStatusListener?.httpEditUserDataSuccess(editedUserData)


        httpResultAndStatusListener?.httpEditUserDataFailure(ErrorCode.TOKEN_EXPIRED, "Пипец")
        // httpResultAndStatusListener?.httpEditUserDataSuccess(editedUserData)

        // Выставляем статус
        updateStatus(HttpClientState.NO_WORK)
    }

    // Загружаем отправления пользователя
    suspend fun loadUserExpeditions(token: String) {
        // Выставляем статус
        updateStatus(HttpClientState.IN_PROCESS)

        delay(1500)

        val expeditions = listOf(
                EntityExpedition(
                        0,
                        EntityAddress(
                                "Россия",
                                "Бобруйск",
                                "ул. Левая",
                                "полуторка"
                        ),
                        EntityAddress(
                                "Россия2",
                                "Бобруйск2",
                                "ул. Левая2",
                                "полуторка2"
                        ),
                        status = EntityExpedition.Companion.ExpeditionStatus.WAIT_SEND,
                        0L,
                        EntityPackage(
                                category = "Категория1",
                                description = "Большое-при большое, длинное-при длинное описание",
                                dimensions = arrayOf(10.0F, 20.0F, 12.0F),
                                weight = 0.1F,
                                name = "Супер пупер посылка",
                                picture = "Э img?"

                        )
                ),
                EntityExpedition(
                        1,
                        EntityAddress(
                                "Россия1",
                                "Бобруйск2",
                                "ул. Левая3",
                                "полуторка4"
                        ),
                        EntityAddress(
                                "Россия2",
                                "Бобруйск2",
                                "ул. Левая2",
                                "полуторка2"
                        ),
                        status = EntityExpedition.Companion.ExpeditionStatus.SENT,
                        0L,
                        EntityPackage(
                                category = "Категория2",
                                description = "Большое-при большое, длинное-при длинное описание",
                                dimensions = arrayOf(10.0F, 20.0F, 12.0F),
                                weight = 0.1F,
                                name = "Супер пупер посылка2",
                                picture = "Э img?"

                        )
                ),
                EntityExpedition(
                        2,
                        EntityAddress(
                                "Россия1",
                                "Бобруйск2",
                                "ул. Левая3",
                                "полуторка4"
                        ),
                        EntityAddress(
                                "Россия2",
                                "Бобруйск2",
                                "ул. Левая2",
                                "полуторка2"
                        ),
                        status = EntityExpedition.Companion.ExpeditionStatus.DONE,
                        0L,
                        EntityPackage(
                                category = "Категория2",
                                description = "Большое-при большое, длинное-при длинное описание",
                                dimensions = arrayOf(10.0F, 20.0F, 12.0F),
                                weight = 0.1F,
                                name = "Супер пупер посылка2",
                                picture = "Э img?"

                        )
                )
        )

        httpResultAndStatusListener?.httpLoadUserExpeditionsSuccess(expeditions)
//        httpResultAndStatusListener?.httpLoadUserExpeditionsFailure(errorCode, status)
//        httpResultAndStatusListener?.httpLoadUserExpeditionsSuccess(expeditions)


        // Выставляем статус
        updateStatus(HttpClientState.NO_WORK)
    }

    // создание нового отправления
    suspend fun createUserExpedition(token: String, expedition: EntityExpedition) {
        // Выставляем статус
        updateStatus(HttpClientState.IN_PROCESS)

        delay(1500)


        // Создание нового отправления
        //httpResultAndStatusListener?.httpCreateExpeditionFailure(errorCode: ErrorCode, status: String)
        httpResultAndStatusListener?.httpCreateExpeditionSuccess(expedition)


        // Выставляем статус
        updateStatus(HttpClientState.NO_WORK)
    }


    suspend fun editExpedition(token: String, expedition: EntityExpedition) {
        // Выставляем статус
        updateStatus(HttpClientState.IN_PROCESS)

        delay(1500)


//        // Редактирование старого отправления
//        httpResultAndStatusListener?.httpEditExpeditionFailure(errorCode: ErrorCode, status: String)
        httpResultAndStatusListener?.httpEditExpeditionSuccess(expedition)


        // Выставляем статус
        updateStatus(HttpClientState.NO_WORK)
    }


    // Загрузка списка маршрутов по параметрам
    suspend fun loadTrajectoriesByParams() {
        // Выставляем статус
        updateStatus(HttpClientState.IN_PROCESS)

        delay(1500)

        httpResultAndStatusListener?.httpLoadTrajectoriesSuccess(
                listOf(
                        EntityTrip(
                                0,
                                "100$",
                                "Russia",
                                "Moscow",
                                "Russia",
                                "Vladivostok",
                                Date(),
                                3F
                        )
                )
        )

//        httpResultAndStatusListener?.httpLoadTrajectoriesFailure(errorCode: ErrorCode, status: String)
//        httpResultAndStatusListener?.httpLoadTrajectoriesSuccess(trajectories: List<EntityTrip>)

        // Выставляем статус
        updateStatus(HttpClientState.NO_WORK)
    }

    // посмотреть данные отправления и маршрута
    suspend fun loadTrajectoryDataForExpedition(token: String, expedition: EntityExpedition) {

        // Выставляем статус
        updateStatus(HttpClientState.IN_PROCESS)

        delay(1500)
        httpResultAndStatusListener?.loadTrajectoryDataForExpeditionSuccess(EntityTrip(
                0,
                "100$",
                "Russia",
                "Moscow",
                expedition.addressSender.getCountry(),
                expedition.addressSender.getCity(),
                Date(),
                3F
        ))

        // посмотреть данные отправления и маршрута
//        httpResultAndStatusListener?.loadTrajectoryDataForExpeditionFailure(errorCode: ErrorCode, status: String)
//        httpResultAndStatusListener?.loadTrajectoryDataForExpeditionSuccess(trajectoryAndExpedition: EntityExpedition)

        // Выставляем статус
        updateStatus(HttpClientState.NO_WORK)
    }

    // в "Сделать заявку" была выбрана посылка
    suspend fun selectPackageDeliveryTrip(token: String, expedition: EntityExpedition, value: EntityTrip) {

        // Выставляем статус
        updateStatus(HttpClientState.IN_PROCESS)

        delay(1500)

        //httpResultAndStatusListener?.selectPackageDeliveryTripFailure()
        httpResultAndStatusListener?.selectPackageDeliveryTripSuccess()

        // Выставляем статус
        updateStatus(HttpClientState.NO_WORK)
    }

}


