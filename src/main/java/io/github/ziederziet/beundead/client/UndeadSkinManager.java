package io.github.ziederziet.beundead.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.properties.Property;
import com.mojang.blaze3d.platform.NativeImage;
import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Player;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class UndeadSkinManager {
    private static final Map<UUID, ResourceLocation> modifiedSkins = new HashMap<>();

    public static ResourceLocation getOrCreateSkin(ResourceLocation defaultLocation, Player player){
        return modifiedSkins.computeIfAbsent(player.getUUID(), uuid -> {
            Optional<String> skinUrlOpt = getSkinUrl(player.getGameProfile());

            // System.out.println("GHBEBSBDSSGBSKJBDGHJSKDBGHSKJDBSDJKBS");

            try {
                NativeImage original = downloadSkinImage(skinUrlOpt.get());
                NativeImage modified = applyAlgorithm(original);

                ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(BeUndead.MODID, "modskin/" + uuid.toString());
                Minecraft.getInstance().getTextureManager().register(loc, new DynamicTexture(modified));
                return loc;

            } catch (IOException e) {
                e.printStackTrace();
                return defaultLocation;
            }
        });
    }

    private static NativeImage applyAlgorithm(NativeImage input) {
        NativeImage result = new NativeImage(input.getWidth(), input.getHeight(), false);
        for (int x = 0; x < input.getWidth(); x++) {
            for (int y = 0; y < input.getHeight(); y++) {
                int color = input.getPixelRGBA(x, y);

                int a = FastColor.ARGB32.alpha(color);
                int r = 255 - FastColor.ARGB32.red(color);
                int g = 255 - FastColor.ARGB32.green(color);
                int b = 255 - FastColor.ARGB32.blue(color);

                result.setPixelRGBA(x, y, FastColor.ARGB32.color(a, r, g, b));
            }
        }
        return result;
    }
    public static NativeImage downloadSkinImage(String skinUrl) throws IOException {
        try (InputStream in = new URL(skinUrl).openStream()) {
            return NativeImage.read(NativeImage.Format.RGBA, in);
        }
    }
    public static Optional<String> getSkinUrl(GameProfile profile) {
        Property property = profile.getProperties().get("textures").stream().findFirst().orElse(null);
        if (property == null) return Optional.empty();

        try {
            String json = new String(Base64.getDecoder().decode(property.value()), StandardCharsets.UTF_8);
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();

            return Optional.ofNullable(obj.getAsJsonObject("textures"))
                    .map(textures -> textures.getAsJsonObject("SKIN"))
                    .map(skin -> skin.get("url").getAsString());

        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
