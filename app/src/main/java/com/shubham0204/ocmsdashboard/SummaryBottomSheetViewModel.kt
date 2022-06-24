package com.shubham0204.ocmsdashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SummaryBottomSheetViewModel : ViewModel() {

    val totalStudentsCount = MutableLiveData<Int>().apply { value = 0 }
    val onScreenStudentsCount = MutableLiveData<Int>().apply { value = 0 }
    val presentStudentsCount = MutableLiveData<Int>().apply { value = 0 }

}