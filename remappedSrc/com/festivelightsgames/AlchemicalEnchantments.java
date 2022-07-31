package com.festivelightsgames;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlchemicalEnchantments implements ModInitializer {
	public static final Item EnchantmentRoller = new Item(new FabricItemSettings().group(ItemGroup.MISC));

    @Override
	public void onInitialize()
	{
		 Registry.register(Registry.ITEM, new Identifier("AlchemicalEnchantments", "EnchantmentRoller"), EnchantmentRoller);
	}
}
