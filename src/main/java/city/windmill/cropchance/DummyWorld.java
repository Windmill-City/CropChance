package city.windmill.cropchance;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.*;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;

public class DummyWorld extends World {

    public DummyWorld() {
        super(
            null,
            "Dummy",
            new DummyWorldProvider(),
            new WorldSettings(new WorldInfo(new NBTTagCompound())),
            new Profiler());
        createChunkProvider();
    }

    /**
     * Abstracts
     */
    @Override
    protected IChunkProvider createChunkProvider() {
        if (chunkProvider == null) chunkProvider = new DummyChunkProvider(this);
        return chunkProvider;
    }

    /**
     * Abstracts
     */
    @Override
    protected int func_152379_p() {
        return 0;
    }

    /**
     * Abstracts
     */
    @Override
    public Entity getEntityByID(int p_73045_1_) {
        return null;
    }

    /**
     * Override as we do not generate biomes
     */
    @Override
    public BiomeGenBase getBiomeGenForCoords(int x, int z) {
        return BiomeGenBase.ocean;
    }

    /**
     * Override as archaicFix crash when running cross
     */
    @Override
    public boolean func_147451_t(int x, int y, int z) {
        return false;
    }

    /**
     * Override as archaicFix crash when running cross
     */
    @Override
    public boolean updateLightByType(EnumSkyBlock p_147463_1_, int p_147463_2_, int p_147463_3_, int p_147463_4_) {
        return false;
    }

    /**
     * Override as archaicFix crash when running cross
     */
    @Override
    public int getBlockLightValue_do(int p_72849_1_, int p_72849_2_, int p_72849_3_, boolean p_72849_4_) {
        return 15;
    }

    private static class DummyWorldProvider extends WorldProvider {

        @Override
        public String getDimensionName() {
            return "CropChance_DummyWorld";
        }
    }

    private static class DummyChunkProvider implements IChunkProvider {

        public final World world;
        public final LongHashMap LoadedChunks = new LongHashMap();

        public DummyChunkProvider(World world) {
            this.world = world;
        }

        @Override
        public boolean chunkExists(int cx, int cz) {
            return this.LoadedChunks.containsItem(ChunkCoordIntPair.chunkXZ2Int(cx, cz));
        }

        @Override
        public Chunk provideChunk(int cx, int cz) {
            return loadChunk(cx, cz);
        }

        @Override
        public Chunk loadChunk(int cx, int cz) {
            long ck = ChunkCoordIntPair.chunkXZ2Int(cx, cz);

            if (LoadedChunks.containsItem(ck))
                return (Chunk) LoadedChunks.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(cx, cz));
            else {
                Chunk chunk = new Chunk(world, cx, cz);
                chunk.isChunkLoaded = true;
                LoadedChunks.add(ck, chunk);
                return chunk;
            }
        }

        @Override
        public void populate(IChunkProvider p_73153_1_, int p_73153_2_, int p_73153_3_) {

        }

        @Override
        public boolean saveChunks(boolean p_73151_1_, IProgressUpdate p_73151_2_) {
            return false;
        }

        @Override
        public boolean unloadQueuedChunks() {
            return false;
        }

        @Override
        public boolean canSave() {
            return false;
        }

        @Override
        public String makeString() {
            return DummyChunkProvider.class.toString();
        }

        @Override
        public List getPossibleCreatures(EnumCreatureType p_73155_1_, int p_73155_2_, int p_73155_3_, int p_73155_4_) {
            return Collections.emptyList();
        }

        @Override
        public ChunkPosition func_147416_a(World p_147416_1_, String p_147416_2_, int p_147416_3_, int p_147416_4_,
            int p_147416_5_) {
            return null;
        }

        @Override
        public int getLoadedChunkCount() {
            return LoadedChunks.getNumHashElements();
        }

        @Override
        public void recreateStructures(int p_82695_1_, int p_82695_2_) {}

        @Override
        public void saveExtraData() {}
    }
}
