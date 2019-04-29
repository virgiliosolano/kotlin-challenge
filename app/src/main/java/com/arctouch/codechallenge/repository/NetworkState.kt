package com.arctouch.codechallenge.repository

class NetworkState(val status: Status, val message: String) {

    enum class Status {
        RUNNING,
        SUCCESS,
        FAILED
    }

    companion object {

        val LOADED: NetworkState
        val LOADING: NetworkState
        val FAILED: NetworkState

        init {
            LOADED = NetworkState(Status.SUCCESS, "Success")
            LOADING = NetworkState(Status.RUNNING, "Running")
            FAILED = NetworkState(Status.FAILED, "Failed")
        }
    }
}