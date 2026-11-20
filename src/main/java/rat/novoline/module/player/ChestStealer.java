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
import net.minecraft.item.ItemStack;
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
   public final CheckboxSetting stealFromCustomChests = settingManager.register(new CheckboxSetting(this, "Steal from custom chests", false));
   public final CheckboxSetting disableInLobby = settingManager.register(new CheckboxSetting(this, "Disable in lobby", true));

   public final NumberSetting sword = settingManager.register(new NumberSetting(this, "Sword", 0, 0, 9).addLabel(0.0f, "§cDisabled"));
   public final NumberSetting bow = settingManager.register(new NumberSetting(this, "Bow", 0, 0, 9).addLabel(0.0f, "§cDisabled"));
   public final NumberSetting blocks = settingManager.register(new NumberSetting(this, "Blocks", 0, 0, 9).addLabel(0.0f, "§cDisabled"));
   public final NumberSetting goldenApple = settingManager.register(new NumberSetting(this, "Golden apple", 0, 0, 9).addLabel(0.0f, "§cDisabled"));
   public final NumberSetting projectile = settingManager.register(new NumberSetting(this, "Projectile/Rod", 0, 0, 9).addLabel(0.0f, "§cDisabled"));
   public final NumberSetting speedPotion = settingManager.register(new NumberSetting(this, "Speed potion", 0, 0, 9).addLabel(0.0f, "§cDisabled"));
   public final NumberSetting enderPearl = settingManager.register(new NumberSetting(this, "Ender pearl", 0, 0, 9).addLabel(0.0f, "§cDisabled"));
   public final NumberSetting waterBucket = settingManager.register(new NumberSetting(this, "Water bucket", 0, 0, 9).addLabel(0.0f, "§cDisabled"));
   public final NumberSetting lavaBucket = settingManager.register(new NumberSetting(this, "Lava bucket", 0, 0, 9).addLabel(0.0f, "§cDisabled"));
   public final CheckboxSetting rodOverProjectile = settingManager.register(new CheckboxSetting(this, "Rod over projectile", true));
   public final CheckboxSetting onlyWithKnockback = settingManager.register(new CheckboxSetting(this, "^ Only with knockback", true) {
      @Override
      public boolean shouldShow() {
         return ChestStealer.this.rodOverProjectile.isEnabled();
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
   private static int bestSwordSlot = -1;
   private static double bestSword = -1.0;
   private static int swordDuplicates;
   private static int bestBowSlot = -1;
   private static double bestBow = -1.0;
   private static int bowDuplicates;
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
         c.dropWorse(p);

         if (p.size != p.filled) {
            if (sortSword(c, false, stealDelay)) {
               return;
            }
            if (sortBow(c, false, stealDelay)) {
               return;
            }
            for (int n = 0; n < c.armor[0].length; ++n) {
               if (c.armor[1][n] == -1.0 || !(c.armor[1][n] > armor[n].value) || !click((int) c.armor[0][n], 0, 1, stealDelay)) {
                  continue;
               }
               return;
            }
            for (int n = 0; n < c.size; ++n) {
               ItemStack stack = c.inv.getStackInSlot(n);
               if (stack == null || ItemUtils.isSword(stack) || ItemUtils.isBow(stack) || ItemUtils.isArmor(stack)) {
                  continue;
               }
               Item item = stack.getItem();
               int n16;
               ItemStack other;
               if (ItemUtils.isBlock(item)) {
                  n16 = Math.round(this.blocks.getCurrentValue()) - 1;
                  if (n16 == -1) {
                     if (!click(n, 0, 1, stealDelay)) continue;
                     return;
                  }
                  other = p.inv.getStackInSlot(n16);
                  if ((other == null || !ItemUtils.isBlock(other.getItem())
                        || ((ItemBlock) item).getBlock() != ((ItemBlock) other.getItem()).getBlock() && stack.stackSize > other.stackSize)
                        && click(n, n16, 2, stealDelay)) {
                     return;
                  }
               } else if (item == Items.golden_apple) {
                  n16 = Math.round(this.goldenApple.getCurrentValue()) - 1;
                  if (n16 == -1) {
                     if (!click(n, 0, 1, stealDelay)) continue;
                     return;
                  }
                  other = p.inv.getStackInSlot(n16);
                  if ((other == null || other.getItem() != Items.golden_apple) && click(n, n16, 2, stealDelay)) {
                     return;
                  }
               } else if (item == Items.snowball || item == Items.egg || item == Items.fishing_rod) {
                  n16 = Math.round(this.projectile.getCurrentValue()) - 1;
                  if (n16 == -1) {
                     if (!click(n, 0, 1, stealDelay)) continue;
                     return;
                  }
                  other = p.inv.getStackInSlot(n16);
                  if (other == null || (other.getItem() != Items.snowball && other.getItem() != Items.egg && other.getItem() != Items.fishing_rod)
                        || (preferredRod(stack) && !preferredRod(other))) {
                     if (click(n, n16, 2, stealDelay)) {
                        return;
                     }
                  } else if (stack.stackSize > other.stackSize && other.getItem() != Items.fishing_rod) {
                     boolean done = item == other.getItem() ? (stack.stackSize <= 16 ? click(n, 0, 1, stealDelay) : click(n, n16, 2, stealDelay))
                           : click(n, n16, 2, stealDelay);
                     if (done) {
                        return;
                     }
                  }
               } else if (item == Items.water_bucket || item == Items.lava_bucket) {
                  if (!(item == Items.water_bucket ? !p.water : !p.lava)) continue;
                  n16 = Math.round((item == Items.water_bucket ? this.waterBucket : this.lavaBucket).getCurrentValue()) - 1;
                  if (n16 == -1) {
                     if (click(n, 0, 1, stealDelay)) {
                        return;
                     }
                  } else if (click(n, n16, 2, stealDelay)) {
                     return;
                  }
                  if (item == Items.water_bucket) {
                     p.water = true;
                  } else {
                     p.lava = true;
                  }
                  continue;
               } else if (item == Items.potionitem) {
                  if (ItemUtils.isSpeedPotion(stack)) {
                     n16 = Math.round(this.speedPotion.getCurrentValue()) - 1;
                     if (n16 == -1) {
                        if (!click(n, 0, 1, stealDelay)) continue;
                        return;
                     }
                     other = p.inv.getStackInSlot(n16);
                     if (!ItemUtils.isSpeedPotion(other) && click(n, n16, 2, stealDelay)) {
                        return;
                     }
                  }
               } else if (item == Items.ender_pearl) {
                  n16 = Math.round(this.enderPearl.getCurrentValue()) - 1;
                  if (n16 == -1) {
                     if (!click(n, 0, 1, stealDelay)) continue;
                     return;
                  }
                  other = p.inv.getStackInSlot(n16);
                  if ((other == null || other.getItem() != Items.ender_pearl) && click(n, n16, 2, stealDelay)) {
                     return;
                  }
               }
               if (item instanceof ItemBlock && !ItemUtils.isBlock(item) && !ItemUtils.isUtilityBlock(item)) {
                  continue;
               }
               if (!click(n, 0, 1, stealDelay)) continue;
               return;
            }
         } else {
            for (int i = 0; i < c.size; ++i) {
               ItemStack stack = c.inv.getStackInSlot(i);
               if (stack == null) continue;
               Item item = stack.getItem();
               if (!ItemUtils.isBlock(item) && item != Items.golden_apple && item != Items.snowball && item != Items.egg
                     && item != Items.ender_pearl && item != Items.arrow && item != Items.clock && item != Items.diamond
                     && item != Items.emerald && item != Items.gold_ingot && item != Items.iron_ingot && !ItemUtils.isUtilityBlock(item)) {
                  continue;
               }
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

   private boolean sortBow(Scan s, boolean player, long actionDelay) {
      if (s.bow[1] != -1.0) {
         int bw = Math.round(this.bow.getCurrentValue());
         if (bw > 0) {
            if (s.bow[1] > bestBow) {
               int n = (int) s.bow[0];
               if (player) {
                  n = n < 9 ? n + 36 : n;
               }
               return click(n, bw - 1, 2, actionDelay);
            }
         } else if (!player) {
            return click((int) s.bow[0], 0, 1, actionDelay);
         }
      }
      return false;
   }

   private boolean sortSword(Scan s, boolean player, long actionDelay) {
      if (s.sword[1] != -1.0) {
         int sw = Math.round(this.sword.getCurrentValue());
         if (sw > 0) {
            if (s.sword[1] > bestSword) {
               int n = (int) s.sword[0];
               if (player) {
                  n = n < 9 ? n + 36 : n;
               }
               return click(n, sw - 1, 2, actionDelay);
            }
         } else if (!player) {
            return click((int) s.sword[0], 0, 1, actionDelay);
         }
      }
      return false;
   }

   private boolean preferredRod(ItemStack stack) {
      return ItemUtils.preferredRod(stack, this.rodOverProjectile.isEnabled(), this.onlyWithKnockback.isEnabled());
   }

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
      int emptyMain, filled;
      boolean water, lava;

      Scan(IInventory inv, boolean player) {
         this.inv = inv;
         this.size = !player ? inv.getSizeInventory() : inv.getSizeInventory() - 4;
         int sw = Math.round(ChestStealer.this.sword.getCurrentValue()) - 1 + (!player ? (this.size != 54 ? 54 : 81) : 0);
         int bw = Math.round(ChestStealer.this.bow.getCurrentValue()) - 1 + (!player ? (this.size != 54 ? 54 : 81) : 0);
         for (int i = 0; i < this.size; ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack != null) {
               ++this.filled;
               Item item = stack.getItem();
               this.water |= item == Items.water_bucket;
               this.lava |= item == Items.lava_bucket;
            } else if (i >= 9) {
               ++this.emptyMain;
            }
            if (i == sw) {
               swordDuplicates = 0;
               if (stack != null && ItemUtils.isSword(stack) && ItemUtils.durable(stack)) {
                  bestSwordSlot = i;
                  bestSword = ItemUtils.attackDamage(stack);
                  continue;
               }
               bestSwordSlot = -1;
               bestSword = -1;
            } else if (i == bw) {
               bowDuplicates = 0;
               if (stack != null && ItemUtils.isBow(stack) && ItemUtils.durable(stack)) {
                  bestBowSlot = i;
                  bestBow = ItemUtils.bowEnchantLevels(stack);
                  continue;
               }
               bestBowSlot = -1;
               bestBow = -1;
            }
            if (stack == null || !ItemUtils.durable(stack)) continue;
            if (ItemUtils.isSword(stack)) {
               double d = ItemUtils.attackDamage(stack);
               if (d > this.sword[1]) {
                  this.sword[1] = d;
                  this.sword[0] = i;
               }
            } else if (ItemUtils.isBow(stack)) {
               double d = ItemUtils.bowEnchantLevels(stack);
               if (d > this.bow[1]) {
                  this.bow[1] = d;
                  this.bow[0] = i;
               }
            } else if (ItemUtils.isArmor(stack)) {
               int type = 3 - ((ItemArmor) stack.getItem()).armorType;
               double d = ItemUtils.armorValue(stack);
               if (d > this.armor[1][type]) {
                  this.armor[1][type] = d;
                  this.armor[0][type] = i;
               }
            }
         }
      }

      void dropWorse(Scan player) {
         if (player.sword[1] > this.sword[1]) {
            this.sword[1] = -1.0;
         }
         if (player.bow[1] > this.bow[1]) {
            this.bow[1] = -1.0;
         }
         for (int i = 0; i < this.armor[0].length; ++i) {
            if (player.armor[1][i] > this.armor[1][i]) {
               this.armor[1][i] = -1.0;
            }
         }
      }
   }
}
