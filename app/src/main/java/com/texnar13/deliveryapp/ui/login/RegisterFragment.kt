package com.texnar13.deliveryapp.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.texnar13.deliveryapp.R
import com.texnar13.deliveryapp.model.entities.EntityAddress
import com.texnar13.deliveryapp.view_model.MainViewModel.Companion.getViewModel


class RegisterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_register, container, false)


        val emailField = rootView.findViewById<TextInputLayout>(R.id.fragment_register_input_mail)
        val passwordField =
            rootView.findViewById<TextInputLayout>(R.id.fragment_register_input_password)
        val nameField = rootView.findViewById<TextInputLayout>(R.id.fragment_register_input_name)
        val phoneField = rootView.findViewById<TextInputLayout>(R.id.fragment_register_input_phone)
        val addressField =
            rootView.findViewById<TextInputLayout>(R.id.fragment_register_input_address)


        // получаем вьюмодель
        val viewModel = getViewModel(requireActivity())


        // отслеживаем авторизацию и состояние текущего пользователя
        viewModel.token.observe(
            viewLifecycleOwner
        ) {
            // если токен получен
            if (it != null) {
                // переход на страницу пользователя
                findNavController(
                    requireActivity(),
                    R.id.activity_main_nav_host_fragment
                ).navigate(R.id.action_registerFragment_to_mainFragment)
            }
        }


        // нажатие кнопки регистрация
        rootView.findViewById<View>(R.id.fragment_register_create_button)
            .setOnClickListener {

                // todo сделать блокировку кнопки если все данные корректны
                // --------- проверка полей ---------
                var isCorrect = true

                // Регулярное выражение для проверки почты

                val mailText = emailField.editText!!.text.toString().trim { it <= ' ' }
                val passwordText = passwordField.editText!!.text.toString().trim { it <= ' ' }
                val nameText = nameField.editText!!.text.toString().trim { it <= ' ' }
                val phoneText = phoneField.editText!!.text.toString().trim { it <= ' ' }
                val addressText = addressField.editText!!.text.toString().trim { it <= ' ' }

                val mailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$".toRegex()
                if (mailText.isEmpty()) {
                    isCorrect = false
                    emailField.error = "Поле пустое!"
                } else if (!mailRegex.matches(mailText)) {
                    isCorrect = false
                    emailField.error = "Неправильный формат "
                } else  emailField.isErrorEnabled = false

                if (passwordText.isEmpty()) {
                    isCorrect = false
                    passwordField.error = "Поле пустое!"
                } else passwordField.isErrorEnabled = false

                if (nameText.isEmpty()) {
                    isCorrect = false
                    nameField.error = "Поле пустое!"
                } else nameField.isErrorEnabled = false

                if (phoneText.isEmpty()) {
                    isCorrect = false
                    phoneField.error = "Поле пустое!"
                } else if (phoneText.length != 11) {
                    isCorrect = false
                    phoneField.error = "Некорректный телефон!"
                } else phoneField.isErrorEnabled = false

                // разбиваем адрес по запятым
                val addressesArray =
                    addressText.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                for (i in addressesArray.indices) addressesArray[i] =
                    addressesArray[i].trim { it <= ' ' }


                if (addressText.isEmpty()) {
                    isCorrect = false
                    addressField.error = "Поле пустое!"
                } else {
                    if (addressesArray.size != 4) {
                        isCorrect = false
                        addressField.error = "Страна, Город, Район, Улица"
                    } else {
                        // проверка каждого отдельного слова

                        for (s in addressesArray) {
                            if (s.isEmpty()) isCorrect = false
                        }
                        if (isCorrect) addressField.isErrorEnabled = false
                    }
                }


                // если всё ок
                if (isCorrect) {
                    // отправляем во вьюмодель
                    viewModel.tryRegisterUser(
                        mailText,
                        passwordText,
                        EntityAddress(addressesArray),
                        nameText,
                        "+$phoneText"
                    )
                }
            }

        return rootView
    }
}