package io.github.ziederziet.beundead.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;

import java.util.ArrayList;

public class ModCommands {
    private static final ArrayList<BaseCommand> commands = new ArrayList<>();

    public static void registerCommand(final RegisterCommandsEvent event){
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        CommandBuildContext context = event.getBuildContext();

        commands.add(new ZombieCommand("zombie", 2));
//        commands.add(new RedmoonCommand("redmoon", 2));
//        commands.add(new FogCommand("fog", 2));

        commands.forEach(command -> {
            command.register(dispatcher, context);
        });
    }
}
