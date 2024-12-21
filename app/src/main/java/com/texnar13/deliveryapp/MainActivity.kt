package com.texnar13.deliveryapp

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.texnar13.deliveryapp.model.DBUser
import com.texnar13.deliveryapp.model.http.HttpApi
import com.texnar13.deliveryapp.ui.login.LoginFragmentInterface
import com.texnar13.deliveryapp.view_model.MainViewModel
import com.texnar13.deliveryapp.view_model.MainViewModelFactory
import java.util.Objects

class MainActivity : AppCompatActivity(), LoaderAndBottomPanel {
    // ссылки на контроллер с фрагментами
    private var navController: NavController? = null
    private var navFragmentManager: FragmentManager? = null

    // все варианты фрагментов
    enum class FState {
        LOGIN_FRAGMENT,
        REGISTER_FRAGMENT,
        USER_FRAGMENT,
        TRAJECTORIES_FRAGMENT,
        EXPEDITIONS_FRAGMENT
    }

    // пременная для отслеживания текущего фрагмента
    var currentState: FState = FState.LOGIN_FRAGMENT


    // TODO УБРАТЬ ВО FRAGMENT
    // блоировка нажатий на экран и прогресс бар, когда идет загрузка данных
    private var loadScreenBlocking: View? = null
    private var isNowOutedLoadScreen: Boolean = false

    // нижнее меню навигации
    private var bottomNavigationView: BottomNavigationView? = null

    @SuppressLint("SourceLockedOrientationActivity", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // мне лень ваять интерфейс :)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT


        // Получаем ViewModel
        val viewModel = MainViewModel.getViewModel(this)


        // Привязываем отображение загрузки к сотоянию отправщика
        viewModel.httpLoadingStatus.observe(this){ state ->
            when(state){
                HttpApi.Companion.HttpClientState.NO_WORK -> {
                    disableLoadBar()
                }
                HttpApi.Companion.HttpClientState.IN_PROCESS -> {
                    enableLoadBar()
                }
            }
        }




        // отслеживаем состояние подключения
//        viewModel.activityConnectionStatus.observe(viewLifecycleOwner) { status ->
//            when (status) {
//                MainViewModel.ConnectionStatusValue.STATUS_NONE -> {
//
//                    // Включаем отображение загрузки
//                    (requireActivity() as LoaderAndBottomPanel).enableLoadBar()
//                    internetConnected = false
//                }
//
//                MainViewModel.ConnectionStatusValue.STATUS_ERROR -> {
//                    Toast.makeText(
//                            context,
//                            "Нет соединения с сервером...",
//                            Toast.LENGTH_SHORT
//                    ).show()
//
//                    internetConnected = false
//                }
//
//                MainViewModel.ConnectionStatusValue.STATUS_CONNECTED -> {
//        Log.e("Hello", "loaded")
//        internetConnected = true
//        loginContainer.visibility = View.VISIBLE
//        registerButton.visibility = View.VISIBLE
//
//        (requireActivity() as LoaderAndBottomPanel).disableLoadBar()
//
//        Toast.makeText(context, "Соединение с сервером установлено", Toast.LENGTH_SHORT).show()
//                }
//
//                null -> {}
//            }
//        }





        // TODO УБРАТЬ ВО FRAGMENT ПОСТЕПЕННО
        // отслеживаем авторизацию и состояние текущего пользователя
        viewModel.currentUser.observe(this) { user ->
            // если пользователь получен из базы
            if (user == null) {
//                // переход на страницу входа через backstack// TODO
//                myBackCallback.isEnabled = false
//                onBackPressedDispatcher.onBackPressed()
//                myBackCallback.isEnabled = true
            }
        }


        // тосты
        viewModel.toastMessage.observe(this) { s: String? -> Toast.makeText(this, s, Toast.LENGTH_SHORT).show() }

        // ------------------------ Фрагменты и View --------------------------------

        // контроллер в котором пропсана навигация между фрагментами
        navController =
                (supportFragmentManager.findFragmentById(R.id.activity_main_nav_host_fragment) as NavHostFragment).navController


        //findNavController(this, R.id.activity_main_nav_host_fragment)
        // нижнее меню навигации
        bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation)
        // отслеживаем какой сейчас фрагмент
        navController!!.addOnDestinationChangedListener { _, navDestination, _ ->
            when (navDestination.id) {
                R.id.loginFragment -> {
                    currentState = FState.LOGIN_FRAGMENT
                    // TODO ВЫЗОВ ИЗ FRAGMENT
                    hideBottomNavigation()
                }

                R.id.registerFragment -> {
                    currentState = FState.REGISTER_FRAGMENT
                    // TODO ВЫЗОВ ИЗ FRAGMENT
                    hideBottomNavigation()
                }

                R.id.fragment_user -> {
                    currentState = FState.USER_FRAGMENT
                    // TODO ВЫЗОВ ИЗ FRAGMENT
                    showBottomNavigation()
                }

                R.id.fragment_trajectories -> {
                    currentState = FState.TRAJECTORIES_FRAGMENT
                    // TODO ВЫЗОВ ИЗ FRAGMENT
                    showBottomNavigation()
                }

                R.id.fragment_expeditions -> {
                    currentState = FState.EXPEDITIONS_FRAGMENT
                    // TODO ВЫЗОВ ИЗ FRAGMENT
                    showBottomNavigation()
                }
            }
            Log.d("Hello", "currentState = " + currentState.name)
        }
        // связываем меню навигации и контроллер (он будет работать по id пунктов меню)
        setupWithNavController(bottomNavigationView!!, navController!!)

        // Менеджер фрагментов, нужен для получения ссылки на текщий фрагмент (чтобы отправлять ему данные)
        navFragmentManager = Objects.requireNonNull(supportFragmentManager
                .findFragmentById(R.id.activity_main_nav_host_fragment))!!.childFragmentManager

        // блоировка нажатий на экран и прогресс бар, когда идет загрузка данных
        loadScreenBlocking = findViewById(R.id.activity_main_load_block)
        loadScreenBlocking!!.setOnTouchListener { _: View?, _: MotionEvent? -> true }
        loadScreenBlocking!!.visibility = if ((isNowOutedLoadScreen)) (View.VISIBLE) else (View.INVISIBLE)

        // добавление слушателя кнопке назад (В качестве владельца слушателя текущая активити)
        onBackPressedDispatcher.addCallback(this, myBackCallback)
    }


    // TODO УБРАТЬ В NAVIGATION?
    // Обработка нажатия кнопки назад
    private var myBackCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // В зависимости от того на каком мы сейчас фрагменте
            when (currentState) {
                FState.LOGIN_FRAGMENT, FState.REGISTER_FRAGMENT -> {
                    // В остальных случаях работает обычный Callback
                    // а этот Callback мы отключаем
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }

                FState.USER_FRAGMENT, FState.TRAJECTORIES_FRAGMENT, FState.EXPEDITIONS_FRAGMENT -> // Описание точек выхода из приложения
                    // выход с главного интерфейса это просто выход из приложения
                    finish()
            }
        }
    }


    // ----------------------------------------- View элементы -----------------------------------------

    override fun enableLoadBar() {
        isNowOutedLoadScreen = true

        // отключение элементов интерфейса и включение progress bar загрузки
        if (loadScreenBlocking != null) {
            loadScreenBlocking!!.visibility = View.VISIBLE
        }
        // отключение меню навигации
        if (bottomNavigationView != null) for (i in 0..2) bottomNavigationView!!.menu.getItem(i).setEnabled(false)
    }

    override fun disableLoadBar() {
        isNowOutedLoadScreen = false

        // включение элементов интерфейса и скрытие progress bar загрузки
        if (loadScreenBlocking != null) {
            loadScreenBlocking!!.visibility = View.INVISIBLE
        }
        // включение меню навигации
        if (bottomNavigationView != null) for (i in 0..2) bottomNavigationView!!.menu.getItem(i).setEnabled(true)
    }

    override fun showBottomNavigation() {
        // показываем нижнюю панель навигации
        bottomNavigationView!!.visibility = View.VISIBLE

        // перепривязываем низ базового фрагмента к низу bottomNavigationView
        val constraintLayout = findViewById<ConstraintLayout>(R.id.main_container)
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        constraintSet.connect(R.id.activity_main_nav_host_fragment, ConstraintSet.BOTTOM,
                bottomNavigationView!!.id, ConstraintSet.TOP, 0)
        constraintSet.applyTo(constraintLayout)
    }

    override fun hideBottomNavigation() {
        // скрываем нижнюю панель навигации
        bottomNavigationView!!.visibility = View.INVISIBLE
        // перепривязываем низ базового фрагмента к низу экрана
        val constraintLayout = findViewById<ConstraintLayout>(R.id.main_container)
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        constraintSet.connect(R.id.activity_main_nav_host_fragment, ConstraintSet.BOTTOM,
                ConstraintLayout.LayoutParams.PARENT_ID, ConstraintSet.BOTTOM, 0)
        constraintSet.applyTo(constraintLayout)
    }

}

interface LoaderAndBottomPanel {

    fun enableLoadBar()
    fun disableLoadBar()
    fun showBottomNavigation()
    fun hideBottomNavigation()

}

