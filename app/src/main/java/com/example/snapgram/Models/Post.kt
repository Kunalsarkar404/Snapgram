package com.example.snapgram.Models

class Post {
    var postUrl: String? = null
    var caption: String? = null
    var uid: String? = null
    var time: String? = null

    constructor()
    constructor(postUrl: String, caption: String) {
        this.postUrl = postUrl
        this.caption = caption
    }

    constructor(postUrl: String?, caption: String?, uid: String?, time: String?) {
        this.postUrl = postUrl
        this.caption = caption
        this.uid = uid
        this.time = time
    }


}
