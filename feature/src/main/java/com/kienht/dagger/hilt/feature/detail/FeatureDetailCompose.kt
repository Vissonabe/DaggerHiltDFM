package com.kienht.dagger.hilt.feature.detail

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.state
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import com.kienht.dagger.hilt.feature.FeatureSharedNavViewModel

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