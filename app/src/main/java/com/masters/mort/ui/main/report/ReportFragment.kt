package com.masters.mort.ui.main.report

import com.masters.mort.R
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.masters.mort.databinding.FragmentReportBinding
import com.masters.mort.model.mortality.Mortality
import com.masters.mort.model.deceased_person.DeceasedPerson
import com.masters.mort.ui.main.MainActivity
import com.masters.mort.ui.main.ReportActivity
import com.masters.mort.ui.main.report.utilities.OnSelectedReport
import com.masters.mort.ui.main.report.utilities.SearchReportAdapter
import com.masters.mort.ui.main.report.utilities.XSLXExporter
import com.masters.mort.utilities.NetworkConnectivityChecker
import com.masters.mort.viewmodel.MortalityViewModel
import com.masters.mort.viewmodel.DeceasedPersonViewModel

class ReportFragment : Fragment(), OnSelectedReport {

    private lateinit var binding: FragmentReportBinding
    private lateinit var adapter: SearchReportAdapter

    private var patientsForExport: MutableList<DeceasedPerson> = mutableListOf()
    private var mortalitiesForExport: MutableList<Mortality> = mutableListOf()

    private lateinit var viewModelPatient: DeceasedPersonViewModel
    private lateinit var viewModelMort: MortalityViewModel

    private val firebaseAuth = Firebase.auth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportBinding.inflate(layoutInflater)
        viewModelPatient = ViewModelProvider(
            requireActivity(),
            defaultViewModelProviderFactory
        )[DeceasedPersonViewModel::class.java]
        viewModelMort = ViewModelProvider(
            requireActivity(),
            defaultViewModelProviderFactory
        )[MortalityViewModel::class.java]
        var isNetworkAvail = false;
        val networkAvailability = ReportActivity.connectionLiveData
        networkAvailability.observe(this){ isNetworkAvailable ->
            isNetworkAvailable.let {
                isNetworkAvail = it
            }
        }
        fetchMortalities()
        setUpRecycler()
        setUpSearchable()
        binding.exportAndSendButton.setOnClickListener {
            if(isNetworkAvail) {
                try {
                    firebaseAuth.currentUser?.email?.let { email ->
                        XSLXExporter.exportToXSLX(
                            patientsForExport,
                            mortalitiesForExport,
                            requireActivity(),
                            email
                        )
                    }
                    Toast.makeText(
                        context,
                        "Data exported and sent to central app email successfully! Contact mort.testings@gmail.com to get your data!",
                        Toast.LENGTH_LONG
                    ).show()
                } catch (exception: Exception) {
                    Toast.makeText(context, exception.message, Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(context, "No Internet! You can not use this function! Try again when you are online.", Toast.LENGTH_LONG).show()
            }
        }
        return binding.root
    }

    private fun fetchMortalities() {
        viewModelMort.getMortalitiesSync().observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.isNotEmpty())
                    mortalitiesForExport.addAll(it)
            }
        }
    }

    private fun setUpSearchable() {
        binding.searchEngine.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    adapter.filter.filter(newText)
                    return false
                }
            })
    }

    private fun setUpRecycler() {
        binding.queryResultsRecylcer.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )
        val itemDecorator = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(ContextCompat.getDrawable(context!!, R.drawable.divider)!!)
        binding.queryResultsRecylcer.addItemDecoration(itemDecorator)
        adapter = SearchReportAdapter()
        viewModelPatient.getDeceasedPersonsSync().observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.isNotEmpty()) {
                    adapter.setPatients(it)
                    patientsForExport = it
                }
            }
        }
        adapter.onReportSelectedListener = this
        adapter.onReportSelectedLongListener = this
        binding.queryResultsRecylcer.adapter = adapter
    }

    override fun onReportSelected(deceasedPerson: DeceasedPerson) {
        var isProcessed = false
        viewModelPatient.getDeceasedPersonsSync().observe(viewLifecycleOwner){ patients ->
            if (!patients.isNullOrEmpty()) {
                isProcessed = patients.firstOrNull { it.pid == deceasedPerson.pid }!!.processed
            }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            val action =
                ReportFragmentDirections.actionReportFragmentToUpdateChoiceFragment(
                    deceasedPerson.pid,
                    isProcessed,
                    deceasedPerson.birthDate
                )
            findNavController().navigate(action)
        }, 1000)
        Toast.makeText(context, "Redirecting to update page. Please wait!", Toast.LENGTH_LONG).show()
    }

    override fun onReportLongSelected(deceasedPerson: DeceasedPerson) {
        viewModelPatient.deleteDeceasedPerson(deceasedPerson)
        viewModelMort.deleteMortality(deceasedPerson.pid)
    }
}