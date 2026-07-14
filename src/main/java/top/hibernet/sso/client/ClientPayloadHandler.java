package top.hibernet.sso.client;

import top.hibernet.sso.SSOPluginMessageHandlerMod;
import top.hibernet.sso.client.gui.AuthSuccessScreen;
import top.hibernet.sso.client.gui.AuthWaitScreen;
import top.hibernet.sso.client.gui.BrowserWindow;
import top.hibernet.sso.network.payload.*;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Handles all server-to-client (client-bound) payloads on the client side.
 */
public class ClientPayloadHandler {

    private ClientPayloadHandler() {}

    /**
     * Handles {@link ClientboundQuerySSOPayload} — reads SSO data from the
     * local JVM properties / environment variables and sends the response
     * back to the server.
     */
    public static void handleQuerySSO(ClientboundQuerySSOPayload payload, IPayloadContext context) {
        SSOPluginMessageHandlerMod.LOGGER.info("Received query_sso from server, reading SSO data");
        var ssoData = SSODataReader.readSSOData();
        PacketDistributor.sendToServer(new ServerboundSSOResponsePayload(
                ssoData.username(),
                ssoData.uuid(),
                ssoData.accessToken(),
                ssoData.idToken(),
                ssoData.expiresAt()
        ));
        SSOPluginMessageHandlerMod.LOGGER.info("Sent sso_response to server");
    }

    /**
     * Handles {@link ClientboundOpenAuthGuiPayload} — opens a full-screen
     * Minecraft GUI indicating the server is authenticating the player.
     */
    public static void handleOpenAuthGui(ClientboundOpenAuthGuiPayload payload, IPayloadContext context) {
        SSOPluginMessageHandlerMod.LOGGER.info("Received open_auth_gui from server, opening auth waiting GUI");
        Minecraft.getInstance().execute(() -> {
            Minecraft.getInstance().setScreen(new AuthWaitScreen());
        });
    }

    /**
     * Handles {@link ClientboundAuthSuccessPayload} — shows a success GUI
     * with a 3-second countdown that auto-closes and returns to the game.
     */
    public static void handleAuthSuccess(ClientboundAuthSuccessPayload payload, IPayloadContext context) {
        SSOPluginMessageHandlerMod.LOGGER.info("Received auth_success from server, showing success screen");
        Minecraft.getInstance().execute(() -> {
            Minecraft.getInstance().setScreen(new AuthSuccessScreen());
        });
    }

    /**
     * Handles {@link ClientboundOpenBrowserPayload} — opens (or updates) the
     * embedded JavaFX WebView browser with the given URL and display parameters.
     */
    public static void handleOpenBrowser(ClientboundOpenBrowserPayload payload, IPayloadContext context) {
        SSOPluginMessageHandlerMod.LOGGER.info("Received open_browser: url={}, fullscreen={}, allowExit={}",
                payload.url(), payload.fullscreen(), payload.allowExit());
        Minecraft.getInstance().execute(() -> {
            BrowserWindow.openOrUpdate(payload.url(), payload.fullscreen(), payload.allowExit());
        });
    }

    /**
     * Handles {@link ClientboundCloseBrowserPayload} — closes the embedded
     * JavaFX WebView browser if it is currently open.
     */
    public static void handleCloseBrowser(ClientboundCloseBrowserPayload payload, IPayloadContext context) {
        SSOPluginMessageHandlerMod.LOGGER.info("Received close_browser from server");
        Minecraft.getInstance().execute(() -> {
            BrowserWindow.close();
        });
    }
}
