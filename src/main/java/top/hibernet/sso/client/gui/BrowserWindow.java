package top.hibernet.sso.client.gui;

import top.hibernet.sso.SSOPluginMessageHandlerMod;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;

import java.net.URI;

/**
 * Manages the browser window with optional JavaFX WebView integration.
 * <p>
 * Since JavaFX classes cannot be imported at compile time in the NeoForge
 * MDG environment, all JavaFX interaction is done via reflection.
 * <p>
 * Flow:
 * <ol>
 *   <li>{@link #openOrUpdate} shows a {@link BrowserConfirmScreen}</li>
 *   <li>User confirms → {@link #launchBrowser} tries JavaFX (via
 *       {@link SSOBrowserApp}), falls back to system browser</li>
 * </ol>
 */
public class BrowserWindow {

    /**
     * Shows a confirmation screen before opening the URL.
     */
    public static synchronized void openOrUpdate(String url, boolean fullscreen, boolean allowExit) {
        Minecraft.getInstance().setScreen(new BrowserConfirmScreen(url, fullscreen, allowExit));
    }

    /**
     * Closes the browser overlay and JavaFX stage.
     */
    public static synchronized void close() {
        SSOBrowserApp.closeBrowser();
        var mc = Minecraft.getInstance();
        if (mc.screen instanceof BrowserScreen || mc.screen instanceof BrowserConfirmScreen) {
            mc.setScreen(null);
        }
    }

    /**
     * Tries MCEF embedded browser first; falls back to system browser.
     */
    public static void launchBrowser(String url, boolean fullscreen, boolean allowExit) {
        if (SSOBrowserApp.launchBrowser(url, fullscreen, allowExit)) {
            // MCEF handled it — MCEFScreen is shown
            return;
        }
        // MCEF unavailable → open system browser directly + show overlay
        openSystemBrowser(url);
        Minecraft.getInstance().setScreen(new BrowserScreen(url, fullscreen, allowExit));
    }

    /**
     * Opens the URL in the system default browser.
     */
    public static void openSystemBrowser(String url) {
        try {
            Util.getPlatform().openUri(URI.create(url));
            SSOPluginMessageHandlerMod.LOGGER.info("Opened system browser: {}", url);
        } catch (Exception e) {
            SSOPluginMessageHandlerMod.LOGGER.error("Failed to open system browser: {}", url, e);
        }
    }
}
