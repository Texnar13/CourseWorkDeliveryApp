package com.texnar13.deliveryapp.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.texnar13.deliveryapp.MainViewModel;
import com.texnar13.deliveryapp.R;
import com.texnar13.deliveryapp.model.DBNotification;

import org.bson.types.ObjectId;

import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;

public class UserFragment extends Fragment {

    // the fragment initialization parameters
    private static final String ARG_PARAM1 = "param1";

    private String mParam1;

    // Required empty public constructor
    public UserFragment() {
    }


    // Фабрика
    public static LoginFragment newInstance(String param1) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user, container, false);

        MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

// ------------------------------------------- разметка -------------------------------------------

        // кнопка выхода из аккаунта
        rootView.findViewById(R.id.fragment_user_logout_button).setOnClickListener(view -> {
            // вызов метода в Activity
            ((MainActivityInterface) Objects.requireNonNull(getActivity())).logout();
        });

        // кнопка редактирования информации о пользователе
        rootView.findViewById(R.id.fragment_user_edit_user_button).setOnClickListener(view -> {
            // вызов диалога
            UserEditDialogFragment.newInstance(mainViewModel.currentUser.getValue())
                    .show(getParentFragmentManager(), "edit_user_dialog");
        });

        //rootView.findViewById(R.id.fragment_user_user_img);

        // поле описания полльзователя
        TextView userDescription = rootView.findViewById(R.id.fragment_user_user_description);

        // контейнер уведомлений пользователя
        LinearLayout userNotificationsContainer = rootView.findViewById(R.id.fragment_user_notifications_container);


// -------------------------------- подписываемся на изменения во viewModel --------------------------------

        // глобальное изменение переменной USER (создание или очистка)
        mainViewModel.currentUser.observe(this, dbUser -> {

            // вывод строки состояния пользователя
            if (dbUser != null) {
                StringBuilder address = new StringBuilder();
                for (int i = 0; i < dbUser.getAddress().getArray().length; i++) {
                    address.append(dbUser.getAddress().getArray()[i]);
                    if (i != dbUser.getAddress().getArray().length - 1) address.append(", ");
                }

                userDescription.setText(String.format(Locale.getDefault(),
                        "Рейтинг %.1f/5\nЭл. Почта %s\nЯ из %s\nИмя %s\nТелефон %s",
                        dbUser.getRating(),
                        dbUser.getEmail(),
                        address,
                        dbUser.getName(),
                        dbUser.getPhoneNumber()

                ));
            }

        });

        // глобальное изменение переменной с уведомлениями пользователя
        mainViewModel.currentUserNotifications.observe(this, notifications -> {

            // вывод списка
            userNotificationsContainer.removeAllViews();


            Iterator<DBNotification> notificationIterator = notifications.iterator();
            while (notificationIterator.hasNext()) {
                // получеам конкретное уведомление
                DBNotification notificationUnit = notificationIterator.next();

                // создание разметки
                View notificationViewElement = getLayoutInflater().inflate(R.layout.element_user_notification, userNotificationsContainer);
                TextView textView = notificationViewElement.findViewById(R.id.element_user_notification_text);
                textView.setText(notificationUnit.getNotificationMessage());

                // кнопки
                View buttonApprove = notificationViewElement.findViewById(R.id.element_user_notification_button_approve);
                View buttonDisagree = notificationViewElement.findViewById(R.id.element_user_notification_button_disagree);
                View buttonRead = notificationViewElement.findViewById(R.id.element_user_notification_button_read);

                switch (notificationUnit.getNotificationType()) {
                    // уведомления администратора
                    case DBNotification.NOTIFICATION_TYPE_VALUE_ADMIN:

                        // кнопки
                        buttonApprove.setVisibility(View.INVISIBLE);
                        buttonDisagree.setVisibility(View.INVISIBLE);
                        buttonRead.setOnClickListener(v -> mainViewModel.markReadAdminNotification(
                                notificationUnit.get_id()
                        ));

                        break;
                    case DBNotification.NOTIFICATION_TYPE_VALUE_SEND_REQUEST:
                        break;
                    case DBNotification.NOTIFICATION_TYPE_VALUE_EXPEDITION:
                        break;
                }

            }
        });


        return rootView;
    }
}