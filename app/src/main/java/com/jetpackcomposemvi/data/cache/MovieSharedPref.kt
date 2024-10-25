package com.jetpackcomposemvi.data.cache

import android.content.Context
import javax.inject.Inject

class MovieSharedPref @Inject constructor(private val applicationContext: Context) {
    private val fileName = "MOVIE_LIST"
    private val sharedPref = applicationContext.getSharedPreferences(fileName, Context.MODE_PRIVATE)

    fun updateMovieList(pageNumber: String, movieListStr: String){
        with(sharedPref.edit()) {
            putString(pageNumber, movieListStr)
            apply()
        }
    }

    fun getMovieList(pageNumber: String): String? {
        return sharedPref.getString(pageNumber,"")
    }
}