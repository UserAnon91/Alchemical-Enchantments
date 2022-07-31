package com.festivelightsgames;

import com.festivelightsgames.screen.EnchantmentRollerScreenHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class AlchemicalEnchantments implements ModInitializer {
    public static final EnchantmentRoller EnchantmentRoller = new EnchantmentRoller(FabricBlockSettings.of(Material.METAL).strength(4.0f).requiresTool());
    public static final ScreenHandlerType<EnchantmentRollerScreenHandler> ENCHANTMENT_SCREEN_HANDLER;
    public static final EnchantmentDust Dust_Amodus = new EnchantmentDust(new Item.Settings());
    public static final EnchantmentDust Dust_Lieval = new EnchantmentDust(new Item.Settings());
    public static final EnchantmentDust Dust_Lipidius = new EnchantmentDust(new Item.Settings());
    public static final EnchantmentDust Dust_Tridiur = new EnchantmentDust(new Item.Settings());
    public static final EnchantmentDust Dust_Vantium = new EnchantmentDust(new Item.Settings());
    public static final EnchantmentDust Dust_Waumuless = new EnchantmentDust(new Item.Settings());

    public static BlockEntityType<EnchantmentRollerEntity> ENCHANTMENT_ROLLER_ENTITY;

    static {
        //Enchantment Dusts
        Registry.register(Registry.ITEM, new Identifier("alchemicalenchantments", "dust_amodus"), Dust_Amodus);
        Registry.register(Registry.ITEM, new Identifier("alchemicalenchantments", "dust_lieval"), Dust_Lieval);
        Registry.register(Registry.ITEM, new Identifier("alchemicalenchantments", "dust_lipidius"), Dust_Lipidius);
        Registry.register(Registry.ITEM, new Identifier("alchemicalenchantments", "dust_tridiur"), Dust_Tridiur);
        Registry.register(Registry.ITEM, new Identifier("alchemicalenchantments", "dust_vantium"), Dust_Vantium);
        Registry.register(Registry.ITEM, new Identifier("alchemicalenchantments", "dust_waumuless"), Dust_Waumuless);
        //End Enchantment Dusts


        Registry.register(Registry.BLOCK, new Identifier("alchemicalenchantments", "enchantmentroller"), EnchantmentRoller);
        Registry.register(Registry.ITEM, new Identifier("alchemicalenchantments", "enchantmentroller"),
                new BlockItem(EnchantmentRoller, new FabricItemSettings().group(ItemGroup.MISC)));


        ServerPlayNetworking.registerGlobalReceiver(new Identifier("alchemicalenchantments", "push_enchantment_button"),
                ((server, player, handler, buf, responseSender) -> {
                    if(player.currentScreenHandler instanceof EnchantmentRollerScreenHandler){
                        ((EnchantmentRollerScreenHandler) player.currentScreenHandler).EnchantRoll();
                    }
                }));


		ENCHANTMENT_ROLLER_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE,
                "alchemicalenchantments:enchantment_roller_entity",
                FabricBlockEntityTypeBuilder.create(EnchantmentRollerEntity::new, EnchantmentRoller).build(null));
        ENCHANTMENT_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier("alchemicalenchantments", "enchantmentroller"), EnchantmentRollerScreenHandler::new);
    }

    @Override
    public void onInitialize() {

    }
}

