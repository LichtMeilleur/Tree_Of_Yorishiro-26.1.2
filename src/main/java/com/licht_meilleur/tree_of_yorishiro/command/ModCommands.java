package com.licht_meilleur.tree_of_yorishiro.command;

import com.licht_meilleur.tree_of_yorishiro.block.entity.TreeOfYorishiroBlockEntity;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroEntity;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class ModCommands {
    private ModCommands() {}

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("yorishiro_cleanup_orphan")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(ctx -> cleanupOrphans(ctx.getSource())));
        });
    }

    private static int cleanupOrphans(ServerCommandSource source) {
        ServerWorld world = source.getWorld();

        int removed = 0;

        for (ChibishiroEntity chibi : world.getEntitiesByClass(
                ChibishiroEntity.class,
                new Box(-30000000, -64, -30000000, 30000000, 320, 30000000),
                e -> true
        )) {
            UUID homeTreeUuid = chibi.getHomeTreeUuid();
            BlockPos homeTreePos = chibi.getHomeTreePos();

            boolean remove = false;

            if (homeTreeUuid == null || homeTreePos == null) {
                remove = true;
            } else {
                BlockEntity be = world.getBlockEntity(homeTreePos);

                if (!(be instanceof TreeOfYorishiroBlockEntity treeBe)) {
                    remove = true;
                } else if (treeBe.getTreeId() == null || !homeTreeUuid.equals(treeBe.getTreeId())) {
                    remove = true;
                }
            }

            if (remove) {
                chibi.discard();
                removed++;
            }
        }

        int finalRemoved = removed;
        source.sendFeedback(() -> Text.literal("Removed orphan chibishiro: " + finalRemoved), true);
        return finalRemoved;
    }
}