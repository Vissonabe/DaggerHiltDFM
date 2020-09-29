package com.example.feature.list

import androidx.compose.foundation.Text
import androidx.compose.runtime.Composable
import com.example.feature.model.CounterState

@Composable
fun textCompose(state : CounterState) {
    Text(text = "${state.counterValue}")
}