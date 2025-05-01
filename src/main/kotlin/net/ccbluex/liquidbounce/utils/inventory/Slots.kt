package net.ccbluex.liquidbounce.utils.inventory

import net.ccbluex.liquidbounce.utils.client.mc
import net.ccbluex.liquidbounce.utils.client.player
import net.ccbluex.liquidbounce.utils.item.isNothing
import net.minecraft.block.BlockState
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import kotlin.math.abs

object Slots {

    object Hotbar : SlotGroup<HotbarItemSlot>(
        List(9) { HotbarItemSlot(it) }
    ) {
        fun findSlotIndex(item: Item): Int? {
            return findSlotIndex { it.item == item }
        }

        inline fun findSlotIndex(predicate: (ItemStack) -> Boolean): Int? {
            return if (mc.player == null) null else find { predicate(it.itemStack) }?.hotbarSlot
        }

        fun findClosestItem(vararg items: Item): HotbarItemSlot? {
            return filter { it.itemStack.item in items }
                .minByOrNull { abs(player.inventory.selectedSlot - it.hotbarSlotForServer) }
        }

        fun findBestToolToMineBlock(
            blockState: BlockState,
            ignoreDurability: Boolean = true
        ): HotbarItemSlot? {
            val player = mc.player ?: return null

            val slot = filter {
                val stack = it.itemStack
                val durabilityCheck = (stack.damage < (stack.maxDamage - 2) || ignoreDurability)
                stack.isNothing() || (!player.isCreative && durabilityCheck)
            }.maxByOrNull {
                it.itemStack.getMiningSpeedMultiplier(blockState)
            } ?: return null

            val miningSpeedMultiplier = slot.itemStack.getMiningSpeedMultiplier(blockState)

            // The current slot already matches the best
            if (miningSpeedMultiplier == player.inventory.mainHandStack.getMiningSpeedMultiplier(blockState)) {
                return null
            }

            return slot
        }
    }

    @JvmField
    val Inventory = SlotGroup(
        List(27) { InventoryItemSlot(it) }
    )

    @JvmField
    val OffHand = SlotGroup(
        listOf(OffHandSlot)
    )

    @JvmField
    val Armor = SlotGroup(
        List(4) { ArmorItemSlot(it) }
    )

    @JvmField
    val OffhandWithHotbar = SlotGroup(
        (OffHand + Hotbar).map { it as HotbarItemSlot }
    )

    @JvmField
    val All = Hotbar + OffHand + Inventory + Armor
}

open class SlotGroup<T : ItemSlot>(val slots: List<T>) : List<T> by slots {
    val items: List<Item>
        get() = slots.map { it.itemStack.item }

    fun findSlot(item: Item): T? {
        return if (mc.player == null) null else find { it.itemStack.item == item }
    }

    inline fun findSlot(predicate: (ItemStack) -> Boolean): T? {
        return if (mc.player == null) null else find { predicate(it.itemStack) }
    }

    operator fun plus(other: SlotGroup<*>): SlotGroup<ItemSlot> {
        val newList = ArrayList<ItemSlot>(this.size + other.size)
        newList.addAll(this)
        newList.addAll(other)
        return SlotGroup(newList)
    }

    operator fun plus(other: ItemSlot): SlotGroup<ItemSlot> {
        val newList = ArrayList<ItemSlot>(this.size + 1)
        newList.addAll(this)
        newList.add(other)
        return SlotGroup(newList)
    }
}
