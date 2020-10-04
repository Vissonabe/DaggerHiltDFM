package com.viswa.feature.list

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.viswa.core.UserModel
import com.viswa.core.di.UserModelFeatureQualifier
import com.viswa.feature.model.MovieCollection
import com.viswa.feature.model.MovieItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author kienht
 * @since 15/09/2020
 */
class FeatureListViewModel @ViewModelInject constructor(
    @UserModelFeatureQualifier val userModel: UserModel,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var cachedMovieList : MovieCollection? = null

    fun getMovieCollectionData() : LiveData<MovieCollection> = liveData {
        if(cachedMovieList == null) {
            val data = getDummyMovieCollection()
            cachedMovieList = data
        }
        cachedMovieList?.let {
            emit(it)
        }
    }

    private suspend fun getDummyMovieCollection() : MovieCollection{
        delay(2000)
        val items = mutableListOf<MovieItem>()
        for (i in 0..100){
            items.add(MovieItem(id = i.toString(), title = "title movie $i", imdbRating = i))
        }
        return MovieCollection(items)
    }
}