package com.arctouch.codechallenge.movie

import androidx.lifecycle.ViewModel
import com.arctouch.codechallenge.repository.MovieRepository
import io.reactivex.disposables.CompositeDisposable

class MovieViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val moviePagedList by lazy {
        movieRepository.fetchMoviePagedList(compositeDisposable)
    }

    val  networkState by lazy {
        movieRepository.getNetworkState()
    }

    fun listIsEmpty(): Boolean {
        return moviePagedList.value?.isEmpty() ?: true
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}