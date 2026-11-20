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
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPotion;
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

   public static int getToolScore(ItemStack itemStack) {
      Item item2 = itemStack.getItem();
      int i = 0;
      if (item2 instanceof ItemSword itemSword) {
         i = 0 + getMaterialScore(itemSword.getToolMaterialName());
         i = (int)(i + itemSword.attackDamage);
      } else if (item2 instanceof ItemTool itemTool) {
         i = 0 + getMaterialScore(itemTool.getToolMaterialName());
      } else if (item2 instanceof ItemHoe itemHoe) {
         i = 0 + getMaterialScore(itemHoe.getMaterialName());
      }

      i += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack) * 4;
      i += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, itemStack) * 2;
      i += EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, itemStack);
      i += EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, itemStack);
      i += EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, itemStack);
      i += EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, itemStack);
      i += EnchantmentHelper.getEnchantmentLevel(Enchantment.smite.effectId, itemStack);
      i += EnchantmentHelper.getEnchantmentLevel(Enchantment.silkTouch.effectId, itemStack);
      i += EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack);
      i += EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, itemStack);
      i += EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, itemStack);
      i += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, itemStack);
      return i + EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId, itemStack);
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
      return block == Blocks.glass || (block.isFullBlock() && block != Blocks.gravel && block != Blocks.sand && block != Blocks.soul_sand
            && block != Blocks.tnt && block != Blocks.crafting_table && block != Blocks.furnace && block != Blocks.dispenser
            && block != Blocks.dropper && block != Blocks.noteblock && block != Blocks.command_block);
   }

   public static boolean isBlock(Item item) {
      return item instanceof ItemBlock && isPlaceable(((ItemBlock) item).getBlock());
   }

   public static boolean isUtilityBlock(Item item) {
      return item == Item.getItemFromBlock(Blocks.chest) || item == Item.getItemFromBlock(Blocks.crafting_table)
            || item == Item.getItemFromBlock(Blocks.enchanting_table) || item == Item.getItemFromBlock(Blocks.anvil);
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
      List<PotionEffect> list;
      if (stack != null && stack.getItem() == Items.potionitem && (list = ((ItemPotion) stack.getItem()).getEffects(stack)) != null) {
         for (PotionEffect e : list) {
            String s = e.toString();
            if (s.contains("poison") || s.contains("moveSlowdown")) {
               return true;
            }
         }
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
