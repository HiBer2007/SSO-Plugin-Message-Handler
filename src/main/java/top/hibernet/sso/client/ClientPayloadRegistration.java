package top.hibernet.sso.client;

import top.hibernet.sso.SSOPluginMessageHandlerMod;
import top.hibernet.sso.network.payload.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/**
 * Registers all client-bound (server → client) payload handlers.
 * This class is only loaded on the physical client (Dist.CLIENT).
 */
@EventBusSubscriber(
        modid = SSOPluginMessageHandlerMod.MODID,
        bus = EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public class ClientPayloadRegistration {

    private ClientPayloadRegistration() {}

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1").optional();

        // Server → Client: Query SSO login information
        registrar.playToClient(
                ClientboundQuerySSOPayload.TYPE,
                ClientboundQuerySSOPayload.STREAM_CODEC,
                ClientPayloadHandler::handleQuerySSO
        );

        // Server → Client: Open authentication waiting GUI
        registrar.playToClient(
                ClientboundOpenAuthGuiPayload.TYPE,
                ClientboundOpenAuthGuiPayload.STREAM_CODEC,
                ClientPayloadHandler::handleOpenAuthGui
        );

        // Server → Client: Authentication successful, show success GUI
        registrar.playToClient(
                ClientboundAuthSuccessPayload.TYPE,
                ClientboundAuthSuccessPayload.STREAM_CODEC,
                ClientPayloadHandler::handleAuthSuccess
        );

        // Server → Client: Open embedded browser with given URL
        registrar.playToClient(
                ClientboundOpenBrowserPayload.TYPE,
                ClientboundOpenBrowserPayload.STREAM_CODEC,
                ClientPayloadHandler::handleOpenBrowser
        );

        // Server → Client: Close embedded browser
        registrar.playToClient(
                ClientboundCloseBrowserPayload.TYPE,
                ClientboundCloseBrowserPayload.STREAM_CODEC,
                ClientPayloadHandler::handleCloseBrowser
        );

        SSOPluginMessageHandlerMod.LOGGER.info(
                "HiBerNET SSO client-bound payload handlers registered");
    }
}
