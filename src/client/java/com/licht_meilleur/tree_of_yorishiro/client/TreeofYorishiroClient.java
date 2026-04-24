package com.licht_meilleur.tree_of_yorishiro.client;

import com.licht_meilleur.tree_of_yorishiro.client.block.*;
import com.licht_meilleur.tree_of_yorishiro.client.entity.*;
import com.licht_meilleur.tree_of_yorishiro.client.screen.TreeOfYorishiroScreen;
import com.licht_meilleur.tree_of_yorishiro.client.screen.YorisyokuninTradeScreen;
import com.licht_meilleur.tree_of_yorishiro.registry.ModBlockEntities;
import com.licht_meilleur.tree_of_yorishiro.registry.ModBlocks;
import com.licht_meilleur.tree_of_yorishiro.registry.ModEntities;
import com.licht_meilleur.tree_of_yorishiro.screen.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;

public class TreeofYorishiroClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(ModBlockEntities.TREE_OF_YORISHIRO, ctx -> new TreeOfYorishiroRenderer());
        EntityRendererRegistry.register(ModEntities.CHIBISHIRO, ChibishiroRenderer::new);
        HandledScreens.register(ModScreenHandlers.TREE_OF_YORISHIRO, TreeOfYorishiroScreen::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.YORISHIRO_STONE, RenderLayer.getCutout());
        BlockEntityRendererRegistry.register(ModBlockEntities.SYOKUNIN_DESK, ctx -> new SyokuninDeskRenderer());
        EntityRendererRegistry.register(ModEntities.YORISYOKUNIN, YorisyokuninRenderer::new);
        HandledScreens.register(ModScreenHandlers.YORISYOKUNIN_TRADE, YorisyokuninTradeScreen::new);
    }
}