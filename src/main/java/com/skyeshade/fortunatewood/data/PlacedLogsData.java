package com.skyeshade.fortunatewood.data;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;

public class PlacedLogsData extends SavedData {
    private static final org.slf4j.Logger LOG = com.mojang.logging.LogUtils.getLogger();
    // Per-chunk set of absolute BlockPos (packed as long) that were player-placed logs
    private final Long2ObjectOpenHashMap<LongOpenHashSet> byChunk = new Long2ObjectOpenHashMap<>();


    public static final SavedData.Factory<PlacedLogsData> FACTORY = new SavedData.Factory<>(
            PlacedLogsData::new,
            (tag, lookup) -> PlacedLogsData.load(tag),
            null
    );


    public static PlacedLogsData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, "fortunatewood_placed_logs");
    }


    public void mark(BlockPos pos) {
        long chunkKey = new ChunkPos(pos).toLong();
        byChunk.computeIfAbsent(chunkKey, k -> new LongOpenHashSet()).add(pos.asLong());
        setDirty();
        //LOG.info("[FW] mark   pos={} chunk={}", pos, new ChunkPos(pos));
    }
    public void unmark(BlockPos pos) {
        long chunkKey = new ChunkPos(pos).toLong();
        var set = byChunk.get(chunkKey);
        if (set != null) {
            set.remove(pos.asLong());
            if (set.isEmpty()) byChunk.remove(chunkKey);
            setDirty();
        }
        //LOG.info("[FW] unmark pos={} chunk={}", pos, new ChunkPos(pos));
    }

    public boolean isMarked(BlockPos pos) {
        boolean marked = false;
        var set = byChunk.get(new ChunkPos(pos).toLong());
        if (set != null) marked = set.contains(pos.asLong());
        //LOG.info("[FW] check  pos={} chunk={} -> {}", pos, new ChunkPos(pos), marked);
        return marked;
    }




    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider lookup) {
        ListTag chunkList = new ListTag();
        byChunk.long2ObjectEntrySet().forEach(e -> {
            CompoundTag c = new CompoundTag();
            c.putLong("chunk", e.getLongKey());
            var posSet = e.getValue();
            long[] arr = posSet.toLongArray();
            c.putLongArray("positions", arr);
            chunkList.add(c);
        });
        tag.put("chunks", chunkList);
        return tag;
    }


    public static PlacedLogsData load(CompoundTag tag) {
        PlacedLogsData data = new PlacedLogsData();
        if (!tag.contains("chunks", 9)) {
            return data;
        }
        ListTag chunks = tag.getList("chunks", 10);
        for (int i = 0; i < chunks.size(); i++) {
            CompoundTag c = chunks.getCompound(i);
            long chunkKey = c.getLong("chunk");
            long[] arr = c.getLongArray("positions");
            LongOpenHashSet set = new LongOpenHashSet(arr.length);
            for (long p : arr) set.add(p);
            if (!set.isEmpty()) data.byChunk.put(chunkKey, set);
        }
        return data;
    }

    // ctor
    public PlacedLogsData() {}
}