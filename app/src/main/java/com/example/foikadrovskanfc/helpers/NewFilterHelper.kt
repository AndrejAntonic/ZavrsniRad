package com.example.foikadrovskanfc.helpers

import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.foikadrovskanfc.R

class NewFilterHelper(private val view: View) {
    private val filterOptionsSpinner: Spinner = view.findViewById(R.id.spn_filterOptions)
    private val filterTypeSpinner: Spinner = view.findViewById(R.id.spn_filterType)

    fun populateSpinnerOptions() {
        val options: Array<String> = view.context.resources.getStringArray(R.array.filter_options)
        val spinnerAdapter = ArrayAdapter(view.context, android.R.layout.simple_spinner_item, options)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filterOptionsSpinner.adapter = spinnerAdapter
    }

    fun populateSpinnerType() {
        val types: Array<String> = view.context.resources.getStringArray(R.array.filter_types)
        val spinnerAdapter = ArrayAdapter(view.context, android.R.layout.simple_spinner_item, types)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filterTypeSpinner.adapter = spinnerAdapter
    }

    fun getSelectedSpinnerOption(): String {
        return filterOptionsSpinner.selectedItem.toString()
    }

    fun getSelectedSpinnerType(): String {
        return filterTypeSpinner.selectedItem.toString()
    }
}