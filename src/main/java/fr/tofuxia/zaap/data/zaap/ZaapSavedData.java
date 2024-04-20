package fr.tofuxia.zaap.data.zaap;

import java.util.Collection;
import java.util.HashMap;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class ZaapSavedData extends SavedData {

    public static final String SAVE_KEY = "zaap";
    private HashMap<ResourceLocation, ZaapData> zaaps;

    public ZaapSavedData() {
        zaaps = new HashMap<>();
    }

    public static ZaapSavedData create() {
        return new ZaapSavedData();
    }

    public Collection<ZaapData> getZaaps() {
        return zaaps.values();
    }

    public static ZaapSavedData fromLevel(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new Factory<>(ZaapSavedData::create, (tag) -> load(tag)),
                SAVE_KEY);
    }

    public static ZaapSavedData load(CompoundTag tag) {
        ZaapSavedData zaapSavedData = create();
        tag.getAllKeys().forEach(resourceLoc -> {
            int x = 0, y = 0, z = 0;
            CompoundTag posTag = tag.getCompound(resourceLoc);
            if (posTag.contains("x"))
                x = posTag.getInt("x");
            if (posTag.contains("y"))
                y = posTag.getInt("y");
            if (posTag.contains("z"))
                z = posTag.getInt("z");
            ZaapData zaapData = new ZaapData(new ResourceLocation(resourceLoc), new BlockPos(x, y, z));
            zaapSavedData.zaaps.put(new ResourceLocation(resourceLoc), zaapData);
        });
        return zaapSavedData;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        zaaps.forEach((resourceLoc, zaapData) -> {
            CompoundTag posTag = new CompoundTag();
            posTag.putInt("x", zaapData.getPos().getX());
            posTag.putInt("y", zaapData.getPos().getY());
            posTag.putInt("z", zaapData.getPos().getZ());
            tag.put(resourceLoc.toString(), posTag);
        });
        return tag;
    }

    public Iterable<ResourceLocation> getZaapIds() {
        return zaaps.keySet();
    }

    public ZaapData getZaap(ResourceLocation argument) {
        return zaaps.get(argument);
    }

    public void addZaap(ZaapData data) {
        zaaps.put(data.getID(), data);
        setDirty();
    }

    public void removeZaap(ZaapData data) {
        zaaps.remove(data.getID());
        setDirty();
    }

}
