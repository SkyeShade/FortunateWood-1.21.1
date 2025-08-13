package com.skyeshade.fortunatewood.loot;

import com.mojang.serialization.MapCodec;
import com.skyeshade.fortunatewood.FortunateWood;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModLootModifiers {


    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>>
            GLM_CODECS = DeferredRegister.create(
            net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS,
            FortunateWood.MODID
    );

    public static final Supplier<MapCodec<AxeFortuneLogsModifier>> AXE_FORTUNE_LOGS =
            GLM_CODECS.register("axe_fortune_logs", () -> AxeFortuneLogsModifier.CODEC);

    private ModLootModifiers() {}
}
