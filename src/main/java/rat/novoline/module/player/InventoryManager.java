package rat.novoline.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import rat.novoline.event.PreUpdateEvent;
import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.setting.CheckboxSetting;
import rat.novoline.setting.MultiComboSetting;
import rat.novoline.setting.NumberSetting;
import rat.novoline.utils.player.ItemUtils;

public class InventoryManager extends Module {
   public final CheckboxSetting closeInventory = settingManager.register(new CheckboxSetting(this, "Close inventory", false));
   public final CheckboxSetting disableInLobby = settingManager.register(new CheckboxSetting(this, "Disable in lobby", true));

   public final MultiComboSetting function = settingManager.register(
      new MultiComboSetting(this, "Functions")
         .add("Sort", true)
         .add("Clean", true)
   );

   public final NumberSetting sortDelay = settingManager.register(new NumberSetting(this, "Sort delay", 1, 0, 20, " ticks") {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.function.isSelected("Sort");
      }
   });

   public final NumberSetting cleanDelay = settingManager.register(new NumberSetting(this, "Clean delay", 1, 0, 20, " ticks") {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.function.isSelected("Clean");
      }
   });

   public final NumberSetting sword = settingManager.register(new NumberSetting(this, "Sword", 0, 0, 9) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.function.isSelected("Sort");
      }
   });

   public final NumberSetting bow = settingManager.register(new NumberSetting(this, "Bow", 0, 0, 9) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.function.isSelected("Sort");
      }
   });

   public final NumberSetting blocks = settingManager.register(new NumberSetting(this, "Blocks", 0, 0, 9) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.function.isSelected("Sort");
      }
   });

   public final NumberSetting goldenApple = settingManager.register(new NumberSetting(this, "Golden apple", 0, 0, 9) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.function.isSelected("Sort");
      }
   });

   public final NumberSetting projectile = settingManager.register(new NumberSetting(this, "Projectile/Rod", 0, 0, 9) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.function.isSelected("Sort");
      }
   });

   public final CheckboxSetting rodOverProjectile = settingManager.register(new CheckboxSetting(this, "Rod over projectile", true) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.function.isSelected("Sort") && Math.round(InventoryManager.this.projectile.getCurrentValue()) != 0;
      }
   });

   public final CheckboxSetting onlyWithKnockback = settingManager.register(new CheckboxSetting(this, "Only with knockback", true) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.function.isSelected("Sort")
               && Math.round(InventoryManager.this.projectile.getCurrentValue()) != 0
               && InventoryManager.this.rodOverProjectile.isEnabled();
      }
   });

   public final NumberSetting speedPotion = settingManager.register(new NumberSetting(this, "Speed potion", 0, 0, 9) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.function.isSelected("Sort");
      }
   });

   public final NumberSetting enderPearl = settingManager.register(new NumberSetting(this, "Ender pearl", 0, 0, 9) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.function.isSelected("Sort");
      }
   });

   public final NumberSetting waterBucket = settingManager.register(new NumberSetting(this, "Water bucket", 0, 0, 9) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.function.isSelected("Sort");
      }
   });

   public final NumberSetting lavaBucket = settingManager.register(new NumberSetting(this, "Lava bucket", 0, 0, 9) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.function.isSelected("Sort");
      }
   });

   // --- Inventory Cleaner Quantity Limits ---
   public final NumberSetting maxBlocks = settingManager.register(new NumberSetting(this, "Max blocks", 256, 0, 512) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.function.isSelected("Clean");
      }
   });

   public final NumberSetting maxGoldenApples = settingManager.register(new NumberSetting(this, "Max golden apples", 64, 0, 512) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.function.isSelected("Clean");
      }
   });

   public final NumberSetting maxEnderPearls = settingManager.register(new NumberSetting(this, "Max ender pearls", 16, 0, 512) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.function.isSelected("Clean");
      }
   });

   public final NumberSetting maxProjectiles = settingManager.register(new NumberSetting(this, "Max projectiles", 0, 0, 512) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.function.isSelected("Clean");
      }
   });

   public final NumberSetting maxArrows = settingManager.register(new NumberSetting(this, "Max arrows", 0, 0, 512) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.function.isSelected("Clean");
      }
   });

   public final NumberSetting maxPotions = settingManager.register(new NumberSetting(this, "Max potions", 4, 0, 16) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.function.isSelected("Clean");
      }
   });

   public final NumberSetting maxWaterBuckets = settingManager.register(new NumberSetting(this, "Max water buckets", 0, 0, 16) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.function.isSelected("Clean");
      }
   });

   public final NumberSetting maxLavaBuckets = settingManager.register(new NumberSetting(this, "Max lava buckets", 0, 0, 16) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.function.isSelected("Clean");
      }
   });

   public final NumberSetting maxFishingRods = settingManager.register(new NumberSetting(this, "Max fishing rods", 0, 0, 16) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.function.isSelected("Clean");
      }
   });

   public final NumberSetting maxFlintAndSteel = settingManager.register(new NumberSetting(this, "Max flint and steel", 0, 0, 16) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.function.isSelected("Clean");
      }
   });

   public final NumberSetting maxShears = settingManager.register(new NumberSetting(this, "Max shears", 0, 0, 16) {
      @Override
      public boolean shouldShow() {
         return InventoryManager.this.function.isSelected("Clean");
      }
   });

   enum Action {
      SORT, CLEANER;

      long getDelay(InventoryManager inv) {
         switch (this) {
            case SORT: return Math.round(inv.sortDelay.getCurrentValue());
            default: return Math.round(inv.cleanDelay.getCurrentValue());
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
         if (minecraft.thePlayer.inventory.getItemStack() != null) {
            return;
         }

         boolean hasSort = this.function.isSelected("Sort");
         boolean hasCleaner = this.function.isSelected("Clean");

         if (hasSort || hasCleaner) {
            updateArmor();
            Scan s = new Scan(minecraft.thePlayer.inventory);

            if (hasCleaner) {
               if (cleanInventory(s)) {
                  return;
               }
            }

            if (hasSort) {
               if (sortSword(s)) {
                  return;
               }
               if (sortBow(s)) {
                  return;
               }
               if (sortHotbar(s)) {
                  return;
               }
               if (sortMainInventory()) {
                  return;
               }
            }

            if (this.closeInventory.isEnabled()) {
               minecraft.thePlayer.closeScreen();
            }
         }
      }
   };

   private boolean cleanInventory(Scan s) {
      IInventory inv = s.inv;
      boolean[] keep = new boolean[36];

      // 1. Best Sword
      double maxSwordDmg = -1.0;
      int bestSwordIndex = -1;
      int sw = Math.round(this.sword.getCurrentValue()) - 1;
      if (sw >= 0 && sw < 36) {
         ItemStack swStack = inv.getStackInSlot(sw);
         if (swStack != null && ItemUtils.isSword(swStack) && ItemUtils.durable(swStack)) {
            maxSwordDmg = ItemUtils.attackDamage(swStack);
            bestSwordIndex = sw;
         }
      }
      for (int i = 0; i < 36; ++i) {
         ItemStack stack = inv.getStackInSlot(i);
         if (stack != null && ItemUtils.isSword(stack) && ItemUtils.durable(stack)) {
            double dmg = ItemUtils.attackDamage(stack);
            if (dmg > maxSwordDmg) {
               maxSwordDmg = dmg;
               bestSwordIndex = i;
            }
         }
      }
      if (bestSwordIndex != -1) {
         keep[bestSwordIndex] = true;
      }

      // 2. Best Bow
      double maxBowScore = -1.0;
      int bestBowIndex = -1;
      int bw = Math.round(this.bow.getCurrentValue()) - 1;
      if (bw >= 0 && bw < 36) {
         ItemStack bwStack = inv.getStackInSlot(bw);
         if (bwStack != null && ItemUtils.isBow(bwStack) && ItemUtils.durable(bwStack)) {
            maxBowScore = ItemUtils.bowEnchantLevels(bwStack);
            bestBowIndex = bw;
         }
      }
      for (int i = 0; i < 36; ++i) {
         ItemStack stack = inv.getStackInSlot(i);
         if (stack != null && ItemUtils.isBow(stack) && ItemUtils.durable(stack)) {
            double score = ItemUtils.bowEnchantLevels(stack);
            if (score > maxBowScore) {
               maxBowScore = score;
               bestBowIndex = i;
            }
         }
      }
      if (bestBowIndex != -1) {
         keep[bestBowIndex] = true;
      }

      // 3. Best Armor (per armor slot)
      for (int type = 0; type < 4; ++type) {
         double equippedVal = 0.0;
         ItemStack equipped = minecraft.thePlayer.inventory.armorItemInSlot(type);
         if (equipped != null && ItemUtils.durable(equipped)) {
            equippedVal = ItemUtils.armorValue(equipped);
         }
         double maxInvVal = 0.0;
         int bestArmorIndex = -1;
         for (int i = 0; i < 36; ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack != null && ItemUtils.isArmor(stack) && ItemUtils.durable(stack)) {
               if (3 - ((ItemArmor) stack.getItem()).armorType == type) {
                  double val = ItemUtils.armorValue(stack);
                  if (val > maxInvVal) {
                     maxInvVal = val;
                     bestArmorIndex = i;
                  }
               }
            }
         }
         if (bestArmorIndex != -1 && maxInvVal > equippedVal) {
            keep[bestArmorIndex] = true;
         }
      }

      // 4. Best Tools (Pickaxe=0, Axe=1, Spade=2)
      for (int t = 0; t < 3; ++t) {
         double maxToolScore = 0.0;
         int bestToolIndex = -1;
         for (int i = 0; i < 36; ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack != null && ItemUtils.getToolType(stack) == t && ItemUtils.durable(stack)) {
               double score = ItemUtils.getToolScore(stack);
               if (score > maxToolScore) {
                  maxToolScore = score;
                  bestToolIndex = i;
               }
            }
         }
         if (bestToolIndex != -1) {
            keep[bestToolIndex] = true;
         }
      }

      // 5. Utility tools
      int rodLimit = Math.round(this.maxFishingRods.getCurrentValue());
      List<Integer> rodIndices = new ArrayList<>();
      for (int i = 0; i < 36; ++i) {
         ItemStack stack = inv.getStackInSlot(i);
         if (stack != null && stack.getItem() == Items.fishing_rod && ItemUtils.durable(stack)) {
            rodIndices.add(i);
         }
      }
      rodIndices.sort((a, b) -> {
         ItemStack sa = inv.getStackInSlot(a);
         ItemStack sb = inv.getStackInSlot(b);
         boolean prefA = preferredRod(sa);
         boolean prefB = preferredRod(sb);
         if (prefA != prefB) {
            return prefA ? -1 : 1;
         }
         return Integer.compare(sa.getItemDamage(), sb.getItemDamage());
      });
      for (int k = 0; k < Math.min(rodLimit, rodIndices.size()); ++k) {
         keep[rodIndices.get(k)] = true;
      }

      int flintLimit = Math.round(this.maxFlintAndSteel.getCurrentValue());
      List<Integer> flintIndices = new ArrayList<>();
      for (int i = 0; i < 36; ++i) {
         ItemStack stack = inv.getStackInSlot(i);
         if (stack != null && stack.getItem() == Items.flint_and_steel && ItemUtils.durable(stack)) {
            flintIndices.add(i);
         }
      }
      flintIndices.sort((a, b) -> Integer.compare(inv.getStackInSlot(a).getItemDamage(), inv.getStackInSlot(b).getItemDamage()));
      for (int k = 0; k < Math.min(flintLimit, flintIndices.size()); ++k) {
         keep[flintIndices.get(k)] = true;
      }

      int shearsLimit = Math.round(this.maxShears.getCurrentValue());
      List<Integer> shearsIndices = new ArrayList<>();
      for (int i = 0; i < 36; ++i) {
         ItemStack stack = inv.getStackInSlot(i);
         if (stack != null && stack.getItem() == Items.shears && ItemUtils.durable(stack)) {
            shearsIndices.add(i);
         }
      }
      shearsIndices.sort((a, b) -> Integer.compare(inv.getStackInSlot(a).getItemDamage(), inv.getStackInSlot(b).getItemDamage()));
      for (int k = 0; k < Math.min(shearsLimit, shearsIndices.size()); ++k) {
         keep[shearsIndices.get(k)] = true;
      }

      for (int i = 0; i < 36; ++i) {
         ItemStack stack = inv.getStackInSlot(i);
         if (stack != null && stack.getItem() == Items.compass) {
            keep[i] = true;
            break;
         }
      }

      for (int i = 0; i < 36; ++i) {
         ItemStack stack = inv.getStackInSlot(i);
         if (stack != null && stack.getItem() == Items.clock) {
            keep[i] = true;
            break;
         }
      }

      // 6. Whitelisted items with limits
      int keptBlocks = 0;
      int keptGapples = 0;
      int keptPearls = 0;
      int keptProjectiles = 0;
      int keptArrows = 0;
      int keptPotions = 0;
      int keptWaterBuckets = 0;
      int keptLavaBuckets = 0;

      int blockLimit = Math.round(this.maxBlocks.getCurrentValue());
      int gappleLimit = Math.round(this.maxGoldenApples.getCurrentValue());
      int pearlLimit = Math.round(this.maxEnderPearls.getCurrentValue());
      int projLimit = Math.round(this.maxProjectiles.getCurrentValue());
      int arrowLimit = Math.round(this.maxArrows.getCurrentValue());
      int potionLimit = Math.round(this.maxPotions.getCurrentValue());
      int waterLimit = Math.round(this.maxWaterBuckets.getCurrentValue());
      int lavaLimit = Math.round(this.maxLavaBuckets.getCurrentValue());

      for (int i = 0; i < 36; ++i) {
         ItemStack stack = inv.getStackInSlot(i);
         if (stack == null || keep[i]) continue;
         Item item = stack.getItem();

         if (ItemUtils.isBlock(item)) {
            if (keptBlocks < blockLimit) {
               keep[i] = true;
               keptBlocks += stack.stackSize;
            }
         } else if (item == Items.golden_apple) {
            if (keptGapples < gappleLimit) {
               keep[i] = true;
               keptGapples += stack.stackSize;
            }
         } else if (item == Items.ender_pearl) {
            if (keptPearls < pearlLimit) {
               keep[i] = true;
               keptPearls += stack.stackSize;
            }
         } else if (item == Items.snowball || item == Items.egg) {
            if (keptProjectiles < projLimit) {
               keep[i] = true;
               keptProjectiles += stack.stackSize;
            }
         } else if (item == Items.arrow) {
            if (keptArrows < arrowLimit) {
               keep[i] = true;
               keptArrows += stack.stackSize;
            }
         } else if (item == Items.potionitem && ItemUtils.isGoodPotion(stack)) {
            if (keptPotions < potionLimit) {
               keep[i] = true;
               ++keptPotions;
            }
         } else if (item == Items.water_bucket) {
            if (keptWaterBuckets < waterLimit) {
               keep[i] = true;
               ++keptWaterBuckets;
            }
         } else if (item == Items.lava_bucket) {
            if (keptLavaBuckets < lavaLimit) {
               keep[i] = true;
               ++keptLavaBuckets;
            }
         } else if (ItemUtils.isUtilityBlock(item)
               || item == Item.getItemFromBlock(Blocks.tnt) || item == Item.getItemFromBlock(Blocks.web)
               || ItemUtils.isGoodFood(stack) || item == Items.milk_bucket
               || item == Items.diamond || item == Items.emerald || item == Items.gold_ingot || item == Items.iron_ingot
               || item == Items.experience_bottle || item == Items.enchanted_book
               || item == Items.spawn_egg) {
            keep[i] = true;
         }
      }

      // 7. Drop everything not kept
      for (int i = 0; i < 36; ++i) {
         ItemStack stack = inv.getStackInSlot(i);
         if (stack == null || keep[i]) continue;
         int windowSlot = i < 9 ? i + 36 : i;
         if (click(windowSlot, 1, 4, Action.CLEANER)) {
            return true;
         }
         if (this.clicks > 50) {
            return true;
         }
      }

      return false;
   }

   private boolean sortBow(Scan s) {
      if (s.bow[1] != -1.0) {
         int bw = Math.round(this.bow.getCurrentValue());
         if (bw > 0) {
            if (s.bow[1] > bestBow) {
               int n = (int) s.bow[0];
               n = n < 9 ? n + 36 : n;
               if (click(n, bw - 1, 2, Action.SORT)) return true;
               return this.clicks > 50;
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
               if (click(n, sw - 1, 2, Action.SORT)) return true;
               return this.clicks > 50;
            }
         }
      }
      return false;
   }

   private boolean sortHotbar(Scan s) {
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
               if (click(n4, n5, 2, Action.SORT)) return true;
               if (this.clicks > 50) return true;
               continue;
            }
            if (other.stackSize >= 64) continue;
            if (item == other.getItem() && stack.getItemDamage() == other.getItemDamage()) {
               if (stack.stackSize == 64) {
                  if (click(n4, n5, 2, Action.SORT)) return true;
                  if (this.clicks > 50) return true;
                  continue;
               }
               if (s.emptyMain == 0 && n != n4 || !click(n4, 0, 1, Action.SORT)) continue;
               return true;
            }
            if (stack.stackSize <= other.stackSize) continue;
            if (click(n4, n5, 2, Action.SORT)) return true;
            if (this.clicks > 50) return true;
            continue;
         }
         if (item == Items.golden_apple) {
            int n5 = Math.round(this.goldenApple.getCurrentValue()) - 1;
            if (n5 == -1 || n == n5) continue;
            ItemStack other = s.inv.getStackInSlot(n5);
            if (other != null && other.getItem() == Items.golden_apple) continue;
            if (click(n4, n5, 2, Action.SORT)) return true;
            if (this.clicks > 50) return true;
            continue;
         }
         if (item == Items.snowball || item == Items.egg || item == Items.fishing_rod) {
            int n5 = Math.round(this.projectile.getCurrentValue()) - 1;
            if (n5 == -1 || n == n5) continue;
            ItemStack other = s.inv.getStackInSlot(n5);
            if (other == null || (other.getItem() != Items.snowball && other.getItem() != Items.egg && other.getItem() != Items.fishing_rod)) {
               if (click(n4, n5, 2, Action.SORT)) return true;
               if (this.clicks > 50) return true;
               continue;
            }
            if (preferredRod(other)) continue;
            if (preferredRod(stack)) {
               if (click(n4, n5, 2, Action.SORT)) return true;
               if (this.clicks > 50) return true;
               continue;
            }
            if (item == Items.fishing_rod) continue;
            if (stack.stackSize >= 16) {
               if (stack.stackSize <= other.stackSize) continue;
               if (click(n4, n5, 2, Action.SORT)) return true;
               if (this.clicks > 50) return true;
               continue;
            }
            if (other.stackSize >= 16) continue;
            if (item == other.getItem()) {
               if (s.emptyMain == 0 && n != n4 || !click(n4, 0, 1, Action.SORT)) continue;
               return true;
            }
            if (stack.stackSize <= other.stackSize) continue;
            if (click(n4, n5, 2, Action.SORT)) return true;
            if (this.clicks > 50) return true;
            continue;
         }
         if (item == Items.water_bucket || item == Items.lava_bucket) {
            int n5 = Math.round((item == Items.water_bucket ? this.waterBucket : this.lavaBucket).getCurrentValue()) - 1;
            if (n5 == -1 || n == n5) continue;
            ItemStack other = s.inv.getStackInSlot(n5);
            if (other != null && other.getItem() == item) continue;
            if (click(n4, n5, 2, Action.SORT)) return true;
            if (this.clicks > 50) return true;
            continue;
         }
         if (item == Items.potionitem) {
            int n5 = Math.round(this.speedPotion.getCurrentValue()) - 1;
            if (n5 == -1 || n == n5) continue;
            if (!ItemUtils.isSpeedPotion(stack) || ItemUtils.isSpeedPotion(s.inv.getStackInSlot(n5))) continue;
            if (click(n4, n5, 2, Action.SORT)) return true;
            if (this.clicks > 50) return true;
            continue;
         }
         if (item == Items.ender_pearl) {
            int n5 = Math.round(this.enderPearl.getCurrentValue()) - 1;
            if (n5 == -1 || n == n5) continue;
            ItemStack other = s.inv.getStackInSlot(n5);
            if (other != null && other.getItem() == Items.ender_pearl) continue;
            if (click(n4, n5, 2, Action.SORT)) return true;
            if (this.clicks > 50) return true;
            continue;
         }
      }
      return false;
   }

   private boolean moveOrSwap(int fromSlot, int toSlot, Action action) {
      this.delay = action.getDelay(this);
      ++this.clicks;
      int windowId = minecraft.thePlayer.openContainer.windowId;
      minecraft.playerController.windowClick(windowId, fromSlot, 0, 0, minecraft.thePlayer);
      minecraft.playerController.windowClick(windowId, toSlot, 0, 0, minecraft.thePlayer);
      if (minecraft.thePlayer.inventory.getItemStack() != null) {
         minecraft.playerController.windowClick(windowId, fromSlot, 0, 0, minecraft.thePlayer);
      }
      return this.delay != 0L;
   }

   private boolean canMerge(ItemStack target, ItemStack source) {
      if (target == null || source == null) return false;
      if (!target.isStackable() || target.stackSize >= target.getMaxStackSize()) return false;
      if (target.getItem() != source.getItem()) return false;
      if (target.getItemDamage() != source.getItemDamage()) return false;
      return ItemStack.areItemStackTagsEqual(target, source);
   }

   private int getItemPriority(ItemStack stack) {
      if (stack == null) return 999;
      Item item = stack.getItem();
      if (item == null) return 999;

      // 1. Tools
      if (item instanceof ItemPickaxe) return 10;
      if (item instanceof ItemAxe) return 11;
      if (item instanceof ItemSpade) return 12;
      if (item == Items.fishing_rod) return 13;
      if (item == Items.flint_and_steel) return 14;
      if (item == Items.shears) return 15;

      // 2. Weapons & Armor
      if (ItemUtils.isSword(stack)) return 20;
      if (ItemUtils.isBow(stack)) return 21;
      if (ItemUtils.isArmor(stack)) return 22;

      // 3. Consumables
      if (item == Items.golden_apple) return 30;
      if (item == Items.potionitem) return 31;
      if (ItemUtils.isGoodFood(stack)) return 32;

      // 4. Movement / Buckets
      if (item == Items.ender_pearl) return 40;
      if (item == Items.water_bucket) return 41;
      if (item == Items.lava_bucket) return 42;
      if (item == Items.milk_bucket) return 43;

      // 5. Projectiles
      if (item == Items.snowball || item == Items.egg) return 50;
      if (item == Items.arrow) return 51;

      // 6. Blocks
      if (ItemUtils.isBlock(item)) return 60;
      if (ItemUtils.isUtilityBlock(item) || item == Item.getItemFromBlock(Blocks.tnt) || item == Item.getItemFromBlock(Blocks.web)) return 61;

      // 7. Valuables & Tracking
      if (item == Items.diamond) return 70;
      if (item == Items.emerald) return 71;
      if (item == Items.gold_ingot) return 72;
      if (item == Items.iron_ingot) return 73;
      if (item == Items.experience_bottle) return 74;
      if (item == Items.enchanted_book) return 75;
      if (item == Items.compass) return 76;
      if (item == Items.clock) return 77;
      if (item == Items.spawn_egg) return 78;

      return 80;
   }

   private int compareItems(ItemStack a, ItemStack b) {
      if (a == null && b == null) return 0;
      if (a == null) return 1;
      if (b == null) return -1;

      int pA = getItemPriority(a);
      int pB = getItemPriority(b);
      if (pA != pB) {
         return Integer.compare(pA, pB);
      }

      int idA = Item.getIdFromItem(a.getItem());
      int idB = Item.getIdFromItem(b.getItem());
      if (idA != idB) {
         return Integer.compare(idA, idB);
      }

      if (a.getItemDamage() != b.getItemDamage()) {
         return Integer.compare(a.getItemDamage(), b.getItemDamage());
      }

      return Integer.compare(b.stackSize, a.stackSize);
   }

   private boolean sortMainInventory() {
      IInventory inv = minecraft.thePlayer.inventory;

      // Phase 1: Stack merging into hotbar
      for (int h = 0; h < 9; ++h) {
         ItemStack hotbarStack = inv.getStackInSlot(h);
         if (hotbarStack == null || !hotbarStack.isStackable() || hotbarStack.stackSize >= hotbarStack.getMaxStackSize()) continue;
         for (int m = 9; m < 36; ++m) {
            ItemStack mainStack = inv.getStackInSlot(m);
            if (mainStack == null) continue;
            if (canMerge(hotbarStack, mainStack)) {
               moveOrSwap(m, h + 36, Action.SORT);
               if (this.delay != 0L || this.clicks > 50) return true;
            }
         }
      }

      // Phase 1: Stack merging within main inventory
      for (int i = 9; i < 36; ++i) {
         ItemStack stackI = inv.getStackInSlot(i);
         if (stackI == null || !stackI.isStackable() || stackI.stackSize >= stackI.getMaxStackSize()) continue;
         for (int j = i + 1; j < 36; ++j) {
            ItemStack stackJ = inv.getStackInSlot(j);
            if (stackJ == null) continue;
            if (canMerge(stackI, stackJ)) {
               moveOrSwap(j, i, Action.SORT);
               if (this.delay != 0L || this.clicks > 50) return true;
            }
         }
      }

      // Phase 2: Ordering and compacting slots 9 to 35
      for (int i = 9; i < 35; ++i) {
         int bestSlot = i;
         ItemStack bestStack = inv.getStackInSlot(bestSlot);
         for (int j = i + 1; j < 36; ++j) {
            ItemStack stackJ = inv.getStackInSlot(j);
            if (compareItems(stackJ, bestStack) < 0) {
               bestSlot = j;
               bestStack = stackJ;
            }
         }
         if (bestSlot != i) {
            moveOrSwap(bestSlot, i, Action.SORT);
            if (this.delay != 0L || this.clicks > 50) {
               return true;
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
