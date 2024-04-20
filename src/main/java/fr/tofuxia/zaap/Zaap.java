package fr.tofuxia.zaap;

import org.slf4j.Logger;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;

import fr.tofuxia.zaap.attachments.ZaapMemory;
import fr.tofuxia.zaap.attachments.ZaapMemorySerializer;
import fr.tofuxia.zaap.commands.ZaapCommand;
import fr.tofuxia.zaap.commands.arguments.ZaapArgument;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@Mod(Zaap.MODID)
public class Zaap {

    public static final String MODID = "zaap";
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister
            .create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPES = DeferredRegister
            .create(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, MODID);

    private static final ZaapMemorySerializer ZAAP_MEMORY_SERIALIZER = new ZaapMemorySerializer();

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ZaapMemory>> ZAAP_MEMORY = ATTACHMENT_TYPES
            .register(
                    "zaap_memory", () -> AttachmentType.builder(() -> new ZaapMemory())
                            .serialize(ZAAP_MEMORY_SERIALIZER)
                            .copyOnDeath()
                            .build());

    static {
        ARGUMENT_TYPES.register("zaap", () -> ArgumentTypeInfos.registerByClass(ZaapArgument.class,
                SingletonArgumentInfo.contextFree(ZaapArgument::new)));
    }

    public Zaap(IEventBus modEventBus) {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);
        ARGUMENT_TYPES.register(modEventBus);
        ATTACHMENT_TYPES.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

    }

    @SubscribeEvent
    public void onServerStarting(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        new ZaapCommand(dispatcher, event.getBuildContext());
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }
    }
}
