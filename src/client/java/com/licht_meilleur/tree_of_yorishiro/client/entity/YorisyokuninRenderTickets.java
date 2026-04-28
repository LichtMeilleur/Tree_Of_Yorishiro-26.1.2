package com.licht_meilleur.tree_of_yorishiro.client.entity;

import com.geckolib.constant.dataticket.DataTicket;
import net.minecraft.world.item.ItemStack;

public final class YorisyokuninRenderTickets {

    public static final DataTicket<ItemStack> HELD_WORK_ITEM =
            DataTicket.create("tree_of_yorishiro_yorisyokunin_held_work_item", ItemStack.class);

    private YorisyokuninRenderTickets() {
    }
}