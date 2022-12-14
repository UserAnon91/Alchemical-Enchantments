package com.festivelightsgames;
import com.festivelightsgames.screen.EnchantmentRollerScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.*;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class EnchantmentRollerEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory{
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(EnchantmentRollerScreenHandler.InventorySize, ItemStack.EMPTY);
    public EnchantmentRollerEntity(BlockPos pos, BlockState state){
        super(AlchemicalEnchantments.ENCHANTMENT_ROLLER_ENTITY, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player){
        return new EnchantmentRollerScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public Text getDisplayName(){
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }


    @Override
    public void readNbt(NbtCompound nbt){
        super.readNbt(nbt);
        Inventories.readNbt(nbt, this.inventory);
    }

    @Override
    public void writeNbt(NbtCompound nbt){
        Inventories.writeNbt(nbt, this.inventory);
        super.writeNbt(nbt);
    }
}
