package com.arctouch.codechallenge.repository

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arctouch.codechallenge.api.TmdbApi
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.util.MovieImageUrlBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * MovieNetworkData
 *
 * Responsible to make network request
 */
class MovieNetworkData(private val tmdbApi: TmdbApi, private val compositeDisposable: CompositeDisposable) {

    private val _moviesResponse = MutableLiveData<List<Movie>>()
    val moviesResponse: LiveData<List<Movie>>
        get() = _moviesResponse

    private val _movieResponse = MutableLiveData<Movie>()
    val movieResponse: LiveData<Movie>
        get() = _movieResponse

    fun fetchMovies(page: Long, region: String) {
        try {
            compositeDisposable.add(
                    tmdbApi.upcomingMovies(page, region)
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

}