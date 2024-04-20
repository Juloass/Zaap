package fr.tofuxia.zaap.attachments;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.resources.ResourceLocation;

public class ZaapMemory {
    private final Set<ResourceLocation> zaapLocations;

    public ZaapMemory() {
        this.zaapLocations = new HashSet<>();
    }

    public Set<ResourceLocation> getZaapLocations() {
        return zaapLocations;
    }

    public void addZaapLocation(ResourceLocation location) {
        zaapLocations.add(location);
    }

    public void removeZaapLocation(ResourceLocation location) {
        zaapLocations.remove(location);
    }

    public boolean knowsZaapLocation(ResourceLocation location) {
        return zaapLocations.contains(location);
    }

}