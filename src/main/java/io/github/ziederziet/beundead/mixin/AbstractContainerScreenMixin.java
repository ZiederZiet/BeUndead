package io.github.ziederziet.beundead.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.ziederziet.beundead.api.BeUndeadApi;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen {
    private static final ResourceLocation SLOT_DISABLED_TEXTURE = new ResourceLocation("textures/gui/sprites/container/slot_disabled.png");

    protected AbstractContainerScreenMixin(Component component) {
        super(component);
    }

    @Inject(at = @At("TAIL"), method = "renderSlot")
    protected void renderSlot(PoseStack poseStack, Slot slot, CallbackInfo ci){
        if (slot.container instanceof Inventory inventory && BeUndeadApi.getZombieType(inventory.player) > 0) {
            int invState = BeUndeadApi.getInvStateOfPlayer(inventory.player);
            if (invState < 2)
            {
                int slotId = slot.getContainerSlot();
                if ((Object)this instanceof CreativeModeInventoryScreen creativeModeInventoryScreen && creativeModeInventoryScreen.isInventoryOpen()){
                    if ((!(invState > 0 && slotId > 35) && slotId != 40) && (slotId < 45 && slotId > 8)){
                        TextureAtlasSprite textureAtlasSprite = (TextureAtlasSprite)this.minecraft.getTextureAtlas(SLOT_DISABLED_TEXTURE);
                        RenderSystem.setShaderTexture(0, textureAtlasSprite.atlasLocation());
                        blit(poseStack, slot.x - 1, slot.y - 1, 0, 18, 18, textureAtlasSprite);
                    }
                }
                else {
                    if (slotId < 36 && (!(invState > 0 && slotId < 9) && slotId != 4)) {
                        TextureAtlasSprite textureAtlasSprite = (TextureAtlasSprite)this.minecraft.getTextureAtlas(SLOT_DISABLED_TEXTURE);
                        RenderSystem.setShaderTexture(0, textureAtlasSprite.atlasLocation());
                        blit(poseStack, slot.x - 1, slot.y - 1, 0, 18, 18, textureAtlasSprite);
                    }
                }

                //pGuiGraphics.drawString(Minecraft.getInstance().font, String.valueOf(slot), pSlot.x - 1, pSlot.y - 1, 16711935, false);
            }
        }
    }

}
