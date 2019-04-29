package com.arctouch.codechallenge.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.arctouch.codechallenge.api.TmdbApi
import com.arctouch.codechallenge.model.Movie
import io.reactivex.disposables.CompositeDisposable

class MovieRepository(private val tmdbApi: TmdbApi) {

    private lateinit var moviePagedList: LiveData<PagedList<Movie>>
    private lateinit var moviesDataSourceFactory: MovieDataSourceFactory
    private lateinit var movieNetworkDataSource: MovieNetworkDataSource

    fun fetchMovieDetails(compositeDisposable: CompositeDisposable, movieId: Int) : LiveData<Movie> {
        movieNetworkDataSource = MovieNetworkDataSource(tmdbApi, compositeDisposable)
        movieNetworkDataSource.fetchMovieDetails(movieId)
        return movieNetworkDataSource.movieResponse
    }

    fun fetchMoviePagedList (compositeDisposable: CompositeDisposable) : LiveData<PagedList<Movie>> {
        moviesDataSourceFactory = MovieDataSourceFactory(tmdbApi, compositeDisposable)

        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(10)
                .build()

        moviePagedList = LivePagedListBuilder(moviesDataSourceFactory, config).build()

        return moviePagedList
    }

    fun getNetworkState(): LiveData<NetworkState> {
        return Transformations.switchMap<MovieNetworkDataSource, NetworkState>(
                moviesDataSourceFactory.moviesDataSource, MovieNetworkDataSource::networkState)
    }
}




