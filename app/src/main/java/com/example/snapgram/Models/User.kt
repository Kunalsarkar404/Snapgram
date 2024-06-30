package com.example.snapgram.Models

class User {
    var email: String? = null
    var name: String? = null
    var username: String? = null
    var password: String? = null
    var image: String? = null // Added image property

    constructor()

    constructor(email: String?, name: String?, username: String?, password: String?, image: String?) {
        this.email = email
        this.name = name
        this.username = username
        this.password = password
        this.image = image // Initialize image property
    }

    constructor(email: String?, password: String?) {
        this.email = email
        this.password = password
    }
}
