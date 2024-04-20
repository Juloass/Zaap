package fr.tofuxia.zaap;

import fr.tofuxia.zaap.data.zaap.ZaapData;
import fr.tofuxia.zaap.data.zaap.ZaapSavedData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData.Factory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

@Mod.EventBusSubscriber(modid = Zaap.MODID)
public class ZaapEvents {
    
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {

        // Check if the discover feature is enabled
        if(!Config.enableDiscover) return;

        LivingEntity entity = event.getEntity();
        // Verify that the entity is a player
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        // Verify that the player is Server side
        if(!(player instanceof ServerPlayer)) {
            return;
        }

        ServerPlayer serverPlayer = (ServerPlayer) player;
        // We only do the zaap proximity check every 20 ticks to avoid lag
        if (serverPlayer.tickCount % 20 != 0) {
            return;
        }

        // Get the player's position
        double x = serverPlayer.getX();
        double y = serverPlayer.getY();
        double z = serverPlayer.getZ();
        // Get the player's world
        ServerLevel level = (ServerLevel) serverPlayer.level();
        ZaapSavedData data = level.getDataStorage().computeIfAbsent(
            new Factory<>(ZaapSavedData::create, (tag) -> ZaapSavedData.load(tag)),
            ZaapSavedData.SAVE_KEY);
        // Check if the player is near a zaap
        for(ZaapData zaap : data.getZaaps()) {
            // We verify that the player does not already have the zaap in memory
            if (serverPlayer.getData(Zaap.ZAAP_MEMORY).knowsZaapLocation(zaap.getID())) {
                continue;
            }
            double distance = distanceTo(zaap, x, y, z);
            if (distance < 10) {
                // If the player is near a zaap, we add that zaap to the player's memory
                serverPlayer.getData(Zaap.ZAAP_MEMORY).addZaapLocation(zaap.getID());
                serverPlayer.sendSystemMessage(Component.translatable("zaap.memory.added", Component.translatable("zaaps." + zaap.getID().toString().replace(":", "."))));
                // If the player is near a zaap, we dont need to check the other zaaps
                return;
            }
        }
    }

    private static double distanceTo(ZaapData zaap, double x, double y, double z) {
        double dx = zaap.getPos().getX() - x;
        double dy = zaap.getPos().getY() - y;
        double dz = zaap.getPos().getZ() - z;

        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

}
