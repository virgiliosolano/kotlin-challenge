package com.arctouch.codechallenge.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arctouch.codechallenge.api.TmdbApi
import com.arctouch.codechallenge.data.Cache
import com.arctouch.codechallenge.model.Movie
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

    private fun initializeMovieData(page: Long, region: String) {
        try {

            compositeDisposable.add(
                    tmdbApi.genres()
                            .subscribeOn(Schedulers.io())
                            .subscribe({
                                if (it != null) {
                                    Cache.cacheGenres(it.genres)
                                    fetchMovies(page, region)
                                }
                            }, {
                                Log.e("MovieNetworkDataError", it.message)
                            })
            )

        } catch (e: java.lang.Exception) {
            Log.e("MovieNetworkDataError", e.message)
        }
    }

    fun fetchMovies(page: Long, region: String) {
        try {

            if (Cache.genres.isEmpty()) {
                initializeMovieData(page, region)
                return
            }

            compositeDisposable.add(
                    tmdbApi.upcomingMovies(page, region)
                            .subscribeOn(Schedulers.io())
                            .subscribe({
                                val moviesWithGenres = it.results.map { movie ->
                                    movie.copy(genres = Cache.genres.filter { movie.genreIds?.contains(it.id) == true })
                                }
                                _moviesResponse.postValue(moviesWithGenres)
                            }, {

                            })
            )
        } catch (e: java.lang.Exception) {
            Log.e("MovieNetworkDataError", e.message)
        }
    }

    fun fetchGenders() {
        compositeDisposable.add(
                tmdbApi.genres()
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            if (it != null) {
                                Cache.cacheGenres(it.genres)
                            }
                        }, {
                            Log.e("MovieNetworkDataError", it.message)
                        })
        )
    }

}