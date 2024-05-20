package com.texnar13.deliveryapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.texnar13.deliveryapp.R;
import com.texnar13.deliveryapp.model.DBUser;

import java.util.Objects;

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

                // передача данных в Activity
                ((MainActivityInterface) Objects.requireNonNull(getActivity())).registerUser(
                        passwordField.getEditText().getText().toString().trim(),
                        addressesArray,
                        emailField.getEditText().getText().toString().trim(),
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