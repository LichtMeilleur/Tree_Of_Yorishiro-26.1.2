package com.licht_meilleur.tree_of_yorishiro.screen;

import com.licht_meilleur.tree_of_yorishiro.block.entity.TreeOfYorishiroPartBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class TreeOfYorishiroMenu extends AbstractContainerMenu {

    private final TreeOfYorishiroPartBlockEntity blockEntity;

    public TreeOfYorishiroMenu(int syncId, Inventory inv, TreeOfYorishiroPartBlockEntity be) {
        super(ModMenus.TREE_OF_YORISHIRO, syncId);
        this.blockEntity = be;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public TreeOfYorishiroPartBlockEntity getBlockEntity() {
        return blockEntity;
    }
}