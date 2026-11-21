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
import rat.novoline.setting.MultiComboSetting;
import rat.novoline.setting.NumberSetting;
import rat.novoline.utils.player.ItemUtils;

public class ChestStealer extends Module {
   public final NumberSetting delay = settingManager.register(
      new NumberSetting(this, "Delay", 1, 0, 20, " ticks")
   );
   public final CheckboxSetting closeChest = settingManager.register(new CheckboxSetting(this, "Close chest", false));
   public final CheckboxSetting stealFromCustomChests = settingManager.register(new CheckboxSetting(this, "Steal from custom chests", false));
   public final CheckboxSetting disableInLobby = settingManager.register(new CheckboxSetting(this, "Disable in lobby", true));

   public final MultiComboSetting items = settingManager.register(
      new MultiComboSetting(this, "Item")
         .add("Swords", true)
         .add("Armors", true)
         .add("Pickaxes", true)
         .add("Axes", true)
         .add("Shovels", true)
         .add("Flint and Steel", true)
         .add("Shears", true)
         .add("Potions", true)
         .add("Blocks", true)
         .add("Golden Apple", true)
         .add("Ender Pearl", true)
         .add("Projectiles", true)
         .add("Bow", true)
         .add("Arrow", true)
         .add("Buckets", true)
         .add("Fish Rod", true)
   );

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

         c.dropWorse(p);

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
            if (c.rod[1] != -1.0 && click((int) c.rod[0], 0, 1, stealDelay)) {
               return;
            }
            if (c.flint[1] != -1.0 && click((int) c.flint[0], 0, 1, stealDelay)) {
               return;
            }
            if (c.shears[1] != -1.0 && click((int) c.shears[0], 0, 1, stealDelay)) {
               return;
            }

            for (int n = 0; n < c.size; ++n) {
               ItemStack stack = c.inv.getStackInSlot(n);
               if (stack == null) {
                  continue;
               }
               if (ItemUtils.isSword(stack) || ItemUtils.isBow(stack) || ItemUtils.isArmor(stack)
                     || stack.getItem() instanceof ItemTool || stack.getItem() instanceof ItemHoe
                     || stack.getItem() == Items.fishing_rod || stack.getItem() == Items.flint_and_steel
                     || stack.getItem() == Items.shears) {
                  continue;
               }
               if (!isWantedConsumable(stack)) {
                  continue;
               }
               if (!click(n, 0, 1, stealDelay)) continue;
               return;
            }
         } else {
            for (int i = 0; i < c.size; ++i) {
               ItemStack stack = c.inv.getStackInSlot(i);
               if (stack == null) continue;
               if (ItemUtils.isSword(stack) || ItemUtils.isBow(stack) || ItemUtils.isArmor(stack)
                     || stack.getItem() instanceof ItemTool || stack.getItem() instanceof ItemHoe
                     || stack.getItem() == Items.fishing_rod || stack.getItem() == Items.flint_and_steel
                     || stack.getItem() == Items.shears) {
                  continue;
               }
               if (!isWantedConsumable(stack)) continue;
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

   private boolean isWantedConsumable(ItemStack stack) {
      if (stack == null) return false;
      Item item = stack.getItem();
      if (item == null) return false;

      if (ItemUtils.isGoodPotion(stack)) {
         return this.items.isSelected("Potions");
      }
      if (ItemUtils.isBlock(item)) {
         return this.items.isSelected("Blocks");
      }
      if (item == Items.golden_apple) {
         return this.items.isSelected("Golden Apple");
      }
      if (item == Items.ender_pearl) {
         return this.items.isSelected("Ender Pearl");
      }
      if (item == Items.snowball || item == Items.egg) {
         return this.items.isSelected("Projectiles");
      }
      if (item == Items.arrow) {
         return this.items.isSelected("Arrow");
      }
      if (item == Items.water_bucket || item == Items.lava_bucket || item == Items.milk_bucket || item == Items.bucket) {
         return this.items.isSelected("Buckets");
      }
      return false;
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
      final double[][] tools = {{-1.0, -1.0, -1.0}, {-1.0, -1.0, -1.0}};
      final double[] rod = {-1.0, -1.0};
      final double[] flint = {-1.0, -1.0};
      final double[] shears = {-1.0, -1.0};
      int filled;

      Scan(IInventory inv, boolean player) {
         this.inv = inv;
         this.size = !player ? inv.getSizeInventory() : inv.getSizeInventory() - 4;
         for (int i = 0; i < this.size; ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack != null) {
               ++this.filled;
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
            } else if (stack.getItem() instanceof ItemTool && !(stack.getItem() instanceof ItemHoe)) {
               int toolType = ItemUtils.getToolType(stack);
               if (toolType >= 0 && toolType < 3) {
                  double d = ItemUtils.getToolScore(stack);
                  if (d > this.tools[1][toolType]) {
                     this.tools[1][toolType] = d;
                     this.tools[0][toolType] = i;
                  }
               }
            } else if (stack.getItem() == Items.fishing_rod) {
               double d = (double) (stack.getMaxDamage() - stack.getItemDamage());
               int kb = net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(net.minecraft.enchantment.Enchantment.knockback.effectId, stack);
               if (kb > 0) d += 1000.0 * kb;
               if (d > this.rod[1]) {
                  this.rod[1] = d;
                  this.rod[0] = i;
               }
            } else if (stack.getItem() == Items.flint_and_steel) {
               double d = (double) (stack.getMaxDamage() - stack.getItemDamage());
               if (d > this.flint[1]) {
                  this.flint[1] = d;
                  this.flint[0] = i;
               }
            } else if (stack.getItem() == Items.shears) {
               double d = (double) (stack.getMaxDamage() - stack.getItemDamage());
               if (d > this.shears[1]) {
                  this.shears[1] = d;
                  this.shears[0] = i;
               }
            }
         }
      }

      void dropWorse(Scan player) {
         if (!ChestStealer.this.items.isSelected("Swords") || player.sword[1] >= this.sword[1]) {
            this.sword[1] = -1.0;
         }
         if (!ChestStealer.this.items.isSelected("Bow") || player.bow[1] >= this.bow[1]) {
            this.bow[1] = -1.0;
         }
         for (int i = 0; i < this.armor[0].length; ++i) {
            double playerArmorVal = Math.max(ChestStealer.armor[i].value, player.armor[1][i]);
            if (!ChestStealer.this.items.isSelected("Armors") || playerArmorVal >= this.armor[1][i]) {
               this.armor[1][i] = -1.0;
            }
         }
         if (!ChestStealer.this.items.isSelected("Pickaxes") || player.tools[1][0] >= this.tools[1][0]) {
            this.tools[1][0] = -1.0;
         }
         if (!ChestStealer.this.items.isSelected("Axes") || player.tools[1][1] >= this.tools[1][1]) {
            this.tools[1][1] = -1.0;
         }
         if (!ChestStealer.this.items.isSelected("Shovels") || player.tools[1][2] >= this.tools[1][2]) {
            this.tools[1][2] = -1.0;
         }
         if (!ChestStealer.this.items.isSelected("Fish Rod") || player.rod[1] >= this.rod[1]) {
            this.rod[1] = -1.0;
         }
         if (!ChestStealer.this.items.isSelected("Flint and Steel") || player.flint[1] >= this.flint[1]) {
            this.flint[1] = -1.0;
         }
         if (!ChestStealer.this.items.isSelected("Shears") || player.shears[1] >= this.shears[1]) {
            this.shears[1] = -1.0;
         }
      }
   }
}
