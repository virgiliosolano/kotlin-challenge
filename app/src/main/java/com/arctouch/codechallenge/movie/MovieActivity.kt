package com.arctouch.codechallenge.movie

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.api.TmdbNetwork
import com.arctouch.codechallenge.repository.MovieRepository
import com.arctouch.codechallenge.repository.NetworkState
import kotlinx.android.synthetic.main.movie_activity.*

class MovieActivity : AppCompatActivity(), MoviePageListAdapter.OnItemClickListener {

    private lateinit var movieViewModel: MovieViewModel
    private lateinit var moviePageListAdapter: MoviePageListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.movie_activity)

        moviePageListAdapter = MoviePageListAdapter( this)
        recyclerMovie.layoutManager = GridLayoutManager(this, 3)
        recyclerMovie.setHasFixedSize(true)
        recyclerMovie.adapter = moviePageListAdapter

        movieViewModel = getMovieViewModel(MovieRepository(TmdbNetwork.create()))
        movieViewModel.moviePagedList.observe(this, Observer {
            moviePageListAdapter.submitList(it)
        })

        movieViewModel.networkState.observe(this, Observer {
            if (movieViewModel.listIsEmpty() && it == NetworkState.LOADING) {
                recyclerShimmer.showShimmerAdapter()
            }

            if (!movieViewModel.listIsEmpty()) {
                recyclerShimmer.hideShimmerAdapter()
                recyclerShimmer.visibility = View.GONE
                moviePageListAdapter.setNetworkState(it)
            }
        })
    }

    override fun onStop() {
        recyclerShimmer.hideShimmerAdapter()
        super.onStop()
    }

    override fun onItemClicked(movieId: Int?) {
        movieId?.let {
            MovieDetailsFragment.newInstance(movieId).show(supportFragmentManager, MovieDetailsFragment::class.java.name)
        }
    }

    private fun getMovieViewModel(movieRepository: MovieRepository): MovieViewModel {
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MovieViewModel(movieRepository) as T
            }
        })[MovieViewModel::class.java]
    }
}