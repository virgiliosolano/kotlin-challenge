package com.arctouch.codechallenge.movie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.repository.NetworkState
import com.arctouch.codechallenge.repository.NetworkState.Status
import com.arctouch.codechallenge.util.MovieImageUrlBuilder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.movie_item.view.*
import kotlinx.android.synthetic.main.network_state_item.view.*

class MoviePageListAdapter(private val onItemClickListener: MoviePageListAdapter.OnItemClickListener) :
        PagedListAdapter<Movie, RecyclerView.ViewHolder>(MovieDiffCallback()) {

    val DATA_VIEW_TYPE = 1
    val NETWORK_VIEW_TYPE = 2

    private var networkState: NetworkState? = null

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(movie: Movie?, onItemClickListener: MoviePageListAdapter.OnItemClickListener) {
            itemView.titleTextView.text = movie?.title
            itemView.releaseDateTextView.text = movie?.releaseDate

            Glide.with(itemView)
                .load(movie?.posterPath?.let { MovieImageUrlBuilder().buildPosterUrl(it) })
                .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                .into(itemView.posterImageView)

            itemView.setOnClickListener {
                onItemClickListener.onItemClicked(movie?.id)
            }
        }
    }

    class NetworkStateItemViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        fun bind(networkState: NetworkState?) {
            if (networkState != null && networkState.status == Status.RUNNING) {
                itemView.progress.visibility = View.VISIBLE
            } else {
                itemView.progress.visibility = View.GONE
            }

            if (networkState != null && networkState.status == Status.FAILED) {
                itemView.textErrorMessage.visibility = View.VISIBLE
                itemView.textErrorMessage.setText(networkState.message)
            } else {
                itemView.textErrorMessage.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view: View
        if (viewType == DATA_VIEW_TYPE) {
            view = layoutInflater.inflate(R.layout.movie_item, parent, false)
            return MovieViewHolder(view)
        } else {
            view = layoutInflater.inflate(R.layout.network_state_item, parent, false)
            return NetworkStateItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == DATA_VIEW_TYPE) {
            (holder as MovieViewHolder).bind(getItem(position), onItemClickListener)
        } else {
            (holder as NetworkStateItemViewHolder).bind(networkState)
        }
    }

    override fun getItemCount() : Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    private fun hasExtraRow(): Boolean {
        return networkState != null && networkState != NetworkState.LOADED
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            NETWORK_VIEW_TYPE
        } else {
            DATA_VIEW_TYPE
        }
    }

    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }

    fun setNetworkState(newNetworkState: NetworkState) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(movieId: Int?)
    }
}