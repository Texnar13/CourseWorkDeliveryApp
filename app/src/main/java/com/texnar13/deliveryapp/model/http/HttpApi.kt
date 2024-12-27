package com.texnar13.deliveryapp.model.http

import android.util.Log
import com.texnar13.deliveryapp.model.entities.EntityAddress
import com.texnar13.deliveryapp.model.entities.EntityExpedition
import com.texnar13.deliveryapp.model.entities.EntityPackage
import com.texnar13.deliveryapp.model.entities.EntityTrip
import com.texnar13.deliveryapp.model.entities.EntityUser
import kotlinx.coroutines.delay
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.Date

class HttpApi(
    address: String
) {
    // todo здесь можно сделать переменную статуса HTTPклиента, по которой выводить ProgressBar
    companion object {
        private const val TAG = "HTTP_API"


        enum class HttpClientState {
            NO_WORK,
            IN_PROCESS
        }

        enum class ErrorCode {
            TOKEN_EXPIRED, // токен устарел
            AUTH_ERROR,// ошибка данных авторизации
            BAD_REQUEST, // Некорректный запрос
            UNDEFINED_ERROR
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
        fun httpEditUserDataSuccess()

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
    private var serverAddress = "http://$address"

    fun updateServerAddress(newAddress: String) {
        serverAddress = "http://$newAddress"
    }

// --------------------------------------------------------
// ------------------------ Методы ------------------------
// --------------------------------------------------------

    // POST запрос создания нового пользователя и получения токена
    suspend fun createNewUser(
        email: String,
        password: String,
        address: EntityAddress,//[4]
        name: String,
        phone: String
    ) {

        // Создаём тело запроса с медиатипом JSON
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaType(),
            // Тело запроса в формате JSON
            JSONObject().apply {
                put("email", email)
                put("password", password)
                put("confirm_password", password)
                put("name", name)
                put("phone", phone)
                put("country", address.getCountry())
                put("city", address.getCity())
                put("district", address.getDistrict())
                put("street", address.getStreet())
            }.toString()
        )

        // Создаём запрос
        val request = Request.Builder()
            .url("$serverAddress/create_user")
            .post(requestBody)
            .build()

        // Отправляем запрос асинхронно
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Обработка ошибки подключения
                httpResultAndStatusListener?.httpCreateUserFailure(
                    ErrorCode.UNDEFINED_ERROR,
                    "Ошибка подключения: ${e.message}"
                )
            }

            override fun onResponse(call: Call, response: Response) {
                // Обработка ответа сервера
                response.use {
                    if (!it.isSuccessful) {
                        httpResultAndStatusListener?.httpCreateUserFailure(
                            ErrorCode.UNDEFINED_ERROR,
                            "${it.code}"
                        )
                    } else {

                        // если пришел ответ с пустым телом
                        if (it.body == null) {
                            httpResultAndStatusListener?.httpCreateUserFailure(
                                ErrorCode.UNDEFINED_ERROR,
                                "Пустой ответ"
                            )
                        } else {

                            // если пришел ответ
                            val responseBody = it.body!!.string()
                            Log.i(TAG, "Ответ сервера: $responseBody")

                            // Получаем JSON ответ
                            val jsonResponse = JSONObject(responseBody)

                            // Если получили HTTP ошибку
                            if (jsonResponse.has("error")) {
                                httpResultAndStatusListener?.httpCreateUserFailure(
                                    ErrorCode.UNDEFINED_ERROR,
                                    "Ошибка: code=" +
                                            jsonResponse.getString("code") +
                                            "error=" +
                                            jsonResponse.getString("error")
                                )
                            } else {
                                httpResultAndStatusListener?.httpCreateUserSuccess(
                                    jsonResponse.getString("token")
                                )
                            }
                        }
                    }
                }
            }
        })
    }


    /** POST запрос авторизации и получения токена пользователя
    "$serverAddress/login_user"
    {
    "email": "someemail2@email.com",
    "password": "1"
    }
     */
    suspend fun loginUser(
        email: String,
        password: String
    ) {
        // Выставляем статус
        updateStatus(HttpClientState.IN_PROCESS)

        // Создаём тело запроса с медиатипом JSON
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaType(),
            // Тело запроса в формате JSON
            JSONObject().apply {
                put("email", email)
                put("password", password)
            }.toString()
        )

        // Создаём запрос
        val request = Request.Builder()
            .url("$serverAddress/login_user")
            .post(requestBody)
            .build()

        // Отправляем запрос асинхронно
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Обработка ошибки подключения
                httpResultAndStatusListener?.httpLoginUserFailure(
                    ErrorCode.UNDEFINED_ERROR,
                    "${e.message}"
                )
                // Выставляем статус
                updateStatus(HttpClientState.NO_WORK)
            }

            override fun onResponse(call: Call, response: Response) {
                // Обработка ответа сервера
                if (response.isSuccessful) {
                    httpResultAndStatusListener?.httpLoginUserSuccess(
                        // Получаем JSON ответ
                        JSONObject(response.body!!.string()).getString("token")
                    )
                } else {
                    when (response.code) {
                        400 -> {
                            httpResultAndStatusListener?.httpLoginUserFailure(
                                ErrorCode.AUTH_ERROR,
                                "[${response.code}] некорректные данные"
                            )
                        }

                        401 -> {
                            httpResultAndStatusListener?.httpLoginUserFailure(
                                ErrorCode.AUTH_ERROR,
                                "[${response.code}] Проверьте данные аутентификации"
                            )
                        }

                        else -> {
                            httpResultAndStatusListener?.httpLoginUserFailure(
                                ErrorCode.UNDEFINED_ERROR,
                                "${response.code}"
                            )
                        }
                    }
                }
                // Выставляем статус
                updateStatus(HttpClientState.NO_WORK)
            }
        })
    }


    /** Get запрос получения данных пользователя
     * $serverAddress/get_user
     * Передаётся jwt-токен
     * Ответ
     * {
     *     "ID": 3,
     *     "CreatedAt": "2024-12-25T01:05:36.162233Z",
     *     "UpdatedAt": "2024-12-25T01:05:36.162233Z",
     *     "DeletedAt": null,
     *     "email": "someemail2@email.com",
     *     "name": "qwe",
     *     "phone": "+7(952)812",
     *     "country": "russia",
     *     "city": "52",
     *     "district": "52",
     *     "street": "len"
     * }
     */
    suspend fun getUserData(token: String) {// TODO Сделать
        // Выставляем статус
        updateStatus(HttpClientState.IN_PROCESS)

        // Создаём запрос
        val request = Request.Builder()
            .url("$serverAddress/get_user") // URL для получения данных пользователя
            .addHeader("Authorization", "Bearer $token") // Добавляем заголовок с JWT токеном
            .get() // GET запрос
            .build()

        // Отправляем запрос асинхронно
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Обработка ошибки подключения
                httpResultAndStatusListener?.httpGetUserDataFailure(
                    ErrorCode.UNDEFINED_ERROR,
                    "${e.message}"
                )
                // Выставляем статус
                updateStatus(HttpClientState.NO_WORK)
            }

            override fun onResponse(call: Call, response: Response) {
                // Обработка ответа сервера
                if (response.isSuccessful) {
                    // Обрабатываем успешный ответ
                    val responseData = response.body?.string()
                    if (responseData.isNullOrEmpty()) {
                        httpResultAndStatusListener?.httpGetUserDataFailure(
                            ErrorCode.UNDEFINED_ERROR,
                            "Empty response body"
                        )
                    } else {
                        val json = JSONObject(responseData)
                        httpResultAndStatusListener?.httpGetUserDataSuccess(
                            EntityUser(
                                id = json.getLong("ID"),
                                email = json.getString("email"),
                                name = json.getString("name"),
                                phoneNumber = json.getString("phone"),
                                address = EntityAddress(
                                    json.getString("country"),
                                    json.getString("city"),
                                    json.getString("district"),
                                    json.getString("street")
                                )
                            )
                        )
                    }
                } else {
                    when (response.code) {
                        400 -> {
                            httpResultAndStatusListener?.httpGetUserDataFailure(
                                ErrorCode.BAD_REQUEST,
                                "[${response.code}] Некорректный запрос"
                            )
                        }

                        401 -> {
                            httpResultAndStatusListener?.httpGetUserDataFailure(
                                ErrorCode.TOKEN_EXPIRED,
                                "[${response.code}] Токен авторизации истёк или некорректен"
                            )
                        }

                        else -> {
                            httpResultAndStatusListener?.httpGetUserDataFailure(
                                ErrorCode.UNDEFINED_ERROR,
                                "${response.code}"
                            )
                        }
                    }
                }
                // Выставляем статус
                updateStatus(HttpClientState.NO_WORK)
            }
        })
    }

    /** редактирование пользователя (METHOD POST)
     * $serverAddress/update_user
     * Передаётся jwt-токен
     *
     * body
     * {
     * "email": "example@example.com",
     * "name": "John Doe",
     * "phone": "+1234567890",
     * "country": "USA",
     * "city": "New York",
     * "district": "Manhattan",
     * "street": "5th Avenue"
     * }
     *
     * {
     * "message": "Registration successful"
     * }
     *
     */
    suspend fun editUserData(token: String, editedUserData: EntityUser) {
        // Выставляем статус
        updateStatus(HttpClientState.IN_PROCESS)

        // Создаём тело запроса с медиатипом JSON
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaType(),
            // Тело запроса в формате JSON
            JSONObject().apply {
                put("email", editedUserData.email)
                put("name", editedUserData.name)
                put("phone", editedUserData.phoneNumber)
                put("country", editedUserData.address.getCountry())
                put("city", editedUserData.address.getCity())
                put("district", editedUserData.address.getDistrict())
                put("street", editedUserData.address.getStreet())
            }.toString()
        )

        // Создаём запрос
        val request = Request.Builder()
            .url("$serverAddress/update_user")
            .addHeader("Authorization", "Bearer $token") // Добавляем заголовок с JWT токеном
            .post(requestBody)
            .build()

        // Отправляем запрос асинхронно
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Обработка ошибки подключения
                httpResultAndStatusListener?.httpEditUserDataFailure(
                    ErrorCode.UNDEFINED_ERROR,
                    "${e.message}"
                )
                // Выставляем статус
                updateStatus(HttpClientState.NO_WORK)
            }

            override fun onResponse(call: Call, response: Response) {
                // Обработка ответа сервера
                if (response.isSuccessful) {
//                    // Получаем JSON ответ
//                    val jsonObject = JSONObject(response.body!!.string())
                    httpResultAndStatusListener?.httpEditUserDataSuccess()
                } else {
                    when (response.code) {
                        400 -> {
                            httpResultAndStatusListener?.httpEditUserDataFailure(
                                ErrorCode.BAD_REQUEST,
                                "[${response.code}] некорректный запрос"
                            )
                        }

                        401 -> {
                            httpResultAndStatusListener?.httpEditUserDataFailure(
                                ErrorCode.TOKEN_EXPIRED,
                                "[${response.code}] Токен авторизации истёк или некорректен"
                            )
                        }

                        else -> {
                            httpResultAndStatusListener?.httpEditUserDataFailure(
                                ErrorCode.UNDEFINED_ERROR,
                                "${response.code}"
                            )
                        }
                    }
                }
                // Выставляем статус
                updateStatus(HttpClientState.NO_WORK)
            }
        })
    }

    /** Загружаем отправления пользователя (METHOD GET)
     * /select_by_sender Передаётся jwt-токен
     *
     * response:
     *
     * [
     * {
     * "ID": 2,
     * "CreatedAt": "2024-12-25T01:05:52.067598Z",
     * "UpdatedAt": "2024-12-25T01:05:52.067598Z",
     * "DeletedAt": null,
     * "sender_id": 3,
     * "courier_id": null,
     * "status": null,
     * "dep_country": "USA",
     * "dep_city": "New York",
     * "dep_district": "Manhattan",
     * "dep_street": "5th Avenue",
     * "dest_country": "Canada",
     * "dest_city": "Toronto",
     * "dest_district": "Downtown",
     * "dest_street": "King Street",
     * "name": "Electronics",
     * "category": "Gadgets",
     * "description": "Smartphone and accessories",
     * "width": 15,
     * "height": 10,
     * "length": 20,
     * "weight": 1.5
     * },
     * ...
     * ]
     */
    suspend fun loadUserExpeditions(token: String) {
        // Выставляем статус
        updateStatus(HttpClientState.IN_PROCESS)

        // Создаём запрос
        val request = Request.Builder()
            .url("$serverAddress/select_by_sender") // URL
            .addHeader("Authorization", "Bearer $token") // JWT токен
            .get() // GET запрос
            .build()

        // Отправляем запрос асинхронно
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Обработка ошибки подключения
                httpResultAndStatusListener?.httpLoadUserExpeditionsFailure(
                    ErrorCode.UNDEFINED_ERROR,
                    "${e.message}"
                )
                // Выставляем статус
                updateStatus(HttpClientState.NO_WORK)
            }

            override fun onResponse(call: Call, response: Response) {
                // Обработка ответа сервера
                if (response.isSuccessful) {
                    // Обрабатываем успешный ответ
                    // получаем json обьект и преобразуем строку в массив объектов
                    val jsonArray = JSONArray(response.body!!.string())

                    // результирующий лист
                    val expeditions = List(jsonArray.length()) { pos ->
                        // json обьект
                        val jsonObject = jsonArray.getJSONObject(pos)

                        // Создаём объект посылки и как результат добавляем в список
                        EntityExpedition(
                            id = jsonObject.getLong("ID"),
                            // Парсинг адресов отправителя и получателя
                            addressReceiver = EntityAddress(
                                jsonObject.getString("dest_country"),
                                jsonObject.getString("dest_city"),
                                jsonObject.getString("dest_district"),
                                jsonObject.getString("dest_street")
                            ),
                            addressSender = EntityAddress(
                                jsonObject.getString("dep_country"),
                                jsonObject.getString("dep_city"),
                                jsonObject.getString("dep_district"),
                                jsonObject.getString("dep_street")
                            ),
                            status = when (jsonObject.optString("status")) {
                                "Sent" -> EntityExpedition.Companion.ExpeditionStatus.SENT
                                "Done" -> EntityExpedition.Companion.ExpeditionStatus.DONE
                                else -> EntityExpedition.Companion.ExpeditionStatus.WAIT_SEND
                            },
                            senderId = jsonObject.getLong("sender_id"),
                            courierId = jsonObject.getLong("courier_id"),
                            // посылка
                            expeditionPackage = EntityPackage(
                                category = jsonObject.getString("category"),
                                description = jsonObject.getString("description"),
                                dimensions = arrayOf(
                                    jsonObject.getDouble("width").toFloat(),
                                    jsonObject.getDouble("height").toFloat(),
                                    jsonObject.getDouble("length").toFloat()
                                ),
                                weight = jsonObject.getDouble("weight").toFloat(),
                                name = jsonObject.getString("name"),
                                picture = ""
                            )
                        )
                    }


                    httpResultAndStatusListener?.httpLoadUserExpeditionsSuccess(expeditions)

                } else {
                    when (response.code) {
                        400 -> {
                            httpResultAndStatusListener?.httpLoadUserExpeditionsFailure(
                                ErrorCode.BAD_REQUEST,
                                "[${response.code}] Некорректный запрос"
                            )
                        }

                        401 -> {
                            httpResultAndStatusListener?.httpLoadUserExpeditionsFailure(
                                ErrorCode.TOKEN_EXPIRED,
                                "[${response.code}] Токен авторизации истёк или некорректен"
                            )
                        }

                        else -> {
                            httpResultAndStatusListener?.httpLoadUserExpeditionsFailure(
                                ErrorCode.UNDEFINED_ERROR,
                                "${response.code} хз почему посылки не грузит /select_by_sender"
                            )
                        }
                    }
                }
                // Выставляем статус
                updateStatus(HttpClientState.NO_WORK)
            }
        })
    }

    /** создание нового отправления (METHOD POST)
     *  /create_package Передаётся jwt-токен
     *
     *  body:
     *{
     * "dep_country": "USA3",
     * "dep_city": "New York",
     * "dep_district": "Manhattan",
     * "dep_street": "5th Avenue",
     * "dest_country": "Canada",
     * "dest_city": "Toronto",
     * "dest_district": "Downtown",
     * "dest_street": "King Street",
     * "name": "Electronics",
     * "category": "Gadgets",
     * "description": "Smartphone and accessories",
     * "width": 15.0,
     * "height": 10.0,
     * "length": 20.0,
     * "weight": 1.5
     * }
     *
     * response:
     * {
     * "message": "Package created"
     * }
     *
     */
    suspend fun createUserExpedition(token: String, expedition: EntityExpedition) {
        // Выставляем статус
        updateStatus(HttpClientState.IN_PROCESS)

        // Создаём тело запроса с медиатипом JSON
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaType(),
            // Тело запроса в формате JSON
            JSONObject().apply {
                put("dep_country" , expedition.addressSender.getCountry())
                put("dep_city"    , expedition.addressSender.getCity())
                put("dep_district", expedition.addressSender.getDistrict())
                put("dep_street"  , expedition.addressSender.getStreet())
                put("dest_country", expedition.addressReceiver.getCountry())
                put("dest_city", expedition.addressReceiver.getCity())
                put("dest_district", expedition.addressReceiver.getDistrict())
                put("dest_street", expedition.addressReceiver.getStreet())
                put("name",expedition.expeditionPackage.name)
                put("category",expedition.expeditionPackage.category)
                put("description",expedition.expeditionPackage.description)
                put("width",expedition.expeditionPackage.dimensions[0])
                put("height",expedition.expeditionPackage.dimensions[1])
                put("length",expedition.expeditionPackage.dimensions[2])
                put("weight",expedition.expeditionPackage.weight)
            }.toString()
        )

        // Создаём запрос
        val request = Request.Builder()
            .url("$serverAddress/create_package")
            .addHeader("Authorization", "Bearer $token") // Добавляем заголовок с JWT токеном
            .post(requestBody)
            .build()

        // Отправляем запрос асинхронно
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Обработка ошибки подключения
                httpResultAndStatusListener?.httpCreateExpeditionFailure(
                    ErrorCode.UNDEFINED_ERROR,
                    "${e.message}"
                )
                // Выставляем статус
                updateStatus(HttpClientState.NO_WORK)
            }

            override fun onResponse(call: Call, response: Response) {
                // Обработка ответа сервера
                if (response.isSuccessful) {
                    httpResultAndStatusListener?.httpCreateExpeditionSuccess(expedition)
                } else {
                    when (response.code) {
                        400 -> {
                            httpResultAndStatusListener?.httpCreateExpeditionFailure(
                                ErrorCode.BAD_REQUEST,
                                "[${response.code}] некорректный запрос"
                            )
                        }

                        401 -> {
                            httpResultAndStatusListener?.httpCreateExpeditionFailure(
                                ErrorCode.TOKEN_EXPIRED,
                                "[${response.code}] Токен авторизации истёк или некорректен"
                            )
                        }

                        else -> {
                            httpResultAndStatusListener?.httpCreateExpeditionFailure(
                                ErrorCode.UNDEFINED_ERROR,
                                "${response.code}"
                            )
                        }
                    }
                }
                // Выставляем статус
                updateStatus(HttpClientState.NO_WORK)
            }
        })
    }


    /** Редактирование отправления (METHOD POST)
     * /update_package Передаётся jwt-токен
     *
     * body:
     * {
     * "ID":12,
     * "sender_id": 3,
     * "courier_id": 3,
     * "status": "Delivered",
     * "dep_country": "KZKZKZKZ",
     * "dep_city": "New York",
     * "dep_district": "Manhattan",
     * "dep_street": "5th Avenue",
     * "dest_country": "Russia",
     * "dest_city": "Toronto",
     * "dest_district": "Downtown",
     * "dest_street": "Queen Street",
     * "name": "Electronics Package",
     * "category": "Electronics",
     * "description": "A box containing various electronic devices.",
     * "width": 30.5,
     * "height": 20.0,
     * "length": 40.0,
     * "weight": 15.75
     * }
     *
     * response:
     * {
     * "message": "Package updated"
     * }
     */
    suspend fun editExpedition(token: String, expedition: EntityExpedition) {
        // Выставляем статус
        updateStatus(HttpClientState.IN_PROCESS)



        // Создаём тело запроса с медиатипом JSON
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaType(),
            // Тело запроса в формате JSON
            JSONObject().apply {
                put("ID",expedition.id)
                put("sender_id",expedition.senderId)
                put("courier_id",expedition.courierId)
                put("status",expedition.status)
                put("dep_country" ,expedition.addressSender.getCountry())
                put("dep_city"    ,expedition.addressSender.getCity())
                put("dep_district",expedition.addressSender.getDistrict())
                put("dep_street"  ,expedition.addressSender.getStreet())
                put("dest_country",expedition.addressReceiver.getCountry())
                put("dest_city",expedition.addressReceiver.getCity())
                put("dest_district",expedition.addressReceiver.getDistrict())
                put("dest_street",expedition.addressReceiver.getStreet())
                put("name",expedition.expeditionPackage.name)
                put("category",expedition.expeditionPackage.category)
                put("description",expedition.expeditionPackage.description)
                put("width",expedition.expeditionPackage.dimensions[0])
                put("height",expedition.expeditionPackage.dimensions[1])
                put("length",expedition.expeditionPackage.dimensions[2])
                put("weight",expedition.expeditionPackage.weight)
            }.toString()
        )

        // Создаём запрос
        val request = Request.Builder()
            .url("$serverAddress/update_package")
            .addHeader("Authorization", "Bearer $token") // Добавляем заголовок с JWT токеном
            .post(requestBody)
            .build()

        // Отправляем запрос асинхронно
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Обработка ошибки подключения
                httpResultAndStatusListener?.httpEditExpeditionFailure(
                    ErrorCode.UNDEFINED_ERROR,
                    "${e.message}"
                )
                // Выставляем статус
                updateStatus(HttpClientState.NO_WORK)
            }

            override fun onResponse(call: Call, response: Response) {
                // Обработка ответа сервера
                if (response.isSuccessful) {
                    httpResultAndStatusListener?.httpEditExpeditionSuccess(expedition)
                } else {
                    when (response.code) {
                        400 -> {
                            httpResultAndStatusListener?.httpEditExpeditionFailure(
                                ErrorCode.BAD_REQUEST,
                                "[${response.code}] некорректный запрос"
                            )
                        }

                        401 -> {
                            httpResultAndStatusListener?.httpEditExpeditionFailure(
                                ErrorCode.TOKEN_EXPIRED,
                                "[${response.code}] Токен авторизации истёк или некорректен"
                            )
                        }

                        else -> {
                            httpResultAndStatusListener?.httpEditExpeditionFailure(
                                ErrorCode.UNDEFINED_ERROR,
                                "${response.code}"
                            )
                        }
                    }
                }
                // Выставляем статус
                updateStatus(HttpClientState.NO_WORK)
            }
        })
    }


    /** Поиск маршрутов по параметрам и загрузка списка
     *
     */
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

    /** посмотреть данные отправления и маршрута
     *
     */
    suspend fun loadTrajectoryDataForExpedition(token: String, expedition: EntityExpedition) {

        // Выставляем статус
        updateStatus(HttpClientState.IN_PROCESS)

        delay(1500)
        httpResultAndStatusListener?.loadTrajectoryDataForExpeditionSuccess(
            EntityTrip(
                0,
                "100$",
                "Russia",
                "Moscow",
                expedition.addressSender.getCountry(),
                expedition.addressSender.getCity(),
                Date(),
                3F
            )
        )

        // посмотреть данные отправления и маршрута
//        httpResultAndStatusListener?.loadTrajectoryDataForExpeditionFailure(errorCode: ErrorCode, status: String)
//        httpResultAndStatusListener?.loadTrajectoryDataForExpeditionSuccess(trajectoryAndExpedition: EntityExpedition)

        // Выставляем статус
        updateStatus(HttpClientState.NO_WORK)
    }

    /** Привязать свою посылку к маршруту
     *
     */
    suspend fun selectPackageDeliveryTrip(
        token: String,
        expedition: EntityExpedition,
        value: EntityTrip
    ) {

        // Выставляем статус
        updateStatus(HttpClientState.IN_PROCESS)

        delay(1500)

        //httpResultAndStatusListener?.selectPackageDeliveryTripFailure()
        httpResultAndStatusListener?.selectPackageDeliveryTripSuccess()

        // Выставляем статус
        updateStatus(HttpClientState.NO_WORK)
    }

}


