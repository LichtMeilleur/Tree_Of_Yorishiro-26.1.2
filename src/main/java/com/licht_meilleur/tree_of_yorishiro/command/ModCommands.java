package com.licht_meilleur.tree_of_yorishiro.command;

import com.licht_meilleur.tree_of_yorishiro.block.entity.TreeOfYorishiroBlockEntity;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroEntity;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

public final class ModCommands {

    private ModCommands() {
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(
                        Commands.literal("yorishiro_cleanup_orphan")
                                .requires(source -> {
                                    var player = source.getPlayer();
                                    return player != null && source.getServer().getPlayerList().isOp(player.nameAndId());
                                })
                                .executes(ctx -> cleanupOrphans(ctx.getSource()))
                )
        );
    }

    private static int cleanupOrphans(CommandSourceStack source) {
        ServerLevel level = source.getLevel();

        int removed = 0;

        for (ChibishiroEntity chibi : level.getEntitiesOfClass(
                ChibishiroEntity.class,
                new AABB(-30000000, -64, -30000000, 30000000, 320, 30000000)
        )) {
            var homeTreePos = chibi.getHomeTreePos();

            boolean remove = false;

            if (homeTreePos == null) {
                remove = true;
            } else {
                BlockEntity be = level.getBlockEntity(homeTreePos);
                if (!(be instanceof TreeOfYorishiroBlockEntity)) {
                    remove = true;
                }
            }

            if (remove) {
                chibi.remove(Entity.RemovalReason.DISCARDED);
                removed++;
            }
        }

        int finalRemoved = removed;

        source.sendSuccess(
                () -> Component.literal("Removed orphan chibishiro: " + finalRemoved),
                true
        );

        return finalRemoved;
    }
}