package com.skyeshade.fortunatewood;

import com.skyeshade.fortunatewood.loot.ModLootModifiers;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

import net.neoforged.neoforge.event.server.ServerStartingEvent;



@Mod(FortunateWood.MODID)
public class FortunateWood {

    public static final String MODID = "fortunatewood";


    // main method
    public FortunateWood(IEventBus modEventBus, ModContainer modContainer) {

        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);


        ModLootModifiers.GLM_CODECS.register(modEventBus);

    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {


    }
}
