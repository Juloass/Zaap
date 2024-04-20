package fr.tofuxia.zaap;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@Mod.EventBusSubscriber(modid = Zaap.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {

        private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

        private static final ModConfigSpec.BooleanValue ENABLE_DISCOVER = BUILDER.comment("Enable the discover feature").define("enableDiscover", true);
        private static final ModConfigSpec.BooleanValue ENABLE_COST = BUILDER.comment("Enable the cost feature").define("enableCost", false);
        static final ModConfigSpec SPEC = BUILDER.build();

        public static boolean enableDiscover;
        public static boolean enableCost;

        @SubscribeEvent
        public static void onLoad(final ModConfigEvent event) {
                enableDiscover = ENABLE_DISCOVER.get();
                enableCost = ENABLE_COST.get();
        }

}
