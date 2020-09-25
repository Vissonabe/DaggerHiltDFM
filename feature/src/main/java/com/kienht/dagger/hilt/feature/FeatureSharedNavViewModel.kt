package com.kienht.dagger.hilt.feature

import androidx.databinding.ObservableInt
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.kienht.dagger.hilt.feature.model.CounterState

/**
 * @author kienht
 * @since 16/09/2020
 */
class FeatureSharedNavViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    var localCounter = 0
    val counterValue = MutableLiveData<CounterState>()

    fun onPlusClicked(): Unit {
        localCounter++
        counterValue.value = CounterState(localCounter)
    }

    fun onMinusClicked(): Unit {
        --localCounter
        counterValue.value = CounterState(localCounter)
    }
}