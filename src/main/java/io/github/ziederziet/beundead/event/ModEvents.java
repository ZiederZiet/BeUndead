package io.github.ziederziet.beundead.event;

import io.github.ziederziet.beundead.BeUndead;
import io.github.ziederziet.beundead.goal.CreeperMeleeAttackGoal;
import io.github.ziederziet.beundead.goal.GetAwayFromCreeperGoal;
import io.github.ziederziet.beundead.goal.GetAwayFromExplodingTnt;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Iterator;

@Mod.EventBusSubscriber(modid = BeUndead.MODID)
public class ModEvents {
    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event){
        if (event.getEntity() instanceof Monster monster){
            Iterator<WrappedGoal> goals = monster.goalSelector.getAvailableGoals().iterator();
            boolean hasItAlready = false;
            while (!hasItAlready && goals.hasNext()){
                hasItAlready = goals.next().getGoal() instanceof GetAwayFromCreeperGoal;
            }

            if (!hasItAlready && !(event.getEntity() instanceof Creeper)){
                monster.goalSelector.addGoal(0, new GetAwayFromCreeperGoal(monster, 6.0F, 1.0F, 1.2F));
            }monster.goalSelector.addGoal(1, new GetAwayFromExplodingTnt(monster, 6.0F, 1.0F, 1.2F));
        }
        if (event.getEntity() instanceof Creeper creeper){
            creeper.goalSelector.removeAllGoals(goal -> goal instanceof MeleeAttackGoal);
            creeper.goalSelector.addGoal(4, new CreeperMeleeAttackGoal(creeper, 1.0, false));
        }
    }
}
