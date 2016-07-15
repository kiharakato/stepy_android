package me.stepy.app.api

import android.os.AsyncTask
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject

class BaseApi {
    companion object {
        const val TAG = "BASE_API"
    }

    init {
        execute()
    }

    fun execute() {
        val task = ApiAsyncTask().execute()
    }

    inner class ApiAsyncTask: AsyncTask<String, Void, ApiEvent>() {
        override fun doInBackground(vararg params: String?): ApiEvent? {
            val request = Request.Builder().url(params[0])

            val client = OkHttpClient.Builder().build()
            val response = client.newCall(request.build()).execute()

            if (!response.isSuccessful) {
                Log.e(TAG, response.body().string())
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
