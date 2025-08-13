package com.skyeshade.fortunatewood;



import com.skyeshade.fortunatewood.data.PlacedLogsData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;


@EventBusSubscriber(modid = FortunateWood.MODID)
public class PlacementTracker {
    private static final org.slf4j.Logger LOG = com.mojang.logging.LogUtils.getLogger();
    @SubscribeEvent
    public static void onPlace(BlockEvent.EntityPlaceEvent e) {
        if (!(e.getLevel() instanceof ServerLevel level)) return;
        if (!(e.getEntity() instanceof Player)) return;
        if (e.getPlacedBlock().is(BlockTags.LOGS)) {
            //LOG.info("[FW] onPlace: {}", e.getPos());
            PlacedLogsData.get(level).mark(e.getPos());
            // Immediate verify read:
            PlacedLogsData.get(level).isMarked(e.getPos());
        }
    }


}