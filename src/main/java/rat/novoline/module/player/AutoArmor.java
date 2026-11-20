package rat.novoline.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import rat.novoline.event.PreUpdateEvent;
import rat.novoline.module.Category;
import rat.novoline.module.Module;
import rat.novoline.setting.CheckboxSetting;
import rat.novoline.setting.NumberSetting;
import rat.novoline.utils.player.ItemUtils;

public class AutoArmor extends Module {
   public final NumberSetting delay = settingManager.register(
      new NumberSetting(this, "Delay", 1, 0, 20, " ticks")
   );
   public final CheckboxSetting disableInLobby = settingManager.register(new CheckboxSetting(this, "Disable in lobby", true));
   public final CheckboxSetting dropOldArmor = settingManager.register(new CheckboxSetting(this, "Drop old armor", true));
   public final CheckboxSetting closeInventory = settingManager.register(new CheckboxSetting(this, "Close inventory", false));

   public boolean processing;
   private long currentDelay = 0L;
   private long waited = 0L;

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

   public AutoArmor() {
      super(Category.PLAYER, "AutoArmor", "Equips best armor automatically.");
      this.addAlias("Armor");
   }

   @EventLink
   public final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      this.processing = false;
      if (minecraft.thePlayer == null || minecraft.theWorld == null) {
         this.currentDelay = 0L;
         this.waited = 0L;
         return;
      }

      if (minecraft.currentScreen == null || ItemUtils.pausedInLobby(this.disableInLobby.isEnabled())) {
         this.currentDelay = 0L;
         this.waited = 0L;
         return;
      }

      if (++this.waited < this.currentDelay) {
         return;
      }
      this.waited = 0L;

      if (minecraft.currentScreen instanceof GuiInventory) {
         int armorDelay = Math.round(this.delay.getCurrentValue());
         if (armorDelay >= 0) {
            updateArmor();
            Scan s = new Scan(minecraft.thePlayer.inventory, true);
            for (int n = 0; n < s.armor[0].length; ++n) {
               if (s.armor[1][n] == -1.0) continue;
               ArmorSlot a = armor[n];
               if (!(s.armor[1][n] > a.value)) continue;
               if (a.getItemStack() != null && this.dropOldArmor.isEnabled()) {
                  click(a.slot, 0, 4, armorDelay);
               }
               int n2 = (int) s.armor[0][n];
               int n3 = n2 < 9 ? n2 + 36 : n2;
               if (!click(n3, 0, 1, armorDelay)) continue;
               this.processing = true;
               return;
            }
            if (this.closeInventory.isEnabled()) {
               minecraft.thePlayer.closeScreen();
            }
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
      this.waited = 0L;
      this.processing = false;
   }

   static class Scan {
      final IInventory inv;
      final int size;
      final double[][] armor = {{-1.0, -1.0, -1.0, -1.0}, {-1.0, -1.0, -1.0, -1.0}};

      Scan(IInventory inv, boolean player) {
         this.inv = inv;
         this.size = !player ? inv.getSizeInventory() : inv.getSizeInventory() - 4;
         for (int i = 0; i < this.size; ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack == null || !ItemUtils.durable(stack)) continue;
            if (ItemUtils.isArmor(stack)) {
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
