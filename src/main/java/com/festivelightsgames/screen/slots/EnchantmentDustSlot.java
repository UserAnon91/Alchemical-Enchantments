package com.festivelightsgames.screen.slots;

import com.festivelightsgames.Dust_Lipidius;
import com.festivelightsgames.EnchantmentDust;
import com.festivelightsgames.MinecraftMod;
import com.festivelightsgames.screen.EnchantmentRollerScreenHandler;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class EnchantmentDustSlot extends Slot {
    protected EnchantmentRollerScreenHandler ourHandler;
    public EnchantmentDustSlot(Inventory inventory, int index, int x, int y, EnchantmentRollerScreenHandler handler)
    {
        super(inventory, index, x, y);

        ourHandler = handler;
    }

    @Override
    public boolean canInsert(ItemStack stack){
        if(stack.getItem() instanceof EnchantmentDust && stack.getItem() != MinecraftMod.Dust_Lipidius){
            return true;
        }
        else{
            return  false;
        }
    }
}