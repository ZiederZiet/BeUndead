package io.github.ziederziet.beundead.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.ArrayList;

public class ModCommands {
    private static final ArrayList<BaseCommand> commands = new ArrayList<>();

    public static void registerCommands(RegisterCommandsEvent event){
        commands.add(new UndeadCommand("undead", 2));
        commands.add(new ConfigCommand("beundeadconfig", 2));

        commands.forEach(command -> {
            command.register(event.getDispatcher(), event.getBuildContext());
        });
    }
}
