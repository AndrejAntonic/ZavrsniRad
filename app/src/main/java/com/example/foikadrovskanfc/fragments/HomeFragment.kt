package com.example.foikadrovskanfc.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foikadrovskanfc.CanvasActivity
import com.example.foikadrovskanfc.R
import com.example.foikadrovskanfc.adapters.PersonnelAdapter
import com.example.foikadrovskanfc.database.PersonnelDatabase
import com.example.foikadrovskanfc.entities.Personnel
import com.example.foikadrovskanfc.utils.NfcUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.view.Window
import android.graphics.Color
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import com.example.foikadrovskanfc.helpers.NewFilterHelper

class HomeFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PersonnelAdapter
    private lateinit var twEmpty: TextView
    private lateinit var fabPersonnel: FloatingActionButton
    val checkedItems: MutableList<Personnel> = mutableListOf()
    private lateinit var filterOn: ImageButton
    private lateinit var filterOff: ImageButton
    private var selectedFilterOption: String = ""
    private var selectedFilterType: String = ""
    private lateinit var searchView: SearchView
    private lateinit var sortedPersonnelList: List<Personnel>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        findViews(view)
        PersonnelDatabase.buildInstance(requireContext().applicationContext)
        loadPersonnelList()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        fabPersonnel.setOnClickListener{
            adapter.getSelectedItems().forEach { newItem ->
                val itemExists = checkedItems.filter { it.id == newItem.id }.isNotEmpty()
                val isListFull = checkedItems.size >= 4
                if(!itemExists && !isListFull)
                    checkedItems.add(newItem)
            }
            checkedItems.removeAll(adapter.getRemovedItems())
            if(checkedItems.isNotEmpty()) {
                val intent = Intent(requireContext(), CanvasActivity::class.java)
                intent.putExtra("personnelList", ArrayList(checkedItems))
                startActivity(intent)
            }
            else
                Toast.makeText(requireContext(), getString(R.string.employeeSelected), Toast.LENGTH_SHORT).show()
        }

        filterOn.setOnClickListener{
            showDialog()
        }
        filterOff.setOnClickListener{
            clearFilter()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val copyList = sortedPersonnelList
                val filteredList = mutableListOf<Personnel>()
                if(newText.isEmpty()) {
                    filteredList.addAll(copyList)
                }
                else {
                    val query = newText.lowercase()
                    for(personnel in copyList) {
                        val fullName = personnel.firstName + " " + personnel.lastName
                        if (personnel.firstName.lowercase().contains(query) ||
                            personnel.lastName.lowercase().contains(query) ||
                            personnel.title.lowercase().contains(query) ||
                            fullName.lowercase().contains(query))
                            filteredList.add(personnel)
                    }
                }

                refreshPersonnelList(filteredList)
                return true
            }
        })
    }

    private fun findViews(view: View) {
        recyclerView = view.findViewById(R.id.rv_personnelRecords)
        twEmpty = view.findViewById(R.id.tw_empty)
        fabPersonnel = view.findViewById(R.id.fab_generateImage)
        filterOn = view.findViewById(R.id.imgbtn_filter)
        filterOff = view.findViewById(R.id.imgbtn_filterOff)
        searchView = view.findViewById(R.id.searchView)
    }
    private fun clearFilter() {
        val personnelList = PersonnelDatabase.getInstance().getPersonnelDAO().getAllPersonnel().toMutableList()
        sortedPersonnelList = personnelList
        adapter = PersonnelAdapter(personnelList.toMutableList())
        recyclerView.adapter = adapter
        checkedItems.clear()
    }
    private fun showDialog(){
        val newFilterView = LayoutInflater.from(requireContext()).inflate(R.layout.filter_options, null)
        val dialogHelper = NewFilterHelper(newFilterView)

        AlertDialog.Builder(requireContext())
            .setView(newFilterView)
            .setTitle(R.string.filter)
            .setPositiveButton(R.string.ok) {_, _ ->
                selectedFilterOption = dialogHelper.getSelectedSpinnerOption()
                selectedFilterType = dialogHelper.getSelectedSpinnerType()
                sortList()
            }.show()

        dialogHelper.populateSpinnerOptions()
        dialogHelper.populateSpinnerType()
    }
    private fun sortList() {
        checkedItems.clear()
        val personnelList = PersonnelDatabase.getInstance().getPersonnelDAO().getAllPersonnel().toMutableList()
        sortedPersonnelList = when (selectedFilterOption) {
            "Name" -> {
                if (selectedFilterType == "Ascending") {
                    personnelList.sortedBy { it.firstName }
                } else {
                    personnelList.sortedByDescending { it.firstName }
                }
            }
            "Last name" -> {
                if (selectedFilterType == "Ascending") {
                    personnelList.sortedBy { it.lastName }
                } else {
                    personnelList.sortedByDescending { it.lastName }
                }
            }
            "Title" -> {
                if (selectedFilterType == "Ascending") {
                    personnelList.sortedBy { it.title }
                } else {
                    personnelList.sortedByDescending { it.title }
                }
            }
            else -> personnelList
        }

        adapter = PersonnelAdapter(sortedPersonnelList.toMutableList())
        recyclerView.adapter = adapter
    }
    fun loadPersonnelList() {
        val personnelList = PersonnelDatabase.getInstance().getPersonnelDAO().getAllPersonnel().toMutableList()
        sortedPersonnelList = personnelList
        if (personnelList.isNotEmpty())
            twEmpty.text = ""
        else
            twEmpty.text = getString(R.string.empty_rw)
        adapter = PersonnelAdapter(personnelList.toMutableList())
        recyclerView.adapter = adapter
    }
    private fun refreshPersonnelList(personnelList: MutableList<Personnel>) {
        adapter.getSelectedItems().forEach { newItem ->
            val itemExists = checkedItems.filter { it.id == newItem.id }.isNotEmpty()
            val isListFull = checkedItems.size >= 4
            if(!itemExists && !isListFull)
                checkedItems.add(newItem)
        }
        checkedItems.removeAll(adapter.getRemovedItems())
        Log.d("Checked items", checkedItems.toString())
        Log.d("Checked tags", adapter.getSelectedItems().toString())
        if(personnelList.isNotEmpty())
            twEmpty.text = ""
        adapter = PersonnelAdapter(personnelList, checkedItems)
        recyclerView.adapter = adapter
    }
}