package me.stepy.app.api

import android.os.AsyncTask
import me.stepy.app.util.SLog
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject

abstract class BaseApi {
    companion object {
        const val TAG = "BASE_API"
        const val POST = "POST"
        const val GET = "GET"
    }

    enum class Methods() {
        POST, GET
    }

    var task: AsyncTask<String, Void, ApiEvent>? = null
    var method: Methods = Methods.GET
    open var postBody: FormBody.Builder? = null

    open fun apiExecute(url: String) {
        task = ApiAsyncTask().execute(url)
    }

    fun cancel() {
        task?.let { it.cancel(true) }
    }

    inner class ApiAsyncTask: AsyncTask<String, Void, ApiEvent>() {
        override fun doInBackground(vararg params: String?): ApiEvent? {
            val request = Request.Builder().url(params[0])

            when(method) {
                Methods.GET -> {
                    request.get()
                }
                Methods.POST -> {
                    postBody?.let {
                        request.post(it.build())
                    }
                }
            }

            val client = OkHttpClient.Builder().build()
            val response = client.newCall(request.build()).execute()

            if (isCancelled) {
                SLog.e(TAG, "Api Cancel")
                return ApiEvent(600, JSONObject("{ body: { error: cancel } }"))
            }

            if (!response.isSuccessful) {
                SLog.e(TAG, response.body().string())
            }

            return ApiEvent(response)
        }

        override fun onPostExecute(result: ApiEvent?) {
            super.onPostExecute(result)
        }
    }

    class ApiEvent {

        var statusCode = -1
        var body: JSONObject? = null

        constructor(response: Response){
            statusCode = response.code()
            body = JSONObject(response.body().string())
        }

        constructor(statusCode: Int, body: JSONObject) {
            this.statusCode = statusCode
            this.body = body
        }

    }
}
