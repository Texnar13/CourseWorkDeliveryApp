package com.texnar13.deliveryapp.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.texnar13.deliveryapp.R;
import com.texnar13.deliveryapp.model.DBUser;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditUserDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditUserDialogFragment extends DialogFragment {

    // the fragment initialization parameters
    private static final String ARG_USER = "user";

    public EditUserDialogFragment() {
        // Required empty public constructor
    }

    //Use this factory method
    public static EditUserDialogFragment newInstance(DBUser user) {
        EditUserDialogFragment fragment = new EditUserDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // данные для диалога
        Bundle arguments = requireArguments();
        // получаем пользователя
        DBUser user = (DBUser) Objects.requireNonNull(arguments.get(ARG_USER));


        // начинаем строить диалог
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Редактирование пользователя");
        // layout диалога
        View dialogLayout = getLayoutInflater().inflate(R.layout.fragment_edit_user_dialog, null);
        builder.setView(dialogLayout);


        // вывод данных в поля погулять
        TextView userIdText = dialogLayout.findViewById(R.id.fragment_edit_user_dialog_id);
        userIdText.setText("id=" + user.get_id().toString());
        TextInputLayout inputMail = dialogLayout.findViewById(R.id.fragment_edit_user_dialog_input_mail);
        inputMail.getEditText().setText(user.getEmail());
        TextInputLayout inputName = dialogLayout.findViewById(R.id.fragment_edit_user_dialog_input_name);
        inputName.getEditText().setText(user.getName());
        TextInputLayout inputPhone = dialogLayout.findViewById(R.id.fragment_edit_user_dialog_input_phone);
        inputPhone.getEditText().setText(user.getPhoneNumber().substring(1));
        TextInputLayout inputCountry = dialogLayout.findViewById(R.id.fragment_edit_user_dialog_input_country);
        inputCountry.getEditText().setText(user.getAddress()[0]);
        TextInputLayout inputCity = dialogLayout.findViewById(R.id.fragment_edit_user_dialog_input_city);
        inputCity.getEditText().setText(user.getAddress()[1]);
        TextInputLayout inputDistrict = dialogLayout.findViewById(R.id.fragment_edit_user_dialog_input_district);
        inputDistrict.getEditText().setText(user.getAddress()[2]);
        TextInputLayout inputStreet = dialogLayout.findViewById(R.id.fragment_edit_user_dialog_input_street);
        inputStreet.getEditText().setText(user.getAddress()[3]);

        // кнопка отмены
        Button cancelButton = dialogLayout.findViewById(R.id.fragment_edit_user_dialog_cancel_button);
        cancelButton.setOnClickListener(view -> dismiss());

        // кнопка сохранения
        Button acceptButton = dialogLayout.findViewById(R.id.fragment_edit_user_dialog_save_button);
        acceptButton.setOnClickListener(v -> {


            // --------- проверка полей ---------
            boolean isCorrect = true;
            if (inputCountry.getEditText().getText().toString().trim().length() == 0) {
                isCorrect = false;
                inputCountry.setError("Поле пустое!");
            } else
                inputCountry.setErrorEnabled(false);

            if (inputCity.getEditText().getText().toString().trim().length() == 0) {
                isCorrect = false;
                inputCity.setError("Поле пустое!");
            } else
                inputCity.setErrorEnabled(false);

            if (inputDistrict.getEditText().getText().toString().trim().length() == 0) {
                isCorrect = false;
                inputDistrict.setError("Поле пустое!");
            } else
                inputDistrict.setErrorEnabled(false);

            if (inputStreet.getEditText().getText().toString().trim().length() == 0) {
                isCorrect = false;
                inputStreet.setError("Поле пустое!");
            } else
                inputStreet.setErrorEnabled(false);

            if (inputMail.getEditText().getText().toString().trim().length() == 0) {
                isCorrect = false;
                inputMail.setError("Поле пустое!");
            } else
                inputMail.setErrorEnabled(false);

            if (inputName.getEditText().getText().toString().trim().length() == 0) {
                isCorrect = false;
                inputName.setError("Поле пустое!");
            } else
                inputName.setErrorEnabled(false);

            if (inputPhone.getEditText().getText().toString().trim().length() == 0) {
                isCorrect = false;
                inputPhone.setError("Поле пустое!");
            } else if (inputPhone.getEditText().getText().toString().trim().length() != 11) {
                isCorrect = false;
                inputPhone.setError("Некорректный телефон!");
            } else
                inputPhone.setErrorEnabled(false);


            if (isCorrect) {
                // отправка изменённых значений в бд
                ((MainActivityInterface) Objects.requireNonNull(getActivity())).editUser(new DBUser(
                        user.get_id(),
                        user.getPassword(),
                        new String[]{
                                inputCountry.getEditText().getText().toString(),
                                inputCity.getEditText().getText().toString(),
                                inputDistrict.getEditText().getText().toString(),
                                inputStreet.getEditText().getText().toString()
                        },
                        inputMail.getEditText().getText().toString(),
                        inputName.getEditText().getText().toString(),
                        "+" + inputPhone.getEditText().getText().toString(),
                        user.getPicture(),
                        user.getRating()
                ));

                dismiss();
            }
        });


        // наконец создаем диалог и возвращаем его
        Dialog dialog = builder.create();
        //dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_user_dialog, container, false);
    }
}


