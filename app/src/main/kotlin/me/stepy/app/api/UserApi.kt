package me.stepy.app.api

import okhttp3.FormBody

class UserApi: BaseApi() {

    val url = "${Config.END_POINT}/user"

    fun execute(email: String, name: String) {
        postBody = FormBody.Builder().apply {
            add("email", email)
            add("name", name)
        }
        apiExecute(url)
    }
}