package io.github.ziederziet.beundead.client;

import com.mojang.blaze3d.platform.NativeImage;
import io.github.ziederziet.beundead.api.BeUndeadApi;
import io.github.ziederziet.beundead.common.BeUndeadHelper;
import io.github.ziederziet.beundead.common.UndeadType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UndeadSkinManager {

    private static final Map<String, Map<ResourceLocation, ResourceLocation>> undeadSkins = new HashMap<>();

    public static void removeSkin(ResourceLocation ofResource){
        undeadSkins.forEach((name, resourceLocationResourceLocationMap) -> {
            resourceLocationResourceLocationMap.remove(ofResource);
        });
    }

    public static void removeAll(){
        undeadSkins.clear();
    }


    public static ResourceLocation getOrCreateSkin(ResourceLocation defaultLocation, String typeName, AbstractClientPlayer player) {
        return undeadSkins.computeIfAbsent(typeName, typ -> {
            return new HashMap<>();
        }).computeIfAbsent(defaultLocation, location -> {
            Minecraft minecraft = Minecraft.getInstance();

            Optional<Resource> optional = minecraft.getResourceManager().getResource(defaultLocation);

            BufferedImage skinImage = null;

            UndeadType type = BeUndeadHelper.getClientUndeadType(typeName);

            if (type == null){
                return defaultLocation;
            }

            try {
                if (optional.isPresent()){
                    skinImage = ImageIO.read(optional.get().open());
                }
                else {
                    URL url = new URL(player.getSkin().textureUrl());
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                    connection.connect();

                    InputStream inputStream = connection.getInputStream();

                    skinImage = ImageIO.read(inputStream);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            int height = skinImage.getHeight();
            int width = skinImage.getWidth();

            NativeImage nativeImage = new NativeImage(NativeImage.Format.RGBA, width, height, true);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgba = skinImage.getRGB(x, y);

                    int a = FastColor.ARGB32.alpha(rgba);
                    int r = FastColor.ARGB32.red(rgba);
                    int g = FastColor.ARGB32.green(rgba);
                    int b = FastColor.ARGB32.blue(rgba);

                    double light = (r * 0.85 + g + b * 0.7) / 2.55;

                    r = (int) Math.round(Mth.clamp(light * type.r() + (type.rOffset() * 255D), 0D, 255D));
                    g = (int) Math.round(Mth.clamp(light * type.g() + (type.gOffset() * 255D), 0D, 255D));
                    b = (int) Math.round(Mth.clamp(light * type.b() + (type.bOffset() * 255D), 0D, 255D));

                    nativeImage.setPixelRGBA(x, y, FastColor.ARGB32.color(a, b, g, r));
                }
            }

            DynamicTexture dynamicTexture = new DynamicTexture(nativeImage);

            return minecraft.getTextureManager().register("undeadskin/" + location.getPath(), dynamicTexture);
        });
    }
}