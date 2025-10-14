package io.github.ziederziet.beundead.config;

import io.github.ziederziet.beundead.BeUndead;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = BeUndead.MODID)
public class ClientModConfig implements ConfigData, ClientConfigAccessor {
    public boolean zombieSoundsPlayers = true;
    @Override
    public boolean hasZombieSoundsPlayers() {
        return zombieSoundsPlayers;
    }
}
