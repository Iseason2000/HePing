package top.iseason.heping.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.iseason.heping.model.ModelManager.queryUsageStatsForDays
import top.iseason.heping.ui.screen.health.hasPermission

class AppViewModel : ViewModel() {
    private val _viewState: MutableStateFlow<AppViewState> = MutableStateFlow(AppViewState())
    private val _isRefreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val viewState = _viewState.asStateFlow()
    val isRefreshing = _isRefreshing.asStateFlow()
    private var isInit = false
    private var pastUsageList: List<List<AppInfo>> = emptyList()
    private fun emitState(viewState: AppViewState) {
        _viewState.value = viewState
    }

    fun refresh() {
        updateAppInfo()
    }

    fun updateAppInfo() {
        Thread {
            emitState(
                viewState.value.copy(appInfo = queryUsageStatsForDays(0).sortedByDescending { it.getTotalTime() },
                    yesterdayAppInfo = queryUsageStatsForDays(1).sortedByDescending { it.getTotalTime() })
            )
        }.start()
    }

    fun loadPastUsage() {
        if (isInit) return
        if (hasPermission())
            viewModelScope.launch {
                Thread {
                    val mutableListOf = mutableListOf<List<AppInfo>>()
                    for (i in 1..6) {
                        mutableListOf.add(queryUsageStatsForDays(i).sortedByDescending {
                            it.getTotalTime()
                        })
                    }
                    pastUsageList = mutableListOf
                    isInit = true
                }.start()

            }
    }

    fun getPastUsage() = pastUsageList

    fun setSelectedApp(index: Int) {
        viewModelScope.launch {
            emitState(viewState.value.copy(selectApp = index))
        }
    }

    fun getAppInfoForAllDays(packageName: String): List<AppInfo> {
        val mutableListOf = mutableListOf<AppInfo>()
        for (appInfo in viewState.value.appInfo) {
            if (appInfo.packageName == packageName) {
                mutableListOf.add(appInfo)
                break
            }
        }
        for (list in pastUsageList) {
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

data class AppViewState(
    val appInfo: List<AppInfo> = emptyList(),
    val yesterdayAppInfo: List<AppInfo> = emptyList(),
    val selectApp: Int = -1
)

fun List<AppInfo>.getTotalTime(): Long {
    var total = 0L
    forEach {
        total += it.getTotalTime()
    }
    return total
}

