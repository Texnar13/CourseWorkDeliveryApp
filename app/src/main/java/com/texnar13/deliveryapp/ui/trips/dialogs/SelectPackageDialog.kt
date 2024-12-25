package com.texnar13.deliveryapp.ui.trips.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.texnar13.deliveryapp.R
import com.texnar13.deliveryapp.view_model.MainViewModel

class SelectPackageDialog : DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreate(savedInstanceState)


        // -------- начинаем строить диалог --------
        val builder = AlertDialog.Builder(activity)
        // layout диалога
        val dialogLayout = layoutInflater.inflate(R.layout.fragment_dialog_select_package, null)
        builder.setView(dialogLayout)

        // инициализация разметки
        val containersOut = dialogLayout.findViewById<LinearLayout>(R.id.out_field)
        val button = dialogLayout.findViewById<View>(R.id.back_button)
        button.setOnClickListener { dismiss() }


        val viewModel = MainViewModel.getViewModel(requireActivity())


        // отслеживаем список
        viewModel.currentUserExpeditions.observe(this) { packagesList ->
            containersOut.removeAllViews()

            val margin = resources.getDimensionPixelOffset(R.dimen.containers_margin)

            if (packagesList == null) {
                val text = TextView(activity)
                text.text = "Загрузка..."

                containersOut.addView(text)
            } else {
                packagesList.forEach { expedition ->

                    val text = TextView(activity)
                    text.text = expedition.expeditionPackage.name
                    text.setPadding(margin, margin, margin, margin)

                    text.setOnClickListener {
                        dismiss()
                        viewModel.selectPackageDeliveryTrip(expedition)
                    }

                    containersOut.addView(text)
                }
            }
        }


        // наконец создаем диалог и возвращаем его
        val dialog: Dialog = builder.create()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }
}