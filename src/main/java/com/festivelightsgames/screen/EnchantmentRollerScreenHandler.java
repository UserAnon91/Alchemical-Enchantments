package com.festivelightsgames.screen;

import com.festivelightsgames.AlchemicalEnchantments;
import com.festivelightsgames.screen.slots.EnchantableItem;
import com.festivelightsgames.screen.slots.EnchantedItemOutput;
import com.festivelightsgames.screen.slots.EnchantmentDustSlot;
import com.festivelightsgames.screen.slots.LipidiusDustSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ClickType;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.registry.Registry;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class EnchantmentRollerScreenHandler extends ScreenHandler {
    public final Inventory inventory;

    private int Dust_Lieval_strength = 0;
    private int Dust_Lipidius_strength = 0;

    public static int InventorySize = 8;

    public EnchantmentRollerScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(InventorySize));
    }

    //Set up slots for inventories
    public EnchantmentRollerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(AlchemicalEnchantments.ENCHANTMENT_SCREEN_HANDLER, syncId);
        checkSize(inventory, InventorySize);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        int m;
        int l;

        //Enchanting target slot
        this.addSlot(new EnchantableItem(inventory, 0, 14, 46));

        //Lipidius slot
        this.addSlot(new LipidiusDustSlot(inventory, 1, 14, 91, this));

        //container inventory
        for (m = 2; m < 7; m++) {
            this.addSlot(new EnchantmentDustSlot(inventory, m, 40, 28 + ((m - 2) * 18), this));
        }

        //enchanting output slot
        this.addSlot(new EnchantedItemOutput(inventory, InventorySize - 1, 169, 64));

        //player inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 20 + (l * 18), 142 + (m * 18)));
            }
        }

        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 20 + (m * 18), 200));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    //shift click player inv slot
    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);

        if (slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }

            } else {
                int indexOfEnchantmentDust = IndexOfStackInInventory(originalStack);
                int firstIndex = indexOfEnchantmentDust == -1 ? 0 : indexOfEnchantmentDust;
                int lastIndex = indexOfEnchantmentDust == -1 ? this.inventory.size() : indexOfEnchantmentDust;

                if(!InventoryHasThisStackAlready(originalStack, slots.subList(1, 7))){
                    if(this.insertItem(newStack, firstIndex, lastIndex + 1, false)){
                        originalStack.setCount(0);
                        newStack.setCount(0);
                    }
                }
                else{
                    for (int i = 0; i < inventory.size(); i++) {
                        if (inventory.getStack(i).getItem() == originalStack.getItem()) {
                            if (indexOfEnchantmentDust == -1) {
                                if (!this.insertItem(originalStack, firstIndex, lastIndex + 1, false)) {
                                    return ItemStack.EMPTY;
                                }
                            } else {
                                int itemTotal = originalStack.getCount() + inventory.getStack(i).getCount();
                                if (itemTotal > originalStack.getMaxCount()) {
                                    int difference = originalStack.getMaxCount()/*64*/ - inventory.getStack(i).getCount()/*the amount in*/;
                                    newStack.setCount(difference);
                                    originalStack.setCount(originalStack.getCount() - newStack.getCount());
                                    this.insertItem(newStack, firstIndex, lastIndex + 1, false);

                                    newStack.setCount(0);

                                } else {
                                    this.insertItem(newStack, firstIndex, lastIndex + 1, false);

                                    originalStack.setCount(0);
                                    newStack.setCount(0);
                                }
                            }

                            break;

                        }
                    }
                }

                if (originalStack.isEmpty()) {
                    slot.setStack(ItemStack.EMPTY);
                } else {
                    slot.markDirty();
                }
            }
        }

        return newStack;

    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        if (!InventoryHasThisStackAlready(stack, slots.subList(1, 7)) ||
                (slot.getStack().getItem() == stack.getItem() && slot.getStack().getCount() < slot.getStack().getMaxCount()))
        {
            return true;
        } else {
            return false;
        }
    }


    public int IndexOfStackInInventory(ItemStack itemStack) {
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.getStack(i).getItem() == itemStack.getItem()) {
                //we found a stack for this item, flag it as found
                return i;
            }
        }

        return -1;

    }

    public void EnchantRoll() {
        Slot slot = slots.get(0);
        int lastSlot = EnchantmentRollerScreenHandler.InventorySize - 1;

        if (slot.hasStack() && !slots.get(lastSlot).hasStack()) {
            if(ApplyEnchant(this.getSlot(0).getStack())){
                this.slots.get(lastSlot).setStack(this.getSlot(0).getStack().copy());
                this.slots.get(0).setStack(new ItemStack(Items.AIR));
            }
        }
    }

    public boolean ApplyEnchant(ItemStack item) {
        boolean[] validityArray = CheckValidityOfEnchantmentDust(slots.subList(1,7));

        for (boolean b : validityArray) {
            if (!b) {
                return false;
            }
        }

        List<Enchantment> validEnchants = new ArrayList<>();
        Enchantment[] allEnchants = RetrieveAllEnchantsRegistered();

        for (Enchantment allEnchant : allEnchants) {
            if (allEnchant.isAcceptableItem(item)) {
                validEnchants.add(allEnchant);
            }
        }

        List<Slot> enchantmentDustsList = slots.subList(1, 7).stream().toList();
        boolean hasTridiur = false;

        for (Slot slot : enchantmentDustsList) {
            ItemStack itemStack = slot.getStack();
            Item item1 = itemStack.getItem();

            switch (item1.toString()) {
                case "dust_amodus": //done
                    RemoveVanilla(validEnchants);
                    break;

                case "dust_lieval": //done
                    Dust_Lieval_strength = itemStack.getCount();
                    break;

                case "dust_lipidius": //done
                    Dust_Lipidius_strength = itemStack.getCount();
                    break;

                case "dust_tridiur": //done
                    hasTridiur = true;
                    break;

                case "dust_vantium": //done
                    validEnchants = RemoveNonVanilla(validEnchants);
                    break;

                case "dust_waumuless":
                    validEnchants = RemoveCurse(validEnchants, itemStack.getCount());
                    break;
            }
        }

        if(!hasTridiur){
            validEnchants = RemoveTreasure(validEnchants);
        }

        Map<Enchantment, Integer> map = new HashMap<>();

        if(Dust_Lipidius_strength > validEnchants.size()){
            Dust_Lipidius_strength  = validEnchants.size();
        }

        if(validEnchants.size() > 0 && Dust_Lipidius_strength > 0){
            int numEnchants = Dust_Lipidius_strength;
            int selectedEnchant = 0;

            for (int i = 0; i < numEnchants; i++) {
                selectedEnchant = ThreadLocalRandom.current().nextInt(0, validEnchants.size());

                map.put(validEnchants.get(selectedEnchant), 1); //TODO: Make this not just max enchant level

                validEnchants.remove(selectedEnchant);
            }

            map = UseLieval(map, Dust_Lieval_strength);

            EnchantmentHelper.set(map, item);

            return true;
        }
        else{
            return false;
        }
    }

    private List<Enchantment> RemoveNonVanilla(List<Enchantment> originalList){
        for(int i = originalList.size() -1; i >= 0; i --){
            if(!Registry.ENCHANTMENT.getId(originalList.get(i)).getNamespace().equals("minecraft")){
                originalList.remove(i);
            }
        }

        return originalList;
    }

    private Map<Enchantment, Integer> UseLieval(Map<Enchantment, Integer> map, int lievalCount){
        Enchantment[] enchantments = map.keySet().toArray(new Enchantment[map.keySet().size()]);
        int selectedEnchant;

        while(CanStillUpgrade(map) && lievalCount > 0){
            selectedEnchant = ThreadLocalRandom.current().nextInt(0, enchantments.length);

            while(map.get(enchantments[selectedEnchant]) >= enchantments[selectedEnchant].getMaxLevel()){
                selectedEnchant = ThreadLocalRandom.current().nextInt(0, enchantments.length);
            }

            map.put(enchantments[selectedEnchant], map.get(enchantments[selectedEnchant]) + 1);
            lievalCount -= 1;
        }
        return map;
    }

    private boolean CanStillUpgrade(Map<Enchantment, Integer> map){
        for(Map.Entry<Enchantment, Integer> e : map.entrySet()){
            if(e.getValue() < e.getKey().getMaxLevel()){
                return true;
            }
        }
        return false;
    }

    private List<Enchantment> RemoveTreasure(List<Enchantment> originalList){
        for(int i = originalList.size() -1; i >= 0; i --){
            if(originalList.get(i).isTreasure() && !originalList.get(i).isCursed()){
                originalList.remove(i);
            }
        }

        return originalList;
    }

    private List<Enchantment> RemoveCurse(List<Enchantment> enchantmentList, int cursesRemoved){
        int selectedEnchant;

        while(CheckCursesExist(enchantmentList) && cursesRemoved > 0){
            selectedEnchant = ThreadLocalRandom.current().nextInt(0, enchantmentList.size());
            if(enchantmentList.get(selectedEnchant).isCursed()){
                enchantmentList.remove(selectedEnchant);
                cursesRemoved --;
            }
        }
        return enchantmentList;
    }

    private boolean CheckCursesExist(List<Enchantment> enchantmentList){
        for (Enchantment enchantment : enchantmentList) {
            if (enchantment.isCursed()) {
                return true;
            }
        }
        return false;
    }

    private void RemoveVanilla(List<Enchantment> originalList){
        for(int i = originalList.size() -1; i >= 0; i --){
            if(!Registry.ENCHANTMENT.getId(originalList.get(i)).getNamespace().equals("minecraft")){
                continue;
            }
            else{
                originalList.remove(i);
            }
        }
    }

    public Enchantment[] RetrieveAllEnchantsRegistered() {
        List<Enchantment> r = Registry.ENCHANTMENT.getEntries().stream().map(temp -> temp.getValue()).collect(Collectors.toList());
        Iterator iterator = r.iterator();

        List<Enchantment> enchantments = new ArrayList<>();

        Enchantment enchantment;
        while (iterator.hasNext()) {
            enchantment = (Enchantment) iterator.next();
            enchantments.add(enchantment);

        }

        Enchantment[] returnValue = new Enchantment[enchantments.size()];
        returnValue = enchantments.toArray(returnValue);

        return returnValue;
    }
    public static boolean InventoryHasThisItemAlready(Item item, List<Slot> slots) {
        for (int i = 0; i < slots.stream().count(); i++) {
            if (slots.get(i).getStack().getItem() == item) {
                return true;
            }
        }

        return false;
    }

    public static boolean InventoryHasThisStackAlready(ItemStack itemStack, List<Slot> slots) {
        return InventoryHasThisItemAlready(itemStack.getItem(), slots);
    }

    public void onSlotClick (int slotIndex, int button, SlotActionType actionType, PlayerEntity player){
        try {
            internalOnSlotClick(slotIndex, button, actionType, player);
        } catch (Exception var8) {
            CrashReport crashReport = CrashReport.create(var8, "Container click");
            CrashReportSection crashReportSection = crashReport.addElement("Click info");
            crashReportSection.add("Menu Type", () -> {
                return this.getType() != null ? Registry.SCREEN_HANDLER.getId(this.getType()).toString() : "<no type>";
            });
            crashReportSection.add("Menu Class", () -> {
                return this.getClass().getCanonicalName();
            });
            crashReportSection.add("Slot Count", this.slots.size());
            crashReportSection.add("Slot", slotIndex);
            crashReportSection.add("Button", button);
            crashReportSection.add("Type", actionType);
            throw new CrashException(crashReport);
        }
    }

    private void internalOnSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        PlayerInventory playerInventory = player.getInventory();
        Slot slot;
        ItemStack itemStack;
        ItemStack itemStack2;
        int j;
        int k;
       {
            int n;
            if ((actionType == SlotActionType.PICKUP || actionType == SlotActionType.QUICK_MOVE) && (button == 0 || button == 1)) {
                ClickType clickType = button == 0 ? ClickType.LEFT : ClickType.RIGHT;
                if (slotIndex == -999) {
                    if (!this.getCursorStack().isEmpty()) {
                        if (clickType == ClickType.LEFT) {
                            player.dropItem(this.getCursorStack(), true);
                            this.setCursorStack(ItemStack.EMPTY);
                        } else {
                            player.dropItem(this.getCursorStack().split(1), true);
                        }
                    }
                } else if (actionType == SlotActionType.QUICK_MOVE) {
                    if (slotIndex < 0) {
                        return;
                    }

                    slot = this.slots.get(slotIndex);
                    if (!slot.canTakeItems(player)) {
                        return;
                    }

                    this.transferSlot(player, slotIndex);
                } else {
                    if(slotIndex == -1 || slots.get(slotIndex) instanceof EnchantmentDustSlot && !canInsertIntoSlot(this.getCursorStack(), (Slot)this.slots.get(slotIndex)) && this.getCursorStack().getItem() != Items.AIR){
                        return;
                    }

                    if (slotIndex < 0) {
                        return;
                    }

                    slot = this.slots.get(slotIndex);
                    itemStack = slot.getStack();
                    ItemStack itemStack5 = this.getCursorStack();
                    player.onPickupSlotClick(itemStack5, slot.getStack(), clickType);
                    if (!itemStack5.onStackClicked(slot, clickType, player) && !itemStack.onClicked(itemStack5, slot, clickType, player, this.getCursorStackReference())) {
                        if (itemStack.isEmpty()) {
                            if (!itemStack5.isEmpty()) {
                                n = clickType == ClickType.LEFT ? itemStack5.getCount() : 1;
                                this.setCursorStack(slot.insertStack(itemStack5, n));
                            }
                        } else if (slot.canTakeItems(player)) {
                            if (itemStack5.isEmpty()) {
                                n = clickType == ClickType.LEFT ? itemStack.getCount() : (itemStack.getCount() + 1) / 2;
                                Optional<ItemStack> optional = slot.tryTakeStackRange(n, Integer.MAX_VALUE, player);
                                optional.ifPresent((stack) -> {
                                    this.setCursorStack(stack);
                                    slot.onTakeItem(player, stack);
                                });
                            } else if (slot.canInsert(itemStack5)) {
                                if (ItemStack.canCombine(itemStack, itemStack5)) {
                                    n = clickType == ClickType.LEFT ? itemStack5.getCount() : 1;
                                    this.setCursorStack(slot.insertStack(itemStack5, n));
                                } else if (itemStack5.getCount() <= slot.getMaxItemCount(itemStack5)) {
                                    slot.setStack(itemStack5);
                                    this.setCursorStack(itemStack);
                                }
                            } else if (ItemStack.canCombine(itemStack, itemStack5)) {
                                Optional<ItemStack> optional2 = slot.tryTakeStackRange(itemStack.getCount(), itemStack5.getMaxCount() - itemStack5.getCount(), player);
                                optional2.ifPresent((stack) -> {
                                    itemStack5.increment(stack.getCount());
                                    slot.onTakeItem(player, stack);
                                });
                            }
                        }
                    }

                    slot.markDirty();
                }
            } else {
                Slot slot3;
                int o;
                if (actionType == SlotActionType.SWAP) {
                    slot3 = (Slot)this.slots.get(slotIndex);
                    itemStack2 = playerInventory.getStack(button);
                    itemStack = slot3.getStack();
                    if (!itemStack2.isEmpty() || !itemStack.isEmpty()) {
                        if (itemStack2.isEmpty()) {
                            if (slot3.canTakeItems(player)) {
                                playerInventory.setStack(button, itemStack);
//                                slot3.onTake(itemStack.getCount());
                                slot3.setStack(ItemStack.EMPTY);
                                slot3.onTakeItem(player, itemStack);
                            }
                        } else if (itemStack.isEmpty()) {
                            if (slot3.canInsert(itemStack2)) {
                                o = slot3.getMaxItemCount(itemStack2);
                                if (itemStack2.getCount() > o) {
                                    slot3.setStack(itemStack2.split(o));
                                } else {
                                    playerInventory.setStack(button, ItemStack.EMPTY);
                                    slot3.setStack(itemStack2);
                                }
                            }
                        } else if (slot3.canTakeItems(player) && slot3.canInsert(itemStack2)) {
                            o = slot3.getMaxItemCount(itemStack2);
                            if (itemStack2.getCount() > o) {
                                slot3.setStack(itemStack2.split(o));
                                slot3.onTakeItem(player, itemStack);
                                if (!playerInventory.insertStack(itemStack)) {
                                    player.dropItem(itemStack, true);
                                }
                            } else {
                                playerInventory.setStack(button, itemStack);
                                slot3.setStack(itemStack2);
                                slot3.onTakeItem(player, itemStack);
                            }
                        }
                    }
                } else if (actionType == SlotActionType.CLONE && player.getAbilities().creativeMode && this.getCursorStack().isEmpty() && slotIndex >= 0) {
                    slot3 = (Slot)this.slots.get(slotIndex);
                    if (slot3.hasStack()) {
                        itemStack2 = slot3.getStack().copy();
                        itemStack2.setCount(itemStack2.getMaxCount());
                        this.setCursorStack(itemStack2);
                    }
                } else if (actionType == SlotActionType.THROW && this.getCursorStack().isEmpty() && slotIndex >= 0) {
                    slot3 = (Slot)this.slots.get(slotIndex);
                    j = button == 0 ? 1 : slot3.getStack().getCount();
                    itemStack = slot3.takeStackRange(j, Integer.MAX_VALUE, player);
                    player.dropItem(itemStack, true);
                } else if (actionType == SlotActionType.PICKUP_ALL && slotIndex >= 0) {
                    slot3 = (Slot)this.slots.get(slotIndex);
                    itemStack2 = this.getCursorStack();
                    if (!itemStack2.isEmpty() && (!slot3.hasStack() || !slot3.canTakeItems(player))) {
                        k = button == 0 ? 0 : this.slots.size() - 1;
                        o = button == 0 ? 1 : -1;

                        for(n = 0; n < 2; ++n) {
                            for(int p = k; p >= 0 && p < this.slots.size() && itemStack2.getCount() < itemStack2.getMaxCount(); p += o) {
                                Slot slot4 = (Slot)this.slots.get(p);
                                if (slot4.hasStack() && canInsertItemIntoSlot(slot4, itemStack2, true) && slot4.canTakeItems(player) && this.canInsertIntoSlot(itemStack2, slot4)) {
                                    ItemStack itemStack6 = slot4.getStack();
                                    if (n != 0 || itemStack6.getCount() != itemStack6.getMaxCount()) {
                                        ItemStack itemStack7 = slot4.takeStackRange(itemStack6.getCount(), itemStack2.getMaxCount() - itemStack2.getCount(), player);
                                        itemStack2.increment(itemStack7.getCount());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private StackReference getCursorStackReference() {
        return new StackReference() {
            public ItemStack get() {
                return getCursorStack();
            }

            public boolean set(ItemStack stack) {
                setCursorStack(stack);
                return true;
            }
        };
    }

    public static boolean[] CheckValidityOfEnchantmentDust(List<Slot> slots) {
        boolean[] slotIsValid = new boolean[5];

        for (int i = 0; i < 5; i++) {
            if (slots.get(i).getStack().getItem() == AlchemicalEnchantments.Dust_Vantium && InventoryHasThisItemAlready(AlchemicalEnchantments.Dust_Amodus, slots)
                    || slots.get(i).getStack().getItem() == AlchemicalEnchantments.Dust_Amodus && InventoryHasThisItemAlready(AlchemicalEnchantments.Dust_Vantium, slots)) {
                slotIsValid[i] = false;
                continue;
            } else {
                slotIsValid[i] = true;
            }
        }

        return slotIsValid;
    }
}
