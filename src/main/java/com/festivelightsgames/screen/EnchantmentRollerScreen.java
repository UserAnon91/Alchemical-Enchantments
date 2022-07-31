package com.festivelightsgames.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class EnchantmentRollerScreen extends HandledScreen<EnchantmentRollerScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("minecraftmod", "textures/enchantmentrollerui.png");

    int validSlotY = 223;
    int invalidSlotY = 239;
    final int descButtonHeight = 16;
    final int descButtonWidth = 68;

    private List<Slot> handlerSlots;

    public EnchantmentRollerScreen(EnchantmentRollerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);

        handlerSlots = handler.slots.subList(2, 7);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    TexturedButtonWidget[] slotDescription;

    @Override
    protected void init() {
        backgroundWidth = 200;
        backgroundHeight = 223;
        slotDescription = new TexturedButtonWidget[6];
        super.init();

        //draw Confirm Enchantment button
        this.addDrawableChild(new TexturedButtonWidget(148 + this.x, 55 + this.y, 10, 35, 202, 1, 35, TEXTURE, 256, 256, new EnchantmentButton(handler)));

        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        titleY = 8;

        playerInventoryTitleX = 18;
        playerInventoryTitleY = 130;
    }

    @Override
    protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
        super.onMouseClick(slot, slotId, button, actionType);

        if (slot != null) {
            RenderValidEnchants();
        }
    }

    private void RenderValidEnchants() {
        boolean[] validEnchants = EnchantmentRollerScreenHandler.CheckValidityOfEnchantmentDust(handlerSlots);
        for (int i = 0; i < validEnchants.length; i++) {
            remove(slotDescription[i]);
            if (handlerSlots.get(i).getStack().getItem() != Items.AIR) {
                DrawDescriptionSlot(i, validEnchants[i] ? validSlotY : invalidSlotY);
            }
        }
    }

    public void DrawDescriptionSlot(int slotToChange, int validityPosition) {
        remove(slotDescription[slotToChange]);

        slotDescription[slotToChange] = this.addDrawableChild(new TexturedButtonWidget(70 + this.x, 28 + this.y + ((descButtonHeight + 2) * (slotToChange)), descButtonWidth, descButtonHeight, 0, validityPosition, 0, TEXTURE, 256, 256, new ButtonWidget.PressAction() {
            @Override
            public void onPress(ButtonWidget button) {
            }
        }));
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }
}

