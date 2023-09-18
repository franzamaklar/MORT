package com.masters.mort.ui.main.report

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.masters.mort.databinding.FragmentUpdateChoiceBinding

class ChoiceUpdateFragment : Fragment() {

    private lateinit var binding: FragmentUpdateChoiceBinding
    private val args: ChoiceUpdateFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdateChoiceBinding.inflate(layoutInflater)
        binding.backToPreviousFragment.setOnClickListener { returnToReports() }
        binding.updatePatientInfoButton.setOnClickListener { forwardToUpdatePatientInfo() }
        binding.updateMortalityInfoButton.setOnClickListener { forwardToUpdateMortalityInfo() }
        return binding.root
    }

    private fun forwardToUpdateMortalityInfo() {
        if(args.isProcessed) {
            val action =
                ChoiceUpdateFragmentDirections.actionUpdateChoiceFragmentToUpdateMortalityFragment(
                    args.deceasedPersonID, args.lowerBoundDate
                )
            findNavController().navigate(action)
        }else{
            Toast.makeText(context, "Patient does not have inserted mortality data!", Toast.LENGTH_LONG).show()
        }
    }

    private fun forwardToUpdatePatientInfo() {
        val action = ChoiceUpdateFragmentDirections.actionUpdateChoiceFragmentToUpdateDeceasedPersonFragment(args.deceasedPersonID, args.isProcessed)
        findNavController().navigate(action)
    }

    private fun returnToReports() {
        val action = ChoiceUpdateFragmentDirections.actionUpdateChoiceFragmentToReportFragment()
        findNavController().navigate(action)
    }


}