package top.iseason.heping.manager

import android.annotation.SuppressLint
import android.content.SharedPreferences

object ConfigManager {
    private var config: SharedPreferences? = null
    private var configEditor: SharedPreferences.Editor? = null

    @SuppressLint("CommitPrefEdits")
    @JvmStatic
    fun setSharedPreferences(config: SharedPreferences) {
        this.config = config
        this.configEditor = config.edit()
        if (!hasKey("App-SetupTime")) {
            setLong("App-SetupTime", System.currentTimeMillis())
        }
        if (!hasKey("MyTheme-AutoSwitch")) {
            setBoolean("MyTheme-AutoSwitch", true)
        }

    }

    @JvmStatic
    fun setString(key: String, data: String) {
        configEditor?.putString(key, data)
        configEditor?.apply()
    }

    @JvmStatic
    fun setInt(key: String, data: Int) {
        configEditor?.putInt(key, data)
        configEditor?.apply()
    }

    @JvmStatic
    fun setBoolean(key: String, data: Boolean) {
        configEditor?.putBoolean(key, data)
        configEditor?.apply()
    }

    @JvmStatic
    fun setLong(key: String, data: Long) {
        configEditor?.putLong(key, data)
        configEditor?.apply()
    }

    @JvmStatic
    fun setFloat(key: String, data: Float) {
        configEditor?.putFloat(key, data)
        configEditor?.apply()
    }

    @JvmStatic
    fun getInt(key: String): Int {
        return config?.getInt(key, 0) ?: 0
    }

    @JvmStatic
    fun getString(key: String): String? {
        return config?.getString(key, null)
    }

    @JvmStatic
    fun getLong(key: String): Long {
        return config?.getLong(key, 0L) ?: 0L
    }

    @JvmStatic
    fun getBoolean(key: String): Boolean {
        return config?.getBoolean(key, false) ?: false
    }

    @JvmStatic
    fun hasKey(key: String): Boolean {
        return config?.contains(key) ?: false
    }
}
