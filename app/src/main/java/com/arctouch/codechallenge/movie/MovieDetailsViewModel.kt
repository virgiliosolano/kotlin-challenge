package com.arctouch.codechallenge.movie

import androidx.lifecycle.ViewModel
import com.arctouch.codechallenge.repository.MovieRepository
import io.reactivex.disposables.CompositeDisposable

class MovieDetailsViewModel(private val movieRepository: MovieRepository, private val movieId: Int) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val movieDetails by lazy {
        movieRepository.fetchMovieDetails(compositeDisposable, movieId)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}
