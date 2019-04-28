package com.arctouch.codechallenge.movie

import androidx.lifecycle.ViewModel
import com.arctouch.codechallenge.BuildConfig
import com.arctouch.codechallenge.repository.MovieRepository
import io.reactivex.disposables.CompositeDisposable

class MovieViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val movies by lazy {
        movieRepository.fetchMovies(compositeDisposable, 1, BuildConfig.DEFAULT_REGION)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}