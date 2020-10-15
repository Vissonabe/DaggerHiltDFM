package com.viswa.feature.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.compose.runtime.Immutable
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class MovieItem(val id: String, val title: String, val imdbRating: Int) : Parcelable

data class MovieCollection(val items: List<MovieItem>)

@Immutable
data class MovieState(val movieCollection: MovieCollection)
