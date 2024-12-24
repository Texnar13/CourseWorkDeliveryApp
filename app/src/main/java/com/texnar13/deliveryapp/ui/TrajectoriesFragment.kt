package com.texnar13.deliveryapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.texnar13.deliveryapp.R
import com.texnar13.deliveryapp.model.entities.EntityTrip
import com.texnar13.deliveryapp.view_model.MainViewModel
import java.lang.String
import java.util.Locale

class TrajectoriesFragment  // Required empty public constructor
    : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val rootView = inflater.inflate(R.layout.fragment_trajectories, container, false)

        // ------------------------------------------- разметка -------------------------------------------

        val textViewDepartCountry = rootView.findViewById<TextInputLayout>(R.id.fragment_trajectories_search_depart_country).editText!!
        val textViewDepartCity = rootView.findViewById<TextInputLayout>(R.id.fragment_trajectories_search_depart_city).editText!!
        val textViewArriveCountry = rootView.findViewById<TextInputLayout>(R.id.fragment_trajectories_search_arrive_country).editText!!
        val textViewArriveCity = rootView.findViewById<TextInputLayout>(R.id.fragment_trajectories_search_arrive_city).editText!!
        val textViewDepartDate = rootView.findViewById<TextInputLayout>(R.id.fragment_trajectories_search_depart_date).editText!!
        val textViewFreeWeight = rootView.findViewById<TextInputLayout>(R.id.fragment_trajectories_search_free_weight).editText!!
        val searchButton = rootView.findViewById<Button>(R.id.fragment_trajectories_search_button)








        // контейнер trip
        val boxesContainer = rootView.findViewById<LinearLayout>(R.id.fragment_trajectories_search_result_container)

        // -------------------------------- подписываемся на изменения во viewModel --------------------------------
        val mainViewModel = MainViewModel.getViewModel(requireActivity())

        // смотрим за отправлением, если оно есть вводим его параметры в поиск маршрутов
        mainViewModel.selectedExpedition.observe(viewLifecycleOwner) {
            if (it != null) {
                // Выставляем данные в поля

                textViewDepartCountry.setText(it.addressSender.address[0])
                textViewDepartCity.setText(it.addressSender.address[1])
                textViewArriveCountry.setText(it.addressReceiver.address[0])
                textViewArriveCity.setText(it.addressReceiver.address[1])
                textViewDepartDate.setText("")
                textViewFreeWeight.setText(it.expeditionPackage.weight.toString())
            }
        }

        mainViewModel.loadTrips()


        // глобальное изменение переменной
        mainViewModel.currentLoadedTrips.observe(viewLifecycleOwner) { trips ->

            // вывод списка
            outList(trips, boxesContainer)
        }


        // кнопка поиска
        searchButton.setOnClickListener {
            mainViewModel.loadTrips()
        }

        return rootView
    }


    // вывод списка
    private fun outList(trips: List<EntityTrip>, outputContainer: LinearLayout){
        outputContainer.removeAllViews()

        // проходимся по всем отправлениям
        for (tripUnit in trips) {
            // создание разметки

            val notificationViewElement = layoutInflater.inflate(R.layout.element_trip_box, null)


            val username: TextView = notificationViewElement.findViewById(R.id.element_trip_box_username)
            val description: TextView = notificationViewElement.findViewById(R.id.element_trip_box_description)
            val joinButton: TextView = notificationViewElement.findViewById(R.id.element_trip_box_join_button)


            description.text = String.format(
                    Locale.getDefault(), """
                            Отправляюсь %s
                            из %s, %s -> в %s, %s
                            Могу взять %fКг веса
                            """.trimIndent(),
                    tripUnit.sentDate.toString(),
                    tripUnit.sendCountry,
                    tripUnit.sendCity,
                    tripUnit.receivingCountry,
                    tripUnit.receivingCity,
                    tripUnit.availableWeight
            )


            val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.topMargin = resources.getDimensionPixelOffset(R.dimen.containers_margin)

            outputContainer.addView(
                    notificationViewElement, layoutParams
            )
        }
    }


}