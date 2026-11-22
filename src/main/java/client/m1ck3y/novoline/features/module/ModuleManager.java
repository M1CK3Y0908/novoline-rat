package client.m1ck3y.novoline.features.module;

import com.google.common.collect.Lists;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import client.m1ck3y.novoline.Novoline.Fonts;
import client.m1ck3y.novoline.features.module.modules.combat.AimBot;
import client.m1ck3y.novoline.features.module.modules.combat.Antibot;
import client.m1ck3y.novoline.features.module.modules.combat.AutoClicker;
import client.m1ck3y.novoline.features.module.modules.combat.AutoHead;
import client.m1ck3y.novoline.features.module.modules.combat.AutoPotion;
import client.m1ck3y.novoline.features.module.modules.combat.BowAimbot;
import client.m1ck3y.novoline.features.module.modules.combat.Criticals;
import client.m1ck3y.novoline.features.module.modules.combat.FastBow;
import client.m1ck3y.novoline.features.module.modules.combat.KeepSprint;
import client.m1ck3y.novoline.features.module.modules.combat.Killaura;
import client.m1ck3y.novoline.features.module.modules.combat.Reach;
import client.m1ck3y.novoline.features.module.modules.combat.TargetStrafe;
import client.m1ck3y.novoline.features.module.modules.combat.WTap;
import client.m1ck3y.novoline.features.module.modules.hud.ArrayList;
import client.m1ck3y.novoline.features.module.modules.hud.ArmorHUD;
import client.m1ck3y.novoline.features.module.modules.hud.ClickGui;
import client.m1ck3y.novoline.features.module.modules.hud.Font;
import client.m1ck3y.novoline.features.module.modules.hud.Hotbar;
import client.m1ck3y.novoline.features.module.modules.hud.Information;
import client.m1ck3y.novoline.features.module.modules.hud.Notifications;
import client.m1ck3y.novoline.features.module.modules.hud.PostProcessing;
import client.m1ck3y.novoline.features.module.modules.hud.PotionHUD;
import client.m1ck3y.novoline.features.module.modules.hud.Scoreboard;
import client.m1ck3y.novoline.features.module.modules.hud.TargetESP;
import client.m1ck3y.novoline.features.module.modules.hud.TargetHUD;
import client.m1ck3y.novoline.features.module.modules.hud.ThemeColor;
import client.m1ck3y.novoline.features.module.modules.hud.Watermark;
import client.m1ck3y.novoline.features.module.modules.misc.AutoHypixel;
import client.m1ck3y.novoline.features.module.modules.misc.AutoRegister;
import client.m1ck3y.novoline.features.module.modules.misc.BetterMinecraft;
import client.m1ck3y.novoline.features.module.modules.misc.ChatBypass;
import client.m1ck3y.novoline.features.module.modules.misc.Derp;
import client.m1ck3y.novoline.features.module.modules.misc.Disabler;
import client.m1ck3y.novoline.features.module.modules.misc.HypixelStats;
import client.m1ck3y.novoline.features.module.modules.misc.Killsults;
import client.m1ck3y.novoline.features.module.modules.misc.LagDetector;
import client.m1ck3y.novoline.features.module.modules.misc.NameProtect;
import client.m1ck3y.novoline.features.module.modules.misc.NoCloseGui;
import client.m1ck3y.novoline.features.module.modules.misc.PingSpoof;
import client.m1ck3y.novoline.features.module.modules.misc.Regen;
import client.m1ck3y.novoline.features.module.modules.misc.Spammer;
import client.m1ck3y.novoline.features.module.modules.movement.Fly;
import client.m1ck3y.novoline.features.module.modules.movement.Jesus;
import client.m1ck3y.novoline.features.module.modules.movement.LongJump;
import client.m1ck3y.novoline.features.module.modules.movement.Phase;
import client.m1ck3y.novoline.features.module.modules.movement.Speed;
import client.m1ck3y.novoline.features.module.modules.movement.Sprint;
import client.m1ck3y.novoline.features.module.modules.movement.Step;
import client.m1ck3y.novoline.features.module.modules.player.AntiVoid;
import client.m1ck3y.novoline.features.module.modules.player.AutoArmor;
import client.m1ck3y.novoline.features.module.modules.player.AutoTool;
import client.m1ck3y.novoline.features.module.modules.player.Blink;
import client.m1ck3y.novoline.features.module.modules.player.FastEat;
import client.m1ck3y.novoline.features.module.modules.player.FreeCam;
import client.m1ck3y.novoline.features.module.modules.player.InventoryManager;
import client.m1ck3y.novoline.features.module.modules.player.InventoryMove;
import client.m1ck3y.novoline.features.module.modules.player.NoFall;
import client.m1ck3y.novoline.features.module.modules.player.NoRotate;
import client.m1ck3y.novoline.features.module.modules.player.NoSlowDown;
import client.m1ck3y.novoline.features.module.modules.player.Timer;
import client.m1ck3y.novoline.features.module.modules.combat.Velocity;
import client.m1ck3y.novoline.features.module.modules.visual.Animations;
import client.m1ck3y.novoline.features.module.modules.visual.CameraClip;
import client.m1ck3y.novoline.features.module.modules.visual.Chams;
import client.m1ck3y.novoline.features.module.modules.visual.ChinaHat;
import client.m1ck3y.novoline.features.module.modules.visual.ChineseKungfu;
import client.m1ck3y.novoline.features.module.modules.visual.Crosshair;
import client.m1ck3y.novoline.features.module.modules.visual.CustomModel;
import client.m1ck3y.novoline.features.module.modules.visual.DamageParticles;
import client.m1ck3y.novoline.features.module.modules.visual.EntityCulling;
import client.m1ck3y.novoline.features.module.modules.visual.FreeLook;
import client.m1ck3y.novoline.features.module.modules.visual.ItemESP;
import client.m1ck3y.novoline.features.module.modules.visual.ItemPhysics;
import client.m1ck3y.novoline.features.module.modules.visual.JumpCircle;
import client.m1ck3y.novoline.features.module.modules.visual.MobESP;
import client.m1ck3y.novoline.features.module.modules.visual.MoBends;
import client.m1ck3y.novoline.features.module.modules.visual.NameTags;
import client.m1ck3y.novoline.features.module.modules.visual.NightVision;
import client.m1ck3y.novoline.features.module.modules.visual.NoHurtCam;
import client.m1ck3y.novoline.features.module.modules.visual.NoRender;
import client.m1ck3y.novoline.features.module.modules.visual.PlayerESP;
import client.m1ck3y.novoline.features.module.modules.visual.Radar;
import client.m1ck3y.novoline.features.module.modules.visual.SeeBarriers;
import client.m1ck3y.novoline.features.module.modules.visual.SeeInvisibles;
import client.m1ck3y.novoline.features.module.modules.visual.SkinChanger;
import client.m1ck3y.novoline.features.module.modules.visual.StorageESP;
import client.m1ck3y.novoline.features.module.modules.visual.Trails;
import client.m1ck3y.novoline.features.module.modules.visual.Weather;
import client.m1ck3y.novoline.features.module.modules.visual.WorldColor;
import client.m1ck3y.novoline.features.module.modules.visual.WorldTime;
import client.m1ck3y.novoline.features.module.modules.world.AntiCactus;
import client.m1ck3y.novoline.features.module.modules.world.Breaker;
import client.m1ck3y.novoline.features.module.modules.world.BridgeAssist;
import client.m1ck3y.novoline.features.module.modules.player.ChestStealer;
import client.m1ck3y.novoline.features.module.modules.world.FastPlace;
import client.m1ck3y.novoline.features.module.modules.world.LightningTracker;
import client.m1ck3y.novoline.features.module.modules.world.SafeWalk;
import client.m1ck3y.novoline.features.module.modules.world.Scaffold;
import client.m1ck3y.novoline.features.module.modules.world.SpeedMine;
import client.m1ck3y.novoline.features.module.modules.world.XRay;
import client.m1ck3y.novoline.utils.render.CFontRenderer;
import client.m1ck3y.novoline.utils.render.FontAdapter;

public class ModuleManager {
   public static List<Module> modules = new LinkedList<>();
   public static List<Module> modulesByWidth = new CopyOnWriteArrayList<>();
   public static List<Module> modulesByWidthReversed = new CopyOnWriteArrayList<>();
   public static Map<Class<? extends Module>, Module> modulesByClass = new HashMap<>();
   public static Map<String, Module> modulesByName = new HashMap<>();
   public static final Killaura killaura = new Killaura();
   public static final Antibot antibot = new Antibot();
   public static final Criticals criticals = new Criticals();
   public static final AutoHead autoHead = new AutoHead();
   public static final TargetStrafe targetStrafe = new TargetStrafe();
   public static final AutoPotion autoPotion = new AutoPotion();
   public static final WTap wTap = new WTap();
   public static final AutoClicker autoClicker = new AutoClicker();
   public static final AimBot aimBot = new AimBot();
   public static final BowAimbot bowAimbot = new BowAimbot();
   public static final Reach reach = new Reach();
   public static final FastBow fastBow = new FastBow();
   public static final KeepSprint keepSprint = new KeepSprint();
   public static final ArrayList arrayList = new ArrayList();
   public static final ThemeColor themeColor = new ThemeColor();
   public static final Font font = new Font();
   public static final Watermark watermark = new Watermark();
   public static final Scoreboard scoreboard = new Scoreboard();
   public static final ArmorHUD armorHUD = new ArmorHUD();
   public static final Hotbar hotbar = new Hotbar();
   public static final PotionHUD potionHUD = new PotionHUD();
   public static final Information information = new Information();
   public static final ClickGui clickGui = new ClickGui();
   public static final PostProcessing postProcessing = new PostProcessing();
   public static final Notifications notifications = new Notifications();
   public static final TargetHUD targetHUD = new TargetHUD();
   public static final TargetESP targetESP = new TargetESP();
   public static final BetterMinecraft betterMinecraft = new BetterMinecraft();
   public static final Disabler disabler = new Disabler();
   public static final HypixelStats hypixelStats = new HypixelStats();
   public static final AutoHypixel autoHypixel = new AutoHypixel();
   public static final LagDetector lagDetector = new LagDetector();
   public static final Killsults killsults = new Killsults();
   public static final PingSpoof pingSpoof = new PingSpoof();
   public static final ChatBypass chatBypass = new ChatBypass();
   public static final Spammer spammer = new Spammer();
   public static final NameProtect nameProtect = new NameProtect();
   public static final AutoRegister autoRegister = new AutoRegister();
   public static final Derp derp = new Derp();
   public static final NoCloseGui noCloseGui = new NoCloseGui();
   public static final Regen regen = new Regen();
   public static final Sprint sprint = new Sprint();
   public static final Speed speed = new Speed();
   public static final Fly fly = new Fly();
   public static final LongJump longJump = new LongJump();
   public static final Phase phase = new Phase();
   public static final Step step = new Step();
   public static final Jesus jesus = new Jesus();
   public static final InventoryMove inventoryMove = new InventoryMove();
   public static final Timer timer = new Timer();
   public static final NoFall noFall = new NoFall();
   public static final Velocity velocity = new Velocity();
   public static final AntiVoid antiVoid = new AntiVoid();
   public static final NoRotate noRotate = new NoRotate();
   public static final NoSlowDown noSlowDown = new NoSlowDown();
   public static final AutoTool autoTool = new AutoTool();
   public static final InventoryManager inventoryManager = new InventoryManager();
   public static final AutoArmor autoArmor = new AutoArmor();
   public static final Blink blink = new Blink();
   public static final FreeCam freeCam = new FreeCam();
   public static final FastEat fastEat = new FastEat();
   public static final Crosshair crosshair = new Crosshair();
   public static final CameraClip cameraClip = new CameraClip();
   public static final NoRender noRender = new NoRender();
   public static final JumpCircle jumpCircle = new JumpCircle();
   public static final Trails trails = new Trails();
   public static final CustomModel customModel = new CustomModel();
   public static final MoBends moBends = new MoBends();
   public static final ChineseKungfu chineseKungfu = new ChineseKungfu();
   public static final NightVision nightVision = new NightVision();
   public static final NoHurtCam noHurtCam = new NoHurtCam();
   public static final SeeInvisibles seeInvisibles = new SeeInvisibles();
   public static final PlayerESP playerESP = new PlayerESP();
   public static final ChinaHat chinaHat = new ChinaHat();
   public static final SeeBarriers seeBarriers = new SeeBarriers();
   public static final SkinChanger skinChanger = new SkinChanger();
   public static final DamageParticles damageParticles = new DamageParticles();
   public static final WorldTime worldTime = new WorldTime();
   public static final Weather weather = new Weather();
   public static final Animations animations = new Animations();
   public static final StorageESP storageESP = new StorageESP();
   public static final Chams chams = new Chams();
   public static final ItemPhysics itemPhysics = new ItemPhysics();
   public static final Radar radar = new Radar();
   public static final NameTags nameTags = new NameTags();
   public static final FreeLook freeLook = new FreeLook();
   public static final MobESP mobESP = new MobESP();
   public static final ItemESP itemESP = new ItemESP();
   public static final WorldColor worldColor = new WorldColor();
   public static final EntityCulling entityCulling = new EntityCulling();
   public static final ChestStealer chestStealer = new ChestStealer();
   public static final Scaffold scaffold = new Scaffold();
   public static final Breaker breaker = new Breaker();
   public static final AntiCactus antiCactus = new AntiCactus();
   public static final FastPlace fastPlace = new FastPlace();
   public static final SpeedMine speedMine = new SpeedMine();
   public static final XRay xRay = new XRay();
   public static final LightningTracker lightningTracker = new LightningTracker();
   public static final SafeWalk safeWalk = new SafeWalk();
   public static final BridgeAssist bridgeAssist = new BridgeAssist();

   public void updateCachedFeatures() {
      if (arrayList == null) return;
      boolean hideStatus = arrayList.arrayList.isSelected(0);
      FontAdapter cFontRenderer = arrayList.getFont();
      modules.forEach(Module::clearWidthCache);
      modulesByWidth.sort(
         (module2, module3) -> {
            float w3 = module3.getNameWidth(cFontRenderer) + (hideStatus ? 0.0F : module3.getSuffixWidth(cFontRenderer));
            float w2 = module2.getNameWidth(cFontRenderer) + (hideStatus ? 0.0F : module2.getSuffixWidth(cFontRenderer));
            return Float.compare(w3, w2);
         }
      );
      modulesByWidthReversed = Lists.reverse(modulesByWidth);
   }

   public ModuleManager() {
      for (Field field : ModuleManager.class.getDeclaredFields()) {
         if (this.extendsModule(field.getType())) {
            try {
               this.register((Module)field.get(null));
            } catch (Exception exception) {
               exception.printStackTrace();
            }
         }
      }

      this.updateCachedFeatures();
      modules.forEach(Module::onInitialized);
   }

   public Module register(Module module4) {
      modules.add(module4);
      modulesByWidth.add(module4);
      modulesByClass.put((Class<? extends Module>)module4.getClass(), module4);
      modulesByName.put(module4.getName().toLowerCase(Locale.ROOT), module4);
      for (String alias : module4.getAliases()) {
         modulesByName.put(alias.toLowerCase(Locale.ROOT), module4);
      }
      return module4;
   }

   private boolean extendsModule(Class clazz) {
      if (clazz == null) {
         return false;
      } else if (clazz.equals(Module.class)) {
         return true;
      } else {
         Class superclass = clazz.getSuperclass();
         if (superclass == null) {
            return false;
         } else {
            return superclass.equals(Module.class) ? true : this.extendsModule(superclass);
         }
      }
   }

   public void unregister(Module module4) {
      if (modules.remove(module4)) {
         if (!(modulesByWidth.remove(module4) & modulesByClass.remove(module4.getClass(), module4) & modulesByName.remove(module4.getName().toLowerCase(Locale.ROOT), module4))
            )
          {
            throw new IllegalStateException("Corrupted feature registry");
         }
      }
   }
}
