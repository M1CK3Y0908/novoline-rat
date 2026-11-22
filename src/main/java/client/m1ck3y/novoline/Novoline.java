package client.m1ck3y.novoline;

import com.google.common.collect.Iterables;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import io.github.nevalackin.homoBus.bus.Bus;
import io.github.nevalackin.homoBus.bus.impl.EventBus;
import io.sentry.Sentry;
import io.sentry.protocol.User;
import java.awt.Font;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.codec.digest.DigestUtils;
import org.lwjgl.opengl.Display;
import org.tinylog.Logger;
import org.tinylog.TaggedLogger;
import ru.fals3r.aal.API;
import client.m1ck3y.novoline.client.ApiClient;
import client.m1ck3y.novoline.client.IrcConnection;
import client.m1ck3y.novoline.client.ApiRequestHandler.BearerTokenHandler;
import client.m1ck3y.novoline.features.command.CommandManager;
import client.m1ck3y.novoline.event.Event;
import client.m1ck3y.novoline.ui.AltManagerScreen;
import client.m1ck3y.novoline.ui.clickgui.ClickGuiScreen;
import client.m1ck3y.novoline.ui.menu.MenuClickGuiScreen;
import client.m1ck3y.novoline.lang.AuthException;
import client.m1ck3y.novoline.features.module.KeybindHandler;
import client.m1ck3y.novoline.features.module.ModuleManager;
import client.m1ck3y.novoline.features.module.modules.hud.HudManager;
import client.m1ck3y.novoline.platform.ViaMCP;
import client.m1ck3y.novoline.scripting.Script;
import client.m1ck3y.novoline.scripting.ScriptInfo;
import client.m1ck3y.novoline.scripting.ScriptManager;
import client.m1ck3y.novoline.setting.SettingManager;
import client.m1ck3y.novoline.util.AuthenticationResponse;
import client.m1ck3y.novoline.util.RestClientOptions;
import client.m1ck3y.novoline.utils.ClientPreferences;
import client.m1ck3y.novoline.utils.GreetingMessage;
import client.m1ck3y.novoline.utils.MacroManager;
import client.m1ck3y.novoline.utils.PresenceUpdater;
import client.m1ck3y.novoline.utils.RelationManager;
import client.m1ck3y.novoline.utils.TaskScheduler;
import client.m1ck3y.novoline.utils.account.Account;
import client.m1ck3y.novoline.utils.account.AccountManager;
import client.m1ck3y.novoline.utils.account.BanDetector;
import client.m1ck3y.novoline.utils.account.LoginHandler;
import client.m1ck3y.novoline.utils.account.WebViewLauncher;
import client.m1ck3y.novoline.utils.config.ConfigRegistry;
import client.m1ck3y.novoline.utils.cosmetic.CapeCosmetic;
import client.m1ck3y.novoline.utils.hypixel.GameTracker;
import client.m1ck3y.novoline.utils.hypixel.HypixelApiClient;
import client.m1ck3y.novoline.utils.hypixel.HypixelState;
import client.m1ck3y.novoline.utils.language.Language;
import client.m1ck3y.novoline.utils.language.Language.RuRuType;
import client.m1ck3y.novoline.utils.notification.NotificationManager;
import client.m1ck3y.novoline.utils.proxy.ProxyList;
import client.m1ck3y.novoline.utils.render.CFontRenderer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Locale;
import client.m1ck3y.novoline.utils.render.FontAdapter;
import client.m1ck3y.novoline.utils.render.VanillaFontAdapter;
import client.m1ck3y.novoline.utils.render.FramebufferManager;
import java.io.InputStream;
import client.m1ck3y.novoline.support.SneakyThrow;

public class Novoline {
   public static final String VALUE_3_0 = "3.0";
   public static final String BUILD_DATE = "111826";
   public static final String CLIENT_NAME = "Novoline.Rat";
   public static final String CLIENT_TITLE = "Novoline.Rat - Build " + BUILD_DATE;
   private Bus<? super Event> bus;
   private ClientPreferences clientPreferences;
   private TaskScheduler taskScheduler;
   public static final String BUILD_TAG = "burattino@3.0";
   private ScriptManager scriptManager;
   public static final String VALUE_030122 = "030122";
   private ModuleManager moduleManager;
   public static final String NOVOLINE = "Novoline";
   public static final String RELEASE = "Release";
   private NotificationManager notificationManager;
   private HudManager hudManager;
   private MenuClickGuiScreen menuClickGuiScreen;
   private AltManagerScreen altManagerScreen;
   public static final String SENTRY_DSN = "https://5e40be09bd864a9d8922aee0eb9d85d9@o913306.ingest.sentry.io/6067196";
   private static final Novoline novoline = new Novoline();
   private ProxyList proxyList;
   private MacroManager macroManager;
   public static final TaggedLogger taggedLogger = Logger.tag("[Novoline] ");
   private CommandManager commandManager;
   private RuRuType ruRuType;
   private SettingManager settingManager;
   public static String username = "Player";
   private ClickGuiScreen clickGuiScreen;
   private HypixelApiClient hypixelApiClient;
   private ConfigRegistry configRegistry;
   public static String userId = "User";

   public static String getUserName() {
      Minecraft mc = Minecraft.getMinecraft();
      if (mc != null) {
         if (mc.thePlayer != null) {
            String name = mc.thePlayer.getName();
            if (name != null && !name.trim().isEmpty()) {
               username = name;
               userId = getUserRole();
               return username;
            }
         }
         if (mc.getSession() != null) {
            String sessionName = mc.getSession().getUsername();
            if (sessionName != null && !sessionName.trim().isEmpty()) {
               username = sessionName;
               userId = getUserRole();
               return username;
            }
         }
      }
      if (username != null && !username.trim().isEmpty() && !"1".equals(username)) {
         return username;
      }
      return "Player";
   }

   public static String getUserRole() {
      String name = null;
      Minecraft mc = Minecraft.getMinecraft();
      if (mc != null) {
         if (mc.thePlayer != null) {
            name = mc.thePlayer.getName();
         }
         if ((name == null || name.trim().isEmpty()) && mc.getSession() != null) {
            name = mc.getSession().getUsername();
         }
      }
      if (name == null || name.trim().isEmpty()) {
         name = username;
      }
      return "M1CK3Y0908".equalsIgnoreCase(name) ? "Developer" : "User";
   }
   public static boolean initialized = false;
   private IrcConnection ircConnection;
   private RelationManager relationManager;
   private AccountManager accountManager;
   public static final FramebufferManager framebufferManager = new FramebufferManager();
   private ServerData serverData;

   public SettingManager getSettingManager() {
      return this.settingManager;
   }

   public void initEnvironment() {
      Display.setTitle(CLIENT_TITLE);
      Novoline.Directories.createDirectories();
      this.initLanguage();

      try {
         ViaMCP.getViaMCP().start();
      } catch (Exception exception) {
         Logger.error(exception, "Failed to initialize NovolinePortal");
      }

      this.clientPreferences = new ClientPreferences();
   }

   public void initialize(String text2) {
      String[] texts = text2.split("\\|<\\|>\\|");
      username = getUserName();
      userId = getUserRole();
      this.bus = new EventBus<>();
      this.taskScheduler = new TaskScheduler();
      this.settingManager = new SettingManager();
      this.commandManager = new CommandManager();
      this.macroManager = new MacroManager();
      this.moduleManager = new ModuleManager();
      this.hudManager = new HudManager();
      this.configRegistry = new ConfigRegistry();
      this.hypixelApiClient = new HypixelApiClient();
      this.accountManager = new AccountManager();
      this.relationManager = new RelationManager();
      this.proxyList = new ProxyList();
      this.scriptManager = new ScriptManager();
      this.notificationManager = new NotificationManager();
      this.loadAllScripts();
      this.restoreSession();
      this.ircConnection = this.connectIrc();
      this.menuClickGuiScreen = new MenuClickGuiScreen();
      this.clickGuiScreen = new ClickGuiScreen();
      this.altManagerScreen = new AltManagerScreen();
      this.configRegistry.getConfigManager().loadGlobal();
      this.configRegistry.getConfigManager().load(true, "proxies", ConfigRegistry.proxiesSection);
      this.ruRuType = RuRuType.EN_US;

      try {
         WebViewLauncher.extract();
      } catch (Throwable throwable2) {
         Logger.error(throwable2, "Couldn't extract WebView");
      }

      this.bus.subscribe(new HypixelState());
      this.bus.subscribe(new GameTracker());
      this.bus.subscribe(new BanDetector(this.accountManager));
      this.bus.subscribe(new GreetingMessage());
      this.bus.subscribe(new KeybindHandler());
      this.bus.subscribe(new PresenceUpdater());
      initialized = true;
   }

   public void sendChatMessage(String text2) {
      Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§8[§bNovoline§8] §7" + text2));
   }

   private void initLanguage() {
      Language.loadAll();
      this.ruRuType = RuRuType.EN_US;
   }

   public HudManager getHudManager() {
      return this.hudManager;
   }

   public void saveAll() {
      try {
         this.configRegistry.getConfigManager().saveGlobal();
      } catch (Throwable throwable2) {
         taggedLogger.error(throwable2, "Couldn't save global config");
      }

      try {
         this.configRegistry.getConfigManager().save(true, "proxies", ConfigRegistry.proxiesSection);
      } catch (Throwable throwable2) {
         taggedLogger.error(throwable2, "Couldn't save proxies");
      }

      try {
         this.accountManager.save();
      } catch (Throwable throwable2) {
         taggedLogger.error(throwable2, "Couldn't save accounts");
      }

      try {
         this.relationManager.save();
      } catch (Throwable throwable2) {
         taggedLogger.error(throwable2, "Couldn't save relations");
      }

      try {
         ModuleManager.killsults.saveMessages();
      } catch (Throwable throwable2) {
         taggedLogger.error(throwable2, "Couldn't save killsults");
      }

      try {
         ModuleManager.spammer.saveMessages();
      } catch (Throwable throwable2) {
         taggedLogger.error(throwable2, "Couldn't save spammer");
      }
   }

   public ServerData getServerData() {
      return this.serverData;
   }

   public RuRuType getRuRuType() {
      return RuRuType.EN_US;
   }

   public RelationManager getRelationManager() {
      return this.relationManager;
   }

   public HypixelApiClient getHypixelApiClient() {
      return this.hypixelApiClient;
   }

   public CommandManager getCommandManager() {
      return this.commandManager;
   }

   public void setServerData(ServerData serverData2) {
      this.serverData = serverData2;
   }

   public static Novoline getNovoline() {
      return novoline;
   }

   public ConfigRegistry getConfigRegistry() {
      return this.configRegistry;
   }

   private void loadAllScripts() {
      try {
         Files.list(Novoline.Directories.SCRIPTS_DIR).filter(path -> Files.isRegularFile(path)).forEach(path -> {
            try {
               ScriptInfo metadata = ((Script)CompletableFuture.<Script>supplyAsync(() -> {
                  try {
                     Script script2 = this.scriptManager.getScript(path);
                     this.scriptManager.loadScript(this.scriptManager.getScript(path));
                     return script2;
                  } catch (Throwable throwable2) {
                     throw new RuntimeException(throwable2);
                  }
               }).get(15L, TimeUnit.SECONDS)).getMetadata();
               Logger.info("Loaded script {} v{} by {}", metadata.getName(), metadata.getVersion(), metadata.getAuthor());
            } catch (Throwable throwable3) {
               Logger.error(throwable3, "Couldn't load script {}", path.getFileName().toString());
            }
         });
      } catch (IOException ioException) {
         Logger.error(ioException, "Couldn't list script files");
      }

      Logger.info("Powered by NovoScript v2.0.0");
   }

   public ClientPreferences getClientPreferences() {
      return this.clientPreferences;
   }

   public MenuClickGuiScreen getMenuClickGuiScreen() {
      return this.menuClickGuiScreen;
   }

   public ProxyList getProxyList() {
      return this.proxyList;
   }

   public AltManagerScreen getAltManagerScreen() {
      return this.altManagerScreen;
   }

   public void setClientPreferences(ClientPreferences clientPreferences2) {
      this.clientPreferences = clientPreferences2;
   }

   public MacroManager getMacroManager() {
      return this.macroManager;
   }

   public Bus<? super Event> getBus() {
      return this.bus;
   }

   public ModuleManager getModuleManager() {
      return this.moduleManager;
   }

   public TaskScheduler getTaskScheduler() {
      return this.taskScheduler;
   }

   public ScriptManager getScriptManager() {
      return this.scriptManager;
   }

   public void setRuRuType(RuRuType ruRuType2) {
      this.ruRuType = RuRuType.EN_US;
   }

   private IrcConnection connectIrc() {
      RestClientOptions restClientOptions = new RestClientOptions().baseUri("https://nos.novoline.rat/api/v1");
      ApiClient apiClient = new ApiClient(restClientOptions);

      AuthenticationResponse authenticationResponse;
      try {
         authenticationResponse = (AuthenticationResponse)apiClient.authenticate(System.getProperty("novo.delsyToken")).get();
      } catch (Exception exception) {
         Logger.error(exception, "Unable to authenticate for NOS");
         new RuntimeException(exception);
         return null;
      }

      String jwtToken = authenticationResponse.getJwtToken();
      apiClient.setApiRequestHandler(new BearerTokenHandler(jwtToken));
      return new IrcConnection(apiClient);
   }

   public NotificationManager getNotificationManager() {
      return this.notificationManager;
   }

   public void reportException(Throwable throwable2) {
      User user2 = new User();
      user2.setId(userId);
      user2.setUsername(username);
      Sentry.setTag("cpu", OpenGlHelper.getCpu());
      Sentry.setTag("client.version", "3.0");
      Sentry.setTag("client.build", BUILD_DATE);
      Sentry.setUser(user2);
      Sentry.captureException(throwable2);
   }

   public ClickGuiScreen getClickGuiScreen() {
      return this.clickGuiScreen;
   }

   private void restoreSession() {
      Account account2 = this.accountManager.getAccount();
      if (account2 != null && account2.getAccountId() != null) {
         try {
            LoginHandler.onLoggedIn(account2.login());

            for (int i = 0; i < this.accountManager.size(); i++) {
               Account account3 = this.accountManager.get(i);
               if (account3.getAccountId() != null && account2.getAccountId().equals(account3.getAccountId())) {
                  this.accountManager.setIntValue(i);
                  break;
               }
            }
         } catch (AuthException authException) {
            LoginHandler.reportLoginFailure(authException);
         }
      }
   }

   public AccountManager getAccountManager() {
      return this.accountManager;
   }

   public IrcConnection getIrcConnection() {
      return this.ircConnection;
   }

   public static class Directories {
      public static final Path ROOT_DIR = Paths.get(System.getProperty("user.dir"), "Novoline");
      public static final Path STORAGE_DIR = ROOT_DIR.resolve("storage");
      public static final Path CONFIGS_DIR = ROOT_DIR.resolve("configs");
      public static final Path SCRIPTS_DIR = ROOT_DIR.resolve("scripts");

      public static void createDirectories() {
      try {
         for (Field field : Novoline.Directories.class.getFields()) {
            if (field.getType() == Path.class) {
               Files.createDirectories((Path)field.get(null));
            }
         }
      } catch (Throwable sneakyThrowable) {
         throw SneakyThrow.sneak(sneakyThrowable);
      }
   }
   }

   public static class Fonts {
      public static final ResourceLocation MUSEO_SANS_900 = new ResourceLocation("novoline", "fonts/museosanscyrl900.ttf");
      public static final ResourceLocation MUSEO_SANS_700 = new ResourceLocation("novoline", "fonts/museosanscyrl700.ttf");
      public static final ResourceLocation MUSEO_SANS_500 = new ResourceLocation("novoline", "fonts/museosanscyrl500.ttf");
      public static CFontRenderer montserratRegularSize18;
      public static CFontRenderer rubikRegularSize18;
      public static CFontRenderer museoSans500Size14;
      public static CFontRenderer pixelSize8;
      public static final ResourceLocation MONTSERRAT_REGULAR = new ResourceLocation("novoline", "fonts/montserrat-regular.ttf");
      public static final ResourceLocation MONTSERRAT_SEMIBOLD = new ResourceLocation("novoline", "fonts/montserrat-semibold.ttf");
      public static final String[] FONT_NAMES = new String[]{"SF", "Rubik", "Montserrat", "Museo Sans", "Roboto", "Pixel", "Tahoma", "Minecraft"};
      private static final Map<String, CFontRenderer> DYNAMIC_FONTS = new ConcurrentHashMap<>();

      public static FontAdapter getFont(String fontName, float size, boolean bold) {
         if (fontName == null || fontName.equalsIgnoreCase("Minecraft") || fontName.equalsIgnoreCase("Vanilla")) {
            return VanillaFontAdapter.INSTANCE;
         }

         int intSize = Math.round(size);
         String key = fontName.toLowerCase(Locale.ROOT) + ":" + intSize + ":" + bold;
         CFontRenderer cached = DYNAMIC_FONTS.get(key);
         if (cached != null) {
            return cached;
         }

         ResourceLocation res;
         String lower = fontName.toLowerCase(Locale.ROOT);
         if (lower.contains("sf") || lower.contains("san francisco")) {
            res = bold ? SF_BOLD : SF_REGULAR;
         } else if (lower.contains("tahoma") || lower.contains("fixedsys")) {
            res = bold ? TAHOMA_BOLD : TAHOMA;
         } else if (lower.contains("montserrat")) {
            res = bold ? MONTSERRAT_SEMIBOLD : MONTSERRAT_REGULAR;
         } else if (lower.contains("museo")) {
            res = bold ? MUSEO_SANS_700 : MUSEO_SANS_500;
         } else if (lower.contains("roboto")) {
            res = ROBOTO_REGULAR;
         } else if (lower.contains("pixel")) {
            res = PIXEL;
         } else {
            res = bold ? RUBIK_SEMIBOLD : RUBIK_REGULAR;
         }

         try {
            CFontRenderer created = createCFontRenderer(res, (float) intSize);
            DYNAMIC_FONTS.put(key, created);
            return created;
         } catch (Throwable t) {
            return sfRegularSize18 != null ? sfRegularSize18 : (rubikRegularSize16 != null ? rubikRegularSize16 : VanillaFontAdapter.INSTANCE);
         }
      }

      public static FontAdapter getFont(String fontName, float size) {
         return getFont(fontName, size, false);
      }
      public static CFontRenderer museoSans900Size32;
      public static final ResourceLocation RUBIK_SEMIBOLD = new ResourceLocation("novoline", "fonts/rubik-semibold.ttf");
      public static CFontRenderer rubikSemiboldSize18;
      public static final ResourceLocation RUBIK_REGULAR = new ResourceLocation("novoline", "fonts/rubik-regular.ttf");
      public static CFontRenderer rubikSemiboldSize24;
      public static final ResourceLocation ROBOTO_REGULAR = new ResourceLocation("novoline", "fonts/roboto-regular.ttf");
      public static CFontRenderer museoSans900Size36;
      public static final ResourceLocation PIXEL = new ResourceLocation("novoline", "fonts/pixel.ttf");
      public static final ResourceLocation SF_REGULAR = new ResourceLocation("novoline", "fonts/sf.ttf");
      public static final ResourceLocation SF_BOLD = new ResourceLocation("novoline", "fonts/sf-bold.ttf");
      public static final ResourceLocation SF_THIN = new ResourceLocation("novoline", "fonts/sf-thin.ttf");
      public static final ResourceLocation TAHOMA = new ResourceLocation("novoline", "fonts/tahoma.ttf");
      public static final ResourceLocation TAHOMA_BOLD = new ResourceLocation("novoline", "fonts/tahoma-bold.ttf");
      public static CFontRenderer sfRegularSize18;
      public static CFontRenderer sfRegularSize20;
      public static CFontRenderer sfRegularSize21;
      public static CFontRenderer sfBoldSize20;
      public static CFontRenderer tahomaSize16;
      public static CFontRenderer tahomaBoldSize16;
      public static CFontRenderer rubikRegularSize14;
      public static CFontRenderer museoSans500Size11;
      public static CFontRenderer rubikSemiboldSize16;
      public static CFontRenderer museoSans900Size12;
      public static CFontRenderer museoSans900Size16;
      public static CFontRenderer museoSans700Size14;
      public static CFontRenderer museoSans500Size14Tab;
      public static CFontRenderer rubikRegularSize16;
      private static final Map<ResourceLocation, Font> fontCache = new WeakHashMap<>();
      public static CFontRenderer robotoRegularSize14;

      private static Font loadTTF(ResourceLocation resourceLocation3) {
         return fontCache.computeIfAbsent(resourceLocation3, resourceLocation2 -> {
            try {
               IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation2);
               return Font.createFont(0, resource.getInputStream());
            } catch (Exception exception) {
               throw new RuntimeException("Couldn't create font @ " + resourceLocation3, exception);
            }
         });
      }

      public static void loadFonts() {
         museoSans900Size36 = createCFontRenderer(MUSEO_SANS_900, 36.0F);
         museoSans900Size32 = createCFontRenderer(MUSEO_SANS_900, 32.0F);
         museoSans900Size16 = createCFontRenderer(MUSEO_SANS_900, 16.0F);
         museoSans900Size12 = createCFontRenderer(MUSEO_SANS_900, 12.0F);
         museoSans700Size14 = createCFontRenderer(MUSEO_SANS_700, 14.0F);
         museoSans500Size14Tab = createCFontRenderer(MUSEO_SANS_500, 14.0F);
         museoSans500Size14 = createCFontRenderer(MUSEO_SANS_500, 14.0F);
         museoSans500Size11 = createCFontRenderer(MUSEO_SANS_500, 11.0F);
         montserratRegularSize18 = createCFontRenderer(MONTSERRAT_REGULAR, 18.0F);
         rubikSemiboldSize18 = createCFontRenderer(RUBIK_SEMIBOLD, 18.0F);
         rubikSemiboldSize24 = createCFontRenderer(RUBIK_SEMIBOLD, 24.0F);
         rubikSemiboldSize16 = createCFontRenderer(RUBIK_SEMIBOLD, 16.0F);
         rubikRegularSize18 = createCFontRenderer(RUBIK_REGULAR, 18.0F);
         rubikRegularSize16 = createCFontRenderer(RUBIK_REGULAR, 16.0F);
         rubikRegularSize14 = createCFontRenderer(RUBIK_REGULAR, 14.0F);
         robotoRegularSize14 = createCFontRenderer(ROBOTO_REGULAR, 14.0F);
         pixelSize8 = createCFontRenderer(PIXEL, 8.0F);
         sfRegularSize18 = createCFontRenderer(SF_REGULAR, 18.0F);
         sfRegularSize20 = createCFontRenderer(SF_REGULAR, 20.0F);
         sfRegularSize21 = createCFontRenderer(SF_REGULAR, 21.0F);
         sfBoldSize20 = createCFontRenderer(SF_BOLD, 20.0F);
         tahomaSize16 = createCFontRenderer(TAHOMA, 16.0F);
         tahomaBoldSize16 = createCFontRenderer(TAHOMA_BOLD, 16.0F);
      }

      public static CFontRenderer createCFontRenderer(ResourceLocation resourceLocation3, float f) {
         Font font2 = loadTTF(resourceLocation3).deriveFont(f);
         return new CFontRenderer(font2);
      }
   }

}

