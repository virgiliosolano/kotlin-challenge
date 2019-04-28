package com.arctouch.codechallenge.movie

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.api.TmdbApi
import com.arctouch.codechallenge.util.MovieImageUrlBuilder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.movie_details_fragment.*

class MovieDetailsFragment : BottomSheetDialogFragment() {

    private val MOVIE_ID = "movie_id"

    private lateinit var tmdbApiService: TmdbApi
    private lateinit var viewModel: MovieDetailsViewModel
    private lateinit var apiService: TmdbApi

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //tmdbApiService = TmdbApi.create()

        return inflater.inflate(R.layout.movie_details_fragment, container, false)
    }

    @SuppressLint("CheckResult")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MovieDetailsViewModel::class.java)

        tmdbApiService.movie(arguments?.get(MOVIE_ID) as Int)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {

                    textMovieName.text = it.title
                    textMovieDescription.text = it.overview
                    textMovieDate.text = it.releaseDate
                    textMovieGenders.text = it.genres?.joinToString { it.name }

                    context?.let { context ->
                        Glide.with(context)
                                .load(it.backdropPath?.let { MovieImageUrlBuilder().buildBackdropUrl(it) })
                                .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                                .into(imageMovieBackdrop)
                    }

                    context?.let { context ->
                        Glide.with(context)
                                .load(it.posterPath?.let { MovieImageUrlBuilder().buildPosterUrl(it) })
                                .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                                .into(imageMoviePoster)
                    }
                }
    }

    companion object {
        fun newInstance(movieId: Int): MovieDetailsFragment =
                MovieDetailsFragment().apply {
                    arguments = Bundle().apply {
                        putInt(MOVIE_ID, movieId)
                    }
                }
    }
}