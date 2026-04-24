package com.licht_meilleur.tree_of_yorishiro.registry;

import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import com.licht_meilleur.tree_of_yorishiro.block.entity.SyokuninDeskBlockEntity;
import com.licht_meilleur.tree_of_yorishiro.block.entity.TreeOfYorishiroBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModBlockEntities {

    public static final BlockEntityType<TreeOfYorishiroBlockEntity> TREE_OF_YORISHIRO =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    TreeofYorishiroMod.id("tree_of_yorishiro"),
                    FabricBlockEntityTypeBuilder.create(
                            TreeOfYorishiroBlockEntity::new,
                            ModBlocks.TREE_OF_YORISHIRO,
                            ModBlocks.DEBUG_TREE_OF_YORISHIRO
                    ).build()
            );
    public static final BlockEntityType<SyokuninDeskBlockEntity> SYOKUNIN_DESK =
            Registry.register(Registries.BLOCK_ENTITY_TYPE,
                    TreeofYorishiroMod.id("syokunin_desk"),
                    FabricBlockEntityTypeBuilder.create(SyokuninDeskBlockEntity::new,
                            ModBlocks.SYOKUNIN_DESK).build());

    public static void register() {
        TreeofYorishiroMod.LOGGER.info("[TreeOfYorishiro] Registering block entities");
    }
}