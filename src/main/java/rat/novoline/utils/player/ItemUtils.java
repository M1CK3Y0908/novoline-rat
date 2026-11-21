package rat.novoline.utils.player;

import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import rat.novoline.enums.HypixelGameType;
import rat.novoline.utils.hypixel.HypixelState;

public class ItemUtils {
   public static int getArmorScore(ItemStack itemStack) {
      ItemArmor itemArmor = (ItemArmor)itemStack.getItem();
      return 0
         + itemArmor.damageReduceAmount
         + EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, itemStack) * 4
         + EnchantmentHelper.getEnchantmentLevel(Enchantment.projectileProtection.effectId, itemStack)
         + EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, itemStack)
         + EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, itemStack)
         + EnchantmentHelper.getEnchantmentLevel(Enchantment.depthStrider.effectId, itemStack)
         + EnchantmentHelper.getEnchantmentLevel(Enchantment.aquaAffinity.effectId, itemStack);
   }

   public static int getMaterialScore(String text) {
      switch (text) {
         case "EMERALD":
         case "DIAMOND":
            return 3;
         case "IRON":
            return 2;
         case "STONE":
            return 1;
         case "GOLD":
         case "WOOD":
            return 0;
         default:
            return 0;
      }
   }

   public static int getToolType(ItemStack stack) {
      if (stack == null) return -1;
      Item item = stack.getItem();
      if (item instanceof ItemPickaxe) return 0;
      if (item instanceof ItemAxe) return 1;
      if (item instanceof ItemSpade) return 2;
      return -1;
   }

   public static double getToolScore(ItemStack stack) {
      if (stack == null || !(stack.getItem() instanceof ItemTool)) {
         return 0.0;
      }
      ItemTool tool = (ItemTool) stack.getItem();
      String mat = tool.getToolMaterialName();
      double base;
      switch (mat) {
         case "EMERALD":
         case "DIAMOND":
            base = 80.0;
            break;
         case "IRON":
            base = 60.0;
            break;
         case "STONE":
            base = 40.0;
            break;
         case "GOLD":
            base = 20.0;
            break;
         case "WOOD":
         default:
            base = 10.0;
            break;
      }
      int eff = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack);
      base += eff * 5.0;
      int unb = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
      base += unb * 1.0;
      int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack);
      base += fortune * 2.0;
      int silk = EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, stack);
      base += silk * 2.0;
      if (tool instanceof ItemAxe) {
         base += attackDamage(stack);
      }
      return base;
   }

   // --- Raven XD Compatibility & Exact Logic ---

   public static double attackDamage(ItemStack itemStack) {
      if (itemStack == null) {
         return 0.0;
      }
      double d = 0.0;
      for (Map.Entry<String, AttributeModifier> entry : itemStack.getAttributeModifiers().entries()) {
         if (!entry.getKey().equals("generic.attackDamage")) continue;
         d = entry.getValue().getAmount();
         break;
      }
      int n = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack);
      int n2 = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, itemStack);
      return d + (double) n * 1.25 + (double) (n2 > 0 ? n2 * 4 - 1 : 0);
   }

   public static double bowEnchantLevels(ItemStack itemStack) {
      if (itemStack == null) {
         return 0.0;
      }
      return (double) EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, itemStack)
           + (double) EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, itemStack)
           + (double) EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, itemStack);
   }

   public static double armorValue(ItemStack itemStack) {
      if (itemStack == null || !(itemStack.getItem() instanceof ItemArmor)) {
         return 0.0;
      }
      DamageSource damageSource = DamageSource.generic;
      int n = ((ItemArmor) itemStack.getItem()).damageReduceAmount;
      int n2 = 0;
      NBTTagList nBTTagList = itemStack.getEnchantmentTagList();
      if (nBTTagList != null) {
         for (int i = 0; i < nBTTagList.tagCount(); ++i) {
            short s = nBTTagList.getCompoundTagAt(i).getShort("id");
            short s2 = nBTTagList.getCompoundTagAt(i).getShort("lvl");
            Enchantment enchantment = Enchantment.getEnchantmentById(s);
            if (enchantment == null) continue;
            n2 += enchantment.calcModifierDamage(s2, damageSource);
         }
      }
      if (n2 > 25) {
         n2 = 25;
      }
      if (n2 < 0) {
         n2 = 0;
      }
      double d = (double) (3 * n2 + (n2 & 1)) / 4.0;
      return (double) n + d;
   }

   public static boolean hasDurability(ItemStack itemStack, double fraction) {
      return itemStack != null && (!itemStack.isItemStackDamageable() || itemStack.getMaxDamage() <= 0
            || (double) (itemStack.getMaxDamage() - itemStack.getItemDamage()) / (double) itemStack.getMaxDamage() >= fraction);
   }

   public static boolean durable(ItemStack stack) {
      return hasDurability(stack, 0.1);
   }

   public static boolean isPlaceable(Block block) {
      return block == Blocks.glass || block == Blocks.stained_glass
            || (block.isFullBlock() && block != Blocks.gravel && block != Blocks.sand && block != Blocks.soul_sand
            && block != Blocks.tnt && block != Blocks.crafting_table && block != Blocks.furnace && block != Blocks.dispenser
            && block != Blocks.dropper && block != Blocks.noteblock && block != Blocks.command_block);
   }

   public static boolean isBlock(Item item) {
      return item instanceof ItemBlock && isPlaceable(((ItemBlock) item).getBlock());
   }

   public static boolean isUtilityBlock(Item item) {
      return item == Item.getItemFromBlock(Blocks.chest) || item == Item.getItemFromBlock(Blocks.crafting_table)
            || item == Item.getItemFromBlock(Blocks.furnace) || item == Item.getItemFromBlock(Blocks.enchanting_table)
            || item == Item.getItemFromBlock(Blocks.anvil);
   }

   public static boolean isSword(ItemStack stack) {
      return stack != null && stack.getItem() instanceof ItemSword;
   }

   public static boolean isBow(ItemStack stack) {
      return stack != null && stack.getItem() == Items.bow;
   }

   public static boolean isArmor(ItemStack stack) {
      return stack != null && stack.getItem() instanceof ItemArmor;
   }

   public static boolean preferredRod(ItemStack stack, boolean rodOverProj, boolean onlyWithKb) {
      return rodOverProj && stack != null && stack.getItem() == Items.fishing_rod
            && (!onlyWithKb || EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, stack) > 0);
   }

   public static boolean isBadPotion(ItemStack stack) {
      if (stack == null || stack.getItem() != Items.potionitem) {
         return false;
      }
      List<PotionEffect> list = ((ItemPotion) stack.getItem()).getEffects(stack);
      if (list == null || list.isEmpty()) {
         return true;
      }
      for (PotionEffect e : list) {
         int id = e.getPotionID();
         if (id == 19 || id == 2 || id == 18 || id == 7 || id == 9 || id == 15 || id == 17) {
            return true;
         }
         String s = e.toString().toLowerCase();
         if (s.contains("poison") || s.contains("moveslowdown") || s.contains("weakness") || s.contains("harm") || s.contains("blind") || s.contains("hunger")) {
            return true;
         }
      }
      return false;
   }

   public static boolean isGoodPotion(ItemStack stack) {
      return stack != null && stack.getItem() == Items.potionitem && !isBadPotion(stack);
   }

   public static boolean isGoodFood(ItemStack stack) {
      if (stack == null || !(stack.getItem() instanceof ItemFood)) {
         return false;
      }
      Item item = stack.getItem();
      if (item == Items.rotten_flesh || item == Items.spider_eye || item == Items.poisonous_potato) {
         return false;
      }
      if (item == Items.beef || item == Items.chicken || item == Items.porkchop || item == Items.mutton || item == Items.rabbit || item == Items.fish) {
         return false;
      }
      return true;
   }

   public static boolean isUsefulItem(ItemStack stack) {
      if (stack == null) {
         return false;
      }
      Item item = stack.getItem();
      if (item == null) {
         return false;
      }

      if (isSword(stack) || isBow(stack) || isArmor(stack) || (item instanceof ItemTool && !(item instanceof ItemHoe))) {
         return durable(stack);
      }

      if (isBlock(item) || isUtilityBlock(item) || item == Item.getItemFromBlock(Blocks.tnt) || item == Item.getItemFromBlock(Blocks.web)) {
         return true;
      }

      if (item == Items.golden_apple || isGoodFood(stack)) {
         return true;
      }

      if (item == Items.potionitem && isGoodPotion(stack)) {
         return true;
      }

      if (item == Items.ender_pearl || item == Items.snowball || item == Items.egg || item == Items.arrow) {
         return true;
      }

      if (item == Items.water_bucket || item == Items.lava_bucket || item == Items.milk_bucket) {
         return true;
      }

      if (item == Items.fishing_rod || item == Items.flint_and_steel || item == Items.shears) {
         return durable(stack);
      }

      if (item == Items.diamond || item == Items.emerald || item == Items.gold_ingot || item == Items.iron_ingot
            || item == Items.experience_bottle || item == Items.enchanted_book) {
         return true;
      }

      if (item == Items.compass || item == Items.clock || item == Items.spawn_egg) {
         return true;
      }

      return false;
   }

   public static boolean isSpeedPotion(ItemStack stack) {
      List<PotionEffect> list;
      if (stack != null && stack.getItem() == Items.potionitem && (list = ((ItemPotion) stack.getItem()).getEffects(stack)) != null) {
         for (PotionEffect e : list) {
            if (e.toString().contains("moveSpeed")) {
               return true;
            }
         }
      }
      return false;
   }

   public static boolean pausedInLobby(boolean disableInLobby) {
      return disableInLobby && (HypixelState.isGameType(HypixelGameType.LOBBY) || HypixelState.isGameType(HypixelGameType.PRE_GAME));
   }
}
