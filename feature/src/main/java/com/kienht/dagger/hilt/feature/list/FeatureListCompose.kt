package com.kienht.dagger.hilt.feature.list

import androidx.compose.foundation.Text
import androidx.compose.runtime.Composable
import com.kienht.dagger.hilt.feature.model.CounterState

@Composable
fun textCompose(state : CounterState) {
    Text(text = "${state.counterValue}")
}