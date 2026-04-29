package com.licht_meilleur.tree_of_yorishiro.client.block;

import com.geckolib.constant.dataticket.DataTicket;
import com.licht_meilleur.tree_of_yorishiro.block.TreeOfYorishiroPartBlock;

public final class TreeOfYorishiroRenderTickets {

    public static final DataTicket<TreeOfYorishiroPartBlock.Part> TREE_PART =
            DataTicket.create("tree_of_yorishiro_tree_part", TreeOfYorishiroPartBlock.Part.class);

    private TreeOfYorishiroRenderTickets() {
    }

}