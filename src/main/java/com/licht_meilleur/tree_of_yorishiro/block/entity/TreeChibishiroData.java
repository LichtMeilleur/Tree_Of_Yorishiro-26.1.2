package com.licht_meilleur.tree_of_yorishiro.block.entity;

import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroAnimState;
import com.licht_meilleur.tree_of_yorishiro.entity.ChibishiroColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class TreeChibishiroData {

    private ChibishiroColor color;
    private int genki;
    private int kashikosa;
    private int chikara;
    private int stress;
    private boolean training;
    private boolean adventuring;
    private ChibishiroAnimState animState;
    private java.util.UUID entityUuid;

    // 追加
    private String trainingType;
    private int trainingLevel;
    private long trainingEndTick;
    private boolean trainingCompleted;

    private ItemStack displayItem = ItemStack.EMPTY;

    private long adventureEndTick;

    private long trainingLastRewardTick;
    private boolean sleeping;
    private long sleepingSinceTick;


    public TreeChibishiroData(ChibishiroColor color) {
        this.color = color;
        this.genki = 100;
        this.kashikosa = 0;
        this.chikara = 0;
        this.stress = 0;
        this.training = false;
        this.adventuring = false;
        this.animState = ChibishiroAnimState.IDLE;

        // 追加
        this.trainingType = "";
        this.trainingLevel = 0;
        this.trainingEndTick = 0L;
        this.trainingCompleted = false;
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

    public java.util.UUID getEntityUuid() {
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
        this.animState = animState;
    }

    public void setEntityUuid(java.util.UUID entityUuid) {
        this.entityUuid = entityUuid;
    }

    public void setTrainingType(String trainingType) {
        this.trainingType = trainingType;
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
    public long getTrainingLastRewardTick() { return trainingLastRewardTick; }
    public void setTrainingLastRewardTick(long tick) { this.trainingLastRewardTick = tick; }

    public boolean isSleeping() { return sleeping; }
    public void setSleeping(boolean sleeping) { this.sleeping = sleeping; }

    public long getSleepingSinceTick() { return sleepingSinceTick; }
    public void setSleepingSinceTick(long tick) { this.sleepingSinceTick = tick; }

    public void writeNbt(NbtCompound nbt) {
        nbt.putString("Color", color.getId());
        nbt.putInt("Genki", genki);
        nbt.putInt("Kashikosa", kashikosa);
        nbt.putInt("Chikara", chikara);
        nbt.putInt("Stress", stress);
        nbt.putBoolean("Training", training);
        nbt.putBoolean("Adventuring", adventuring);
        nbt.putString("AnimState", animState.name());

        if (entityUuid != null) {
            nbt.putUuid("EntityUuid", entityUuid);
        }

        // 追加
        nbt.putString("TrainingType", trainingType);
        nbt.putInt("TrainingLevel", trainingLevel);
        nbt.putLong("TrainingEndTick", trainingEndTick);
        nbt.putBoolean("TrainingCompleted", trainingCompleted);

        nbt.putLong("AdventureEndTick", adventureEndTick);
        nbt.putLong("trainingLastRewardTick", trainingLastRewardTick);
        nbt.putBoolean("Sleeping", sleeping);
        nbt.putLong("SleepingSinceTick", sleepingSinceTick);
    }

    public static TreeChibishiroData fromNbt(NbtCompound nbt) {
        ChibishiroColor color = switch (nbt.getString("Color")) {
            case "blue" -> ChibishiroColor.BLUE;
            case "yellow" -> ChibishiroColor.YELLOW;
            case "purple" -> ChibishiroColor.PURPLE;
            case "red" -> ChibishiroColor.RED;
            default -> ChibishiroColor.WHITE;
        };

        TreeChibishiroData data = new TreeChibishiroData(color);
        data.genki = nbt.getInt("Genki");
        data.kashikosa = nbt.getInt("Kashikosa");
        data.chikara = nbt.getInt("Chikara");
        data.stress = nbt.getInt("Stress");
        data.training = nbt.getBoolean("Training");
        data.adventuring = nbt.getBoolean("Adventuring");

        String animName = nbt.getString("AnimState");
        try {
            data.animState = animName.isEmpty()
                    ? ChibishiroAnimState.IDLE
                    : ChibishiroAnimState.valueOf(animName);
        } catch (IllegalArgumentException ignored) {
            data.animState = ChibishiroAnimState.IDLE;
        }

        if (nbt.containsUuid("EntityUuid")) {
            data.entityUuid = nbt.getUuid("EntityUuid");
        }

        // 追加
        data.trainingType = nbt.getString("TrainingType");
        data.trainingLevel = nbt.getInt("TrainingLevel");
        data.trainingEndTick = nbt.getLong("TrainingEndTick");
        data.trainingCompleted = nbt.getBoolean("TrainingCompleted");

        data.adventureEndTick = nbt.getLong("AdventureEndTick");
        data.trainingLastRewardTick = nbt.getLong("trainingLastRewardTick");
        data.sleeping = nbt.getBoolean("Sleeping");
        data.sleepingSinceTick = nbt.getLong("SleepingSinceTick");


        return data;
    }

    public long getAdventureEndTick() {
        return adventureEndTick;
    }

    public void setAdventureEndTick(long adventureEndTick) {
        this.adventureEndTick = adventureEndTick;
    }
}