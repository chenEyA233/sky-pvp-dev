package net.ccbluex.liquidbounce.skyfiles

import net.minecraft.util.math.BlockPos
import java.util.concurrent.ConcurrentHashMap

class Chest {
    object ChestTracker {
        // 存储被打开过的箱子位置（线程安全）
        private val openedChests = ConcurrentHashMap<BlockPos, Boolean>()

        /**
         * 检测指定位置的箱子是否被打开过
         */
        fun isChestOpened(pos: BlockPos): Boolean = openedChests.containsKey(pos)

        /**
         * 标记箱子为已打开状态
         */
        fun markChestOpened(pos: BlockPos) {
            openedChests[pos] = true
        }

        /**
         * 清除所有打开记录
         */
        fun clearTrackingData() {
            openedChests.clear()
        }

    }

}
