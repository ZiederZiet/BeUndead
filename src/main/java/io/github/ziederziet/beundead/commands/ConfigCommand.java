package io.github.ziederziet.beundead.commands;

import io.github.ziederziet.beundead.api.BeUndeadApi;
import io.github.ziederziet.beundead.config.ConfigAccessor;
import io.github.ziederziet.beundead.networking.ModNetworking;
import net.minecraft.server.MinecraftServer;

public class ConfigCommand extends BaseCommand {
    public ConfigCommand(String name, int permission) {
        super(name, permission);
    }

    private void changed(MinecraftServer server){
        ModNetworking.sendToAllClients(server, ConfigAccessor.getPacket());
    }
}