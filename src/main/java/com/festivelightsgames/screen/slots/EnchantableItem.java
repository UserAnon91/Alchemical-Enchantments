package com.festivelightsgames.screen.slots;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class EnchantableItem extends Slot {
    public EnchantableItem(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack){
        if(stack.hasEnchantments() || stack.isEnchantable()){
            return true;
        }
        return false;
    }
}
