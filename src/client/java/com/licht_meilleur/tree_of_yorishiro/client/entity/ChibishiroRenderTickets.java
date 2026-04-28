package com.licht_meilleur.tree_of_yorishiro.client.entity;

import com.geckolib.constant.dataticket.DataTicket;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroColor;
import net.minecraft.world.item.ItemStack;

public final class ChibishiroRenderTickets {

    public static final DataTicket<ChibishiroColor> COLOR =
            DataTicket.create("tree_of_yorishiro_chibishiro_color", ChibishiroColor.class);

    public static final DataTicket<ItemStack> DISPLAY_FOOD_STACK =
            DataTicket.create("tree_of_yorishiro_display_food_stack", ItemStack.class);

    private ChibishiroRenderTickets() {
    }
}