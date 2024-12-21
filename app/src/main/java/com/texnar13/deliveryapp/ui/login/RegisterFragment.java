package com.texnar13.deliveryapp.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputLayout;
import com.texnar13.deliveryapp.R;
import com.texnar13.deliveryapp.model.entities.EntityUser;
import com.texnar13.deliveryapp.view_model.MainViewModel;

public class RegisterFragment extends Fragment {

    // Required empty public constructor
    public RegisterFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);


        TextInputLayout emailField = rootView.findViewById(R.id.fragment_register_input_mail);
        TextInputLayout passwordField = rootView.findViewById(R.id.fragment_register_input_password);
        TextInputLayout nameField = rootView.findViewById(R.id.fragment_register_input_name);
        TextInputLayout phoneField = rootView.findViewById(R.id.fragment_register_input_phone);
        TextInputLayout addressField = rootView.findViewById(R.id.fragment_register_input_address);


        // получаем вьюмодель
        MainViewModel viewModel = MainViewModel.Companion.getViewModel(requireActivity());


        // отслеживаем авторизацию и состояние текущего пользователя
        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            // если пользователь получен из базы
            if (user != null) {
                // переход на страницу пользователя
                Navigation.findNavController(requireActivity(), R.id.activity_main_nav_host_fragment).navigate(
                        R.id.action_registerFragment_to_mainFragment
                );
            }
        });




        // нажатие кнопки регистрация
        rootView.findViewById(R.id.fragment_register_create_button).setOnClickListener(view -> {

            // todo сделать блокировку кнопки если все данные корректны

            // --------- проверка полей ---------
            boolean isCorrect = true;
            if (emailField.getEditText().getText().toString().trim().length() == 0) {
                isCorrect = false;
                emailField.setError("Поле пустое!");
            } else
                emailField.setErrorEnabled(false);

            if (passwordField.getEditText().getText().toString().trim().length() == 0) {
                isCorrect = false;
                passwordField.setError("Поле пустое!");
            } else
                passwordField.setErrorEnabled(false);

            if (nameField.getEditText().getText().toString().trim().length() == 0) {
                isCorrect = false;
                nameField.setError("Поле пустое!");
            } else
                nameField.setErrorEnabled(false);

            if (phoneField.getEditText().getText().toString().trim().length() == 0) {
                isCorrect = false;
                phoneField.setError("Поле пустое!");
            } else if (phoneField.getEditText().getText().toString().trim().length() != 11) {
                isCorrect = false;
                phoneField.setError("Некорректный телефон!");
            } else
                phoneField.setErrorEnabled(false);

            // разбиваем адрес по запятым
            String address = addressField.getEditText().getText().toString().trim();
            String[] addressesArray = address.split(",");
            for (int i = 0; i < addressesArray.length; i++)
                addressesArray[i] = addressesArray[i].trim();


            if (address.length() == 0) {
                isCorrect = false;
                addressField.setError("Поле пустое!");
            } else {

                if (addressesArray.length != 4) {
                    isCorrect = false;
                    addressField.setError("Ночь, Улица, Фонарь, Аптека");
                } else {

                    // проверка каждого отдельного слова
                    for (String s : addressesArray) {
                        if (s.length() == 0)
                            isCorrect = false;
                    }
                    if (isCorrect)
                        addressField.setErrorEnabled(false);
                }
            }


            // если всё ок
            if (isCorrect) {


                // отправляем во вьюмодель
                viewModel.tryRegisterUser(
                        emailField.getEditText().getText().toString().trim(),
                        passwordField.getEditText().getText().toString().trim(),
                        addressesArray,
                        nameField.getEditText().getText().toString().trim(),
                        "+" + phoneField.getEditText().getText().toString().trim()
                );
            }

        });

        return rootView;
    }
}






/*
 *
 *
 *
 *
 * */