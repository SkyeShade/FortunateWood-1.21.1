package com.skyeshade.fortunatewood.loot;




import com.mojang.serialization.Codec;
import com.skyeshade.fortunatewood.data.PlacedLogsData;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.extensions.IItemStackExtension;
import net.neoforged.neoforge.common.loot.LootModifier;


import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class AxeFortuneLogsModifier extends LootModifier {
    private final int bonusMultiplier;
    private static final org.slf4j.Logger LOG = com.mojang.logging.LogUtils.getLogger();
    public static final MapCodec<AxeFortuneLogsModifier> CODEC =
            RecordCodecBuilder.mapCodec(inst ->
                    LootModifier.codecStart(inst)
                            .and(
                                    Codec.INT
                                            .optionalFieldOf("bonus_multiplier", 1)
                                            .forGetter(m -> m.bonusMultiplier)
                            )
                            .apply(inst, AxeFortuneLogsModifier::new)
            );

    public AxeFortuneLogsModifier(LootItemCondition[] conditions, int bonusMultiplier) {
        super(conditions);
        this.bonusMultiplier = Math.max(0, bonusMultiplier);
    }

    @Override
    public MapCodec<? extends net.neoforged.neoforge.common.loot.IGlobalLootModifier> codec() {
        return CODEC;
    }



    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext ctx) {
        var state = ctx.getParamOrNull(LootContextParams.BLOCK_STATE);
        var tool  = ctx.getParamOrNull(LootContextParams.TOOL);
        if (state == null || tool == null) {
            //LOG.info("[FW] GLM: missing state/tool");
            return generatedLoot;
        }

        if (!state.is(BlockTags.LOGS)) {
            //LOG.info("[FW] GLM: not a log");
            return generatedLoot;
        }

        // ---- Position debug
        var origin = ctx.getParamOrNull(LootContextParams.ORIGIN);
        if (!(ctx.getLevel() instanceof ServerLevel sLevel)) {
            //LOG.info("[FW] GLM: not server level");
            return generatedLoot;
        }
        if (origin == null) {
            //LOG.info("[FW] GLM: ORIGIN is null");
            return generatedLoot;
        }
        var pos = BlockPos.containing(origin);
        boolean marked = PlacedLogsData.get(sLevel).isMarked(pos);
        if (marked) {
            // Player-placed: skip bonus AND clean up the mark now that it’s broken.
            PlacedLogsData.get(sLevel).unmark(pos);
            return generatedLoot;
        }
        //LOG.info("[FW] GLM: pos={} marked={}", pos, marked);
        if (marked) return generatedLoot; // player-placed -> no bonus

        // Tool checks
        boolean isAxe = tool.is(ItemTags.AXES) || tool.getItem() instanceof net.minecraft.world.item.AxeItem;
        if (!isAxe) {
            //LOG.info("[FW] GLM: tool not axe {}", tool.getItem());
            return generatedLoot;
        }

        // Enchant level
        var enchLookup = ctx.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        var fortune    = enchLookup.getOrThrow(Enchantments.FORTUNE);
        int level = ((IItemStackExtension)(Object) tool).getEnchantmentLevel(fortune);
        //LOG.info("[FW] GLM: fortune={}", level);
        if (level <= 0 || bonusMultiplier <= 0) return generatedLoot;

        int extra = ctx.getRandom().nextInt(level * bonusMultiplier + 1);
        //LOG.info("[FW] GLM: extra={}", extra);
        if (extra > 0) generatedLoot.add(new ItemStack(state.getBlock().asItem(), extra));
        return generatedLoot;
    }
}