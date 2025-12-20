package io.github.ziederziet.beundead.commands;

import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.ArrayList;

public class ModCommands {
    private static final ArrayList<BaseCommand> commands = new ArrayList<>();

    public static void registerCommands(RegisterCommandsEvent event){
        commands.add(new UndeadCommand("undead", 2));
        commands.add(new UndeadCommand("undeadmode", 2));

        commands.forEach(command -> {
            command.register(event.getDispatcher(), event.getBuildContext());
        });
    }
}
