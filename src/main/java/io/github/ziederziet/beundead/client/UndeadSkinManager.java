package io.github.ziederziet.beundead.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.NativeImage;
import io.github.ziederziet.beundead.api.BeUndeadApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UndeadSkinManager {
//    public static final Vec3[] ZOMBIE_COLORS = new Vec3[] { new Vec3(0.8D, 1.0D, 0.85D), new Vec3(0.7D, 0.8D, 0.8D), new Vec3(0.80D, 0.72D, 0.49D) };
//    public static final Vec3[] ZOMBIE_COLOR_OFFSETS = new Vec3[] { new Vec3(0.0D, 0.1D, 0.0D), new Vec3(-0.1D, 0.1D, 0.1D), new Vec3(0.03D, 0.05D, 0.0D) };



    private static final Map<ResourceLocation, ResourceLocation> modifiedZombieSkins = new HashMap<>();
    private static final Map<ResourceLocation, ResourceLocation> modifiedHuskSkins = new HashMap<>();
    private static final Map<ResourceLocation, ResourceLocation> modifiedDrownedSkins = new HashMap<>();

    public static void removeSkin(ResourceLocation ofResource){
        modifiedZombieSkins.remove(ofResource);
        modifiedHuskSkins.remove(ofResource);
        modifiedDrownedSkins.remove(ofResource);
    }

    public static void removeAll(){
        modifiedZombieSkins.clear();
        modifiedHuskSkins.clear();
        modifiedDrownedSkins.clear();
    }


    public static ResourceLocation getOrCreateSkin(ResourceLocation defaultLocation, int type, AbstractClientPlayer player) {
        Map<ResourceLocation, ResourceLocation> map = switch (type) {
            case 2 -> modifiedHuskSkins;
            case 3 -> modifiedDrownedSkins;
            default -> modifiedZombieSkins;
        };

        return map.computeIfAbsent(defaultLocation, location -> {
            Minecraft minecraft = Minecraft.getInstance();

            Optional<Resource> optional = minecraft.getResourceManager().getResource(defaultLocation);

            BufferedImage skinImage = null;
            try {
                if (optional.isPresent()){
                    skinImage = ImageIO.read(optional.get().open());
                }
                else {
                    String uuidStr = player.getUUID().toString().replace("-", "");
                    String profileUrlStr = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuidStr;
                    URL profileUrl = new URL(profileUrlStr);
                    HttpURLConnection profileConnection = (HttpURLConnection) profileUrl.openConnection();
                    //connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                    profileConnection.connect();

                    InputStream profileInputStream = profileConnection.getInputStream();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(profileInputStream));
                    StringBuilder json = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        json.append(line);
                    }

                    JsonObject response = new Gson().fromJson(json.toString(), JsonObject.class);

                    System.out.println("RESPAWNE: " + response);

                    //skinImage = ImageIO.read(inputStream);
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

                    r = (int) Math.round(Math.min(Math.max(light * BeUndeadApi.ZOMBIE_COLORS[type - 1].x + (BeUndeadApi.ZOMBIE_COLOR_OFFSETS[type - 1].x * 255D), 0D), 255D));
                    g = (int) Math.round(Math.min(Math.max(light * BeUndeadApi.ZOMBIE_COLORS[type - 1].y + (BeUndeadApi.ZOMBIE_COLOR_OFFSETS[type - 1].y * 255D), 0D), 255D));
                    b = (int) Math.round(Math.min(Math.max(light * BeUndeadApi.ZOMBIE_COLORS[type - 1].z + (BeUndeadApi.ZOMBIE_COLOR_OFFSETS[type - 1].z * 255D), 0D), 255D));

                    nativeImage.setPixelRGBA(x, y, FastColor.ARGB32.color(a, r, g, b));
                }
            }

            DynamicTexture dynamicTexture = new DynamicTexture(nativeImage);

            return minecraft.getTextureManager().register("undeadskin" + type + "/" + location.getPath(), dynamicTexture);

//            if (optional.isPresent()){
//                System.out.println("THERE");
//
//                Resource resource = optional.get();
//
//                try {
//                    NativeImage image = NativeImage.read(resource.open());
//
//                    for (int y = 0; y < image.getHeight(); y++) {
//                        for (int x = 0; x < image.getWidth(); x++) {
//                            int rgba = image.getPixelRGBA(x, y);
//
//                            int a = FastColor.ARGB32.alpha(rgba);
//                            int r = FastColor.ARGB32.red(rgba);
//                            int g = FastColor.ARGB32.green(rgba);
//                            int b = FastColor.ARGB32.blue(rgba);
//
//                            double light = (r * 0.85 + g + b * 0.7) / 2.55;
//
//                            r = (int) Math.round(Math.clamp(light * ZOMBIE_COLORS[type - 1].x + (ZOMBIE_COLOR_OFFSETS[type - 1].x * 255D), 0D, 255D));
//                            g = (int) Math.round(Math.clamp(light * ZOMBIE_COLORS[type - 1].y + (ZOMBIE_COLOR_OFFSETS[type - 1].y * 255D), 0D, 255D));
//                            b = (int) Math.round(Math.clamp(light * ZOMBIE_COLORS[type - 1].z + (ZOMBIE_COLOR_OFFSETS[type - 1].z * 255D), 0D, 255D));
//
//                            image.setPixelRGBA(x, y, FastColor.ARGB32.color(a, r, g, b));
//                        }
//                    }
//
//                    DynamicTexture dynamicTexture = new DynamicTexture(image);
//
//                    return minecraft.getTextureManager().register("modskin/" + uuid.toString(), dynamicTexture);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }

            //return defaultLocation;
        });
    }
}