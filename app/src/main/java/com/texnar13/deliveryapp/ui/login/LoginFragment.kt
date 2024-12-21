package com.texnar13.deliveryapp.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.texnar13.deliveryapp.LoaderAndBottomPanel
import com.texnar13.deliveryapp.R
import com.texnar13.deliveryapp.model.DBUser
import com.texnar13.deliveryapp.view_model.MainViewModel
import com.texnar13.deliveryapp.view_model.MainViewModel.Companion.getViewModel
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.IOException


class LoginFragment : Fragment() {

    // обновляемая разметка
    lateinit var loginContainer: View
    lateinit var registerButton: View


    // Есть ли соединение с сервером
    var internetConnected: Boolean = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_login, container, false)

        // разметка
        loginContainer = rootView.findViewById(R.id.fragment_login_input_container)
        registerButton = rootView.findViewById(R.id.fragment_login_go_to_register_button)
        loginContainer.visibility = View.INVISIBLE
        registerButton.visibility = View.INVISIBLE


        // работа с ViewModel
        val viewModel = getViewModel(requireActivity())


        // отслеживаем состояние подключения
//        viewModel.activityConnectionStatus.observe(viewLifecycleOwner) { status ->
//            when (status) {
//                MainViewModel.ConnectionStatusValue.STATUS_NONE -> {
//
//                    // Включаем отображение загрузки
//                    (requireActivity() as LoaderAndBottomPanel).enableLoadBar()
//                    internetConnected = false
//                }
//
//                MainViewModel.ConnectionStatusValue.STATUS_ERROR -> {
//                    Toast.makeText(
//                            context,
//                            "Нет соединения с сервером...",
//                            Toast.LENGTH_SHORT
//                    ).show()
//
//                    internetConnected = false
//                }
//
//                MainViewModel.ConnectionStatusValue.STATUS_CONNECTED -> {
//        Log.e("Hello", "loaded")

        internetConnected = true
        loginContainer.visibility = View.VISIBLE
        registerButton.visibility = View.VISIBLE

//        (requireActivity() as LoaderAndBottomPanel).disableLoadBar()

//        Toast.makeText(context, "Соединение с сервером установлено", Toast.LENGTH_SHORT).show()
//                }
//
//                null -> {}
//            }
//        }

        // отслеживаем текущего пользователя
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            // если пользователь получен из базы
            if (user != null) {
                // переход на страницу пользователя
                findNavController(requireActivity(), R.id.activity_main_nav_host_fragment).navigate(
                        R.id.action_loginFragment_to_userFragment
                )
            }
        }


        // кнопка авторизации
        rootView.findViewById<View>(R.id.fragment_login_login_button).setOnClickListener {


            if (internetConnected) {
                val email = rootView.findViewById<EditText>(R.id.fragment_login_login_edit_email)
                val password = rootView.findViewById<EditText>(R.id.fragment_login_login_edit_password)

                // отдаем данные аутентификации viewModel
                viewModel.authUser(
                        email.text.toString().trim { it <= ' ' },
                        password.text.toString().trim { it <= ' ' }
                )
            }
        }

        // переход на фрагмент регистрации
        registerButton.setOnClickListener {
            if (internetConnected) {
                // переход на страницу регистрации
                findNavController(requireActivity(), R.id.activity_main_nav_host_fragment).navigate(
                        R.id.action_loginFragment_to_registerFragment
                )
            }
        }

        return rootView
    }

}