package com.example.feature.detail

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp

@Composable
fun getContentView(counterValue : Int, plusClick : () -> Unit, minusClick : () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Row {
            Text(text = "value is ")
            Text(text = "${counterValue}")
        }
        Row {
            Button(onClick = plusClick, contentPadding = PaddingValues(Dp(20f))) {
                Text(text = "+")
            }
            Button(onClick = minusClick, contentPadding = PaddingValues(Dp(20f))) {
                Text(text = "-")
            }
        }

    }
}