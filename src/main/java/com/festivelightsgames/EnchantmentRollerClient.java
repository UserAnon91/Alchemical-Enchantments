package com.festivelightsgames;

import com.festivelightsgames.screen.EnchantmentRollerScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

@Environment(EnvType.CLIENT)
public class EnchantmentRollerClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(AlchemicalEnchantments.ENCHANTMENT_SCREEN_HANDLER, EnchantmentRollerScreen::new);
    }

}
