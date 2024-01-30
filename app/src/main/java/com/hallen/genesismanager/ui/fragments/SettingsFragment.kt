package com.hallen.genesismanager.ui.fragments

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import codes.side.andcolorpicker.group.PickerGroup
import codes.side.andcolorpicker.group.registerPickers
import codes.side.andcolorpicker.model.IntegerRGBColor
import codes.side.andcolorpicker.view.picker.ColorSeekBar
import codes.side.andcolorpicker.view.picker.OnIntegerRGBColorPickListener
import com.example.genesismanager.R
import com.example.genesismanager.databinding.DialogAddNewWorkcraftBinding
import com.example.genesismanager.databinding.DialogShowEventsBinding
import com.hallen.genesismanager.domain.model.WorkCraft
import com.hallen.genesismanager.ui.adapters.SimpleArrayAdapter
import com.hallen.genesismanager.ui.viewmodels.PreferenceViewModel
import com.orhanobut.logger.Logger

class SettingsFragment : PreferenceFragmentCompat() {

    private val preferenceViewModel: PreferenceViewModel by viewModels()
    private lateinit var worksPreferences: Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        worksPreferences = findPreference("key_works")!!
        worksPreferences.setOnPreferenceClickListener {
            showWorksDialog()
            true
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        preferenceViewModel.worksModel.observe(viewLifecycleOwner) {
            val worksCount = it.size
            worksPreferences.summary = "$worksCount tipos de trabajo"
        }
        preferenceViewModel.getWorks()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun showWorksDialog() {
        val context = requireContext()
        val dialog = Dialog(context)
        val dialogBinding = DialogShowEventsBinding.inflate(dialog.layoutInflater)

        dialog.apply {
            setContentView(dialogBinding.root)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.apply {
                copyFrom(window!!.attributes)
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
                gravity = Gravity.CENTER
            }
            window!!.attributes = layoutParams
        }

        with(dialogBinding) {
            val adapter = SimpleArrayAdapter(context, R.layout.list_item) { work: WorkCraft ->
                preferenceViewModel.removeWork(work)
            }
            preferenceViewModel.worksModel.observe(viewLifecycleOwner) { adapter.update(it) }
            listView.adapter = adapter
            agregar.setOnClickListener { showAddWorksDialog() }
            guardar.setOnClickListener { dialog.dismiss() }
            cancelButton.setOnClickListener { dialog.dismiss() }
        }
        dialog.show()
    }

    private fun showAddWorksDialog() {
        val context = requireContext()
        val dialog = Dialog(context)
        val dialogBinding = DialogAddNewWorkcraftBinding.inflate(dialog.layoutInflater)
        dialog.apply {
            setContentView(dialogBinding.root)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.apply {
                copyFrom(window!!.attributes)
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
                gravity = Gravity.CENTER
            }
            window!!.attributes = layoutParams
        }
        with(dialogBinding) {

            var colorHex: String = ""

            val group = PickerGroup<IntegerRGBColor>().also {
                it.registerPickers(redSeekBar, greenSeekBar, blueSeekBar)
            }
            group.addListener(
                object : OnIntegerRGBColorPickListener() {
                    override fun onColorChanged(
                        picker: ColorSeekBar<IntegerRGBColor>,
                        color: IntegerRGBColor,
                        value: Int
                    ) {
                        val redColor = color.floatR.toInt()
                        val greenColor = color.floatG.toInt()
                        val blueColor = color.floatB.toInt()
                        val hex = String.format("#%02x%02x%02x", redColor, greenColor, blueColor)
                        colorHex = hex
                        guardar.backgroundTintList =
                            ColorStateList.valueOf(Color.parseColor(colorHex))
                        val luminocidad = redColor + greenColor + blueColor
                        val textColor = if (luminocidad < 400) Color.WHITE else Color.BLACK
                        guardar.setTextColor(textColor)
                        Logger.i("textColor: $textColor, red: $redColor, green: $greenColor, blue: $blueColor, hex: $hex")
                    }
                }
            )

            guardar.setOnClickListener {
                val text = edit.text
                if (colorHex.isBlank()) colorHex = "#000000"
                if (text.isBlank()) return@setOnClickListener
                val workCraft = WorkCraft(name = text.toString(), color = colorHex)
                preferenceViewModel.addWork(workCraft)
                dialog.dismiss()
            }
        }
        dialog.show()
    }
}