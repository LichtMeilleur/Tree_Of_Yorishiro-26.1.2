package com.licht_meilleur.tree_of_yorishiro.block.entity;

import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroAnimState;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class TreeChibishiroData {

    public static final int DATA_VERSION = 1;

    private ChibishiroColor color;
    private int genki;
    private int kashikosa;
    private int chikara;
    private int stress;

    private boolean training;
    private boolean adventuring;
    private ChibishiroAnimState animState;
    private UUID entityUuid;

    private String trainingType;
    private int trainingLevel;
    private long trainingEndTick;
    private boolean trainingCompleted;
    private long trainingLastRewardTick;

    private long adventureEndTick;

    private boolean sleeping;
    private long sleepingSinceTick;

    // 今は未保存。後でItemStack保存が必要なら registryAccess を持つ親BE側で保存する
    private ItemStack displayItem = ItemStack.EMPTY;

    public TreeChibishiroData(ChibishiroColor color) {
        this.color = color != null ? color : ChibishiroColor.WHITE;
        this.genki = 100;
        this.kashikosa = 0;
        this.chikara = 0;
        this.stress = 0;
        this.training = false;
        this.adventuring = false;
        this.animState = ChibishiroAnimState.IDLE;
        this.trainingType = "";
        this.trainingLevel = 0;
        this.trainingEndTick = 0L;
        this.trainingCompleted = false;
        this.trainingLastRewardTick = 0L;
        this.adventureEndTick = 0L;
        this.sleeping = false;
        this.sleepingSinceTick = 0L;
    }

    public ChibishiroColor getColor() {
        return color;
    }

    public int getGenki() {
        return genki;
    }

    public int getKashikosa() {
        return kashikosa;
    }

    public int getChikara() {
        return chikara;
    }

    public int getStress() {
        return stress;
    }

    public boolean isTraining() {
        return training;
    }

    public boolean isAdventuring() {
        return adventuring;
    }

    public ChibishiroAnimState getAnimState() {
        return animState;
    }

    public UUID getEntityUuid() {
        return entityUuid;
    }

    public String getTrainingType() {
        return trainingType;
    }

    public int getTrainingLevel() {
        return trainingLevel;
    }

    public long getTrainingEndTick() {
        return trainingEndTick;
    }

    public boolean isTrainingCompleted() {
        return trainingCompleted;
    }

    public long getTrainingLastRewardTick() {
        return trainingLastRewardTick;
    }

    public long getAdventureEndTick() {
        return adventureEndTick;
    }

    public boolean isSleeping() {
        return sleeping;
    }

    public long getSleepingSinceTick() {
        return sleepingSinceTick;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public void setGenki(int genki) {
        this.genki = genki;
    }

    public void setKashikosa(int kashikosa) {
        this.kashikosa = kashikosa;
    }

    public void setChikara(int chikara) {
        this.chikara = chikara;
    }

    public void setStress(int stress) {
        this.stress = stress;
    }

    public void setTraining(boolean training) {
        this.training = training;
    }

    public void setAdventuring(boolean adventuring) {
        this.adventuring = adventuring;
    }

    public void setAnimState(ChibishiroAnimState animState) {
        this.animState = animState != null ? animState : ChibishiroAnimState.IDLE;
    }

    public void setEntityUuid(UUID entityUuid) {
        this.entityUuid = entityUuid;
    }

    public void setTrainingType(String trainingType) {
        this.trainingType = trainingType != null ? trainingType : "";
    }

    public void setTrainingLevel(int trainingLevel) {
        this.trainingLevel = trainingLevel;
    }

    public void setTrainingEndTick(long trainingEndTick) {
        this.trainingEndTick = trainingEndTick;
    }

    public void setTrainingCompleted(boolean trainingCompleted) {
        this.trainingCompleted = trainingCompleted;
    }

    public void setTrainingLastRewardTick(long tick) {
        this.trainingLastRewardTick = tick;
    }

    public void setAdventureEndTick(long adventureEndTick) {
        this.adventureEndTick = adventureEndTick;
    }

    public void setSleeping(boolean sleeping) {
        this.sleeping = sleeping;
    }

    public void setSleepingSinceTick(long tick) {
        this.sleepingSinceTick = tick;
    }

    public void setDisplayItem(ItemStack displayItem) {
        this.displayItem = displayItem == null ? ItemStack.EMPTY : displayItem.copy();
    }

    public void save(CompoundTag tag) {
        tag.putInt("DataVersion", DATA_VERSION);

        tag.putString("Color", color.getId());
        tag.putInt("Genki", genki);
        tag.putInt("Kashikosa", kashikosa);
        tag.putInt("Chikara", chikara);
        tag.putInt("Stress", stress);

        tag.putBoolean("Training", training);
        tag.putBoolean("Adventuring", adventuring);
        tag.putString("AnimState", animState.name());

        if (entityUuid != null) {
            tag.putString("EntityUuid", entityUuid.toString());
        }

        tag.putString("TrainingType", trainingType);
        tag.putInt("TrainingLevel", trainingLevel);
        tag.putLong("TrainingEndTick", trainingEndTick);
        tag.putBoolean("TrainingCompleted", trainingCompleted);
        tag.putLong("TrainingLastRewardTick", trainingLastRewardTick);

        tag.putLong("AdventureEndTick", adventureEndTick);

        tag.putBoolean("Sleeping", sleeping);
        tag.putLong("SleepingSinceTick", sleepingSinceTick);
    }

    public static TreeChibishiroData load(CompoundTag tag) {
        int version = tag.getInt("DataVersion").orElse(0);

        String colorId = tag.getString("Color").orElse("white");
        ChibishiroColor color = switch (colorId) {
            case "blue" -> ChibishiroColor.BLUE;
            case "yellow" -> ChibishiroColor.YELLOW;
            case "purple" -> ChibishiroColor.PURPLE;
            case "red" -> ChibishiroColor.RED;
            default -> ChibishiroColor.WHITE;
        };

        TreeChibishiroData data = new TreeChibishiroData(color);

        data.genki = tag.getInt("Genki").orElse(100);
        data.kashikosa = tag.getInt("Kashikosa").orElse(0);
        data.chikara = tag.getInt("Chikara").orElse(0);
        data.stress = tag.getInt("Stress").orElse(0);

        data.training = tag.getBoolean("Training").orElse(false);
        data.adventuring = tag.getBoolean("Adventuring").orElse(false);

        String animName = tag.getString("AnimState").orElse("IDLE");
        try {
            data.animState = animName.isEmpty()
                    ? ChibishiroAnimState.IDLE
                    : ChibishiroAnimState.valueOf(animName);
        } catch (IllegalArgumentException ignored) {
            data.animState = ChibishiroAnimState.IDLE;
        }

        String uuidString = tag.getString("EntityUuid").orElse("");
        if (!uuidString.isEmpty()) {
            try {
                data.entityUuid = UUID.fromString(uuidString);
            } catch (IllegalArgumentException ignored) {
                data.entityUuid = null;
            }
        }

        data.trainingType = tag.getString("TrainingType").orElse("");
        data.trainingLevel = tag.getInt("TrainingLevel").orElse(0);
        data.trainingEndTick = tag.getLong("TrainingEndTick").orElse(0L);
        data.trainingCompleted = tag.getBoolean("TrainingCompleted").orElse(false);

        if (version <= 0) {
            data.trainingLastRewardTick = tag.getLong("trainingLastRewardTick").orElse(0L);
        } else {
            data.trainingLastRewardTick = tag.getLong("TrainingLastRewardTick").orElse(0L);
        }

        data.adventureEndTick = tag.getLong("AdventureEndTick").orElse(0L);

        data.sleeping = tag.getBoolean("Sleeping").orElse(false);
        data.sleepingSinceTick = tag.getLong("SleepingSinceTick").orElse(0L);

        return data;
    }

    // 呼び出し側の移行を楽にするための互換メソッド
    public void writeNbt(CompoundTag tag) {
        save(tag);
    }

    public static TreeChibishiroData fromNbt(CompoundTag tag) {
        return load(tag);
    }
}