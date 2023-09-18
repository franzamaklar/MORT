package com.masters.mort.ui.main.report.utilities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.masters.mort.R
import com.masters.mort.databinding.ReportCellBinding
import com.masters.mort.model.deceased_person.DeceasedPerson

class SearchReportAdapter : RecyclerView.Adapter<SearchViewHolder>(), Filterable {

    val deceasedPeople = mutableListOf<DeceasedPerson>()
    val allDeceasedPeople = mutableListOf<DeceasedPerson>()

    var onReportSelectedListener: OnSelectedReport? = null
    var onReportSelectedLongListener: OnSelectedReport? = null

    fun setPatients(deceasedPeople: MutableList<DeceasedPerson>?){
        this.deceasedPeople.clear()
        allDeceasedPeople.clear()
        this.deceasedPeople.addAll(deceasedPeople!!.sortedBy { patient -> patient.pid })
        allDeceasedPeople.addAll(deceasedPeople.sortedBy { patient -> patient.pid })
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder{
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.report_cell, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int){
        val patient = deceasedPeople[position]
        holder.bind(patient)
        onReportSelectedListener?.let { onSelectedReport ->
            holder.itemView.setOnClickListener { onSelectedReport.onReportSelected(patient) }
        }
        onReportSelectedLongListener?.let { onSelectedReport ->
            holder.itemView.setOnLongClickListener {
                onSelectedReport.onReportLongSelected(patient)
                true
            }
        }
    }

    override fun getItemCount(): Int = deceasedPeople.count()

    override fun getFilter(): Filter {
        return customFilter
    }

    private val customFilter = object : Filter(){
        override fun performFiltering(character: CharSequence?): FilterResults {
            val filteredDeceasedPeople = mutableListOf<DeceasedPerson>()
            if(character.toString().isEmpty()){
                filteredDeceasedPeople.addAll(allDeceasedPeople)
            }else{
                for(patient in allDeceasedPeople){
                    if(patient.pid.contains(character.toString().lowercase())){
                        filteredDeceasedPeople.add(patient)
                    }
                }
            }
            val filterResults = FilterResults()
            filterResults.values = filteredDeceasedPeople
            return filterResults
        }

        override fun publishResults(character: CharSequence?, filter: FilterResults?) {
            deceasedPeople.clear()
            if (filter != null) {
                deceasedPeople.addAll(filter.values as Collection<DeceasedPerson>)
            }
            notifyDataSetChanged()
        }

    }

}

class SearchViewHolder(cellView: View): RecyclerView.ViewHolder(cellView){
    fun bind(deceasedPerson: DeceasedPerson){
        val binding = ReportCellBinding.bind(itemView)
        binding.deceasedPersonFullName.text = String.format("${deceasedPerson.name} ${deceasedPerson.surname}")
        binding.deceasedPersonID.text = deceasedPerson.pid
    }
}