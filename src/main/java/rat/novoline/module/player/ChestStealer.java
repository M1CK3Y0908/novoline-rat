package rat.novoline.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import rat.novoline.event.PreUpdateEvent;
import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.setting.CheckboxSetting;
import rat.novoline.setting.NumberSetting;
import rat.novoline.utils.player.ItemUtils;

public class ChestStealer extends Module {
   public final NumberSetting delay = settingManager.register(
      new NumberSetting(this, "Delay", 1, 0, 20, " ticks")
   );
   public final CheckboxSetting closeChest = settingManager.register(new CheckboxSetting(this, "Close chest", false));
   public final CheckboxSetting ignoreTrash = settingManager.register(new CheckboxSetting(this, "Ignore trash", true));
   public final CheckboxSetting stealFromCustomChests = settingManager.register(new CheckboxSetting(this, "Steal from custom chests", false));
   public final CheckboxSetting disableInLobby = settingManager.register(new CheckboxSetting(this, "Disable in lobby", true));

   // --- Stealing Item Limit Sliders (shown when ignoreTrash is enabled) ---
   // Non-stackable items (0 - 16)
   public final NumberSetting sword = settingManager.register(new NumberSetting(this, "Sword", 1, 0, 16) {
      @Override
      public boolean shouldShow() {
         return ChestStealer.this.ignoreTrash.isEnabled();
      }
   });

   public final NumberSetting bow = settingManager.register(new NumberSetting(this, "Bow", 1, 0, 16) {
      @Override
      public boolean shouldShow() {
         return ChestStealer.this.ignoreTrash.isEnabled();
      }
   });

   public final NumberSetting armorSetting = settingManager.register(new NumberSetting(this, "Armor", 1, 0, 16) {
      @Override
      public boolean shouldShow() {
         return ChestStealer.this.ignoreTrash.isEnabled();
      }
   });

   public final NumberSetting tools = settingManager.register(new NumberSetting(this, "Tools", 1, 0, 16) {
      @Override
      public boolean shouldShow() {
         return ChestStealer.this.ignoreTrash.isEnabled();
      }
   });

   public final NumberSetting waterBucket = settingManager.register(new NumberSetting(this, "Water bucket", 1, 0, 16) {
      @Override
      public boolean shouldShow() {
         return ChestStealer.this.ignoreTrash.isEnabled();
      }
   });

   public final NumberSetting lavaBucket = settingManager.register(new NumberSetting(this, "Lava bucket", 1, 0, 16) {
      @Override
      public boolean shouldShow() {
         return ChestStealer.this.ignoreTrash.isEnabled();
      }
   });

   public final NumberSetting potions = settingManager.register(new NumberSetting(this, "Potions", 4, 0, 16) {
      @Override
      public boolean shouldShow() {
         return ChestStealer.this.ignoreTrash.isEnabled();
      }
   });

   public final NumberSetting fishingRod = settingManager.register(new NumberSetting(this, "Fishing rod", 1, 0, 16) {
      @Override
      public boolean shouldShow() {
         return ChestStealer.this.ignoreTrash.isEnabled();
      }
   });

   public final NumberSetting flintAndSteel = settingManager.register(new NumberSetting(this, "Flint and steel", 1, 0, 16) {
      @Override
      public boolean shouldShow() {
         return ChestStealer.this.ignoreTrash.isEnabled();
      }
   });

   public final NumberSetting shears = settingManager.register(new NumberSetting(this, "Shears", 1, 0, 16) {
      @Override
      public boolean shouldShow() {
         return ChestStealer.this.ignoreTrash.isEnabled();
      }
   });

   // Stackable items (0 - 512)
   public final NumberSetting blocks = settingManager.register(new NumberSetting(this, "Blocks", 128, 0, 512) {
      @Override
      public boolean shouldShow() {
         return ChestStealer.this.ignoreTrash.isEnabled();
      }
   });

   public final NumberSetting goldenApple = settingManager.register(new NumberSetting(this, "Golden apple", 32, 0, 512) {
      @Override
      public boolean shouldShow() {
         return ChestStealer.this.ignoreTrash.isEnabled();
      }
   });

   public final NumberSetting enderPearl = settingManager.register(new NumberSetting(this, "Ender pearl", 16, 0, 512) {
      @Override
      public boolean shouldShow() {
         return ChestStealer.this.ignoreTrash.isEnabled();
      }
   });

   public final NumberSetting projectile = settingManager.register(new NumberSetting(this, "Projectiles", 32, 0, 512) {
      @Override
      public boolean shouldShow() {
         return ChestStealer.this.ignoreTrash.isEnabled();
      }
   });

   public final NumberSetting arrows = settingManager.register(new NumberSetting(this, "Arrows", 64, 0, 512) {
      @Override
      public boolean shouldShow() {
         return ChestStealer.this.ignoreTrash.isEnabled();
      }
   });

   enum ArmorSlot {
      BOOTS(0, 8), LEGGINGS(1, 7), CHESTPLATE(2, 6), HELMET(3, 5);

      final int index, slot;
      double value;
      int duplicates;

      ArmorSlot(int index, int slot) {
         this.index = index;
         this.slot = slot;
      }

      ItemStack getItemStack() {
         return minecraft.thePlayer.inventory.armorItemInSlot(this.index);
      }
   }

   private static final ArmorSlot[] armor = ArmorSlot.values();
   private long currentDelay = 0L;
   private long waited = 0L;
   private long chestTicks = 0L;

   public ChestStealer() {
      super(Category.PLAYER, "ChestStealer", "Steals items from chests automatically.");
   }

   @EventLink
   public final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      if (minecraft.thePlayer == null || minecraft.theWorld == null) {
         this.currentDelay = 0L;
         this.chestTicks = 0L;
         this.waited = 0L;
         return;
      }

      if (minecraft.currentScreen == null || ItemUtils.pausedInLobby(this.disableInLobby.isEnabled())) {
         this.currentDelay = 0L;
         this.chestTicks = 0L;
         this.waited = 0L;
         return;
      }

      if (++this.waited < this.currentDelay) {
         return;
      }
      this.waited = 0L;

      int stealDelay = Math.round(this.delay.getCurrentValue());
      if (stealDelay < 0) {
         return;
      }

      if (minecraft.currentScreen instanceof GuiChest && ++this.chestTicks > 1L) {
         if (!(minecraft.thePlayer.openContainer instanceof ContainerChest)) {
            return;
         }
         IInventory chest = ((ContainerChest) minecraft.thePlayer.openContainer).getLowerChestInventory();
         String chestName = chest.getName();
         String chestDisplay = chest.getDisplayName() != null ? chest.getDisplayName().getUnformattedText() : "";
         if (!this.stealFromCustomChests.isEnabled() && !chestName.isEmpty() && !chestName.contains("Chest")
               && !chestDisplay.isEmpty() && !chestDisplay.contains("Chest") && !chestDisplay.contains("箱子")) {
            return;
         }

         updateArmor();
         Scan c = new Scan(chest, false);
         Scan p = new Scan(minecraft.thePlayer.inventory, true);

         if (this.ignoreTrash.isEnabled()) {
            c.dropWorse(p);
         }

         if (p.size != p.filled) {
            for (int n = 0; n < c.armor[0].length; ++n) {
               if (c.armor[1][n] == -1.0 || !click((int) c.armor[0][n], 0, 1, stealDelay)) {
                  continue;
               }
               return;
            }
            if (c.sword[1] != -1.0 && click((int) c.sword[0], 0, 1, stealDelay)) {
               return;
            }
            if (c.bow[1] != -1.0 && click((int) c.bow[0], 0, 1, stealDelay)) {
               return;
            }
            for (int t = 0; t < 3; ++t) {
               if (c.tools[1][t] != -1.0 && click((int) c.tools[0][t], 0, 1, stealDelay)) {
                  return;
               }
            }

            int blockLimit = Math.round(this.blocks.getCurrentValue());
            int gappleLimit = Math.round(this.goldenApple.getCurrentValue());
            int pearlLimit = Math.round(this.enderPearl.getCurrentValue());
            int projLimit = Math.round(this.projectile.getCurrentValue());
            int arrowLimit = Math.round(this.arrows.getCurrentValue());
            int waterLimit = Math.round(this.waterBucket.getCurrentValue());
            int lavaLimit = Math.round(this.lavaBucket.getCurrentValue());
            int potionLimit = Math.round(this.potions.getCurrentValue());
            int rodLimit = Math.round(this.fishingRod.getCurrentValue());
            int flintLimit = Math.round(this.flintAndSteel.getCurrentValue());
            int shearsLimit = Math.round(this.shears.getCurrentValue());

            for (int n = 0; n < c.size; ++n) {
               ItemStack stack = c.inv.getStackInSlot(n);
               if (stack == null) {
                  continue;
               }
               if (this.ignoreTrash.isEnabled()) {
                  if (!ItemUtils.isUsefulItem(stack)) {
                     continue;
                  }
                  if (ItemUtils.isSword(stack) || ItemUtils.isBow(stack) || ItemUtils.isArmor(stack) || stack.getItem() instanceof ItemTool) {
                     continue;
                  }
                  Item item = stack.getItem();
                  if (ItemUtils.isBlock(item)) {
                     if (blockLimit <= 0 || p.blockCount >= blockLimit) continue;
                  } else if (item == Items.golden_apple) {
                     if (gappleLimit <= 0 || p.gappleCount >= gappleLimit) continue;
                  } else if (item == Items.ender_pearl) {
                     if (pearlLimit <= 0 || p.pearlCount >= pearlLimit) continue;
                  } else if (item == Items.snowball || item == Items.egg) {
                     if (projLimit <= 0 || p.projectileCount >= projLimit) continue;
                  } else if (item == Items.arrow) {
                     if (arrowLimit <= 0 || p.arrowCount >= arrowLimit) continue;
                  } else if (item == Items.water_bucket) {
                     if (waterLimit <= 0 || p.waterBucketCount >= waterLimit) continue;
                  } else if (item == Items.lava_bucket) {
                     if (lavaLimit <= 0 || p.lavaBucketCount >= lavaLimit) continue;
                  } else if (ItemUtils.isGoodPotion(stack)) {
                     if (potionLimit <= 0 || p.potionCount >= potionLimit) continue;
                  } else if (item == Items.fishing_rod) {
                     if (rodLimit <= 0 || p.fishingRodCount >= rodLimit) continue;
                  } else if (item == Items.flint_and_steel) {
                     if (flintLimit <= 0 || p.flintAndSteelCount >= flintLimit) continue;
                  } else if (item == Items.shears) {
                     if (shearsLimit <= 0 || p.shearsCount >= shearsLimit) continue;
                  }
               }
               if (!click(n, 0, 1, stealDelay)) continue;
               return;
            }
         } else {
            int blockLimit = Math.round(this.blocks.getCurrentValue());
            int gappleLimit = Math.round(this.goldenApple.getCurrentValue());
            int pearlLimit = Math.round(this.enderPearl.getCurrentValue());
            int projLimit = Math.round(this.projectile.getCurrentValue());
            int arrowLimit = Math.round(this.arrows.getCurrentValue());

            for (int i = 0; i < c.size; ++i) {
               ItemStack stack = c.inv.getStackInSlot(i);
               if (stack == null) continue;
               if (this.ignoreTrash.isEnabled()) {
                  if (!ItemUtils.isUsefulItem(stack)) continue;
                  Item item = stack.getItem();
                  if (ItemUtils.isBlock(item)) {
                     if (blockLimit <= 0 || p.blockCount >= blockLimit) continue;
                  } else if (item == Items.golden_apple) {
                     if (gappleLimit <= 0 || p.gappleCount >= gappleLimit) continue;
                  } else if (item == Items.ender_pearl) {
                     if (pearlLimit <= 0 || p.pearlCount >= pearlLimit) continue;
                  } else if (item == Items.snowball || item == Items.egg) {
                     if (projLimit <= 0 || p.projectileCount >= projLimit) continue;
                  } else if (item == Items.arrow) {
                     if (arrowLimit <= 0 || p.arrowCount >= arrowLimit) continue;
                  }
               }
               Item item = stack.getItem();
               for (int j = 0; j < p.size; ++j) {
                  ItemStack own = p.inv.getStackInSlot(j);
                  if (own == null || item != own.getItem() || own.stackSize >= own.getMaxStackSize()) continue;
                  if (ItemUtils.isBlock(item) && ((ItemBlock) item).getBlock() != ((ItemBlock) own.getItem()).getBlock()) continue;
                  if (!click(i, 0, 1, stealDelay)) continue;
                  return;
               }
            }
         }
         if (this.closeChest.isEnabled()) {
            minecraft.thePlayer.closeScreen();
         }
      }
   };

   private boolean click(int slot, int button, int mode, long actionDelay) {
      this.currentDelay = actionDelay;
      minecraft.playerController.windowClick(minecraft.thePlayer.openContainer.windowId, slot, button, mode, minecraft.thePlayer);
      return this.currentDelay != 0L;
   }

   private static void updateArmor() {
      for (ArmorSlot a : armor) {
         ItemStack stack = a.getItemStack();
         a.value = stack != null && ItemUtils.isArmor(stack) && ItemUtils.durable(stack) ? ItemUtils.armorValue(stack) : 0.0;
         a.duplicates = 0;
      }
   }

   @Override
   protected void onDisable() {
      this.currentDelay = 0L;
      this.chestTicks = 0L;
      this.waited = 0L;
   }

   class Scan {
      final IInventory inv;
      final int size;
      final double[] sword = {-1.0, -1.0};
      final double[] bow = {-1.0, -1.0};
      final double[][] armor = {{-1.0, -1.0, -1.0, -1.0}, {-1.0, -1.0, -1.0, -1.0}};
      final double[][] tools = {{-1.0, -1.0, -1.0}, {-1.0, -1.0, -1.0}};
      int filled;

      int blockCount;
      int gappleCount;
      int pearlCount;
      int projectileCount;
      int arrowCount;
      int waterBucketCount;
      int lavaBucketCount;
      int potionCount;
      int fishingRodCount;
      int flintAndSteelCount;
      int shearsCount;
      int swordCount;
      int bowCount;
      int toolCount;
      int armorCount;

      Scan(IInventory inv, boolean player) {
         this.inv = inv;
         this.size = !player ? inv.getSizeInventory() : inv.getSizeInventory() - 4;
         for (int i = 0; i < this.size; ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack != null) {
               ++this.filled;
               Item item = stack.getItem();
               if (ItemUtils.isBlock(item)) {
                  this.blockCount += stack.stackSize;
               } else if (item == Items.golden_apple) {
                  this.gappleCount += stack.stackSize;
               } else if (item == Items.ender_pearl) {
                  this.pearlCount += stack.stackSize;
               } else if (item == Items.snowball || item == Items.egg) {
                  this.projectileCount += stack.stackSize;
               } else if (item == Items.arrow) {
                  this.arrowCount += stack.stackSize;
               } else if (item == Items.water_bucket) {
                  ++this.waterBucketCount;
               } else if (item == Items.lava_bucket) {
                  ++this.lavaBucketCount;
               } else if (ItemUtils.isGoodPotion(stack)) {
                  ++this.potionCount;
               } else if (item == Items.fishing_rod) {
                  ++this.fishingRodCount;
               } else if (item == Items.flint_and_steel) {
                  ++this.flintAndSteelCount;
               } else if (item == Items.shears) {
                  ++this.shearsCount;
               }
            }

            if (stack == null || !ItemUtils.durable(stack)) continue;

            if (ItemUtils.isSword(stack)) {
               ++this.swordCount;
               double d = ItemUtils.attackDamage(stack);
               if (d > this.sword[1]) {
                  this.sword[1] = d;
                  this.sword[0] = i;
               }
            } else if (ItemUtils.isBow(stack)) {
               ++this.bowCount;
               double d = ItemUtils.bowEnchantLevels(stack);
               if (d > this.bow[1]) {
                  this.bow[1] = d;
                  this.bow[0] = i;
               }
            } else if (ItemUtils.isArmor(stack)) {
               ++this.armorCount;
               int type = 3 - ((ItemArmor) stack.getItem()).armorType;
               double d = ItemUtils.armorValue(stack);
               if (d > this.armor[1][type]) {
                  this.armor[1][type] = d;
                  this.armor[0][type] = i;
               }
            } else if (stack.getItem() instanceof ItemTool && !(stack.getItem() instanceof ItemHoe)) {
               int toolType = ItemUtils.getToolType(stack);
               if (toolType >= 0 && toolType < 3) {
                  ++this.toolCount;
                  double d = ItemUtils.getToolScore(stack);
                  if (d > this.tools[1][toolType]) {
                     this.tools[1][toolType] = d;
                     this.tools[0][toolType] = i;
                  }
               }
            }
         }
      }

      void dropWorse(Scan player) {
         int swordLimit = Math.round(ChestStealer.this.sword.getCurrentValue());
         if (swordLimit <= 0 || (player.swordCount >= swordLimit && player.sword[1] >= this.sword[1])) {
            this.sword[1] = -1.0;
         }
         int bowLimit = Math.round(ChestStealer.this.bow.getCurrentValue());
         if (bowLimit <= 0 || (player.bowCount >= bowLimit && player.bow[1] >= this.bow[1])) {
            this.bow[1] = -1.0;
         }
         int armorLimit = Math.round(ChestStealer.this.armorSetting.getCurrentValue());
         for (int i = 0; i < this.armor[0].length; ++i) {
            double playerArmorVal = Math.max(ChestStealer.armor[i].value, player.armor[1][i]);
            if (armorLimit <= 0 || playerArmorVal >= this.armor[1][i]) {
               this.armor[1][i] = -1.0;
            }
         }
         int toolLimit = Math.round(ChestStealer.this.tools.getCurrentValue());
         for (int t = 0; t < 3; ++t) {
            if (toolLimit <= 0 || (player.toolCount >= toolLimit && player.tools[1][t] >= this.tools[1][t])) {
               this.tools[1][t] = -1.0;
            }
         }
      }
   }
}
