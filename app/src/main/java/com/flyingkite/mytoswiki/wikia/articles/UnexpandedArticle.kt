package com.flyingkite.mytoswiki.wikia.articles

import com.google.gson.annotations.SerializedName

open class UnexpandedArticle (
    @SerializedName("id")
    val id: Int = 0
    ,
    @SerializedName("title")
    val title: String? = null
    ,
    @SerializedName("url")
    val url: String? = null
    ,
    @SerializedName("ns")
    val nameSpace: Int = 0
){
    // Which one is better for us?
    // data class is final and cannot extend to change name when name collision
    // open class? data class? copy constructor?
    /*
    @SerializedName("id")
    val id: Int = 0

    @SerializedName("title")
    val title: String? = null

    @SerializedName("url")
    val url: String? = null

    @SerializedName("ns")
    val nameSpace: Int = 0
    */

    override fun toString(): String {
        return "(id = $id, title = $title, url = $url, ns = $nameSpace)"
    }

    fun copy(): UnexpandedArticle {
        return UnexpandedArticle(id, title, url, nameSpace)
    }
}