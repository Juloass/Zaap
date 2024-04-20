package fr.tofuxia.zaap.attachments;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

public class ZaapMemorySerializer implements IAttachmentSerializer<ListTag, ZaapMemory> {

    @Override
    public ListTag write(ZaapMemory memory) {
        ListTag list = new ListTag();
        memory.getZaapLocations().forEach(location -> {
            StringTag tag = StringTag.valueOf(location.toString());
            list.add(tag);
        });
        return list;
    }

    @Override
    public ZaapMemory read(IAttachmentHolder holder, ListTag tag) {
        // Create a new memory object
        ZaapMemory memory = new ZaapMemory();
        // For each tag in the list
        tag.forEach(tagElement -> {
            // If the tag is a string
            if (tagElement instanceof StringTag) {
                // Get the string value
                String value = ((StringTag) tagElement).getAsString();
                // Parse the string value as a ResourceLocation
                ResourceLocation location = ResourceLocation.tryParse(value);
                // If the location is not null
                if (location != null) {
                    // Add the location to the memory object
                    memory.addZaapLocation(location);
                }
            }
        });
        return memory;
    }

}