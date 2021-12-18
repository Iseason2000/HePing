package top.iseason.heping.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class AppViewModel : ViewModel() {
    private val _viewState: MutableStateFlow<AppViewState> = MutableStateFlow(AppViewState())
    val viewState = _viewState.asStateFlow()
    fun emitState(viewState: AppViewState) {
        _viewState.value = viewState
    }

    fun updateAppInfo() {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val start = calendar.timeInMillis
            calendar.add(Calendar.DATE, 1)
            val end = calendar.timeInMillis
            emitState(
                viewState.value.copy(appInfo = ModelManager.queryUsageStatistics(
                    start,
                    end
                ).values.sortedByDescending { it.useTime })
            )
        }
    }
}

data class AppViewState(
    val appInfo: List<AppInfo> = emptyList()
)