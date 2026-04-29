package com.licht_meilleur.tree_of_yorishiro.block.entity;

import com.geckolib.animatable.GeoBlockEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.util.GeckoLibUtil;
import com.licht_meilleur.tree_of_yorishiro.block.TreeOfYorishiroPartBlock;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroColor;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroEntity;
import com.licht_meilleur.tree_of_yorishiro.registry.ModBlockEntities;
import com.licht_meilleur.tree_of_yorishiro.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TreeOfYorishiroPartBlockEntity extends BlockEntity implements GeoBlockEntity, MenuProvider {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private final List<TreeChibishiroData> chibis = new ArrayList<>();
    private boolean initialized = false;
    private int summonCheckCooldown = 0;
    private UUID treeId = UUID.randomUUID();

    public TreeOfYorishiroPartBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TREE_PART, pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Yorishiro Tree");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new TreeOfYorishiroMenu(syncId, playerInventory, this);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TreeOfYorishiroPartBlockEntity be) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        if (level.isClientSide()) {
            return;
        }

        if (be.getPart() != TreeOfYorishiroPartBlock.Part.UNDER) {
            return;
        }

        be.initDefaultChibisIfNeeded();

        if (be.summonCheckCooldown > 0) {
            be.summonCheckCooldown--;
            return;
        }


        be.cleanupOrphanChibis(serverLevel);
        be.summonCheckCooldown = 40;
        be.ensureChibishiros();
    }

    public TreeOfYorishiroPartBlock.Part getPart() {
        if (this.getBlockState().getBlock() instanceof TreeOfYorishiroPartBlock partBlock) {
            return partBlock.getPart();
        }

        return TreeOfYorishiroPartBlock.Part.UNDER;
    }

    public UUID getTreeId() {
        return this.treeId;
    }

    public List<TreeChibishiroData> getChibis() {
        return this.chibis;
    }

    public void initDefaultChibisIfNeeded() {
        if (initialized) {
            return;
        }

        if (!chibis.isEmpty()) {
            initialized = true;
            setChanged();
            return;
        }

        chibis.add(new TreeChibishiroData(ChibishiroColor.WHITE));
        chibis.add(new TreeChibishiroData(ChibishiroColor.RED));
        chibis.add(new TreeChibishiroData(ChibishiroColor.BLUE));
        chibis.add(new TreeChibishiroData(ChibishiroColor.YELLOW));
        chibis.add(new TreeChibishiroData(ChibishiroColor.PURPLE));

        initialized = true;
        setChanged();
    }

    public void ensureChibishiros() {
        if (!(this.level instanceof ServerLevel serverLevel)) return;

        cleanupOrphanChibis(serverLevel);

        for (TreeChibishiroData data : this.chibis) {

            ChibishiroEntity found = null;

            if (data.getEntityUuid() != null) {
                Entity entity = serverLevel.getEntity(data.getEntityUuid());

                if (entity instanceof ChibishiroEntity chibi && chibi.isAlive()) {

                    // ✔ 同じ木かチェック
                    if (this.treeId.equals(chibi.getHomeTreeUuid())
                            && this.worldPosition.equals(chibi.getHomeTreePos())) {

                        found = chibi;

                    } else {
                        // ❌ 消さない！！
                        data.setEntityUuid(null);
                    }
                }
            }

            if (found == null) {
                spawnOneChibi(serverLevel, data);
            }
        }
    }

    private void cleanupOrphanChibis(ServerLevel level) {
        List<ChibishiroEntity> list = level.getEntitiesOfClass(
                ChibishiroEntity.class,
                new AABB(this.worldPosition).inflate(48.0D)
        );

        for (ChibishiroEntity chibi : list) {
            BlockPos homePos = chibi.getHomeTreePos();
            UUID homeUuid = chibi.getHomeTreeUuid();

            if (homePos == null || homeUuid == null) {
                chibi.discard();
                continue;
            }

            if (homePos.equals(this.worldPosition) && !homeUuid.equals(this.treeId)) {
                chibi.discard();
                continue;
            }

            if (homeUuid.equals(this.treeId) && !homePos.equals(this.worldPosition)) {
                chibi.discard();
            }
        }
    }

    private void spawnOneChibi(ServerLevel level, TreeChibishiroData data) {

        ChibishiroEntity chibi = new ChibishiroEntity(ModEntities.CHIBISHIRO, level);

        chibi.setColor(data.getColor());
        chibi.setHomeTreePos(this.worldPosition);
        chibi.setHomeTreeUuid(this.treeId);

        double x = this.worldPosition.getX() + 0.5 + (level.getRandom().nextDouble() - 0.5) * 2.0;
        double y = this.worldPosition.getY() + 1.0;
        double z = this.worldPosition.getZ() + 0.5 + (level.getRandom().nextDouble() - 0.5) * 2.0;

        chibi.setPos(x, y, z);

        level.addFreshEntity(chibi);

        data.setEntityUuid(chibi.getUUID());
    }

    public void removeAllChibishiros() {
        if (!(this.level instanceof ServerLevel serverLevel)) {
            return;
        }

        List<ChibishiroEntity> list = serverLevel.getEntitiesOfClass(
                ChibishiroEntity.class,
                new AABB(this.worldPosition).inflate(48.0D)
        );

        for (ChibishiroEntity chibi : list) {
            BlockPos homePos = chibi.getHomeTreePos();
            UUID homeUuid = chibi.getHomeTreeUuid();

            if (homePos == null || homeUuid == null) {
                chibi.discard();
                continue;
            }

            if (homePos.equals(this.worldPosition) || homeUuid.equals(this.treeId)) {
                chibi.discard();
            }
        }

        for (TreeChibishiroData data : this.chibis) {
            data.setEntityUuid(null);
        }

        setChanged();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // 今はアニメなし
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        output.putInt("version", 1);
        output.putString("tree_id", this.treeId.toString());
        output.putBoolean("initialized", this.initialized);

        ValueOutput.ValueOutputList list = output.childrenList("chibis");

        for (TreeChibishiroData data : this.chibis) {
            ValueOutput child = list.addChild();

            child.putString("color", data.getColor().name());

            if (data.getEntityUuid() != null) {
                child.putString("entity_uuid", data.getEntityUuid().toString());
            }
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        int version = input.getIntOr("version", 0);

        String treeIdString = input.getStringOr("tree_id", "");
        if (!treeIdString.isEmpty()) {
            try {
                this.treeId = UUID.fromString(treeIdString);
            } catch (Exception ignored) {
                this.treeId = UUID.randomUUID();
            }
        } else {
            this.treeId = UUID.randomUUID();
        }

        this.initialized = input.getBooleanOr("initialized", false);
        this.chibis.clear();

        for (ValueInput child : input.childrenListOrEmpty("chibis")) {
            String colorName = child.getStringOr("color", ChibishiroColor.WHITE.name());

            ChibishiroColor color;
            try {
                color = ChibishiroColor.valueOf(colorName);
            } catch (Exception ignored) {
                color = ChibishiroColor.WHITE;
            }

            TreeChibishiroData data = new TreeChibishiroData(color);

            String uuidString = child.getStringOr("entity_uuid", "");
            if (!uuidString.isEmpty()) {
                try {
                    data.setEntityUuid(UUID.fromString(uuidString));
                } catch (Exception ignored) {
                    data.setEntityUuid(null);
                }
            }

            this.chibis.add(data);
        }

        if (!this.chibis.isEmpty()) {
            this.initialized = true;
        }
    }

}