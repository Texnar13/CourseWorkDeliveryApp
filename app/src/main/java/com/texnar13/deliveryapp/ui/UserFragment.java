package com.texnar13.deliveryapp.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.texnar13.deliveryapp.R;

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


        // кнопка выхода из аккаунта
        rootView.findViewById(R.id.fragment_user_logout_button).setOnClickListener(view -> {
            // вызов метода в Activity
            ((MainActivityInterface) Objects.requireNonNull(getActivity())).logout();
        });



        // кнопка редактирования информации о пользователе
        rootView.findViewById(R.id.fragment_user_edit_user_button).setOnClickListener(view -> {
            // вызов метода в Activity
            ((MainActivityInterface) Objects.requireNonNull(getActivity())).goToEditUser();
        });

        //rootView.findViewById(R.id.fragment_user_user_img);

        TextView userDescription = rootView.findViewById(R.id.fragment_user_user_description);

        LinearLayout userNotificationsContainer = rootView.findViewById(R.id.fragment_user_notifications_container);



        return rootView;
    }
}