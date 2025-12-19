package io.github.ziederziet.beundead;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;

public class BeUndeadClient {
    public static boolean isCreativeScreen() {
        return Minecraft.getInstance().screen instanceof CreativeModeInventoryScreen creativeModeInventoryScreen && creativeModeInventoryScreen.isInventoryOpen();
    }
}