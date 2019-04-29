package com.arctouch.codechallenge.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.arctouch.codechallenge.api.TmdbApi
import com.arctouch.codechallenge.model.Movie
import io.reactivex.disposables.CompositeDisposable

class MovieDataSourceFactory(private val apiService : TmdbApi,
                             private val compositeDisposable: CompositeDisposable) :
        DataSource.Factory<Int, Movie>()  {

    val moviesDataSource =  MutableLiveData<MovieNetworkDataSource>()

    override fun create(): DataSource<Int, Movie> {
        val movieDataSource = MovieNetworkDataSource(apiService, compositeDisposable)
        moviesDataSource.postValue(movieDataSource)
        return movieDataSource
    }
}