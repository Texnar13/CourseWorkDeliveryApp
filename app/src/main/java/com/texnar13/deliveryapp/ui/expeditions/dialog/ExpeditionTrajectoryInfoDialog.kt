package com.texnar13.deliveryapp.ui.expeditions.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.texnar13.deliveryapp.R
import com.texnar13.deliveryapp.view_model.MainViewModel
import java.util.Locale

class ExpeditionTrajectoryInfoDialog : DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreate(savedInstanceState)


        // -------- начинаем строить диалог --------
        val builder = AlertDialog.Builder(activity)
        // layout диалога
        val dialogLayout = layoutInflater.inflate(R.layout.fragment_dialog_view_expedition_and_trajectory_data, null)
        builder.setView(dialogLayout)



        // инициализация разметки
        val titleText = dialogLayout.findViewById<TextView>(R.id.title)
        val text = dialogLayout.findViewById<TextView>(R.id.text)
        val button = dialogLayout.findViewById<TextView>(R.id.button)

        button.visibility = View.GONE

        // -------- получаем данные из viewModel --------
        val viewModel = MainViewModel.getViewModel(requireActivity())

        // подписываемся на загружаемые данные
        viewModel.trajectoryAndExpeditionData.observe(this) {
            if (it == null) {
                text.text = "Поиск информации..."
                button.visibility = View.GONE
            } else {
                text.text = String.format(Locale.getDefault(),
                        """
                            |Здесь будет всякая информация о маршруте и посылке, например:
                            |Страна отправления - %s
                            |Город отправления - %s
                            |Дата отправления - %s
                        """.trimMargin(),
                        it.sendCountry,
                        it.sendCity,
                        it.sentDate
                )
                button.visibility = View.VISIBLE
            }
        }

        button.setOnClickListener {
            dismiss()
        }


        // наконец создаем диалог и возвращаем его
        val dialog: Dialog = builder.create()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog

    }

//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        // Inflate the layout for this fragment
//        val root = inflater.inflate(R.layout.fragment_dialog_view_expedition_and_trajectory_data, container)
//
//        // инициализация разметки
//        val titleText = root.findViewById<TextView>(R.id.title)
//        val text = root.findViewById<TextView>(R.id.text)
//        val button = root.findViewById<TextView>(R.id.button)
//
//        // -------- получаем данные из viewModel --------
//        val viewModel = MainViewModel.getViewModel(requireActivity())
//
//        // подписываемся на загружаемые данные
//        viewModel.trajectoryAndExpeditionData.observe(viewLifecycleOwner) {
//            if (it == null) {
//                text.text = "Поиск информации..."
//            } else {
//                text.text = String.format(Locale.getDefault(),
//                        """
//                            |Здесь будет всякая информация о маршруте и посылке, например:
//                            |Страна отправления - %s
//                            |Город отправления - %s
//                            |Дата отправления - %s
//                        """.trimMargin(),
//                        it.sendCountry,
//                        it.sendCity,
//                        it.sentDate
//                )
//            }
//        }
//
//        button.setOnClickListener {
//            dismiss()
//        }
//
//        // Убираем фон
//        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        //setStyle(STYLE_NO_FRAME, android.R.style.Theme)
//        return root
//    }


}