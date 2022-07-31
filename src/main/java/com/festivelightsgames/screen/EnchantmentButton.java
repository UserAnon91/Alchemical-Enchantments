package com.festivelightsgames.screen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Identifier;

public class EnchantmentButton implements ButtonWidget.PressAction {
    EnchantmentRollerScreenHandler handler;

    public EnchantmentButton(EnchantmentRollerScreenHandler h) {
        handler = h;
    }

    @Override
    public void onPress(ButtonWidget button) {
        ClientPlayNetworking.send(new Identifier("alchemicalenchantments", "push_enchantment_button"), PacketByteBufs.empty());


    }
}
