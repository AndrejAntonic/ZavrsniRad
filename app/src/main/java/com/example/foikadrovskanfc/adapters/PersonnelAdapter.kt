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

class PersonnelAdapter(private val personnelList: List<Personnel>) :
    RecyclerView.Adapter<PersonnelAdapter.PersonnelViewHolder>() {

    inner class PersonnelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val personnelCard: CardView
        private val personnelId: CheckBox
        private val personnelTitle: TextView
        private val personnelFirstName: TextView
        private val personnelLastName: TextView

        init {
            personnelCard = view.findViewById(R.id.cw_personnelCard)
            personnelId = view.findViewById(R.id.chb_personnelId)
            personnelTitle = view.findViewById(R.id.tw_personnelTitle)
            personnelFirstName = view.findViewById(R.id.tw_personnelFirstName)
            personnelLastName = view.findViewById(R.id.tw_personnelLastName)

            personnelCard.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val isChecked = !personnelId.isChecked
                    personnelId.isChecked = isChecked
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
        val personnelView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.personnel_records_item, parent, false)
        return PersonnelViewHolder(personnelView)
    }

    override fun getItemCount() = personnelList.size

    override fun onBindViewHolder(holder: PersonnelViewHolder, position: Int) {
        holder.bind(personnelList[position])
    }
}
