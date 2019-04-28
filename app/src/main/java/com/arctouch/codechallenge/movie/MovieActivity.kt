package com.arctouch.codechallenge.movie

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.api.TmdbNetwork
import com.arctouch.codechallenge.repository.MovieRepository
import kotlinx.android.synthetic.main.home_activity.*

class MovieActivity : AppCompatActivity(), MovieAdapter.OnItemClickListener {

    private lateinit var movieViewModel: MovieViewModel

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)

        movieViewModel = getMovieViewModel(MovieRepository(TmdbNetwork.create()))

        movieViewModel.movies.observe(this, Observer {
            if (!it.isEmpty()) {
                recyclerView.adapter = MovieAdapter(it, this)
            }
            progressBar.visibility = View.GONE
        })
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