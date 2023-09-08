package city.windmill.cropchance;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.*;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;

public class DummyWorld extends World {

    public LongHashMap LoadedChunks = new LongHashMap();

    public DummyWorld() {
        super(
            null,
            "Dummy",
            new DummyWorldProvider(),
            new WorldSettings(new WorldInfo(new NBTTagCompound())),
            new Profiler());
    }

    /**
     * Abstracts
     */
    @Override
    protected IChunkProvider createChunkProvider() {
        return null;
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
     * Override as we do not have a chunkprovider
     */
    @Override
    protected boolean chunkExists(int cx, int cz) {
        return this.LoadedChunks.containsItem(ChunkCoordIntPair.chunkXZ2Int(cx, cz));
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

    /**
     * Override as we do not have a chunkprovider
     */
    @Override
    public Chunk getChunkFromChunkCoords(int cx, int cz) {
        long ck = ChunkCoordIntPair.chunkXZ2Int(cx, cz);

        if (LoadedChunks.containsItem(ck))
            return (Chunk) LoadedChunks.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(cx, cz));
        else {
            Chunk chunk = new Chunk(this, cx, cz);
            LoadedChunks.add(ck, chunk);
            return chunk;
        }
    }

    /**
     * Override as we do not have a chunkprovider
     */
    @Override
    public boolean isBlockNormalCubeDefault(int x, int y, int z, boolean def) {
        Block block = getBlock(x, y, z);
        return block.isNormalCube(this, x, y, z);
    }

    private static class DummyWorldProvider extends WorldProvider {

        @Override
        public String getDimensionName() {
            return "CropChance_DummyWorld";
        }
    }
}
