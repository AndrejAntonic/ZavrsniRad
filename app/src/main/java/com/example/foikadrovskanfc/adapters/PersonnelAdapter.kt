package com.example.foikadrovskanfc.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.foikadrovskanfc.R
import com.example.foikadrovskanfc.entities.Personnel

class PersonnelAdapter(private var personnelList: List<Personnel>) :
    RecyclerView.Adapter<PersonnelAdapter.PersonnelViewHolder>() {

    private val selectedItems: MutableList<Personnel> = mutableListOf()

    inner class PersonnelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val personnelCard: CardView = view.findViewById(R.id.cw_personnelCard)
        private val personnelId: CheckBox = view.findViewById(R.id.chb_personnelId)
        private val personnelTitle: TextView = view.findViewById(R.id.tw_personnelTitle)
        private val personnelFirstName: TextView = view.findViewById(R.id.tw_personnelFirstName)
        private val personnelLastName: TextView = view.findViewById(R.id.tw_personnelLastName)

        init {
            personnelCard.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val personnel = personnelList[position]
                    val isChecked = !personnelId.isChecked

                    if (selectedItems.size != 4 || !isChecked) {
                        personnelId.isChecked = isChecked
                        if (isChecked) {
                            selectedItems.add(personnel)
                        } else {
                            selectedItems.remove(personnel)
                        }
                    }
                }
            }
        }

        fun bind(personnel: Personnel) {
            personnelId.tag = personnel.id
            personnelTitle.text = personnel.title
            personnelFirstName.text = personnel.firstName
            personnelLastName.text = personnel.lastName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonnelViewHolder {
        val personnelView = LayoutInflater.from(parent.context)
            .inflate(R.layout.personnel_records_item, parent, false)
        return PersonnelViewHolder(personnelView)
    }

    override fun getItemCount() = personnelList.size

    override fun onBindViewHolder(holder: PersonnelViewHolder, position: Int) {
        holder.bind(personnelList[position])
    }

    fun getSelectedItems(): List<Personnel> {
        return selectedItems.toList()
    }

    fun updateData(newData: List<Personnel>) {
        personnelList = newData
        notifyDataSetChanged()
    }
}
