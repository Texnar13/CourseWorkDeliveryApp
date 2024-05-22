package com.texnar13.deliveryapp.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.texnar13.deliveryapp.MainViewModel;
import com.texnar13.deliveryapp.R;
import com.texnar13.deliveryapp.model.DBAddress;
import com.texnar13.deliveryapp.model.DBExpedition;
import com.texnar13.deliveryapp.model.DBPackage;
import com.texnar13.deliveryapp.model.DBUser;

import java.util.Objects;

public class ExpeditionEditDialogFragment extends DialogFragment {

    // the fragment initialization parameters
    private static final String ARG_EXPEDITION = "expedition";


    TextInputLayout senderAddressField;
    TextInputLayout receiverAddressField;
    TextInputLayout boxNameField;
    TextInputLayout boxCategoryField;
    TextInputLayout boxDescriptionField;
    TextInputLayout boxDimensField;
    TextInputLayout boxWeightField;


    public ExpeditionEditDialogFragment() {
        // Required empty public constructor
    }

    //Use this factory method
    public static ExpeditionEditDialogFragment newInstance(DBExpedition expedition) {
        ExpeditionEditDialogFragment fragment = new ExpeditionEditDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_EXPEDITION, expedition);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // получаем отправление
        Bundle arguments = requireArguments();
        DBExpedition expedition = (DBExpedition) arguments.get(ARG_EXPEDITION);

        // начинаем строить диалог
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // layout диалога
        View dialogLayout = getLayoutInflater().inflate(R.layout.fragment_dialog_edit_expeditions, null);
        builder.setView(dialogLayout);


        // инициализация разметки
        TextView titleText = dialogLayout.findViewById(R.id.fragment_dialog_edit_expeditions_title);
        TextView idText = dialogLayout.findViewById(R.id.fragment_dialog_edit_expeditions_id);

        senderAddressField = dialogLayout.findViewById(R.id.fragment_dialog_edit_expeditions_input_sender_address);
        receiverAddressField = dialogLayout.findViewById(R.id.fragment_dialog_edit_expeditions_input_receiver_address);
        boxNameField = dialogLayout.findViewById(R.id.fragment_dialog_edit_expeditions_input_name);
        boxCategoryField = dialogLayout.findViewById(R.id.fragment_dialog_edit_expeditions_input_category);
        boxDescriptionField = dialogLayout.findViewById(R.id.fragment_dialog_edit_expeditions_input_description);
        boxDimensField = dialogLayout.findViewById(R.id.fragment_dialog_edit_expeditions_input_dimens);
        boxWeightField = dialogLayout.findViewById(R.id.fragment_dialog_edit_expeditions_input_weight);

        Button cancelButton = dialogLayout.findViewById(R.id.fragment_dialog_edit_expeditions_cancel_button);
        cancelButton.setOnClickListener(v -> dismiss());
        Button saveButton = dialogLayout.findViewById(R.id.fragment_dialog_edit_expeditions_save_button);


        // вывод данных в поля, погулять
        if (expedition == null) {
            titleText.setText("Cоздание отправления");
            idText.setText("id=new");
        } else {
            titleText.setText("Редактирование отправления");
            idText.setText("id=" + expedition.get_id());

            senderAddressField.getEditText().setText(expedition.getAddressSender().getString());
            receiverAddressField.getEditText().setText(expedition.getAddressReceiver().getString());

            boxNameField.getEditText().setText(expedition.getPackage().getName());
            boxCategoryField.getEditText().setText(expedition.getPackage().getCategory());
            boxDescriptionField.getEditText().setText(expedition.getPackage().getDescription());
            boxDimensField.getEditText().setText(expedition.getPackage().getDimensionsString());
            boxWeightField.getEditText().setText("" + expedition.getPackage().getWeight());
        }

        // кнопка сохранения
        saveButton.setOnClickListener(v -> {

            // проверка полей
            if (checkFields()) {

                // разбиваем по запятым
                String[] senderAddressesArray =
                        senderAddressField.getEditText().getText().toString().trim().split(",");
                for (int i = 0; i < senderAddressesArray.length; i++)
                    senderAddressesArray[i] = senderAddressesArray[i].trim();

                String[] receiverAddressesArray =
                        receiverAddressField.getEditText().getText().toString().trim().split(",");
                for (int i = 0; i < receiverAddressesArray.length; i++)
                    receiverAddressesArray[i] = receiverAddressesArray[i].trim();

                String[] dimensStringArray = boxDimensField.getEditText().getText().toString().trim().split(",");
                double[] dimensArray = new double[dimensStringArray.length];
                for (int i = 0; i < dimensStringArray.length; i++)
                    dimensArray[i] = Double.parseDouble(dimensStringArray[i].trim());

                MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

                // если это создание нового
                if (expedition == null) {
                    mainViewModel.createExpedition(new DBExpedition(
                            null,
                            new DBAddress(receiverAddressesArray),
                            new DBAddress(senderAddressesArray),
                            DBExpedition.EXPEDITION_STATUS_VALUE_WAIT_SEND,
                            null,
                            new DBPackage(
                                    boxCategoryField.getEditText().getText().toString(),
                                    boxDescriptionField.getEditText().getText().toString(),
                                    dimensArray,
                                    Double.parseDouble(boxWeightField.getEditText().getText().toString()),
                                    boxNameField.getEditText().getText().toString(),
                                    "URL"
                            )
                    ));

                } else {
                    // если это редактирование старого
                    mainViewModel.editExpedition(
                        new DBExpedition(
                                expedition.get_id(),
                                new DBAddress(receiverAddressesArray),
                                new DBAddress(senderAddressesArray),
                                DBExpedition.EXPEDITION_STATUS_VALUE_WAIT_SEND,
                                expedition.getSender(),
                                new DBPackage(
                                        boxCategoryField.getEditText().getText().toString(),
                                        boxDescriptionField.getEditText().getText().toString(),
                                        dimensArray,
                                        Double.parseDouble(boxWeightField.getEditText().getText().toString()),
                                        boxNameField.getEditText().getText().toString(),
                                        expedition.getPackage().getPicture()
                                )
                        )
                    );

                }
                dismiss();
            }
        });

        // наконец создаем диалог и возвращаем его
        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dialog_edit_user, container, false);
    }


    // --------- проверка полей ---------
    boolean checkFields() {
        boolean isCorrect = true;

        // Если адрес не прошел проверку
        if (!checkAddress(senderAddressField)) isCorrect = false;
        if (!checkAddress(receiverAddressField)) isCorrect = false;

        // проверка пустых полей
        if (!checkFieldOnEmpty(boxNameField)) isCorrect = false;
        if (!checkFieldOnEmpty(boxCategoryField)) isCorrect = false;
        if (!checkFieldOnEmpty(boxDescriptionField)) isCorrect = false;

        // поле габаритов
        if (!checkDimensField(boxDimensField)) isCorrect = false;

        // поле веса
        try {
            Double.valueOf(boxWeightField.getEditText().getText().toString());
            boxWeightField.setErrorEnabled(false);
        } catch (NumberFormatException e) {
            boxWeightField.setError("Неправильное число!");
            isCorrect = false;
        }
        return isCorrect;
    }

    private boolean checkAddress(@NonNull TextInputLayout inputLayout) {

        String inputText = inputLayout.getEditText().getText().toString().trim();

        if (inputText.length() == 0) {
            inputLayout.setError("Поле пустое!");
            return false;
        } else {
            // разбиваем адрес по запятым
            String[] addressesArray = inputText.split(",");
            for (int i = 0; i < addressesArray.length; i++)
                addressesArray[i] = addressesArray[i].trim();

            // нехватает полей
            if (addressesArray.length != 4) {
                inputLayout.setError("Ночь, Улица, Фонарь, Аптека");
                return false;
            } else {
                // проверка каждого отдельного слова
                for (String s : addressesArray)
                    if (s.length() == 0) {
                        inputLayout.setError("Ночь, Улица, Фонарь, Аптека");
                        return false;
                    }
            }
        }

        // всё ок
        inputLayout.setErrorEnabled(false);
        return true;
    }

    private boolean checkFieldOnEmpty(@NonNull TextInputLayout inputLayout) {
        if (inputLayout.getEditText().getText().toString().trim().length() == 0) {
            inputLayout.setError("Поле пустое!");
            return false;
        }
        inputLayout.setErrorEnabled(false);
        return true;
    }

    private boolean checkDimensField(@NonNull TextInputLayout inputLayout) {

        String inputText = inputLayout.getEditText().getText().toString().trim();

        if (inputText.length() == 0) {
            inputLayout.setError("Поле пустое!");
            return false;
        } else {

            // разбиваем по запятым
            String[] dimensArray = inputText.split(",");
            for (int i = 0; i < dimensArray.length; i++)
                dimensArray[i] = dimensArray[i].trim();

            // нехватает полей
            if (dimensArray.length != 3) {
                inputLayout.setError("Ширина, Высота, Глубина");
                return false;
            } else {
                // проверка каждого отдельного числа
                for (String s : dimensArray)
                    try {
                        Double.parseDouble(s);
                    }catch (NumberFormatException e){
                        inputLayout.setError("Ширина, Высота, Глубина");
                        return false;
                    }
            }
        }

        // всё ок
        inputLayout.setErrorEnabled(false);
        return true;
    }


}