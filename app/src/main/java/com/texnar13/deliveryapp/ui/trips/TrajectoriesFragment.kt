package com.texnar13.deliveryapp.ui.trips

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.texnar13.deliveryapp.R
import com.texnar13.deliveryapp.model.entities.EntityExpedition
import com.texnar13.deliveryapp.model.entities.EntityTrip
import com.texnar13.deliveryapp.ui.expeditions.dialog.ExpeditionEditDialogFragment
import com.texnar13.deliveryapp.ui.trips.dialogs.SelectPackageDialog
import com.texnar13.deliveryapp.ui.trips.dialogs.TripEditDialog
import com.texnar13.deliveryapp.view_model.MainViewModel
import java.lang.String
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrajectoriesFragment  // Required empty public constructor
    : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    // Формат ввода даты
    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.US)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val rootView = inflater.inflate(R.layout.fragment_trajectories, container, false)

        // ------------------------------------------- разметка -------------------------------------------

        val textViewDepartCountry =
            rootView.findViewById<TextInputLayout>(R.id.fragment_trajectories_search_depart_country).editText!!
        val textViewDepartCity =
            rootView.findViewById<TextInputLayout>(R.id.fragment_trajectories_search_depart_city).editText!!
        val textViewArriveCountry =
            rootView.findViewById<TextInputLayout>(R.id.fragment_trajectories_search_arrive_country).editText!!
        val textViewArriveCity =
            rootView.findViewById<TextInputLayout>(R.id.fragment_trajectories_search_arrive_city).editText!!
        val textViewDepartDate =
            rootView.findViewById<TextInputLayout>(R.id.fragment_trajectories_search_depart_date)
        val textViewFreeWeight =
            rootView.findViewById<TextInputLayout>(R.id.fragment_trajectories_search_free_weight).editText!!
        val searchButton = rootView.findViewById<Button>(R.id.fragment_trajectories_search_button)


        // контейнер trip
        val boxesContainer =
            rootView.findViewById<LinearLayout>(R.id.fragment_trajectories_search_result_container)



        // кнопка добавить маршрут
        rootView.findViewById<View>(R.id.add_button).setOnClickListener {

            // вызываем диалог с пустыми начальными данными
            editTrip(null)
        }

        // -------------------------------- подписываемся на изменения во viewModel --------------------------------
        val mainViewModel = MainViewModel.getViewModel(requireActivity())



//        textViewDepartDate.editText!!.addTextChangedListener( /* watcher = */ object : TextWatcher {
//            var key = true
//
//            //            @Override
////            public void afterTextChanged(Editable s) {}
////            @Override
////            public void beforeTextChanged(CharSequence s, int start,
////                int count, int after) {
////            }
////            @Override
////            public void onTextChanged(CharSequence s, int start,
////                int before, int count) {
////                if(s.length() != 0)
////                    field2.setText("");
////            }
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                Log.i("tag", "onTextChanged: s=$s")
//                Log.i("tag", "onTextChanged: start=$start")
//                Log.i("tag", "onTextChanged: before=$before")
//                Log.i("tag", "onTextChanged: count=$count")
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                if (key) {
//                    key = false
//                    val dateText = textViewDepartDate.editText!!.text.toString()
//                    if (dateText.length == 2 || dateText.length == 5) {
//                        textViewDepartDate.editText!!.setText(
//                            dateText + "."
//                        )
//                    }
//                    key = true
//                }
//
//            }
//        })


        // смотрим за отправлением, если оно есть вводим его параметры в поиск маршрутов
        mainViewModel.selectedExpedition.observe(viewLifecycleOwner) {
            if (it != null) {
//                val date = "2020-07-07"
//                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
//                val dateDate: Date = dateFormatter.parse(date) // You got Date object of 2020 jul 7
//
//                println(formatter.format(dateDate)) //it prints "2020-07-07"
//                println(dateDate)


                // Выставляем данные в поля
                textViewDepartCountry.setText(it.addressSender.address[0])
                textViewDepartCity.setText(it.addressSender.address[1])
                textViewArriveCountry.setText(it.addressReceiver.address[0])
                textViewArriveCity.setText(it.addressReceiver.address[1])
                textViewDepartDate.editText!!.setText("")
                textViewFreeWeight.setText(it.expeditionPackage.weight.toString())

                search(
                    mainViewModel,
                    textViewDepartCountry.text.toString(),
                    textViewDepartCity.text.toString(),
                    textViewArriveCountry.text.toString(),
                    textViewArriveCity.text.toString(),
                    textViewDepartDate,
                    textViewFreeWeight
                )

            } else {
                mainViewModel.loadTrips(
                    textViewDepartCountry.text.toString(),
                    textViewDepartCity.text.toString(),
                    textViewArriveCountry.text.toString(),
                    textViewArriveCity.text.toString(),
                    Date(),
                    0F
                )
            }
        }


        // глобальное изменение переменной
        mainViewModel.currentLoadedTrips.observe(viewLifecycleOwner) { trips ->

            // вывод списка
            outList(trips, boxesContainer, mainViewModel)
        }


        // кнопка поиска
        searchButton.setOnClickListener {
            search(
                mainViewModel,
                textViewDepartCountry.text.toString(),
                textViewDepartCity.text.toString(),
                textViewArriveCountry.text.toString(),
                textViewArriveCity.text.toString(),
                textViewDepartDate,
                textViewFreeWeight
            )
        }

        return rootView
    }


    // показать диалог редактирования или создания
    private fun editTrip(trip: EntityTrip?) {

        // передаём данные в диалог через view Model
        //MainViewModel.getViewModel(requireActivity()).selectTripForEdit(trip)

        // вызов диалога
        TripEditDialog()
            .show(parentFragmentManager, "TripEditDialog")

    }


    private fun search(
        mainViewModel: MainViewModel,
        departCountry: kotlin.String,
        departCity: kotlin.String,
        arriveCountry: kotlin.String,
        arriveCity: kotlin.String,
        textViewDepartDate: TextInputLayout,
        textViewFreeWeight: EditText
    ) {

        var freeWeight = 0F
        try {
            freeWeight = textViewFreeWeight.text.toString().toFloat()
        } catch (e: Exception) {
            textViewFreeWeight.setText("0")
        }

        val dateInput = textViewDepartDate.editText!!.text.toString()

        if (dateInput.isEmpty()) {
            mainViewModel.loadTrips(
                departCountry,
                departCity,
                arriveCountry,
                arriveCity,
                Date(),
                freeWeight
            )
        } else {

            try {
                val dateDate = dateFormatter.parse(dateInput)!!

                mainViewModel.loadTrips(
                    departCountry,
                    departCity,
                    arriveCountry,
                    arriveCity,
                    dateDate,
                    freeWeight
                )
                textViewDepartDate.isErrorEnabled = false
            } catch (e: Exception) {
                textViewDepartDate.error = "Неправильный формат"
            }
        }


    }


    // вывод списка
    private fun outList(
        trips: List<EntityTrip>,
        outputContainer: LinearLayout,
        mainViewModel: MainViewModel
    ) {
        outputContainer.removeAllViews()

        // проходимся по всем отправлениям
        for (tripUnit in trips) {
            // создание разметки

            val notificationViewElement = layoutInflater.inflate(R.layout.element_trip_box, null)


            val username: TextView =
                notificationViewElement.findViewById(R.id.element_trip_box_username)
            val description: TextView =
                notificationViewElement.findViewById(R.id.element_trip_box_description)
            val joinButton: TextView =
                notificationViewElement.findViewById(R.id.element_trip_box_join_button)


            description.text = String.format(
                Locale.getDefault(), """
                            Отправляюсь %s
                            из %s, %s -> в %s, %s
                            Могу взять %fКг веса
                            """.trimIndent(),
                tripUnit.sendAddress.getCountry(),
                tripUnit.sendAddress.getCountry(),
                tripUnit.sendAddress.getCountry(),
                tripUnit.sendAddress.getCountry(),
                tripUnit.sendAddress.getCountry(),
                tripUnit.availableWeight
            )


            joinButton.setOnClickListener {
                // выбираем какой будем использовать маршрут
                mainViewModel.selectTripForEdit(tripUnit)
                // запускаем диалог
                SelectPackageDialog().show(parentFragmentManager, "SelectPackageDialog")
            }


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