package com.arctouch.codechallenge.repository

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.arctouch.codechallenge.BuildConfig
import com.arctouch.codechallenge.api.TmdbApi
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.util.MovieImageUrlBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * MovieNetworkDataSource
 *
 * Responsible to make network request
 */
class MovieNetworkDataSource(private val tmdbApi: TmdbApi, private val compositeDisposable: CompositeDisposable) : PageKeyedDataSource<Int, Movie>() {

    private var page: Int = 1

    val networkState: MutableLiveData<NetworkState> = MutableLiveData()

    private val _moviesResponse = MutableLiveData<List<Movie>>()
    val moviesResponse: LiveData<List<Movie>>
        get() = _moviesResponse

    private val _movieResponse = MutableLiveData<Movie>()
    val movieResponse: LiveData<Movie>
        get() = _movieResponse

    fun fetchMovies() {
        try {
            compositeDisposable.add(
                    tmdbApi.upcomingMovies(page, BuildConfig.DEFAULT_REGION)
                            .subscribeOn(Schedulers.io())
                            .subscribe({
                                _moviesResponse.postValue(it.results)
                            }, {
                                Log.e("MovieNetworkDataError", it.message)
                            })
            )
        } catch (e: java.lang.Exception) {
            Log.e("MovieNetworkDataError", e.message)
        }
    }

    @SuppressLint("CheckResult")
    fun fetchMovieDetails(movieId: Int) {

        try {
            tmdbApi.movie(movieId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        it.genresText = it.genres?.joinToString { it.name }
                        it.backdropUrl = MovieImageUrlBuilder().buildBackdropUrl(it.backdropPath)
                        it.posterUrl = MovieImageUrlBuilder().buildPosterUrl(it.posterPath)
                        _movieResponse.postValue(it)
                    },{
                        Log.e("MovieNetworkDataError", it.message)
                    })
        } catch (e: Exception) {
            Log.e("MovieNetworkDataError", e.message)
        }
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Movie>) {
        networkState.postValue(NetworkState.LOADING)
        compositeDisposable.add(
                tmdbApi.upcomingMovies(page, BuildConfig.DEFAULT_REGION)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            callback.onResult(it.results, null, page+1)
                            networkState.postValue(NetworkState.LOADED)
                        }, {
                            networkState.postValue(NetworkState(NetworkState.Status.FAILED, "Error to recovery data"))
                            Log.e("MovieNetworkDataError", it.message)
                        })
                )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        networkState.postValue(NetworkState.LOADING)
        compositeDisposable.add(
                tmdbApi.upcomingMovies(params.key, BuildConfig.DEFAULT_REGION)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                                if (it.totalPages >= params.key) {
                                    callback.onResult(it.results, params.key + 1)
                                    networkState.postValue(NetworkState.LOADED)
                                } else {
                                    networkState.postValue(NetworkState(NetworkState.Status.FAILED, ""))
                                }
                            }, {
                                networkState.postValue(NetworkState(NetworkState.Status.FAILED, "Error to recovery data"))
                                Log.e("MovieDataSource", it.message)
                        }
               )
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {}
}