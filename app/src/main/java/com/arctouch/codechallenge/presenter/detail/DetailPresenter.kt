package com.arctouch.codechallenge.presenter.detail

import com.arctouch.codechallenge.model.Movie

interface DetailPresenter {
    fun setToolbar(title: String)
    fun getExtras()
    fun setLayout(movie: Movie)
    fun setImages(movie: Movie)
    fun setGenresTextView(movie: Movie)
    fun setOtherTexts(movie: Movie)
}