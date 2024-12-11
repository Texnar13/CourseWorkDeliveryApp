package com.texnar13.deliveryapp.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.texnar13.deliveryapp.R;
import com.texnar13.deliveryapp.view_model.MainViewModel;

public class TrajectoriesFragment extends Fragment {


    // Required empty public constructor
    public TrajectoriesFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_trajectories, container, false);

        MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

// ------------------------------------------- разметка -------------------------------------------

        // контейнер trip
        LinearLayout boxesContainer = rootView.findViewById(R.id.fragment_trajectories_search_result_container);

// -------------------------------- подписываемся на изменения во viewModel --------------------------------

//        // глобальное изменение переменной
//        mainViewModel.currentLoadedTrips.observe(this, dbUsers -> {
//
//            // вывод списка
//            boxesContainer.removeAllViews();

//            // проходимся по всем отправлениям
//            for (DBTrip tripUnit : dbUsers) {
//
//                // создание разметки
//                View notificationViewElement = getLayoutInflater().inflate(R.layout.element_trip_box, null);
//
//
//                TextView username = notificationViewElement.findViewById(R.id.element_trip_box_username);
//                TextView description = notificationViewElement.findViewById(R.id.element_trip_box_description);
//                TextView joinButton = notificationViewElement.findViewById(R.id.element_trip_box_join_button);
//
//
//
//                //username.setText(tripUnit.get().getName());
//
//
//
//
//                description.setText(String.format(
//                        Locale.getDefault(), "Категория %s \nиз %s, %s -> в %s, %s\n" +
//                                "Описание %s \nВес %.1fКГ\nГабариты %.1fx%.1fx%.1fсм",
//                        expeditionUnit.getPackage().getCategory(),
//                        expeditionUnit.getAddressSender().getArray()[0],
//                        expeditionUnit.getAddressSender().getArray()[1],
//                        expeditionUnit.getAddressReceiver().getArray()[0],
//                        expeditionUnit.getAddressReceiver().getArray()[1],
//                        expeditionUnit.getPackage().getDescription(),
//                        expeditionUnit.getPackage().getWeight(),
//                        expeditionUnit.getPackage().getDimensions()[0],
//                        expeditionUnit.getPackage().getDimensions()[1],
//                        expeditionUnit.getPackage().getDimensions()[2]
//                ));
//
//                // статус
//                switch (expeditionUnit.getStatus()) {
//                    case DBExpedition.EXPEDITION_STATUS_VALUE_WAIT_SEND:
//                        // выставляем статус
//                        state.setText("Ожидает отправки");
//                        state.setTextColor(getResources().getColor(R.color.wait_mail_text_color));
//
//                        // кнопки
//                        buttonEdit.setOnClickListener(v -> {
//                            // вызов диалога
//                            ExpeditionEditDialogFragment.newInstance(expeditionUnit)
//                                    .show(getParentFragmentManager(), "ExpeditionEditDialogFragment");
//                        });
//                        buttonFindSomebody.setOnClickListener(v -> {
//                            state.setText("buttonFindSomebody");
//                        });
//                        buttonConnect.setVisibility(View.INVISIBLE);
//                        break;
//                    case DBExpedition.EXPEDITION_STATUS_VALUE_SENT:
//                        // выставляем статус
//                        state.setText("ОТПРАВЛЕНО");
//                        state.setTextColor(getResources().getColor(R.color.sent_mail_text_color));
//
//                        // кнопки
//                        buttonEdit.setOnClickListener(v -> {
//                            // вызов диалога
//                            ExpeditionEditDialogFragment.newInstance(expeditionUnit)
//                                    .show(getParentFragmentManager(), "ExpeditionEditDialogFragment");
//                        });
//                        buttonFindSomebody.setVisibility(View.INVISIBLE);
//                        buttonConnect.setOnClickListener(v -> {
//                            state.setText("buttonConnect");
//                        });
//                        break;
//                    case DBExpedition.EXPEDITION_STATUS_VALUE_DONE:
//                        // выставляем статус
//                        state.setText("Завершено");
//                        state.setTextColor(getResources().getColor(R.color.ended_mail_text_color));
//
//                        // кнопки
//                        buttonEdit.setVisibility(View.INVISIBLE);
//                        buttonFindSomebody.setVisibility(View.INVISIBLE);
//                        buttonConnect.setVisibility(View.INVISIBLE);
//                        break;
//                }
//
//
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.MATCH_PARENT,
//                        LinearLayout.LayoutParams.WRAP_CONTENT
//                );
//                layoutParams.topMargin = getResources().getDimensionPixelOffset(R.dimen.containers_margin);
//
//                boxesContainer.addView(
//                        notificationViewElement,layoutParams
//                );
//            }
//        });



        return rootView;
    }
}