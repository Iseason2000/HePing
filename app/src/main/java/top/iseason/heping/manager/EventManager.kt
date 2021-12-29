package top.iseason.heping.manager

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager


object EventManager {
    //使用事件队列，只存储最近12次
    var eventList: MutableList<Pair<Long, Long>> = mutableListOf()
    var tempUnix: Long = System.currentTimeMillis()
    var currentEvent: UsageEvents.Event = UsageEvents.Event()
    var usageStatsManager: UsageStatsManager? = null
    var isInit = false
    val eventGetter = Thread {
        while (true) {
            updateActivity()
            Thread.sleep(1000)
        }
    }

    @JvmStatic
    fun putEvent(event: Pair<Long, Long>) {
        //满队列，只更新不添加
        if (eventList.size >= 12) {
            for ((index, pair) in eventList.withIndex()) {
                if (event.calculateTime() > pair.calculateTime()) {
                    eventList[index] = event
                    break
                }
            }
        } else {
            eventList.add(event)
            eventList = eventList.sortedByDescending { it.calculateTime() }.toMutableList()
            return
        }
    }

    @JvmStatic
    fun updateActivity(): String? {
        if (!isInit) return null
        if (usageStatsManager == null) return null
        val currentTime = System.currentTimeMillis()
        val events: UsageEvents = usageStatsManager!!.queryEvents(
            currentTime - 1000L,
            currentTime
        )
        while (events.hasNextEvent()) {
            val event = UsageEvents.Event()
            events.getNextEvent(event)
            val eventType = event.eventType
            if (eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                currentEvent = event
            }
        }
        return null
    }
}

fun Pair<Long, Long>.calculateTime() = this.second - this.first
