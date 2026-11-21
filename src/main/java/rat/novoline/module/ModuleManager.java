package rat.novoline.module;

import com.google.common.collect.Lists;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import rat.novoline.Novoline.Fonts;
import rat.novoline.module.combat.AimBot;
import rat.novoline.module.combat.Antibot;
import rat.novoline.module.combat.AutoClicker;
import rat.novoline.module.combat.AutoHead;
import rat.novoline.module.combat.AutoPotion;
import rat.novoline.module.combat.BowAimbot;
import rat.novoline.module.combat.Criticals;
import rat.novoline.module.combat.FastBow;
import rat.novoline.module.combat.KeepSprint;
import rat.novoline.module.combat.Killaura;
import rat.novoline.module.combat.Reach;
import rat.novoline.module.combat.TargetStrafe;
import rat.novoline.module.combat.WTap;
import rat.novoline.module.hud.ArrayList;
import rat.novoline.module.hud.ClickGui;
import rat.novoline.module.hud.HUD;
import rat.novoline.module.hud.Notifications;
import rat.novoline.module.hud.PostProcessing;
import rat.novoline.module.hud.TargetESP;
import rat.novoline.module.hud.TargetHUD;
import rat.novoline.module.hud.ThemeColor;
import rat.novoline.module.hud.Watermark;
import rat.novoline.module.misc.AutoHypixel;
import rat.novoline.module.misc.AutoRegister;
import rat.novoline.module.misc.BetterMinecraft;
import rat.novoline.module.misc.ChatBypass;
import rat.novoline.module.misc.Derp;
import rat.novoline.module.misc.Disabler;
import rat.novoline.module.misc.DiscordRPC;
import rat.novoline.module.misc.HypixelStats;
import rat.novoline.module.misc.Killsults;
import rat.novoline.module.misc.LagDetector;
import rat.novoline.module.misc.NameProtect;
import rat.novoline.module.misc.NoCloseGui;
import rat.novoline.module.misc.PingSpoof;
import rat.novoline.module.misc.Spammer;
import rat.novoline.module.movement.Fly;
import rat.novoline.module.movement.Jesus;
import rat.novoline.module.movement.LongJump;
import rat.novoline.module.movement.Phase;
import rat.novoline.module.movement.Speed;
import rat.novoline.module.movement.Sprint;
import rat.novoline.module.movement.Step;
import rat.novoline.module.player.AntiVoid;
import rat.novoline.module.player.AutoArmor;
import rat.novoline.module.player.AutoTool;
import rat.novoline.module.player.Blink;
import rat.novoline.module.player.FastEat;
import rat.novoline.module.player.FreeCam;
import rat.novoline.module.player.InventoryManager;
import rat.novoline.module.player.InventoryMove;
import rat.novoline.module.player.NoFall;
import rat.novoline.module.player.NoRotate;
import rat.novoline.module.player.NoSlowDown;
import rat.novoline.module.player.Timer;
import rat.novoline.module.player.Velocity;
import rat.novoline.module.visual.Animations;
import rat.novoline.module.visual.CameraClip;
import rat.novoline.module.visual.Chams;
import rat.novoline.module.visual.ChinaHat;
import rat.novoline.module.visual.Crosshair;
import rat.novoline.module.visual.CustomModel;
import rat.novoline.module.visual.DamageParticles;
import rat.novoline.module.visual.EntityCulling;
import rat.novoline.module.visual.FreeLook;
import rat.novoline.module.visual.ItemESP;
import rat.novoline.module.visual.ItemPhysics;
import rat.novoline.module.visual.JumpCircle;
import rat.novoline.module.visual.MobESP;
import rat.novoline.module.visual.MoBends;
import rat.novoline.module.visual.NameTags;
import rat.novoline.module.visual.NightVision;
import rat.novoline.module.visual.NoHurtCam;
import rat.novoline.module.visual.NoRender;
import rat.novoline.module.visual.PlayerESP;
import rat.novoline.module.visual.Radar;
import rat.novoline.module.visual.SeeBarriers;
import rat.novoline.module.visual.SeeInvisibles;
import rat.novoline.module.visual.SkinChanger;
import rat.novoline.module.visual.StorageESP;
import rat.novoline.module.visual.Trails;
import rat.novoline.module.visual.Weather;
import rat.novoline.module.visual.WorldColor;
import rat.novoline.module.visual.WorldTime;
import rat.novoline.module.world.AntiCactus;
import rat.novoline.module.world.Breaker;
import rat.novoline.module.world.BridgeAssist;
import rat.novoline.module.player.ChestStealer;
import rat.novoline.module.world.FastPlace;
import rat.novoline.module.world.LightningTracker;
import rat.novoline.module.world.SafeWalk;
import rat.novoline.module.world.Scaffold;
import rat.novoline.module.world.SpeedMine;
import rat.novoline.module.world.XRay;
import rat.novoline.utils.render.CFontRenderer;
import rat.novoline.utils.render.FontAdapter;

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
   public static final Watermark watermark = new Watermark();
   public static final ClickGui clickGui = new ClickGui();
   public static final PostProcessing postProcessing = new PostProcessing();
   public static final Notifications notifications = new Notifications();
   public static final TargetHUD targetHUD = new TargetHUD();
   public static final TargetESP targetESP = new TargetESP();
   public static final HUD hUD = new HUD();
   public static final BetterMinecraft betterMinecraft = new BetterMinecraft();
   public static final Disabler disabler = new Disabler();
   public static final HypixelStats hypixelStats = new HypixelStats();
   public static final AutoHypixel autoHypixel = new AutoHypixel();
   public static final LagDetector lagDetector = new LagDetector();
   public static final DiscordRPC discordRPC = new DiscordRPC();
   public static final Killsults killsults = new Killsults();
   public static final PingSpoof pingSpoof = new PingSpoof();
   public static final ChatBypass chatBypass = new ChatBypass();
   public static final Spammer spammer = new Spammer();
   public static final NameProtect nameProtect = new NameProtect();
   public static final AutoRegister autoRegister = new AutoRegister();
   public static final Derp derp = new Derp();
   public static final NoCloseGui noCloseGui = new NoCloseGui();
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
