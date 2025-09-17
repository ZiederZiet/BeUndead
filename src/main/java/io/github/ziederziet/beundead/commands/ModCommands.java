package io.github.ziederziet.beundead.commands;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import java.util.ArrayList;

public class ModCommands {
    private static final ArrayList<BaseCommand> commands = new ArrayList<>();

    public static void registerCommands(){
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandBuildContext, commandSelection) -> {
            commands.add(new UndeadCommand("undead", 2));
            commands.add(new ConfigCommand("beundeadconfig", 2));

            commands.forEach(command -> {
                command.register(commandDispatcher, commandBuildContext);
            });
        });
    }
}
