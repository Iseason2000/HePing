package top.iseason.heping.manager

import android.annotation.SuppressLint
import android.content.SharedPreferences

object ConfigManager {
    private lateinit var config: SharedPreferences
    private lateinit var configEditor: SharedPreferences.Editor

    @SuppressLint("CommitPrefEdits")
    @JvmStatic
    fun setSharedPreferences(config: SharedPreferences) {
        this.config = config
        this.configEditor = config.edit()
    }

    @JvmStatic
    fun setString(key: String, data: String) {
        configEditor.putString(key, data)
        configEditor.apply()
    }

    @JvmStatic
    fun setInt(key: String, data: Int) {
        configEditor.putInt(key, data)
        configEditor.apply()
    }

    @JvmStatic
    fun setBoolean(key: String, data: Boolean) {
        configEditor.putBoolean(key, data)
        configEditor.apply()
    }

    @JvmStatic
    fun setLong(key: String, data: Long) {
        configEditor.putLong(key, data)
        configEditor.apply()
    }

    @JvmStatic
    fun setFloat(key: String, data: Float) {
        configEditor.putFloat(key, data)
        configEditor.apply()
    }

    @JvmStatic
    fun getInt(key: String): Int {
        return config.getInt(key, 0)
    }

    @JvmStatic
    fun getString(key: String): String? {
        return config.getString(key, null)
    }

    @JvmStatic
    fun getLong(key: String): Long {
        return config.getLong(key, 0L)
    }

    @JvmStatic
    fun getBoolean(key: String): Boolean {
        return config.getBoolean(key, false)
    }

    @JvmStatic
    fun hasKey(key: String): Boolean {
        return config.contains(key)
    }
}

data class AppSetting(
    val packageName: String,

    )