package com.viswa.app.movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Providers
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.viswa.app.movie.api.AmbientMovieApi
import com.viswa.app.movie.api.MovieApi
import com.viswa.app.movie.api.baseOkHttpClient
import com.viswa.app.movie.ui.DogfoodTheme
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MovieFragment : Fragment() {

    private val retrofit: Retrofit = Retrofit
        .Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .client(baseOkHttpClient)
        .addConverterFactory(
            MoshiConverterFactory.create(
                Moshi.Builder()
                    .addLast(KotlinJsonAdapterFactory())
                    .build()
            )
        )
        .build()

    private val api = retrofit.create(MovieApi::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                DogfoodTheme {
                    Providers(AmbientMovieApi provides api) {
                        // A surface container using the 'background' color from the theme
                        Surface(color = MaterialTheme.colors.background) {
                            Screen()
                        }
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MovieFragment()
    }
}