package com.licht_meilleur.tree_of_yorishiro.client;

import com.licht_meilleur.tree_of_yorishiro.client.block.GrowingTreeOfYorishiroRenderer;
import com.licht_meilleur.tree_of_yorishiro.client.block.SyokuninDeskRenderer;
import com.licht_meilleur.tree_of_yorishiro.client.block.TreeOfYorishiroRenderer;
import com.licht_meilleur.tree_of_yorishiro.client.entity.ChibishiroRenderer;
import com.licht_meilleur.tree_of_yorishiro.client.entity.YorisyokuninRenderer;
import com.licht_meilleur.tree_of_yorishiro.client.screen.TreeOfYorishiroScreen;
import com.licht_meilleur.tree_of_yorishiro.client.screen.YorisyokuninTradeScreen;
import com.licht_meilleur.tree_of_yorishiro.registry.ModBlockEntities;
import com.licht_meilleur.tree_of_yorishiro.registry.ModEntities;
import com.licht_meilleur.tree_of_yorishiro.screen.ModScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class TreeofYorishiroClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRenderers.register(
                ModBlockEntities.GROWING_TREE_OF_YORISHIRO,
                GrowingTreeOfYorishiroRenderer::new
        );

        BlockEntityRenderers.register(
                ModBlockEntities.TREE_PART,
                TreeOfYorishiroRenderer::new
        );


        BlockEntityRenderers.register(
                ModBlockEntities.SYOKUNIN_DESK,
                SyokuninDeskRenderer::new
        );


        EntityRendererRegistry.register(
                ModEntities.CHIBISHIRO,
                ChibishiroRenderer::new
        );

        EntityRendererRegistry.register(
                ModEntities.YORISYOKUNIN,
                YorisyokuninRenderer::new
        );

        MenuScreens.register(
                ModScreenHandlers.TREE_OF_YORISHIRO,
                TreeOfYorishiroScreen::new
        );

        MenuScreens.register(
                ModScreenHandlers.YORISYOKUNIN_TRADE,
                YorisyokuninTradeScreen::new
        );

    }
}