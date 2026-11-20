package rat.novoline.module.world;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import rat.novoline.event.BlockCollisionEvent;
import rat.novoline.module.Category;
import rat.novoline.module.Module;

public class AntiCactus extends Module {
   @EventLink
   private final Listener<BlockCollisionEvent> onBlockCollision = blockCollisionEvent -> {
      if (blockCollisionEvent.iBlockState.getBlock() == Blocks.cactus) {
         blockCollisionEvent.axisAlignedBB = axisAlignedBB.offset(blockCollisionEvent.blockPos.getX(), blockCollisionEvent.blockPos.getY(), blockCollisionEvent.blockPos.getZ());
      }
   };
   private static final AxisAlignedBB axisAlignedBB = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);

   public AntiCactus() {
      super(Category.WORLD, "AntiCactus", "Prevents you from getting pricked");
   }
}
