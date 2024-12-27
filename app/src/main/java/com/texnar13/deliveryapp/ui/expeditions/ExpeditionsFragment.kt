package com.texnar13.deliveryapp.ui.expeditions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.texnar13.deliveryapp.R
import com.texnar13.deliveryapp.model.entities.EntityExpedition
import com.texnar13.deliveryapp.ui.expeditions.dialog.ExpeditionEditDialogFragment
import com.texnar13.deliveryapp.ui.expeditions.dialog.ExpeditionTrajectoryInfoDialog
import com.texnar13.deliveryapp.view_model.MainViewModel
import java.util.Locale


class ExpeditionsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        // ----- разметка -----
        val rootView = inflater.inflate(R.layout.fragment_expeditions, container, false)

        // контейнер отправлений
        val boxesContainer = rootView.findViewById<LinearLayout>(R.id.fragment_expeditions_container)


        // ----- Работа с viewModel -----
        val mainViewModel = MainViewModel.getViewModel(requireActivity())


        // кнопка добавить отправление
        val addButton = rootView.findViewById<View>(R.id.fragment_expeditions_add_button)
        addButton.setOnClickListener {

            // вызываем диалог с пустыми начальными данными
            editExpedition(null)
        }


        // получаем отправления пользователя
        mainViewModel.loadUserExpeditions()

        // Подписываемся на список отправлений
        mainViewModel.currentUserExpeditions.observe(viewLifecycleOwner) { dbExpeditions ->
            if (dbExpeditions != null) {

                // вывод списка
                listOut(
                        boxesContainer,
                        dbExpeditions
                )
            }
        }
        return rootView
    }

    // вывод списка
    private fun listOut(outContainer: LinearLayout, expeditionsList: List<EntityExpedition>) {
        outContainer.removeAllViews()

        // проходимся по всем отправлениям
        for (expeditionUnit in expeditionsList) {
            // получеам конкретное уведомление
            // создание разметки
            val notificationViewElement = layoutInflater.inflate(
                    R.layout.element_expedition_box, null)

            val title = notificationViewElement.findViewById<TextView>(R.id.element_expedition_box_title)
            val description = notificationViewElement.findViewById<TextView>(R.id.element_expedition_box_description)
            val state = notificationViewElement.findViewById<TextView>(R.id.element_expedition_box_state)
            //ImageView img = notificationViewElement.findViewById(R.id.element_expedition_box_img);
            val buttonEdit = notificationViewElement.findViewById<TextView>(R.id.element_expedition_box_button_edit)
            val buttonFindSomebody = notificationViewElement.findViewById<TextView>(R.id.element_expedition_box_button_find_somebody)
            val buttonConnect = notificationViewElement.findViewById<TextView>(R.id.element_expedition_box_button_connect)


            title.text = expeditionUnit.expeditionPackage.name
            description.text = String.format(
                    Locale.getDefault(), """
                        Категория %s 
                        из %s, %s -> в %s, %s
                        Описание %s 
                        Вес %.1fКГ
                        Габариты %.1fx%.1fx%.1fсм
                        """.trimIndent(),
                    expeditionUnit.expeditionPackage.category,
                    expeditionUnit.addressSender.address[0],
                    expeditionUnit.addressSender.address[1],
                    expeditionUnit.addressReceiver.address[0],
                    expeditionUnit.addressReceiver.address[1],
                    expeditionUnit.expeditionPackage.description,
                    expeditionUnit.expeditionPackage.weight,
                    expeditionUnit.expeditionPackage.dimensions[0],
                    expeditionUnit.expeditionPackage.dimensions[1],
                    expeditionUnit.expeditionPackage.dimensions[2]
            )

            when (expeditionUnit.status) {
                EntityExpedition.Companion.ExpeditionStatus.WAIT_SEND -> {
                    // выставляем статус
                    state.text = "НЕ ОТПРАВЛЕНО"
                    state.setTextColor(resources.getColor(R.color.wait_mail_text_color))

                    // кнопки
                    buttonEdit.setOnClickListener {
                        // редактирование отправления
                        editExpedition(expeditionUnit)
                    }
                    buttonFindSomebody.setOnClickListener {
                        // поиск маршрута
                        gotoFindTrajectory(expeditionUnit)
                    }
                    buttonConnect.visibility = View.INVISIBLE
                }

                EntityExpedition.Companion.ExpeditionStatus.SENT -> {
                    // выставляем статус
                    state.text = "ОТПРАВЛЕНО"
                    state.setTextColor(resources.getColor(R.color.sent_mail_text_color))

                    // кнопки
                    buttonEdit.visibility = View.INVISIBLE
                    buttonFindSomebody.visibility = View.INVISIBLE
                    buttonConnect.setOnClickListener {
                        // вывод информации о маршруте
                        showTrajectoryInfo(expeditionUnit)
                    }
                }

                EntityExpedition.Companion.ExpeditionStatus.DONE -> {
                    // выставляем статус
                    state.text = "ЗАВЕРШЕНО"
                    state.setTextColor(resources.getColor(R.color.ended_mail_text_color))

                    // кнопки
                    buttonEdit.visibility = View.INVISIBLE
                    buttonFindSomebody.visibility = View.INVISIBLE
                    buttonConnect.visibility = View.INVISIBLE
                }
            }
            val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.topMargin = resources.getDimensionPixelOffset(R.dimen.containers_margin)

            outContainer.addView(notificationViewElement, layoutParams)
        }
    }


    // показать диалог редактирования или создания
    private fun editExpedition(expedition: EntityExpedition?) {

        // передаём данные в диалог через view Model
        MainViewModel.getViewModel(requireActivity()).selectExpeditionForEdit(expedition)

        // вызов диалога
        ExpeditionEditDialogFragment()
                .show(parentFragmentManager, "ExpeditionEditDialogFragment")

    }

    // перейти к окну поиска
    private fun gotoFindTrajectory(expedition: EntityExpedition) {

        // передаём данные на страницу поиска маршрутов через view Model
        MainViewModel.getViewModel(requireActivity()).selectExpeditionForEdit(expedition)


        // переход на страницу поиска маршрутов

        val bottomNavigationView: BottomNavigationView = requireActivity().findViewById(R.id.activity_main_bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.fragment_trajectories


    }

    // вывести информацию о маршруте и посылке
    private fun showTrajectoryInfo(expedition: EntityExpedition) {

        // Ставим во ViewModel обьект как выбранный
        MainViewModel.getViewModel(requireActivity()).loadTrajectoryDataForExpedition(expedition)

        // вызов диалога
        ExpeditionTrajectoryInfoDialog()
                .show(parentFragmentManager, "ExpeditionTrajectoryInfoDialog")
    }


    companion object {
        // factory method
        fun newInstance(param1: String?): ExpeditionsFragment {
            val fragment = ExpeditionsFragment()
            val args = Bundle()
            //args.putString(ARG_PARAM1, param1);
            fragment.setArguments(args)
            return fragment
        }
    }
}