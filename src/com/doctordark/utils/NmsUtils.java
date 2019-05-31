package com.doctordark.utils;

import org.bukkit.entity.*;
import org.bukkit.craftbukkit.v1_7_R4.entity.*;

import net.minecraft.server.v1_7_R4.*;
import net.minecraft.server.v1_7_R4.ItemStack;
import net.minecraft.server.v1_7_R4.PlayerInventory;

import org.bukkit.inventory.*;
import org.bukkit.craftbukkit.v1_7_R4.inventory.*;

public class NmsUtils {
    public static int getProtocolVersion(final Player player) {
        return ((CraftPlayer) player).getHandle().playerConnection.networkManager.getVersion();
    }

    public static void resendHeldItemPacket(final Player player) {
        sendItemPacketAtHeldSlot(player, getCleanHeldItem(player));
    }

    public static void sendItemPacketAtHeldSlot(final Player player, final ItemStack stack) {
        sendItemPacketAtSlot(player, stack, player.getInventory().getHeldItemSlot());
    }

    public static void sendItemPacketAtSlot(final Player player, final ItemStack stack, final int index) {
        sendItemPacketAtSlot(player, stack, index, ((CraftPlayer) player).getHandle().defaultContainer.windowId);
    }

    public static void sendItemPacketAtSlot(final Player player, final ItemStack stack, int index, final int windowID) {
        final EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        if (entityPlayer.playerConnection != null) {
            if (index < PlayerInventory.getHotbarSize()) {
                index += 36;
            } else if (index > 35) {
                index = 8 - (index - 36);
            }
            entityPlayer.playerConnection.sendPacket((Packet) new PacketPlayOutSetSlot(windowID, index, stack));
        }
    }

    public static ItemStack getCleanItem(final Inventory inventory, final int slot) {
        return ((CraftInventory) inventory).getInventory().getItem(slot);
    }

    public static ItemStack getCleanItem(final Player player, final int slot) {
        return getCleanItem((Inventory) player.getInventory(), slot);
    }

    public static ItemStack getCleanHeldItem(final Player player) {
        return getCleanItem(player, player.getInventory().getHeldItemSlot());
    }
}
