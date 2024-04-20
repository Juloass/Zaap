package fr.tofuxia.zaap.data.zaap;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class ZaapData {

    private final ResourceLocation id;
    private final BlockPos pos;

    public ZaapData(ResourceLocation resourceLocation, BlockPos pos) {
        this.id = resourceLocation;
        this.pos = pos;
    }

    public ResourceLocation getID() {
        return id;
    }

    public BlockPos getPos() {
        return pos;
    }

}
