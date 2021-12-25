package top.iseason.heping.manager


object EventManager {
    //使用事件队列，只存储最近12次
    var eventList: MutableList<Pair<Long, Long>> = mutableListOf()
    var tempUnix: Long = System.currentTimeMillis()

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

}

fun Pair<Long, Long>.calculateTime() = this.second - this.first

data class ScreenEvent(
    val isOpening: Boolean = true,
    val unixTime: Long
)