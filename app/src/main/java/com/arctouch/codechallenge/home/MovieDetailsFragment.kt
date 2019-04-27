package com.arctouch.codechallenge.home

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.api.TmdbApi
import com.arctouch.codechallenge.util.MovieImageUrlBuilder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.movie_details_fragment.*
import kotlinx.android.synthetic.main.movie_item.view.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.reflect.KMutableProperty1

const val MOVIE_ID = "movie_id"

class MovieDetailsFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel: MovieDetailsViewModel
    private lateinit var api: TmdbApi

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        api = Retrofit.Builder()
                .baseUrl(TmdbApi.URL)
                .client(OkHttpClient.Builder().build())
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(TmdbApi::class.java)

        return inflater.inflate(R.layout.movie_details_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MovieDetailsViewModel::class.java)

        api.movie(arguments?.get(MOVIE_ID) as Int, TmdbApi.API_KEY, TmdbApi.DEFAULT_LANGUAGE)
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