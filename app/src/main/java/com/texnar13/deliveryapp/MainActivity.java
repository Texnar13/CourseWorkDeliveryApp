package com.texnar13.deliveryapp;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.texnar13.deliveryapp.model.DBUser;
import com.texnar13.deliveryapp.ui.EditUserDialogFragment;
import com.texnar13.deliveryapp.ui.LoginFragmentInterface;
import com.texnar13.deliveryapp.ui.MainActivityInterface;

import java.util.Objects;


public class MainActivity extends AppCompatActivity implements MainActivityInterface {

    // ссылки на контроллер с фрагментами
    NavController navController;
    FragmentManager navFragmentManager;

    // все варианты фрагментов
    private enum FState {
        LOGIN_FRAGMENT,
        REGISTER_FRAGMENT,
        USER_FRAGMENT,
        TRAJECTORIES_FRAGMENT,
        EXPEDITIONS_FRAGMENT
    }

    // пременная для отслеживания текущего фрагмента
    FState currentState = FState.LOGIN_FRAGMENT;


    // блоировка нажатий на экран и прогресс бар, когда идет загрузка данных
    View loadScreenBlocking;
    boolean isNowOutedLoadScreen;

    // нижнее меню навигации
    BottomNavigationView bottomNavigationView;


    // viewModel в которой содержится вся бизнеслогика
    MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // мне лень ваять интерфейс :)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        // ------------------------ Подписка на ViewModel --------------------------------


        viewModel = (new ViewModelProvider(this,
                new MainViewModelFactory(this))).get(MainViewModel.class);
        //getDefaultViewModelProviderFactory().create(MainViewModel.class);


        // отслеживаем состояние подключения
        viewModel.activityConnectionStatus.observe(this, connectionStatusValue -> {
            switch (connectionStatusValue) {
                case STATUS_NONE:
                    enableLoadBar();
                    //Toast.makeText(this, "Никогде",Toast.LENGTH_SHORT).show();
                    break;
                case STATUS_ERROR:
                    Toast.makeText(this, "Ошибка подключения, включите VPN. Повторная попытка...", Toast.LENGTH_SHORT).show();
                    break;
                case STATUS_CONNECTED:

                    // уведомляем фрагмент что загрузка завершена
                    if (currentState == FState.LOGIN_FRAGMENT) {
                        // текущий фрагмент (всегда первый в списке)
                        LoginFragmentInterface loginFragmentInterface =
                                (LoginFragmentInterface) navFragmentManager.getFragments().get(0);
                        //.findFragmentById(R.id.fragment_main);
                        loginFragmentInterface.loadOver();
                    }
                    disableLoadBar();

                    Toast.makeText(this, "Соединение с сервером установлено", Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        // отслеживаем авторизацию и состояние текущего пользователя
        viewModel.currentUser.observe(this, user -> {

            // это должно срабатывать, когда мы меняем пользователя целиком,
            //  то есть его ссылку, а не данные в нем

            // если пользователь получен из базы
            if (user != null) {
                // переход на страницу пользователя
                if (currentState == FState.LOGIN_FRAGMENT){
                    navController.navigate(R.id.action_loginFragment_to_userFragment);
                }else if(currentState == FState.REGISTER_FRAGMENT){
                    // переход на главную страницу
                    navController.navigate(R.id.action_registerFragment_to_mainFragment);
                }
            } else {
                // переход на страницу регистрации через backstack
                myBackCallback.setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
                myBackCallback.setEnabled(true);
            }
        });


        // тосты
        viewModel.toastMessage.observe(this, s ->
                Toast.makeText(this, s, Toast.LENGTH_SHORT).show());


//        // открытие активности а не перерисовка
//        if (savedInstanceState == null) {
//
//            // todo это соответственно переносится во ViewModel,
//            //   а enableLoadBar() работает через подписку на ViewModel
//            //   нажатие кнопок и обратня связь от фрагментов делегируется во viewModel
//            //   LiveData и MutableLiveData :)
//            //   Можно сделать так, чтобы LiveData следила за MutableLiveData и избежать getter-ов и setter-ов
//            //   контекст view model не хранит, но он передаётся в методах
//
//            //
//
//        }


        // ------------------------ Фрагменты и View --------------------------------

        // контроллер в котором пропсана навигация между фрагментами
        navController = Navigation.findNavController(this, R.id.activity_main_nav_host_fragment);
        // нижнее меню навигации
        bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation);
        // отслеживаем какой сейчас фрагмент
        navController.addOnDestinationChangedListener((navController, navDestination, bundle) -> {
            if (navDestination.getId() == R.id.loginFragment) {
                currentState = FState.LOGIN_FRAGMENT;
                hideBottomNavigation();
            } else if (navDestination.getId() == R.id.registerFragment) {
                currentState = FState.REGISTER_FRAGMENT;
                hideBottomNavigation();
            } else if (navDestination.getId() == R.id.fragment_user) {
                currentState = FState.USER_FRAGMENT;
                showBottomNavigation();
            } else if (navDestination.getId() == R.id.fragment_trajectories) {
                currentState = FState.TRAJECTORIES_FRAGMENT;
                showBottomNavigation();
            } else if (navDestination.getId() == R.id.fragment_expeditions) {
                currentState = FState.EXPEDITIONS_FRAGMENT;
                showBottomNavigation();
            }
            Log.d("Hello", "currentState = " + currentState.name());
        });
        // связываем меню навигации и контроллер (он будет работать по id пунктов меню)
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // Менеджер фрагментов, нужен для получения ссылки на текщий фрагмент (чтобы отправлять ему данные)
        navFragmentManager = Objects.requireNonNull(getSupportFragmentManager()
                        .findFragmentById(R.id.activity_main_nav_host_fragment))
                .getChildFragmentManager();

        // блоировка нажатий на экран и прогресс бар, когда идет загрузка данных
        loadScreenBlocking = findViewById(R.id.activity_main_load_block);
        loadScreenBlocking.setOnTouchListener((view, motionEvent) -> true);
        loadScreenBlocking.setVisibility((isNowOutedLoadScreen) ? (View.VISIBLE) : (View.INVISIBLE));

        // добавление слушателя кнопке назад (В качестве владельца слушателя текущая активити)
        getOnBackPressedDispatcher().addCallback(this, myBackCallback);

    }

    // Обработка нажатия кнопки назад
    OnBackPressedCallback myBackCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            // В зависимости от того на каком мы сейчас фрагменте
            switch (currentState) {
                case LOGIN_FRAGMENT:
                case REGISTER_FRAGMENT:
                    // В остальных случаях работает обычный Callback
                    // а этот Callback мы отключаем
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                    setEnabled(true);
                    break;

                case USER_FRAGMENT:
                case TRAJECTORIES_FRAGMENT:
                case EXPEDITIONS_FRAGMENT:
                    // Описание точек выхода из приложения
                    // выход с главного интерфейса это просто выход из приложения
                    finish();
            }
        }
    };


// ----------------------------------------- View элементы -----------------------------------------


    void enableLoadBar() {
        isNowOutedLoadScreen = true;

        // отключение элементов интерфейса и включение progress bar загрузки
        if (loadScreenBlocking != null) {
            loadScreenBlocking.setVisibility(View.VISIBLE);
        }
        // отключение меню навигации
        if (bottomNavigationView != null)
            for (int i = 0; i < 3; i++)
                bottomNavigationView.getMenu().getItem(i).setEnabled(false);
    }

    void disableLoadBar() {
        isNowOutedLoadScreen = false;

        // включение элементов интерфейса и скрытие progress bar загрузки
        if (loadScreenBlocking != null) {
            loadScreenBlocking.setVisibility(View.INVISIBLE);
        }
        // включение меню навигации
        if (bottomNavigationView != null)
            for (int i = 0; i < 3; i++)
                bottomNavigationView.getMenu().getItem(i).setEnabled(true);
    }

    void showBottomNavigation() {
        // показываем нижнюю панель навигации
        bottomNavigationView.setVisibility(View.VISIBLE);

        // перепривязываем низ базового фрагмента к низу bottomNavigationView
        ConstraintLayout constraintLayout = findViewById(R.id.main_container);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(R.id.activity_main_nav_host_fragment, ConstraintSet.BOTTOM,
                bottomNavigationView.getId(), ConstraintSet.TOP, 0);
        constraintSet.applyTo(constraintLayout);
    }

    void hideBottomNavigation() {
        // скрываем нижнюю панель навигации
        bottomNavigationView.setVisibility(View.INVISIBLE);
        // перепривязываем низ базового фрагмента к низу экрана
        ConstraintLayout constraintLayout = findViewById(R.id.main_container);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(R.id.activity_main_nav_host_fragment, ConstraintSet.BOTTOM,
                ConstraintLayout.LayoutParams.PARENT_ID, ConstraintSet.BOTTOM, 0);
        constraintSet.applyTo(constraintLayout);

    }


// =================================================================================================
// ====================================== Вызовы от фрагментов =====================================
// =================================================================================================


// ----------------------------------------- login fragment ----------------------------------------

    // связь из фрагмента авторизации
    @Override
    public boolean authoriseUser(String email, String password) {
        // отдаем данные аутентификации viewModel
        viewModel.authUser(email, password);
        return true;
    }

    @Override
    public void gotoRegister() {
        // переход на страницу регистрации
        navController.navigate(R.id.action_loginFragment_to_registerFragment);
    }

// --------------------------------------- Register fragment ---------------------------------------

    @Override
    public void registerUser(String password, String[] address, String email, String name, String phone) {

        // отправляем во вьюмодель
        viewModel.tryRegisterUser(password, address, email, name, phone);
    }


// --------------------------------------- User fragment ---------------------------------------

    @Override
    public void logout() {

        // удаление обьекта пользователя во вьюмодели
        viewModel.logout();

//        // вызов выхода из активности с экраном загрузки
//        enableLoadBar();
//        (new Handler(Looper.getMainLooper())).postDelayed(() -> {
//
//            // выключение экрана загрузки
//            disableLoadBar();
//
//            // переход на страницу регистрации
//            // тут единственное место где при нажатии кнопки назад пользователь не выходит из приложения
//            myBackCallback.setEnabled(false);
//            getOnBackPressedDispatcher().onBackPressed();
//            myBackCallback.setEnabled(true);
//
//        }, 1000);

    }


    @Override
    public void goToEditUser() {
        EditUserDialogFragment.newInstance(viewModel.currentUser.getValue())
                .show(getSupportFragmentManager(), "edit_user_dialog");
    }


// --------------------------------------- User edit dialog ---------------------------------------

    @Override
    public void editUser(DBUser editedUser) {
        viewModel.editUser(editedUser);
    }

}

