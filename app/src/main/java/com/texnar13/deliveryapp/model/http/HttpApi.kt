package com.texnar13.deliveryapp.model.http

import android.util.Log
import com.texnar13.deliveryapp.model.DBAddress
import com.texnar13.deliveryapp.model.entities.EntityUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Dispatcher
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

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
    }


    //private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())

// --------------------------------------------------------
// ------------ Для просмотра статуса запросов ------------
// --------------------------------------------------------


    // Интерфейс
    interface HttpWorkStatusListener {
        fun onHttpStatusUpdated(state: HttpClientState)
    }

    // внешний сеттер
    fun setHttpWorkStatusListener(listener: HttpWorkStatusListener) {
        statusListener = listener
    }

    // внутренняя ссылка на слушатель
    private var statusListener: HttpWorkStatusListener? = null

    private fun updateStatus(state: HttpClientState) {
        statusListener?.onHttpStatusUpdated(state)
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
    fun createNewUser(
            email: String,
            password: String,
            confirmPassword: String,
            address: Array<String>,//[4]
            name: String,
            phone: String
    ) {
        // test
        httpResultListener?.httpCreateUserSuccess("token")


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
//                httpResultListener?.httpCreateUserFailure(
//                        "Ошибка подключения: ${e.message}"
//                )
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                // Обработка ответа сервера
//                response.use {
//                    if (!it.isSuccessful) {
//                        httpResultListener?.httpCreateUserFailure(
//                                "Ошибка сервера: ${it.code}"
//                        )
//                    } else {
//
//                        // если пришел ответ с пустым телом
//                        if (it.body == null) {
//                            httpResultListener?.httpCreateUserFailure(
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
//                                httpResultListener?.httpCreateUserFailure("Ошибка: code=" +
//                                        jsonResponse.getString("code") +
//                                        "error=" +
//                                        jsonResponse.getString("error")
//                                )
//                            } else {
//                                httpResultListener?.httpCreateUserSuccess(
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
        httpResultListener?.httpLoginUserSuccess("success 123")
        // httpResultListener?.httpLoginUserFailure(status: String)
        // httpResultListener?.httpLoginUserSuccess(token: String)

    }


    // POST запрос получения токена пользователя и авторизации
    suspend fun getUserData(token: String) {// TODO Сделать
        // Выставляем статус
        updateStatus(HttpClientState.IN_PROCESS)


        delay(1500)
        httpResultListener?.httpGetUserDataSuccess(
                EntityUser(
                        "1234L",
                        "password",
                        DBAddress(
                                arrayOf("Россия",
                                        "Бобруйск",
                                        "ул. Раковая",
                                        "полуторка"
                                )
                        ),
                        "email",
                        "Тот самый человек яйца",
                        "88005553535",
                        5.4
                )

        )
        //httpResultListener?.httpGetUserDataFailure(status: String)
        //httpResultListener?.httpGetUserDataSuccess(user: EntityUser)

        // Выставляем статус
        updateStatus(HttpClientState.NO_WORK)
    }


// --------------------------------- Обратная связь для слушателей ---------------------------------


    private var httpResultListener: HttpResultListener? = null

    fun setHttpResultListener(listener: HttpResultListener) {
        this.httpResultListener = listener
    }

    interface HttpResultListener {

        // создание пользователя
        fun httpCreateUserFailure(status: String)
        fun httpCreateUserSuccess(token: String)

        // аутентификация
        fun httpLoginUserFailure(status: String)
        fun httpLoginUserSuccess(token: String)

        // получение данных о пользователе
        fun httpGetUserDataFailure(status: String)
        fun httpGetUserDataSuccess(user: EntityUser)

        // ..

    }


}


