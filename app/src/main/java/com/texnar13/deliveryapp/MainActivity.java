package com.texnar13.deliveryapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.texnar13.deliveryapp.ui.MainActivityInterface;
import com.texnar13.deliveryapp.ui.LoginFragmentInterface;

import java.util.Objects;


public class MainActivity extends AppCompatActivity implements MainActivityInterface {

    // ссылки на контроллер с фрагментами
    NavController navController;
    FragmentManager navFragmentManager;

    // пременная для отслеживания текущего фрагмента
    private enum FState {
        LOGIN_FRAGMENT,
        USER_FRAGMENT
    }
    FState currentState = FState.LOGIN_FRAGMENT;


    // прогресс бар загрузки данных
    View loadScreenBlocking;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navController = Navigation.findNavController(this, R.id.activity_main_nav_host_fragment);
        navFragmentManager = Objects.requireNonNull(getSupportFragmentManager()
                        .findFragmentById(R.id.activity_main_nav_host_fragment))
                .getChildFragmentManager();

        navController.addOnDestinationChangedListener((navController, navDestination, bundle) -> {

            if (navDestination.getId() == R.id.loginFragment) {
                currentState = FState.LOGIN_FRAGMENT;
                Log.e("Hello", "LOGIN_FRAGMENT");
            } else if (navDestination.getId() == R.id.mainFragment) {
                currentState = FState.USER_FRAGMENT;
                Log.e("Hello", "USER_FRAGMENT");
            }

        });





        // блоировка нажатий на экран, когда идет загрузка и прогресс бар загрузки данных
        loadScreenBlocking = findViewById(R.id.activity_main_load_block);
        loadScreenBlocking.setOnTouchListener((view, motionEvent) -> true);


        // просто отложенный вызов
        enableLoadBar();
        (new Handler(Looper.getMainLooper())).postDelayed(() -> {
                    if (currentState == FState.LOGIN_FRAGMENT) {


                        // текущий фрагмент (всегда первый в списке)
                        LoginFragmentInterface loginFragmentInterface =
                                (LoginFragmentInterface) navFragmentManager.getFragments().get(0);
                        //.findFragmentById(R.id.fragment_main);
                        loginFragmentInterface.loadOver();

                        disableLoadBar();
                    }
                },
                3000);


        //BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        //AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
        //        R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
        //        .build();


        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        //NavigationUI.setupWithNavController(binding.navView, navController);


    }

    void enableLoadBar(){
        if(loadScreenBlocking != null){
            loadScreenBlocking.setVisibility(View.VISIBLE);
        }
    }

    void disableLoadBar(){
        if(loadScreenBlocking != null){
            loadScreenBlocking.setVisibility(View.INVISIBLE);
        }
    }

// =================================================================================================
// ====================================== Вызовы от фрагментов =====================================
// =================================================================================================


// ----------------------------------------- login fragment ----------------------------------------

    // связь из фрагмента авторизации
    @Override
    public boolean authoriseUser(String email, String password) {
        // переход на главную страницу
        navController.navigate(R.id.action_loginFragment_to_mainFragment);
        return true;
    }

    @Override
    public void gotoRegister() {
        // переход на страницу регистрации
        navController.navigate(R.id.action_loginFragment_to_registerFragment);
    }

// --------------------------------------- Register fragment ---------------------------------------

    @Override
    public void registerUser(String email, String name, String password){

        // переход на главную страницу
        navController.navigate(R.id.action_registerFragment_to_mainFragment);

        // todo проверка уже существующих пользователей
    }
}

