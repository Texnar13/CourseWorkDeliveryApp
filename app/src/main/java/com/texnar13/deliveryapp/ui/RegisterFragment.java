package com.texnar13.deliveryapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.texnar13.deliveryapp.R;

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


        rootView.findViewById(R.id.fragment_login_login_button).setOnClickListener(view -> {

            EditText emailText = rootView.findViewById(R.id.fragment_login_login_edit_email);
            EditText nameText = rootView.findViewById(R.id.fragment_login_login_edit_name);
            EditText passwordText = rootView.findViewById(R.id.fragment_login_login_edit_password);
            EditText passwordRepeatText = rootView.findViewById(R.id.fragment_login_login_edit_password_repeat);

            // если введённые пароли одинаковы
            if (passwordText.getText().toString().equals(passwordRepeatText.getText().toString())) {
                // передача данных в Activity
                ((MainActivityInterface) Objects.requireNonNull(getActivity())).registerUser(
                        emailText.getText().toString().trim(),
                        nameText.getText().toString().trim(),
                        passwordText.getText().toString().trim()
                );
            } else {
                Toast.makeText(getContext(), "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            }

        });

        return rootView;
    }
}