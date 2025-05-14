package io.github.ziederziet.beundead.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.blaze3d.platform.NativeImage;
import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.*;

public class UndeadSkinManager {
    public static final Vec3[] ZOMBIE_COLORS = new Vec3[] { new Vec3(0.8D, 1.0D, 0.85D), new Vec3(0.7D, 0.8D, 0.8D), new Vec3(0.80D, 0.72D, 0.49D) };
    public static final Vec3[] ZOMBIE_COLOR_OFFSETS = new Vec3[] { new Vec3(0.0D, 0.1D, 0.0D), new Vec3(-0.1D, 0.1D, 0.1D), new Vec3(0.03D, 0.05D, 0.0D) };



    private static final Map<UUID, ResourceLocation> modifiedZombieSkins = new HashMap<>();
    private static final Map<UUID, ResourceLocation> modifiedHuskSkins = new HashMap<>();
    private static final Map<UUID, ResourceLocation> modifiedDrownedSkins = new HashMap<>();
    public static ResourceLocation getOrCreateSkin(ResourceLocation defaultLocation, int type, Player player) {
        Map<UUID, ResourceLocation> map = switch (type){
            case 2 -> modifiedHuskSkins;
            case 3 -> modifiedDrownedSkins;
            default -> modifiedZombieSkins;
        };
        return map.computeIfAbsent(player.getUUID(), uuid -> {
            Minecraft minecraft = Minecraft.getInstance();

            Optional<Resource> optional = minecraft.getResourceManager().getResource(defaultLocation);

            if (optional.isPresent()){
                Resource resource = optional.get();

                try {
                    NativeImage image = NativeImage.read(resource.open());

                    for (int y = 0; y < image.getHeight(); y++) {
                        for (int x = 0; x < image.getWidth(); x++) {
                            int rgba = image.getPixelRGBA(x, y);

                            int a = FastColor.ARGB32.alpha(rgba);
                            int r = FastColor.ARGB32.red(rgba);
                            int g = FastColor.ARGB32.green(rgba);
                            int b = FastColor.ARGB32.blue(rgba);

                            double light = (r * 0.85 + g + b * 0.7) / 2.55;

                            r = (int) Math.round(Math.clamp(light * ZOMBIE_COLORS[type - 1].x + (ZOMBIE_COLOR_OFFSETS[type - 1].x * 255D), 0D, 255D));
                            g = (int) Math.round(Math.clamp(light * ZOMBIE_COLORS[type - 1].y + (ZOMBIE_COLOR_OFFSETS[type - 1].y * 255D), 0D, 255D));
                            b = (int) Math.round(Math.clamp(light * ZOMBIE_COLORS[type - 1].z + (ZOMBIE_COLOR_OFFSETS[type - 1].z * 255D), 0D, 255D));

                            image.setPixelRGBA(x, y, FastColor.ARGB32.color(a, r, g, b));
                        }
                    }

                    DynamicTexture dynamicTexture = new DynamicTexture(image);

                    return minecraft.getTextureManager().register("modskin/" + uuid.toString(), dynamicTexture);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            return defaultLocation;
        });
    }
//    private static NativeImage applyAlgorithm(NativeImage input) {
//        NativeImage result = new NativeImage(input.getWidth(), input.getHeight(), false);
//        for (int x = 0; x < input.getWidth(); x++) {
//            for (int y = 0; y < input.getHeight(); y++) {
//                int color = input.getPixelRGBA(x, y);
//
//                int a = FastColor.ARGB32.alpha(color);
//                int r = 255 - FastColor.ARGB32.red(color);
//                int g = 255 - FastColor.ARGB32.green(color);
//                int b = 255 - FastColor.ARGB32.blue(color);
//
//                result.setPixelRGBA(x, y, FastColor.ARGB32.color(a, r, g, b));
//            }
//        }
//        return result;
//    }
//    public static NativeImage downloadSkinImage(String skinUrl) throws IOException {
//        try (InputStream in = new URL(skinUrl).openStream()) {
//            return NativeImage.read(NativeImage.Format.RGBA, in);
//        }
//    }
//    public static Optional<String> getSkinUrl(GameProfile profile) {
//        Property property = profile.getProperties().get("textures").stream().findFirst().orElse(null);
//        if (property == null) return Optional.empty();
//        try {
//            String json = new String(Base64.getDecoder().decode(property.value()), StandardCharsets.UTF_8);
//            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
//
//            return Optional.ofNullable(obj.getAsJsonObject("textures"))
//                    .map(textures -> textures.getAsJsonObject("SKIN"))
//                    .map(skin -> skin.get("url").getAsString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Optional.empty();
//        }
//    }
}