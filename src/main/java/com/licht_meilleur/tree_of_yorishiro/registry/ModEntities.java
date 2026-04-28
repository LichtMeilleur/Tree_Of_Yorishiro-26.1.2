package com.licht_meilleur.tree_of_yorishiro.registry;

import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroEntity;
import com.licht_meilleur.tree_of_yorishiro.entity.YorisyokuninEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {

    private static final ResourceKey<EntityType<?>> CHIBISHIRO_KEY =
            ResourceKey.create(Registries.ENTITY_TYPE, TreeofYorishiroMod.id("chibishiro"));

    private static final ResourceKey<EntityType<?>> YORISYOKUNIN_KEY =
            ResourceKey.create(Registries.ENTITY_TYPE, TreeofYorishiroMod.id("yorisyokunin"));

    public static final EntityType<ChibishiroEntity> CHIBISHIRO = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            TreeofYorishiroMod.id("chibishiro"),
            FabricEntityTypeBuilder.create(MobCategory.CREATURE, ChibishiroEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 0.9f))
                    .trackRangeBlocks(80)
                    .trackedUpdateRate(3)
                    .build(CHIBISHIRO_KEY)
    );

    public static final EntityType<YorisyokuninEntity> YORISYOKUNIN = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            TreeofYorishiroMod.id("yorisyokunin"),
            FabricEntityTypeBuilder.create(MobCategory.MISC, YorisyokuninEntity::new)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.6f))
                    .trackRangeBlocks(64)
                    .trackedUpdateRate(2)
                    .build(YORISYOKUNIN_KEY)
    );

    public static void register() {
        FabricDefaultAttributeRegistry.register(CHIBISHIRO, ChibishiroEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(YORISYOKUNIN, YorisyokuninEntity.createAttributes());

        TreeofYorishiroMod.LOGGER.info("[TreeOfYorishiro] Registering entities");
    }
}