package wtf.novoline.culling;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;

public class CullingProvider implements DataProvider {
   private final Minecraft mc = Minecraft.getMinecraft();
   private WorldClient world;
   private final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
   private Chunk cachedChunk;
   private int cachedChunkX = Integer.MIN_VALUE;
   private int cachedChunkZ = Integer.MIN_VALUE;

   @Override
   public boolean prepareChunk(int chunkX, int chunkZ) {
      this.world = this.mc.theWorld;
      if (this.world == null) {
         return false;
      }
      if (this.cachedChunk != null && this.cachedChunkX == chunkX && this.cachedChunkZ == chunkZ) {
         return true;
      }
      this.cachedChunkX = chunkX;
      this.cachedChunkZ = chunkZ;
      this.cachedChunk = this.world.getChunkFromChunkCoords(chunkX, chunkZ);
      return this.cachedChunk != null && !(this.cachedChunk instanceof EmptyChunk);
   }

   @Override
   public boolean isOpaqueFullCube(int x, int y, int z) {
      try {
         if (this.world == null || y < 0 || y >= 256) {
            return false;
         }
         int chunkX = x >> 4;
         int chunkZ = z >> 4;
         if (this.cachedChunk != null && this.cachedChunkX == chunkX && this.cachedChunkZ == chunkZ) {
            IBlockState state = this.cachedChunk.getBlockState(this.mutablePos.set(x, y, z));
            return state.getBlock().isOpaqueCube();
         }
         IBlockState state = this.world.getBlockState(this.mutablePos.set(x, y, z));
         return state.getBlock().isOpaqueCube();
      } catch (Throwable t) {
         return false;
      }
   }

   @Override
   public void cleanup() {
      this.world = null;
      this.cachedChunk = null;
      this.cachedChunkX = Integer.MIN_VALUE;
      this.cachedChunkZ = Integer.MIN_VALUE;
   }
}
