package client.m1ck3y.novoline.culling;

public interface DataProvider {
   boolean prepareChunk(int chunkX, int chunkZ);

   boolean isOpaqueFullCube(int x, int y, int z);

   default void cleanup() {
   }
}
