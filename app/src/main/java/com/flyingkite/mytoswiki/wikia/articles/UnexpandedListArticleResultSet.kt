package com.flyingkite.mytoswiki.wikia.articles

import com.google.gson.annotations.SerializedName

open class UnexpandedListArticleResultSet (
    @SerializedName("items")
    val items: Array<UnexpandedArticle>? = null
    ,
    @SerializedName("offset")
    val offset: String? = null
    ,
    @SerializedName("basepath")
    val basePath: String? = null
){
    /*
    @SerializedName("items")
    val items: Array<UnexpandedArticle>? = null,

    @SerializedName("offset")
    val offset: String? = null,

    @SerializedName("basepath")
    val basePath: String? = null
    */

    override fun toString(): String {
        val size = items?.size ?: "N/A"

        return "($size items, offset = $offset, basePath = $basePath)"
    }
}

// Should we use this?  But data class is final ...
/*
open class UnexpandedListArticleResultSet(
        @SerializedName("items")
        private val items: Array<UnexpandedArticle>? = null,

        @SerializedName("offset")
        private val offset: String? = null,

        @SerializedName("basepath")
        private val basePath: String? = null
){

    override fun toString(): String {
        val size = items?.size ?: "N/A"
        return "($size items, offset = $offset, basePath = $basePath)"
    }
}
*/