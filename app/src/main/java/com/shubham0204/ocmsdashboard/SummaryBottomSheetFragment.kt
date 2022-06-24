package com.shubham0204.ocmsdashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shubham0204.ocmsdashboard.databinding.SummaryBottomSheetFragmentBinding

class SummaryBottomSheetFragment : BottomSheetDialogFragment() {

    companion object {
        val TAG = "Summary_Bottom_Sheet_Fragment"
    }

    private lateinit var summaryBottomSheetViewModel : SummaryBottomSheetViewModel
    private lateinit var summaryBottomSheetFragmentBinding : SummaryBottomSheetFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.e( "APP" , "Created Fragment View")
        summaryBottomSheetFragmentBinding = SummaryBottomSheetFragmentBinding.inflate( inflater )
        summaryBottomSheetViewModel = ViewModelProvider( requireActivity() )[ SummaryBottomSheetViewModel::class.java ]
        summaryBottomSheetViewModel.presentStudentsCount.observe( this) {
            summaryBottomSheetFragmentBinding.presentStudentsNum.text = it.toString()
        }
        summaryBottomSheetViewModel.onScreenStudentsCount.observe( this) {
            summaryBottomSheetFragmentBinding.onScreenStudentsNum.text = it.toString()
        }
        summaryBottomSheetViewModel.totalStudentsCount.observe( this ) {
            summaryBottomSheetFragmentBinding.totalStudentsNum.text = it.toString()
        }
        return summaryBottomSheetFragmentBinding.root
    }



}