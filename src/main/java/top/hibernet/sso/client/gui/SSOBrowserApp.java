package top.hibernet.sso.client.gui;

import top.hibernet.sso.SSOPluginMessageHandlerMod;
import net.minecraft.client.Minecraft;

/**
 * Browser launcher — uses MCEF embedded Chromium browser if available,
 * otherwise returns false so the caller falls back to system browser.
 * <p>
 * When MCEF is detected, shows a full-screen {@link MCEFScreen} that
 * embeds the browser. MCEF must be installed as a separate mod.
 */
public class SSOBrowserApp {

    private static Boolean mcefAvailable = null;
    private static MCEFScreen currentScreen = null;

    /**
     * Opens the URL in MCEF if available.
     *
     * @return true if MCEF was used, false if caller should fall back
     */
    public static synchronized boolean launchBrowser(String url, boolean fullscreen, boolean allowExit) {
        if (mcefAvailable == null) {
            mcefAvailable = checkMCEF();
        }
        if (!mcefAvailable) return false;

        try {
            // MCEF is already initialized by its own mod — just create screen
            Minecraft.getInstance().execute(() -> {
                currentScreen = new MCEFScreen(url, fullscreen, allowExit);
                Minecraft.getInstance().setScreen(currentScreen);
            });
            SSOPluginMessageHandlerMod.LOGGER.info("Opened MCEF browser: {}", url);
            return true;
        } catch (Exception e) {
            SSOPluginMessageHandlerMod.LOGGER.warn("MCEF screen failed: {}", e.getMessage());
            mcefAvailable = false;
            return false;
        }
    }

    public static synchronized void closeBrowser() {
        if (currentScreen != null) {
            currentScreen.onClose();
            currentScreen = null;
        }
    }

    private static boolean checkMCEF() {
        try {
            Class.forName("com.cinemamod.mcef.MCEF");
            // Check MCEF is actually initialized (the mod has loaded)
            Class<?> mcefClass = Class.forName("com.cinemamod.mcef.MCEF");
            boolean inited = (boolean) mcefClass.getMethod("isInitialized").invoke(null);
            if (inited) {
                SSOPluginMessageHandlerMod.LOGGER.info("MCEF ready, embedded browser available");
                return true;
            } else {
                SSOPluginMessageHandlerMod.LOGGER.warn("MCEF found but not initialized");
                return false;
            }
        } catch (Exception e) {
            SSOPluginMessageHandlerMod.LOGGER.info("MCEF not installed, will use system browser");
            return false;
        }
    }
}
