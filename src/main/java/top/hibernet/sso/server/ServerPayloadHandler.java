package top.hibernet.sso.server;

import top.hibernet.sso.SSOPluginMessageHandlerMod;
import top.hibernet.sso.network.payload.ServerboundGuiClosedPayload;
import top.hibernet.sso.network.payload.ServerboundSSOResponsePayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Handles all client-to-server (server-bound) payloads on the server side.
 */
public class ServerPayloadHandler {

    private ServerPayloadHandler() {}

    /**
     * Handles {@link ServerboundSSOResponsePayload} — receives the SSO
     * login data sent by the client in response to a query.
     * <p>
     * <b>Velocity plugin integration:</b> On a Velocity proxy, this
     * payload is forwarded through the plugin message channel
     * {@code HiBerNET.SSO}. The Velocity plugin must intercept the
     * response and process the SSO tokens accordingly.
     */
    public static void handleSSOResponse(ServerboundSSOResponsePayload payload, IPayloadContext context) {
        var player = context.player();
        SSOPluginMessageHandlerMod.LOGGER.info(
                "Received SSO response from player {}: username={}, uuid={}, expiresAt={}",
                player.getName().getString(),
                payload.username(),
                payload.uuid(),
                payload.expiresAt());

        // Log token presence (not the actual values for security)
        if (!payload.accessToken().isEmpty()) {
            SSOPluginMessageHandlerMod.LOGGER.info(
                    "Player {} provided accessToken (length={})",
                    player.getName().getString(),
                    payload.accessToken().length());
        }
        if (!payload.idToken().isEmpty()) {
            SSOPluginMessageHandlerMod.LOGGER.info(
                    "Player {} provided idToken (length={})",
                    player.getName().getString(),
                    payload.idToken().length());
        }
    }

    /**
     * Handles {@link ServerboundGuiClosedPayload} — receives notification
     * that a GUI was closed on the client side.
     */
    public static void handleGuiClosed(ServerboundGuiClosedPayload payload, IPayloadContext context) {
        var player = context.player();
        SSOPluginMessageHandlerMod.LOGGER.info(
                "GUI closed by player {}: reason={}",
                player.getName().getString(),
                payload.reason());
    }
}
