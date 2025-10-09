package io.github.ziederziet.beundead.config;

import me.shedaniel.autoconfig.AutoConfig;

public interface ClientConfigAccessor {
    static ClientConfigAccessor getConfig(){
        return AutoConfig.getConfigHolder(ClientModConfig.class).getConfig();
    }
    boolean hasZombieSoundsPlayers();
}
