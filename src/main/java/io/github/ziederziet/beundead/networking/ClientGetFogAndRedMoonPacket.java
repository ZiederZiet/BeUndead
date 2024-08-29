package io.github.ziederziet.beundead.networking;

import io.github.ziederziet.beundead.BeUndead;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class ClientGetFogAndRedMoonPacket {
    private boolean fog;
    private boolean redMoon;

    public ClientGetFogAndRedMoonPacket(boolean fog, boolean redMoon) {
        this.fog = fog;
        this.redMoon = redMoon;
    }

    public ClientGetFogAndRedMoonPacket(FriendlyByteBuf buffer){
        this(buffer.readBoolean(), buffer.readBoolean());
    }

    public void encode(FriendlyByteBuf buffer){
        buffer.writeBoolean(fog);
        buffer.writeBoolean(redMoon);
    }

    public void handle(CustomPayloadEvent.Context context){
        if (context.isClientSide()){
            BeUndead.Mod.setFoggyDay(fog);
            BeUndead.Mod.setRedmoon(redMoon);
        }
    }
}
