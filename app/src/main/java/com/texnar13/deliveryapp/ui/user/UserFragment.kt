package com.texnar13.deliveryapp.ui.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.texnar13.deliveryapp.R
import com.texnar13.deliveryapp.ui.user.dialogs.UserEditDialogFragment
import com.texnar13.deliveryapp.view_model.MainViewModel
import java.util.Locale


class UserFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_user, container, false)

        val mainViewModel = MainViewModel.getViewModel(requireActivity())

        // ------------------------------------------- разметка -------------------------------------------
        Log.e("TAG", "onCreateView: user = " + mainViewModel.currentUser.value)


        // кнопка выхода из аккаунта
        rootView.findViewById<View>(R.id.fragment_user_logout_button).setOnClickListener { view ->
            mainViewModel.logout()
            // переход на страницу регистрации
            //requireActivity().getOnBackPressedDispatcher().onBackPressed();
            //findNavController(requireActivity(), R.id.activity_main_nav_host_fragment).popBackStack()
        }

        // кнопка редактирования информации о пользователе
        rootView.findViewById<View>(R.id.fragment_user_edit_user_button).setOnClickListener { view: View? ->
            Log.e("TAG", "edit user: user = " + mainViewModel.currentUser.value)
            // вызов диалога
            UserEditDialogFragment.newInstance(mainViewModel.currentUser.value)
                    .show(parentFragmentManager, "edit_user_dialog")
        }

        // поле описания полльзователя
        val userDescription = rootView.findViewById<TextView>(R.id.fragment_user_user_description)

        // -------------------------------- подписываемся на изменения во viewModel --------------------------------

        // глобальное изменение переменной USER (создание или очистка)
        mainViewModel.currentUser.observe(viewLifecycleOwner) { dbUser ->

            // вывод строки состояния пользователя
            if (dbUser != null) {
                val address = StringBuilder()

                for (elementPos in dbUser.address.address.indices) {
                    address.append(dbUser.address.address[elementPos])
                    if (elementPos != dbUser.address.address.size-1) address.append(", ")
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
        return rootView
    }
}