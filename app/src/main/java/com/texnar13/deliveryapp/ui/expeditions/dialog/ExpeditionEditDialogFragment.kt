package com.texnar13.deliveryapp.ui.expeditions.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import com.texnar13.deliveryapp.R
import com.texnar13.deliveryapp.model.entities.EntityAddress
import com.texnar13.deliveryapp.model.entities.EntityExpedition
import com.texnar13.deliveryapp.model.entities.EntityPackage
import com.texnar13.deliveryapp.view_model.MainViewModel

class ExpeditionEditDialogFragment : DialogFragment() {
    lateinit var senderAddressField: TextInputLayout
    lateinit var receiverAddressField: TextInputLayout
    lateinit var boxNameField: TextInputLayout
    lateinit var boxCategoryField: TextInputLayout
    lateinit var boxDescriptionField: TextInputLayout
    lateinit var boxDimensField: TextInputLayout
    lateinit var boxWeightField: TextInputLayout


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreate(savedInstanceState)


        // -------- начинаем строить диалог --------
        val builder = AlertDialog.Builder(activity)
        // layout диалога
        val dialogLayout = layoutInflater.inflate(R.layout.fragment_dialog_edit_expeditions, null)
        builder.setView(dialogLayout)

        // инициализация разметки
        val titleText =
            dialogLayout.findViewById<TextView>(R.id.fragment_dialog_edit_expeditions_title)
        val idText = dialogLayout.findViewById<TextView>(R.id.fragment_dialog_edit_expeditions_id)

        senderAddressField =
            dialogLayout.findViewById(R.id.fragment_dialog_edit_expeditions_input_sender_address)
        receiverAddressField =
            dialogLayout.findViewById(R.id.fragment_dialog_edit_expeditions_input_receiver_address)
        boxNameField = dialogLayout.findViewById(R.id.fragment_dialog_edit_expeditions_input_name)
        boxCategoryField =
            dialogLayout.findViewById(R.id.fragment_dialog_edit_expeditions_input_category)
        boxDescriptionField =
            dialogLayout.findViewById(R.id.fragment_dialog_edit_expeditions_input_description)
        boxDimensField =
            dialogLayout.findViewById(R.id.fragment_dialog_edit_expeditions_input_dimens)
        boxWeightField =
            dialogLayout.findViewById(R.id.fragment_dialog_edit_expeditions_input_weight)

        val cancelButton =
            dialogLayout.findViewById<Button>(R.id.fragment_dialog_edit_expeditions_cancel_button)
        cancelButton.setOnClickListener { dismiss() }
        val saveButton =
            dialogLayout.findViewById<Button>(R.id.fragment_dialog_edit_expeditions_save_button)


        // -------- получаем данные из viewModel --------
        val viewModel = MainViewModel.getViewModel(requireActivity())

        // получаем отправление
        val expedition = viewModel.selectedExpedition.value


        // вывод данных в поля, погулять
        if (expedition == null) {
            titleText.text = "Cоздание отправления"
            idText.text = "id=new"
        } else {
            titleText.text = "Редактирование отправления"
            idText.text = "id=" + expedition.id

            senderAddressField.editText!!.setText(expedition.addressSender.getString())
            receiverAddressField.editText!!.setText(expedition.addressReceiver.getString())

            boxNameField.editText!!.setText(expedition.expeditionPackage.name)
            boxCategoryField.editText!!.setText(expedition.expeditionPackage.category)
            boxDescriptionField.editText!!.setText(expedition.expeditionPackage.description)
            boxDimensField.editText!!.setText(expedition.expeditionPackage.getDimensionsString())
            boxWeightField.editText!!.setText("" + expedition.expeditionPackage.weight)
        }

        // кнопка сохранения
        saveButton.setOnClickListener {

            // проверка полей
            if (checkFields()) {
                // разбиваем по запятым

                val senderAddressesArray =
                    senderAddressField.getEditText()!!.text.toString().trim { it <= ' ' }
                        .split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (i in senderAddressesArray.indices) senderAddressesArray[i] =
                    senderAddressesArray[i].trim { it <= ' ' }

                val receiverAddressesArray =
                    receiverAddressField.getEditText()!!.text.toString().trim { it <= ' ' }
                        .split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (i in receiverAddressesArray.indices) receiverAddressesArray[i] =
                    receiverAddressesArray[i].trim { it <= ' ' }

                val dimensStringArray =
                    boxDimensField.getEditText()!!.text.toString().trim { it <= ' ' }
                        .split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val dimensArray =
                    Array(dimensStringArray.size) { pos -> dimensStringArray[pos].trim().toFloat() }

                val mainViewModel =
                    ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

                // если это создание нового
                if (expedition == null) {
                    mainViewModel.createExpedition(
                        0,
                        EntityAddress(receiverAddressesArray),
                        EntityAddress(senderAddressesArray),
                        EntityExpedition.Companion.ExpeditionStatus.WAIT_SEND,
                        EntityPackage(
                            boxCategoryField.editText!!.text.toString(),
                            boxDescriptionField.editText!!.text.toString(),
                            dimensArray,
                            boxWeightField.editText!!.text.toString().toFloat(),
                            boxNameField.editText!!.text.toString(),
                            "URL"
                        )
                    )
                } else {
                    // если это редактирование старого
                    mainViewModel.editExpedition(
                        EntityAddress(receiverAddressesArray),
                        EntityAddress(senderAddressesArray),
                        EntityExpedition.Companion.ExpeditionStatus.WAIT_SEND,
                        EntityPackage(
                            boxCategoryField.editText!!.text.toString(),
                            boxDescriptionField.editText!!.text.toString(),
                            dimensArray,
                            boxWeightField.editText!!.text.toString().toDouble().toFloat(),
                            boxNameField.editText!!.text.toString(),
                            expedition.expeditionPackage.picture
                        )
                    )
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
        var isCorrect = checkAddress(senderAddressField)

        // Если адрес не прошел проверку
        if (!checkAddress(receiverAddressField)) isCorrect = false

        // проверка пустых полей
        if (!checkFieldOnEmpty(boxNameField)) isCorrect = false
        if (!checkFieldOnEmpty(boxCategoryField)) isCorrect = false
        if (!checkFieldOnEmpty(boxDescriptionField)) isCorrect = false

        // поле габаритов
        if (!checkDimensField(boxDimensField)) isCorrect = false

        // поле веса
        try {
            boxWeightField.editText!!.text.toString().toDouble()
            boxWeightField.isErrorEnabled = false
        } catch (e: NumberFormatException) {
            boxWeightField.error = "Неправильное число!"
            isCorrect = false
        }
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

    private fun checkFieldOnEmpty(inputLayout: TextInputLayout): Boolean {
        if (inputLayout.editText!!.text.toString().trim { it <= ' ' }.isEmpty()) {
            inputLayout.error = "Поле пустое!"
            return false
        }
        inputLayout.isErrorEnabled = false
        return true
    }

    private fun checkDimensField(inputLayout: TextInputLayout): Boolean {
        val inputText = inputLayout.editText!!.text.toString().trim { it <= ' ' }

        if (inputText.length == 0) {
            inputLayout.error = "Поле пустое!"
            return false
        } else {
            // разбиваем по запятым

            val dimensArray =
                inputText.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (i in dimensArray.indices) dimensArray[i] = dimensArray[i].trim { it <= ' ' }

            // нехватает полей
            if (dimensArray.size != 3) {
                inputLayout.error = "Ширина, Высота, Глубина"
                return false
            } else {
                // проверка каждого отдельного числа
                for (s in dimensArray) try {
                    s.toDouble()
                } catch (e: NumberFormatException) {
                    inputLayout.error = "Ширина, Высота, Глубина"
                    return false
                }
            }
        }

        // всё ок
        inputLayout.isErrorEnabled = false
        return true
    }


}