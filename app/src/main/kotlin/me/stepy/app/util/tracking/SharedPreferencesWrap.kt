package me.stepy.app.util.tracking

import android.util.Base64
import android.app.Activity
import android.content.SharedPreferences
import me.stepy.app.App

object SharedPreferencesWrap {

    const val KEY_LOGIN = "login"

    val localStorage: SharedPreferences
        get() {
            val packageName = App.getInstance().packageName
            return App.getInstance().getSharedPreferences(packageName, Activity.MODE_PRIVATE)
        }

    /**
     * 情報編集用のエディタオブジェクトの取得

     * @return
     */
    val editor: SharedPreferences.Editor
        get() = localStorage.edit()

    /**
     * 項目の有無

     * @param key
     * *
     * @return
     */
    operator fun contains(key: String): Boolean {
        return localStorage.contains(key)
    }

    /**
     * 真理値の取得

     * @param key
     * *
     * @return
     */
    fun getBoolean(key: String): Boolean {
        return localStorage.getBoolean(key, false)
    }

    /**
     * 真理値の設定

     * @param key
     * *
     * @param value
     */
    fun setBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value).commit()
    }

    /**
     * 整数型情報の取得

     * @param key
     * *
     * @return
     */
    fun getInt(key: String): Int {
        return localStorage.getInt(key, 0)
    }

    /**
     * 整数型情報の設定

     * @param key
     * *
     * @param value
     */
    fun setInt(key: String, value: Int) {
        editor.putInt(key, value).commit()
    }

    /**
     * 長整数型情報の取得

     * @param key
     * *
     * @return
     */
    fun getLong(key: String): Long {
        return localStorage.getLong(key, 0)
    }

    /**
     * 長整数型情報の設定

     * @param key
     * *
     * @param value
     */
    fun setLong(key: String, value: Long) {
        editor.putLong(key, value).commit()
    }

    /**
     * 浮動小数点情報の取得

     * @param key
     * *
     * @return
     */
    fun getFloat(key: String): Float {
        return localStorage.getFloat(key, 0.0f)
    }

    /**
     * 浮動小数点情報の設定

     * @param key
     * *
     * @param value
     */
    fun setFloat(key: String, value: Float) {
        editor.putFloat(key, value).commit()
    }

    /**
     * 文字列情報を取得

     * @param key
     * *
     * @return
     */
    fun getString(key: String): String {
        return localStorage.getString(key, "")
    }

    /**
     * 文字列情報を保存

     * @param key
     * *
     * @param value
     */
    fun setString(key: String, value: String) {
        editor.putString(key, value).commit()
    }


    /**
     * Byte配列を取得

     * @param key
     */
    fun getByte(key: String): ByteArray {
        val srcValue = getString(key)
        val array = Base64.decode(srcValue, Base64.DEFAULT)

        return array
    }

    /**
     * Byte配列を保存

     * @param key
     * *
     * @param values
     */
    fun setByte(key: String, values: ByteArray) {
        val convertValue = Base64.encodeToString(values, Base64.DEFAULT)
        setString(key, convertValue)
    }

    /**
     * 設定内容をすべて削除する
     */
    fun clear() {
        editor.clear().commit()
    }

    /**
     * 指定した情報の削除

     * @param key
     * *
     * @return
     */
    fun remove(key: String): Boolean {
        return localStorage.edit().remove(key).commit()
    }
}
