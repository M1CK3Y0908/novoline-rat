package rat.novoline.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.ArrayList;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
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

public class InventoryManager extends Module {
   public final CheckboxSetting closeInventory = settingManager.register(new CheckboxSetting(this, "Close inventory", false));
   public final CheckboxSetting disableInLobby = settingManager.register(new CheckboxSetting(this, "Disable in lobby", true));

   public final CheckboxSetting autoSort = settingManager.register(new CheckboxSetting(this, "Auto Sort", true));
   public final NumberSetting sortDelay = settingManager.register(new NumberSetting(this, "Sort delay", 1, 0, 20, " ticks") {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.autoSort.isEnabled();
      }
   });

   public final NumberSetting sword = settingManager.register(new NumberSetting(this, "Sword", 0, 0, 9) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.autoSort.isEnabled();
      }
   });

   public final NumberSetting bow = settingManager.register(new NumberSetting(this, "Bow", 0, 0, 9) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.autoSort.isEnabled();
      }
   });

   public final NumberSetting blocks = settingManager.register(new NumberSetting(this, "Blocks", 0, 0, 9) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.autoSort.isEnabled();
      }
   });

   public final NumberSetting goldenApple = settingManager.register(new NumberSetting(this, "Golden apple", 0, 0, 9) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.autoSort.isEnabled();
      }
   });

   public final NumberSetting projectile = settingManager.register(new NumberSetting(this, "Projectile/Rod", 0, 0, 9) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.autoSort.isEnabled();
      }
   });

   public final NumberSetting speedPotion = settingManager.register(new NumberSetting(this, "Speed potion", 0, 0, 9) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.autoSort.isEnabled();
      }
   });

   public final NumberSetting enderPearl = settingManager.register(new NumberSetting(this, "Ender pearl", 0, 0, 9) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.autoSort.isEnabled();
      }
   });

   public final NumberSetting waterBucket = settingManager.register(new NumberSetting(this, "Water bucket", 0, 0, 9) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.autoSort.isEnabled();
      }
   });

   public final NumberSetting lavaBucket = settingManager.register(new NumberSetting(this, "Lava bucket", 0, 0, 9) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.autoSort.isEnabled();
      }
   });

   public final CheckboxSetting rodOverProjectile = settingManager.register(new CheckboxSetting(this, "Rod over projectile", true) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.autoSort.isEnabled();
      }
   });

   public final CheckboxSetting onlyWithKnockback = settingManager.register(new CheckboxSetting(this, "^ Only with knockback", true) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.autoSort.isEnabled() && InventoryManager.this.rodOverProjectile.isEnabled();
      }
   });

   public final NumberSetting inventoryCleaner = settingManager.register(
      new NumberSetting(this, "Inventory cleaner", -1, -1, 20, " ticks").addLabel(-1.0f, "§cDisabled")
   );

   enum Action {
      SORT, CLEANER;

      long getDelay(InventoryManager inv) {
         switch (this) {
            case SORT: return Math.round(inv.sortDelay.getCurrentValue());
            default: return Math.round(inv.inventoryCleaner.getCurrentValue());
         }
      }
   }

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
   private long delay = 0L;
   private long waited = 0L;
   private long clicks = 0L;

   public InventoryManager() {
      super(Category.PLAYER, "InvManager", "Manages inventory, sorting and cleaner.");
      this.addAlias("InventoryManager");
   }

   @EventLink
   public final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      if (minecraft.thePlayer == null || minecraft.theWorld == null) {
         this.delay = 0L;
         this.waited = 0L;
         return;
      }

      if (minecraft.currentScreen == null || ItemUtils.pausedInLobby(this.disableInLobby.isEnabled())) {
         this.delay = 0L;
         this.waited = 0L;
         return;
      }

      if (++this.waited < this.delay) {
         return;
      }
      this.clicks = 0L;
      this.waited = 0L;

      if (minecraft.currentScreen instanceof GuiInventory) {
         boolean hasSort = this.autoSort.isEnabled();
         boolean hasCleaner = Math.round(this.inventoryCleaner.getCurrentValue()) >= 0;

         if (hasSort || hasCleaner) {
            updateArmor();
            Scan s = new Scan(minecraft.thePlayer.inventory);

            if (hasSort) {
               if (sortSword(s)) {
                  return;
               }
               if (sortBow(s)) {
                  return;
               }
               for (int n = 0; n < s.size; ++n) {
                  ItemStack stack = s.inv.getStackInSlot(n);
                  if (stack == null || ItemUtils.isSword(stack) || ItemUtils.isBow(stack) || ItemUtils.isArmor(stack)) continue;
                  Item item = stack.getItem();
                  int n4 = n < 9 ? n + 36 : n;
                  if (ItemUtils.isBlock(item)) {
                     int n5 = Math.round(this.blocks.getCurrentValue()) - 1;
                     if (n5 == -1 || n == n5) continue;
                     ItemStack other = s.inv.getStackInSlot(n5);
                     if (other == null || !ItemUtils.isBlock(other.getItem())) {
                        if (!click(n4, n5, 2, Action.SORT)) continue;
                        return;
                     }
                     if (other.stackSize >= 64) continue;
                     if (item == other.getItem() && stack.getItemDamage() == other.getItemDamage()) {
                        if (stack.stackSize == 64) {
                           if (!click(n4, n5, 2, Action.SORT)) continue;
                           return;
                        }
                        if (s.emptyMain == 0 && n != n4 || !click(n4, 0, 1, Action.SORT)) continue;
                        return;
                     }
                     if (stack.stackSize <= other.stackSize || !click(n4, n5, 2, Action.SORT)) continue;
                     return;
                  }
                  if (item == Items.golden_apple) {
                     int n5 = Math.round(this.goldenApple.getCurrentValue()) - 1;
                     if (n5 == -1 || n == n5) continue;
                     ItemStack other = s.inv.getStackInSlot(n5);
                     if (other != null && other.getItem() == Items.golden_apple) continue;
                     if (!click(n4, n5, 2, Action.SORT)) continue;
                     return;
                  }
                  if (item == Items.snowball || item == Items.egg || item == Items.fishing_rod) {
                     int n5 = Math.round(this.projectile.getCurrentValue()) - 1;
                     if (n5 == -1 || n == n5) continue;
                     ItemStack other = s.inv.getStackInSlot(n5);
                     if (other == null || (other.getItem() != Items.snowball && other.getItem() != Items.egg && other.getItem() != Items.fishing_rod)) {
                        if (!click(n4, n5, 2, Action.SORT)) continue;
                        return;
                     }
                     if (preferredRod(other)) continue;
                     if (preferredRod(stack)) {
                        if (!click(n4, n5, 2, Action.SORT)) continue;
                        return;
                     }
                     if (item == Items.fishing_rod) continue;
                     if (stack.stackSize >= 16) {
                        if (stack.stackSize <= other.stackSize || !click(n4, n5, 2, Action.SORT)) continue;
                        return;
                     }
                     if (other.stackSize >= 16) continue;
                     if (item == other.getItem()) {
                        if (s.emptyMain == 0 && n != n4 || !click(n4, 0, 1, Action.SORT)) continue;
                        return;
                     }
                     if (stack.stackSize <= other.stackSize || !click(n4, n5, 2, Action.SORT)) continue;
                     return;
                  }
                  if (item == Items.water_bucket || item == Items.lava_bucket) {
                     int n5 = Math.round((item == Items.water_bucket ? this.waterBucket : this.lavaBucket).getCurrentValue()) - 1;
                     if (n5 == -1 || n == n5) continue;
                     ItemStack other = s.inv.getStackInSlot(n5);
                     if (other != null && other.getItem() == item) continue;
                     if (!click(n4, n5, 2, Action.SORT)) continue;
                     return;
                  }
                  if (item == Items.potionitem) {
                     int n5 = Math.round(this.speedPotion.getCurrentValue()) - 1;
                     if (n5 == -1 || n == n5) continue;
                     if (!ItemUtils.isSpeedPotion(stack) || ItemUtils.isSpeedPotion(s.inv.getStackInSlot(n5))) continue;
                     if (!click(n4, n5, 2, Action.SORT)) continue;
                     return;
                  }
                  if (item == Items.ender_pearl) {
                     int n5 = Math.round(this.enderPearl.getCurrentValue()) - 1;
                     if (n5 == -1 || n == n5) continue;
                     ItemStack other = s.inv.getStackInSlot(n5);
                     if (other != null && other.getItem() == Items.ender_pearl) continue;
                     if (!click(n4, n5, 2, Action.SORT)) continue;
                     return;
                  }
               }
            }

            if (hasCleaner) {
               ArrayList<Integer> seen = new ArrayList<Integer>();
               for (int i = 0; i < s.size; ++i) {
                  ItemStack stack = s.inv.getStackInSlot(i);
                  if (stack == null) continue;
                  Item item = stack.getItem();
                  int n12 = i < 9 ? i + 36 : i;
                  if (ItemUtils.isSword(stack)) {
                     if (ItemUtils.durable(stack)) {
                        double d = ItemUtils.attackDamage(stack);
                        if (bestSword < d) continue;
                        if (bestSword == d && ++swordDuplicates <= 2) continue;
                     }
                  } else if (item == Items.bow) {
                     if (ItemUtils.durable(stack)) {
                        double d = ItemUtils.bowEnchantLevels(stack);
                        if (bestBow < d) continue;
                        if (bestBow == d && ++bowDuplicates <= 2) continue;
                     }
                  } else if (ItemUtils.isArmor(stack)) {
                     if (ItemUtils.durable(stack)) {
                        int type = 3 - ((ItemArmor) item).armorType;
                        double d = armor[type].value;
                        double d2 = ItemUtils.armorValue(stack);
                        if (d < d2) continue;
                        if (d == d2 && ++armor[type].duplicates <= 1) continue;
                     }
                  } else {
                     if (ItemUtils.isBlock(item) || item == Items.golden_apple || item == Items.snowball || item == Items.egg || item == Items.ender_pearl
                           || item == Items.arrow || item == Items.spawn_egg || item == Items.clock || item == Items.diamond
                           || item == Items.emerald || item == Items.gold_ingot || item == Items.iron_ingot || ItemUtils.isUtilityBlock(item)) continue;
                     if (item == Items.potionitem) {
                        if (!ItemUtils.isBadPotion(stack)) continue;
                     } else {
                        int id;
                        if (stack.getMaxStackSize() == 1 && !seen.contains(id = Item.getIdFromItem(item))) {
                           seen.add(id);
                           continue;
                        }
                     }
                  }
                  if (!click(n12, 1, 4, Action.CLEANER)) continue;
                  return;
               }
            }

            if (this.closeInventory.isEnabled()) {
               minecraft.thePlayer.closeScreen();
            }
         }
      }
   };

   private boolean sortBow(Scan s) {
      if (s.bow[1] != -1.0) {
         int bw = Math.round(this.bow.getCurrentValue());
         if (bw > 0) {
            if (s.bow[1] > bestBow) {
               int n = (int) s.bow[0];
               n = n < 9 ? n + 36 : n;
               return click(n, bw - 1, 2, Action.SORT);
            }
         }
      }
      return false;
   }

   private boolean sortSword(Scan s) {
      if (s.sword[1] != -1.0) {
         int sw = Math.round(this.sword.getCurrentValue());
         if (sw > 0) {
            if (s.sword[1] > bestSword) {
               int n = (int) s.sword[0];
               n = n < 9 ? n + 36 : n;
               return click(n, sw - 1, 2, Action.SORT);
            }
         }
      }
      return false;
   }

   private boolean preferredRod(ItemStack stack) {
      return ItemUtils.preferredRod(stack, this.rodOverProjectile.isEnabled(), this.onlyWithKnockback.isEnabled());
   }

   private boolean click(int slot, int button, int mode, Action action) {
      this.delay = action.getDelay(this);
      ++this.clicks;
      minecraft.playerController.windowClick(minecraft.thePlayer.openContainer.windowId, slot, button, mode, minecraft.thePlayer);
      return this.delay != 0L;
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
      this.clicks = 0L;
      this.delay = 0L;
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

      Scan(IInventory inv) {
         this.inv = inv;
         this.size = inv.getSizeInventory() - 4;
         int sw = Math.round(InventoryManager.this.sword.getCurrentValue()) - 1;
         int bw = Math.round(InventoryManager.this.bow.getCurrentValue()) - 1;
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
   }
}
