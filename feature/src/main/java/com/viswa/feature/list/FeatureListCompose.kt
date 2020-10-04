package com.viswa.feature.list

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.viswa.feature.R
import com.viswa.feature.model.CounterState
import com.viswa.feature.model.MovieItem

@Composable
fun textCompose(state : CounterState) {
    Text(text = "${state.counterValue}")
}

fun movieListLoader() {

}

@Composable
fun movieListNoContent() {
    CircularProgressIndicator(modifier = Modifier.height(50.dp).width(50.dp))
}

@Composable
fun moviesList(items : List<MovieItem>, onClickAction : (MovieItem) -> Unit) {
    ScrollableColumn() {
        items.forEachIndexed { _, movieItem ->
            movieItemView(movieItem, onClickAction)
        }
    }
}

@Composable
fun movieItemView(item : MovieItem, onClickAction : (MovieItem) -> Unit) {
    val modifier = Modifier.clickable(onClick = { onClickAction.invoke(item) })
        .padding(16.dp)
        .fillMaxWidth()
        .border(width = 1.dp, color = Color.Blue)
        .background(color = Color.Gray)
    Column(modifier = modifier) {
        Text(text = item.title, fontSize = TextUnit.Sp(16), modifier = Modifier.padding(4.dp))
        Text(text = "Rating is ${item.imdbRating}", fontSize = TextUnit.Sp(12), modifier = Modifier.padding(4.dp))
    }
}