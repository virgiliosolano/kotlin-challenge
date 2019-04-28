package com.arctouch.codechallenge.repository

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arctouch.codechallenge.api.TmdbApi
import com.arctouch.codechallenge.data.Cache
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
                                Log.e("MovieNetworkDataError", it.message)
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