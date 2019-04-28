package com.arctouch.codechallenge.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.arctouch.codechallenge.BuildConfig
import com.arctouch.codechallenge.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

private val POSTER_URL = "https://image.tmdb.org/t/p/w342"
private val BACKDROP_URL = "https://image.tmdb.org/t/p/w780"

class MovieImageUrlBuilder {

    fun buildPosterUrl(posterPath: String?): String {
        return POSTER_URL + posterPath + "?api_key=" + BuildConfig.API_KEY
    }

    fun buildBackdropUrl(backdropPath: String?): String {
        return BACKDROP_URL + backdropPath + "?api_key=" + BuildConfig.API_KEY
    }

    companion object {
        @BindingAdapter("imageUrl")
        @JvmStatic
        fun loadImage(imageView: ImageView, imageUrl: String?) {
            Glide.with(imageView.context)
                    .load(imageUrl)
                    .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                    .into(imageView)
        }
    }
}