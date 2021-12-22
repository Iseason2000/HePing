package top.iseason.heping.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.iseason.heping.model.ModelManager.queryUsageStatsAllDay

class AppViewModel : ViewModel() {
    private val _viewState: MutableStateFlow<AppViewState> = MutableStateFlow(AppViewState())
    private val _isRefreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val viewState = _viewState.asStateFlow()
    val isRefreshing = _isRefreshing.asStateFlow()

    private fun emitState(viewState: AppViewState) {
        _viewState.value = viewState
    }

    fun refresh() {
        updateAppInfo()
    }

    fun updateAppInfo() {
        viewModelScope.launch {
            Thread {
                emitState(viewState.value.copy(appInfo = queryUsageStatsAllDay().sortedByDescending { it.getTotalTime() }))
            }.start()
        }
    }

    fun setSelectedApp(index: Int) {
        viewModelScope.launch {
            emitState(viewState.value.copy(selectApp = index))
        }
    }
}

data class AppViewState(
    val appInfo: List<AppInfo> = emptyList(),
    val selectApp: Int = -1
)

