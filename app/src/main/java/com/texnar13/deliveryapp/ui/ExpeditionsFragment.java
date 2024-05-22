package com.texnar13.deliveryapp.ui;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.texnar13.deliveryapp.MainViewModel;
import com.texnar13.deliveryapp.R;
import com.texnar13.deliveryapp.model.DBExpedition;

import java.util.Locale;
import java.util.Objects;

public class ExpeditionsFragment extends Fragment {


    // Required empty public constructor
    public ExpeditionsFragment() {
    }

    // factory method
    public static ExpeditionsFragment newInstance(String param1) {
        ExpeditionsFragment fragment = new ExpeditionsFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

// ------------------------------------------- разметка -------------------------------------------
        View rootView = inflater.inflate(R.layout.fragment_expeditions, container, false);

        // кнопка добавить отправление
        View addButton = rootView.findViewById(R.id.fragment_expeditions_add_button);
        addButton.setOnClickListener(v -> {
            // вызов диалога
            ExpeditionEditDialogFragment.newInstance(null)
                    .show(getParentFragmentManager(), "ExpeditionEditDialogFragment");
        });

        // контейнер отправлений
        LinearLayout boxesContainer = rootView.findViewById(R.id.fragment_expeditions_container);


// -------------------------------- подписываемся на изменения во viewModel --------------------------------

        mainViewModel.currentUserExpeditions.observe(this, dbExpeditions -> {

            // вывод списка
            boxesContainer.removeAllViews();

            // проходимся по всем отправлениям
            for (DBExpedition expeditionUnit : dbExpeditions) {
                // получеам конкретное уведомление
                // создание разметки
                View notificationViewElement = getLayoutInflater().inflate(
                        R.layout.element_expedition_box, null);

                TextView title = notificationViewElement.findViewById(R.id.element_expedition_box_title);
                TextView description = notificationViewElement.findViewById(R.id.element_expedition_box_description);
                TextView state = notificationViewElement.findViewById(R.id.element_expedition_box_state);
                //ImageView img = notificationViewElement.findViewById(R.id.element_expedition_box_img);
                TextView buttonEdit = notificationViewElement.findViewById(R.id.element_expedition_box_button_edit);
                TextView buttonFindSomebody = notificationViewElement.findViewById(R.id.element_expedition_box_button_find_somebody);
                TextView buttonConnect = notificationViewElement.findViewById(R.id.element_expedition_box_button_connect);


                title.setText(expeditionUnit.getPackage().getName());
                description.setText(String.format(
                        Locale.getDefault(), "Категория %s \nиз %s, %s -> в %s, %s\n" +
                                "Описание %s \nВес %.1fКГ\nГабариты %.1fx%.1fx%.1fсм",
                        expeditionUnit.getPackage().getCategory(),
                        expeditionUnit.getAddressSender().getArray()[0],
                        expeditionUnit.getAddressSender().getArray()[1],
                        expeditionUnit.getAddressReceiver().getArray()[0],
                        expeditionUnit.getAddressReceiver().getArray()[1],
                        expeditionUnit.getPackage().getDescription(),
                        expeditionUnit.getPackage().getWeight(),
                        expeditionUnit.getPackage().getDimensions()[0],
                        expeditionUnit.getPackage().getDimensions()[1],
                        expeditionUnit.getPackage().getDimensions()[2]
                ));

                // статус
                switch (expeditionUnit.getStatus()) {
                    case DBExpedition.EXPEDITION_STATUS_VALUE_WAIT_SEND:
                        // выставляем статус
                        state.setText("Ожидает отправки");
                        state.setTextColor(getResources().getColor(R.color.wait_mail_text_color));

                        // кнопки
                        buttonEdit.setOnClickListener(v -> {
                            // вызов диалога
                            ExpeditionEditDialogFragment.newInstance(expeditionUnit)
                                    .show(getParentFragmentManager(), "ExpeditionEditDialogFragment");
                        });
                        buttonFindSomebody.setOnClickListener(v -> {
                            state.setText("buttonFindSomebody");
                        });
                        buttonConnect.setVisibility(View.INVISIBLE);
                        break;
                    case DBExpedition.EXPEDITION_STATUS_VALUE_SENT:
                        // выставляем статус
                        state.setText("ОТПРАВЛЕНО");
                        state.setTextColor(getResources().getColor(R.color.sent_mail_text_color));

                        // кнопки
                        buttonEdit.setOnClickListener(v -> {
                            // вызов диалога
                            ExpeditionEditDialogFragment.newInstance(expeditionUnit)
                                    .show(getParentFragmentManager(), "ExpeditionEditDialogFragment");
                        });
                        buttonFindSomebody.setVisibility(View.INVISIBLE);
                        buttonConnect.setOnClickListener(v -> {
                            state.setText("buttonConnect");
                        });
                        break;
                    case DBExpedition.EXPEDITION_STATUS_VALUE_DONE:
                        // выставляем статус
                        state.setText("Завершено");
                        state.setTextColor(getResources().getColor(R.color.ended_mail_text_color));

                        // кнопки
                        buttonEdit.setVisibility(View.INVISIBLE);
                        buttonFindSomebody.setVisibility(View.INVISIBLE);
                        buttonConnect.setVisibility(View.INVISIBLE);
                        break;
                }


                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                layoutParams.topMargin = getResources().getDimensionPixelOffset(R.dimen.containers_margin);

                boxesContainer.addView(
                        notificationViewElement,layoutParams
                );
            }
        });
        return rootView;
    }
}

