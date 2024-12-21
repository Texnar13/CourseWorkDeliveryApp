package com.texnar13.deliveryapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import com.texnar13.deliveryapp.R
import com.texnar13.deliveryapp.ui.login.LoginFragment
import com.texnar13.deliveryapp.view_model.MainViewModel
import java.util.Locale


class UserFragment : Fragment() {
    private var mParam1: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = requireArguments().getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_user, container, false)

        val mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        // ------------------------------------------- разметка -------------------------------------------
        Log.e("TAG", "onCreateView: user = " + mainViewModel.currentUser.value)


        // кнопка выхода из аккаунта
        rootView.findViewById<View>(R.id.fragment_user_logout_button).setOnClickListener { view ->
            mainViewModel.logout()
            // TODO НЕ РАБОТАЕТ СДЕЛАТЬ, работает при двойном нажатии
            // переход на страницу регистрации
            //requireActivity().getOnBackPressedDispatcher().onBackPressed();
            findNavController(requireActivity(), R.id.activity_main_nav_host_fragment).popBackStack()
        }

        // кнопка редактирования информации о пользователе
        rootView.findViewById<View>(R.id.fragment_user_edit_user_button).setOnClickListener { view: View? ->
            Log.e("TAG", "edit user: user = " + mainViewModel.currentUser.value)
            // вызов диалога
            UserEditDialogFragment.newInstance(mainViewModel.currentUser.value)
                    .show(parentFragmentManager, "edit_user_dialog")
        }

        //rootView.findViewById(R.id.fragment_user_user_img);

        // поле описания полльзователя
        val userDescription = rootView.findViewById<TextView>(R.id.fragment_user_user_description)

        // контейнер уведомлений пользователя
        val userNotificationsContainer = rootView.findViewById<LinearLayout>(R.id.fragment_user_notifications_container)


        // -------------------------------- подписываемся на изменения во viewModel --------------------------------

        // глобальное изменение переменной USER (создание или очистка)
        mainViewModel.currentUser.observe(viewLifecycleOwner) { dbUser ->

            // вывод строки состояния пользователя
            if (dbUser != null) {
                val address = StringBuilder()

                for (elementPos in dbUser.address.array.indices) {
                    address.append(dbUser.address.array[elementPos])
                    if (elementPos != dbUser.address.array.size-1) address.append(", ")
                }

                userDescription.text = String.format(Locale.getDefault(),
                        "Рейтинг %.1f/5\nЭл. Почта %s\nЯ из %s\nИмя %s\nТелефон %s",
                        dbUser.rating,
                        dbUser.email,
                        address,
                        dbUser.name,
                        dbUser.phoneNumber

                )
            }

        }

        // глобальное изменение переменной с уведомлениями пользователя
//        mainViewModel.currentUserNotifications.observe(this, notifications -> {
//
//            // вывод списка
//            userNotificationsContainer.removeAllViews();
//
//
//            Iterator<DBNotification> notificationIterator = notifications.iterator();
//            while (notificationIterator.hasNext()) {
//                // получеам конкретное уведомление
//                DBNotification notificationUnit = notificationIterator.next();
//
//                // создание разметки
//                View notificationViewElement = getLayoutInflater().inflate(R.layout.element_user_notification, userNotificationsContainer);
//                TextView textView = notificationViewElement.findViewById(R.id.element_user_notification_text);
//                textView.setText(notificationUnit.getNotificationMessage());
//
//                // кнопки
//                View buttonApprove = notificationViewElement.findViewById(R.id.element_user_notification_button_approve);
//                View buttonDisagree = notificationViewElement.findViewById(R.id.element_user_notification_button_disagree);
//                View buttonRead = notificationViewElement.findViewById(R.id.element_user_notification_button_read);
//
//                switch (notificationUnit.getNotificationType()) {
//                    // уведомления администратора
//                    case DBNotification.NOTIFICATION_TYPE_VALUE_ADMIN:
//
//                        // кнопки
//                        buttonApprove.setVisibility(View.INVISIBLE);
//                        buttonDisagree.setVisibility(View.INVISIBLE);
//                        buttonRead.setOnClickListener(v -> mainViewModel.markReadAdminNotification(
//                                notificationUnit.get_id()
//                        ));
//
//                        break;
//                    case DBNotification.NOTIFICATION_TYPE_VALUE_SEND_REQUEST:
//                        break;
//                    case DBNotification.NOTIFICATION_TYPE_VALUE_EXPEDITION:
//                        break;
//                }
//
//            }
//        });
        return rootView
    }

    companion object {
        // the fragment initialization parameters
        private const val ARG_PARAM1 = "param1"

        // Фабрика
        fun newInstance(param1: String?): LoginFragment {
            val fragment = LoginFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            fragment.arguments = args
            return fragment
        }
    }
}