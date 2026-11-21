package net.gobbob.mobends;

import java.io.File;
import net.gobbob.mobends.client.ClientProxy;

public class MoBends {
    public static final String MODID = "mobends";
    public static final String MODNAME = "Mo' Bends";
    public static final String VERSION = "0.21.3";
    public static CommonProxy proxy = new ClientProxy();
    public static MoBends instance = new MoBends();
    public static File configFile;
    public static int refreshModel = 0;

    public void preinit() {
        proxy.preinit();
    }

    public void init() {
        proxy.init();
    }
}
