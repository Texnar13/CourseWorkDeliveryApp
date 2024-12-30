package com.texnar13.deliveryapp.ui.trips.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputLayout
import com.texnar13.deliveryapp.R
import com.texnar13.deliveryapp.model.entities.EntityAddress
import com.texnar13.deliveryapp.model.entities.EntityTrip
import com.texnar13.deliveryapp.view_model.MainViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class TripEditDialog : DialogFragment() {
    private lateinit var depAddressInput: TextInputLayout
    private lateinit var depDateInput: TextInputLayout
    private lateinit var arrivalAddressInput: TextInputLayout
    private lateinit var arrivalDateInput: TextInputLayout
    private lateinit var freeWeightInput: TextInputLayout



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreate(savedInstanceState)


        // -------- начинаем строить диалог --------
        val builder = AlertDialog.Builder(activity)
        // layout диалога
        val dialogLayout = layoutInflater.inflate(R.layout.dialog_edit_trips, null)
        builder.setView(dialogLayout)

        // инициализация разметки
        val titleText = dialogLayout.findViewById<TextView>(R.id.title)
        val idText = dialogLayout.findViewById<TextView>(R.id.id_out)
        depAddressInput = dialogLayout.findViewById(R.id.dep_address_input)
        depDateInput = dialogLayout.findViewById(R.id.dep_date_input)
        arrivalAddressInput = dialogLayout.findViewById(R.id.arrival_address_input)
        arrivalDateInput = dialogLayout.findViewById(R.id.arrival_date_input)
        freeWeightInput = dialogLayout.findViewById(R.id.free_weight_input)

        val cancelButton = dialogLayout.findViewById<Button>(R.id.cancel_button)
        cancelButton.setOnClickListener { dismiss() }
        val saveButton = dialogLayout.findViewById<Button>(R.id.save_button)


        // -------- получаем данные из viewModel --------
        val viewModel = MainViewModel.getViewModel(requireActivity())

        // получаем отправление
        val trip:EntityTrip? = null//viewModel.selectedExpedition.value


        // вывод данных в поля, погулять
        if (trip == null) {
            titleText.text = "Cоздание маршрута"
            idText.text = "id=new"
        } else {
            titleText.text = "Редактирование маршрута"
            idText.text = "id=" + trip.id
//
//            senderAddressField.editText!!.setText(expedition.addressSender.getString())
//            receiverAddressField.editText!!.setText(expedition.addressReceiver.getString())
//
//            boxNameField.editText!!.setText(expedition.expeditionPackage.name)
//            boxCategoryField.editText!!.setText(expedition.expeditionPackage.category)
//            boxDescriptionField.editText!!.setText(expedition.expeditionPackage.description)
//            boxDimensField.editText!!.setText(expedition.expeditionPackage.getDimensionsString())
//            boxWeightField.editText!!.setText("" + expedition.expeditionPackage.weight)
        }

        // кнопка сохранения
        saveButton.setOnClickListener {

            // проверка полей
            if (checkFields()) {


                val senderAddressesArray =
                    depAddressInput.getEditText()!!.text.toString().trim { it <= ' ' }
                        .split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (i in senderAddressesArray.indices) senderAddressesArray[i] =
                    senderAddressesArray[i].trim { it <= ' ' }

                val receiverAddressesArray =
                    arrivalAddressInput.getEditText()!!.text.toString().trim { it <= ' ' }
                        .split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (i in receiverAddressesArray.indices) receiverAddressesArray[i] =
                    receiverAddressesArray[i].trim { it <= ' ' }



                val depDate = dateFormatter.parse(depDateInput.editText!!.text.toString())!!
                val arrivalDate = dateFormatter.parse(arrivalDateInput.editText!!.text.toString())!!



                // передача данных во вью модель
                val mainViewModel = MainViewModel.getViewModel(requireActivity())

                // если это создание нового
                if (trip == null) {
                    mainViewModel.createTrip(
                        EntityAddress(senderAddressesArray),
                        depDate,
                        EntityAddress(receiverAddressesArray),
                        arrivalDate,
                        freeWeightInput.editText!!.text.toString().trim().toFloat()
                    )
                } else {
//                    // если это редактирование старого
//                    mainViewModel.editExpedition(
//                        EntityAddress(receiverAddressesArray),
//                        EntityAddress(senderAddressesArray),
//                        EntityExpedition.Companion.ExpeditionStatus.WAIT_SEND,
//                        EntityPackage(
//                            boxCategoryField.editText!!.text.toString(),
//                            boxDescriptionField.editText!!.text.toString(),
//                            dimensArray,
//                            boxWeightField.editText!!.text.toString().toDouble().toFloat(),
//                            boxNameField.editText!!.text.toString(),
//                            expedition.expeditionPackage.picture
//                        )
//                    )
                }
                dismiss()
            }
        }


        // наконец создаем диалог и возвращаем его
        val dialog: Dialog = builder.create()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dialog_edit_user, container, false)
    }


    // --------- проверка полей ---------
    private fun checkFields(): Boolean {

        var isCorrect = checkAddress(depAddressInput)

        // Если дата не прошла проверку
        if (!checkDate(depDateInput)) isCorrect = false

        // Если адрес не прошел проверку
        if (!checkAddress(arrivalAddressInput)) isCorrect = false

        // Если дата не прошла проверку
        if (!checkDate(arrivalDateInput)) isCorrect = false

        // проверяем число
        if (!checkFloat(freeWeightInput)) isCorrect = false

        return isCorrect
    }

    private fun checkAddress(inputLayout: TextInputLayout): Boolean {
        val inputText = inputLayout.editText!!.text.toString().trim { it <= ' ' }

        if (inputText.length == 0) {
            inputLayout.error = "Поле пустое!"
            return false
        } else {
            // разбиваем адрес по запятым
            val addressesArray =
                inputText.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (i in addressesArray.indices) addressesArray[i] =
                addressesArray[i].trim { it <= ' ' }

            // нехватает полей
            if (addressesArray.size != 4) {
                inputLayout.error = "Страна, Город, Улица, Дом"
                return false
            } else {
                // проверка каждого отдельного слова
                for (s in addressesArray) if (s.isEmpty()) {
                    inputLayout.error = "Страна, Город, Улица, Дом"
                    return false
                }
            }
        }

        // всё ок
        inputLayout.isErrorEnabled = false
        return true
    }



    // Формат ввода даты
    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.US)

    private fun checkDate(inputLayout: TextInputLayout): Boolean {
        val inputText = inputLayout.editText!!.text.toString().trim { it <= ' ' }

        try {
            // пробуем конвертировать дату
            dateFormatter.parse(inputText)!!

            // всё ок
            inputLayout.isErrorEnabled = false
            return true
        } catch (e: Exception) {
            inputLayout.error = "Неправильный формат"
            return false
        }
    }


    private fun checkFloat(inputLayout: TextInputLayout): Boolean {
        val inputText = inputLayout.editText!!.text.toString().trim { it <= ' ' }

        try {
            inputText.toFloat()

            // всё ок
            inputLayout.isErrorEnabled = false
            return true
        } catch (e: Exception) {
            inputLayout.error = "Неправильное число"
            return false
        }
    }



}