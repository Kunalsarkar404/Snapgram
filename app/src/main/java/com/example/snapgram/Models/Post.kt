package com.example.snapgram.Models

class Post {
    var postUrl: String? = null
    var caption: String? = null
    var uid: String? = null
    var time: String? = null
    var likes: Int = 0
    var likedByUser: Boolean = false

    constructor()


    constructor(
        postUrl: String?,
        caption: String?,
        uid: String?,
        time: String?,
        likes: Int,
        likedByUser: Boolean
    ) {
        this.postUrl = postUrl
        this.caption = caption
        this.uid = uid
        this.time = time
        this.likes = likes
        this.likedByUser = likedByUser
    }


}
