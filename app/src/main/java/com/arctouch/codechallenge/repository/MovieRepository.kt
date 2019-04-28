package com.arctouch.codechallenge.repository

import androidx.lifecycle.LiveData
import com.arctouch.codechallenge.api.TmdbApi
import com.arctouch.codechallenge.model.Movie
import io.reactivex.disposables.CompositeDisposable

class MovieRepository(private val tmdbApi: TmdbApi) {

    private lateinit var movieNetworkData: MovieNetworkData

    fun fetchMovies(compositeDisposable: CompositeDisposable, page: Long, region: String) : LiveData<List<Movie>> {
        movieNetworkData = MovieNetworkData(tmdbApi, compositeDisposable)
        movieNetworkData.fetchMovies(page, region)
        return movieNetworkData.moviesResponse
    }

}