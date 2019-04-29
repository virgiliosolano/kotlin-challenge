package com.arctouch.codechallenge.movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.api.TmdbNetwork
import com.arctouch.codechallenge.databinding.MovieDetailsFragmentBinding
import com.arctouch.codechallenge.repository.MovieRepository
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.movie_details_fragment.*

class MovieDetailsFragment : BottomSheetDialogFragment() {

    private val MOVIE_ID = "movie_id"
    private lateinit var moviewDetailsBinding: MovieDetailsFragmentBinding
    private lateinit var movieDetailsViewModel: MovieDetailsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        moviewDetailsBinding = DataBindingUtil.inflate(inflater,
                R.layout.movie_details_fragment, container, false)

        return moviewDetailsBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        containerDetailsShimmerLayout.startShimmer()

        movieDetailsViewModel = getMovieViewModel(MovieRepository(TmdbNetwork.create()),
                arguments?.get(MOVIE_ID) as Int)

        movieDetailsViewModel.movieDetails.observe(this, Observer {
            moviewDetailsBinding.movie = it
            containerMovieDetails.visibility = View.VISIBLE
            containerDetailsShimmerLayout.stopShimmer()
            containerDetailsShimmerLayout.visibility = View.GONE
        })
    }

    override fun onStop() {
        containerDetailsShimmerLayout.stopShimmer()
        super.onStop()
    }

    companion object {
        fun newInstance(movieId: Int): MovieDetailsFragment =
                MovieDetailsFragment().apply {
                    arguments = Bundle().apply {
                        putInt(MOVIE_ID, movieId)
                    }
                }
    }

    private fun getMovieViewModel(movieRepository: MovieRepository, movieId: Int): MovieDetailsViewModel {
        return ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MovieDetailsViewModel(movieRepository, movieId) as T
            }
        })[MovieDetailsViewModel::class.java]
    }
}