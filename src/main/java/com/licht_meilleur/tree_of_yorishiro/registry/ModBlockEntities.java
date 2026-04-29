package com.licht_meilleur.tree_of_yorishiro.registry;

import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import com.licht_meilleur.tree_of_yorishiro.block.entity.GrowingTreeOfYorishiroBlockEntity;
import com.licht_meilleur.tree_of_yorishiro.block.entity.SyokuninDeskBlockEntity;
import com.licht_meilleur.tree_of_yorishiro.block.entity.TreeOfYorishiroBlockEntity;
import com.licht_meilleur.tree_of_yorishiro.block.entity.TreeOfYorishiroPartBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {

    public static final BlockEntityType<TreeOfYorishiroBlockEntity> TREE_OF_YORISHIRO =
            Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    TreeofYorishiroMod.id("tree_of_yorishiro"),
                    FabricBlockEntityTypeBuilder.create(
                            TreeOfYorishiroBlockEntity::new,
                            ModBlocks.TREE_OF_YORISHIRO_UNDER // ←ここ重要
                    ).build()
            );

    public static final BlockEntityType<GrowingTreeOfYorishiroBlockEntity> GROWING_TREE_OF_YORISHIRO =
            Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    TreeofYorishiroMod.id("growing_tree_of_yorishiro"),
                    FabricBlockEntityTypeBuilder.create(
                            GrowingTreeOfYorishiroBlockEntity::new,
                            ModBlocks.GROWING_TREE_OF_YORISHIRO
                    ).build()
            );

    public static final BlockEntityType<TreeOfYorishiroPartBlockEntity> TREE_PART =
            Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    TreeofYorishiroMod.id("tree_part"),
                    FabricBlockEntityTypeBuilder.create(
                            TreeOfYorishiroPartBlockEntity::new,
                            ModBlocks.TREE_OF_YORISHIRO_UNDER,
                            ModBlocks.TREE_OF_YORISHIRO_MIDDLE,
                            ModBlocks.TREE_OF_YORISHIRO_TOP
                    ).build()
            );

    public static final BlockEntityType<SyokuninDeskBlockEntity> SYOKUNIN_DESK =
            Registry.register(
                    BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    TreeofYorishiroMod.id("syokunin_desk"),
                    FabricBlockEntityTypeBuilder.create(
                            SyokuninDeskBlockEntity::new,
                            ModBlocks.SYOKUNIN_DESK
                    ).build()
            );



    public static void register() {
        TreeofYorishiroMod.LOGGER.info("[TreeOfYorishiro] Registering block entities");
    }
}