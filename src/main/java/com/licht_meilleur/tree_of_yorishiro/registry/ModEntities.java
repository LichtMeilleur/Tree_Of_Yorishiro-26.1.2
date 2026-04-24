package com.licht_meilleur.tree_of_yorishiro.registry;

import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroEntity;
import com.licht_meilleur.tree_of_yorishiro.entity.YorisyokuninEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModEntities {

    public static final EntityType<ChibishiroEntity> CHIBISHIRO = Registry.register(
            Registries.ENTITY_TYPE,
            TreeofYorishiroMod.id("chibishiro"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, ChibishiroEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 0.9f))
                    .trackRangeBlocks(80)
                    .trackedUpdateRate(3)
                    .build()
    );
    public static final EntityType<YorisyokuninEntity> YORISYOKUNIN =
            Registry.register(Registries.ENTITY_TYPE,
                    TreeofYorishiroMod.id("yorisyokunin"),
                    FabricEntityTypeBuilder.create(SpawnGroup.MISC, YorisyokuninEntity::new)
                            .dimensions(EntityDimensions.fixed(0.6f, 1.6f))
                            .build());


    public static void register() {
        FabricDefaultAttributeRegistry.register(CHIBISHIRO, ChibishiroEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.YORISYOKUNIN, YorisyokuninEntity.createAttributes());
        TreeofYorishiroMod.LOGGER.info("[TreeOfYorishiro] Registering entities");

    }
}