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
import kotlinx.android.synthetic.main.movie_activity.*

class MovieActivity : AppCompatActivity(), MovieAdapter.OnItemClickListener {

    private lateinit var movieViewModel: MovieViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.movie_activity)

        recyclerShimmer.showShimmerAdapter()

        recyclerMovie.layoutManager = GridLayoutManager(this, 3)
        recyclerMovie.setHasFixedSize(true)

        movieViewModel = getMovieViewModel(MovieRepository(TmdbNetwork.create()))

        movieViewModel.movies.observe(this, Observer {
            if (!it.isEmpty()) {
                recyclerMovie.adapter = MovieAdapter(it, this)
            }
            recyclerShimmer.hideShimmerAdapter()
            recyclerShimmer.visibility = View.GONE
        })
    }

    override fun onStop() {
        recyclerShimmer.hideShimmerAdapter()
        super.onStop()
    }

    override fun onItemClicked(movieId: Int) {
        //TODO - FIX MULTIPLE CLICK
        MovieDetailsFragment.newInstance(movieId).show(supportFragmentManager, MovieDetailsFragment::class.java.name)
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