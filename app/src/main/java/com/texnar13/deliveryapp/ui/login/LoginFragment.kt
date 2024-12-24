package com.texnar13.deliveryapp.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.texnar13.deliveryapp.R
import com.texnar13.deliveryapp.view_model.MainViewModel.Companion.getViewModel


class LoginFragment : Fragment() {

    // обновляемая разметка
    lateinit var loginContainer: View
    lateinit var registerButton: View


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_login, container, false)

        // разметка
        loginContainer = rootView.findViewById(R.id.fragment_login_input_container)
        registerButton = rootView.findViewById(R.id.fragment_login_go_to_register_button)
        val email = rootView.findViewById<EditText>(R.id.fragment_login_login_edit_email)
        val password = rootView.findViewById<EditText>(R.id.fragment_login_login_edit_password)


        // работа с ViewModel
        val viewModel = getViewModel(requireActivity())

        // Загружаем предыдущие вводимые поля
        val lastAuth = viewModel.getUserLastAuth()
        email.setText(lastAuth[0])
        password.setText(lastAuth[1])

        // отслеживаем текущего пользователя
        viewModel.token.observe(viewLifecycleOwner) { token ->
            // если токен был получен и пользователь авторизован
            if (token != null) {
                // переход на страницу пользователя
                findNavController(requireActivity(), R.id.activity_main_nav_host_fragment).navigate(
                        R.id.action_loginFragment_to_userFragment
                )
            }
        }


        // кнопка авторизации
        rootView.findViewById<View>(R.id.fragment_login_login_button).setOnClickListener {


            // отдаем данные аутентификации viewModel
            viewModel.authUser(
                    email.text.toString().trim { it <= ' ' },
                    password.text.toString().trim { it <= ' ' }
            )

        }

        // переход на фрагмент регистрации
        registerButton.setOnClickListener {
            // переход на страницу регистрации
            findNavController(requireActivity(), R.id.activity_main_nav_host_fragment).navigate(
                    R.id.action_loginFragment_to_registerFragment
            )

        }

        return rootView
    }

}