package top.iseason.heping.model

import android.app.AppOpsManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.iseason.heping.manager.AppInfo
import top.iseason.heping.manager.ConfigManager
import top.iseason.heping.manager.ModelManager.queryUsageStatsForDays
import top.iseason.heping.manager.hasPermission
import top.iseason.heping.utils.Util

class AppViewModel : ViewModel() {
    private val _healthViewState: MutableStateFlow<HealthViewState> =
        MutableStateFlow(HealthViewState())
    val healthViewState = _healthViewState.asStateFlow()

    private val _isRefreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    val _isDarkMod: MutableStateFlow<Int> = MutableStateFlow(0)
    val isDarkMod = _isDarkMod.asStateFlow()

    private var _pastUsageList: MutableStateFlow<List<List<AppInfo>>> =
        MutableStateFlow(emptyList())
    private val pastUsageList = _pastUsageList.asStateFlow()

    private fun emitState(viewState: HealthViewState) {
        _healthViewState.value = viewState
    }

    var initPage = 0
    var isInit = false
    fun refresh() {
        updateAppInfo()
    }

    fun updateAppInfo() {
        Thread {
            val yesterday = Util.getDate(-1)
            val yesterdayLong = ConfigManager.getLong("Yesterday")
            if (yesterdayLong != yesterday.timeInMillis) {
                val totalTime = queryUsageStatsForDays(1).sortedByDescending { it.getTotalTime() }
                    .getTotalTime()
                ConfigManager.setLong("YesterdayUseTime", totalTime)
                ConfigManager.setLong("Yesterday", yesterday.timeInMillis)
            }
            emitState(
                healthViewState.value.copy(appInfo = queryUsageStatsForDays(0).sortedByDescending { it.getTotalTime() })
            )
        }.start()
    }

    fun loadPastUsage() {
        if (isInit) return
        if (hasPermission(AppOpsManager.OPSTR_GET_USAGE_STATS))
            viewModelScope.launch {
                Thread {
                    val mutableListOf = mutableListOf<List<AppInfo>>()
                    for (i in 1..7) {
                        mutableListOf.add(queryUsageStatsForDays(i).sortedByDescending {
                            it.getTotalTime()
                        })
                    }
                    _pastUsageList.value = mutableListOf
                    isInit = true
                }.start()
            }
    }

    fun getPastUsage() = pastUsageList

    fun setSelectedApp(index: Int) {
        viewModelScope.launch {
            emitState(healthViewState.value.copy(selectApp = index))
        }
    }

    fun getAppInfoForAllDays(packageName: String): List<AppInfo> {
        val mutableListOf = mutableListOf<AppInfo>()
        for (appInfo in healthViewState.value.appInfo) {
            if (appInfo.packageName == packageName) {
                mutableListOf.add(appInfo)
                break
            }
        }
        for (list in _pastUsageList.value) {
            for (appInfo in list) {
                if (appInfo.packageName == packageName) {
                    mutableListOf.add(appInfo)
                    break
                }
            }
        }
        return mutableListOf
    }
}

data class HealthViewState(
    val appInfo: List<AppInfo> = emptyList(),
    val selectApp: Int = -1
)

fun List<AppInfo>.getTotalTime(): Long {
    var total = 0L
    forEach {
        total += it.getTotalTime()
    }
    return total
}

