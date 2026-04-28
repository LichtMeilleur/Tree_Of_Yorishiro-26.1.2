package com.licht_meilleur.tree_of_yorishiro.screen;

import com.licht_meilleur.tree_of_yorishiro.TreeofYorishiroMod;
import com.licht_meilleur.tree_of_yorishiro.block.entity.SyokuninDeskBlockEntity;
import com.licht_meilleur.tree_of_yorishiro.block.entity.TreeOfYorishiroBlockEntity;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ModScreenHandlers {

    public static MenuType<TreeOfYorishiroScreenHandler> TREE_OF_YORISHIRO;
    public static MenuType<YorisyokuninTradeScreenHandler> YORISYOKUNIN_TRADE;

    private static boolean REGISTERED = false;

    public static void register() {
        if (REGISTERED) return;
        REGISTERED = true;

        TREE_OF_YORISHIRO = Registry.register(
                BuiltInRegistries.MENU,
                TreeofYorishiroMod.id("tree_of_yorishiro"),
                new ExtendedMenuType<TreeOfYorishiroScreenHandler, TreeOfYorishiroMenuData>(
                        ModScreenHandlers::createTreeOfYorishiro,
                        TreeOfYorishiroMenuData.STREAM_CODEC
                )
        );

        YORISYOKUNIN_TRADE = Registry.register(
                BuiltInRegistries.MENU,
                TreeofYorishiroMod.id("yorisyokunin_trade"),
                new ExtendedMenuType<YorisyokuninTradeScreenHandler, YorisyokuninTradeMenuData>(
                        ModScreenHandlers::createYorisyokuninTrade,
                        YorisyokuninTradeMenuData.STREAM_CODEC
                )
        );

        TreeofYorishiroMod.LOGGER.info("[TreeOfYorishiro] Registering screen handlers");
    }

    private static TreeOfYorishiroScreenHandler createTreeOfYorishiro(
            int syncId,
            Inventory inv,
            TreeOfYorishiroMenuData data
    ) {
        return new TreeOfYorishiroScreenHandler(syncId, inv, data.pos());
    }

    private static YorisyokuninTradeScreenHandler createYorisyokuninTrade(
            int syncId,
            Inventory inv,
            YorisyokuninTradeMenuData data
    ) {
        var level = inv.player.level();

        SyokuninDeskBlockEntity be = null;
        var raw = level.getBlockEntity(data.pos());
        if (raw instanceof SyokuninDeskBlockEntity desk) {
            be = desk;
        }

        return new YorisyokuninTradeScreenHandler(syncId, inv, be, data.pos());
    }
}