package com.texnar13.deliveryapp.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.texnar13.deliveryapp.MainViewModel;
import com.texnar13.deliveryapp.MainViewModelFactory;
import com.texnar13.deliveryapp.R;

import java.util.Objects;

public class LoginFragment extends Fragment implements LoginFragmentInterface {

    // обновляемая разметка
    //ProgressBar connectingProgressBar;
    View loginContainer;
    View registerButton;


    static boolean loaded = false;

    // Required empty public constructor
    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        // скрываемые и показываемые контейнеры
        loginContainer = rootView.findViewById(R.id.fragment_login_input_container);
        registerButton = rootView.findViewById(R.id.fragment_login_go_to_register_button);
        //connectingProgressBar = rootView.findViewById(R.id.fragment_login_load_spinner);
        if (loaded) {
            //connectingProgressBar.setVisibility(View.INVISIBLE);
            loginContainer.setVisibility(View.VISIBLE);
            registerButton.setVisibility(View.VISIBLE);
        } else {
            //connectingProgressBar.setVisibility(View.VISIBLE);
            loginContainer.setVisibility(View.INVISIBLE);
            registerButton.setVisibility(View.INVISIBLE);
        }


        // кнопка авторизации
        rootView.findViewById(R.id.fragment_login_login_button).setOnClickListener(view -> {
            if (loaded) {
                EditText email = rootView.findViewById(R.id.fragment_login_login_edit_email);
                EditText password = rootView.findViewById(R.id.fragment_login_login_edit_password);



                // отдаем данные аутентификации viewModel
                MainViewModel viewModel = (new ViewModelProvider(this,
                        new MainViewModelFactory(requireActivity()))).get(MainViewModel.class);
                viewModel.authUser(
                        email.getText().toString().trim(),
                        password.getText().toString().trim()
                );
            }
        });

        // переход на фрагмент регистрации
        registerButton.setOnClickListener(view -> {
            if (loaded) {

                // переход на страницу регистрации
                Navigation.findNavController(requireActivity(), R.id.activity_main_nav_host_fragment).
                        navigate(R.id.action_loginFragment_to_registerFragment);

            }
        });

        return rootView;
    }


    // Подключение к бд завершено
    @Override
    public void loadOver() {
        Log.e("Hello", "loaded");
        loaded = true;
        //connectingProgressBar.setVisibility(View.INVISIBLE);
        loginContainer.setVisibility(View.VISIBLE);
        registerButton.setVisibility(View.VISIBLE);

//        Toast.makeText(getActivity(), "loaded", Toast.LENGTH_SHORT).show();
    }


}

