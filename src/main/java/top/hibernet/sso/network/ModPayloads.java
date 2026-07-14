package top.hibernet.sso.network;

import top.hibernet.sso.SSOPluginMessageHandlerMod;
import top.hibernet.sso.network.payload.ServerboundGuiClosedPayload;
import top.hibernet.sso.network.payload.ServerboundSSOResponsePayload;
import top.hibernet.sso.server.ServerPayloadHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/**
 * Registers all server-bound (client → server) payload handlers.
 * Registration happens on both client and server, but the actual
 * handler methods only run when a packet arrives on the logical server.
 */
@EventBusSubscriber(
        modid = SSOPluginMessageHandlerMod.MODID,
        bus = EventBusSubscriber.Bus.MOD
)
public class ModPayloads {

    private ModPayloads() {}

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1").optional();

        // Client → Server: SSO login response
        registrar.playToServer(
                ServerboundSSOResponsePayload.TYPE,
                ServerboundSSOResponsePayload.STREAM_CODEC,
                ServerPayloadHandler::handleSSOResponse
        );

        // Client → Server: GUI closed notification
        registrar.playToServer(
                ServerboundGuiClosedPayload.TYPE,
                ServerboundGuiClosedPayload.STREAM_CODEC,
                ServerPayloadHandler::handleGuiClosed
        );

        SSOPluginMessageHandlerMod.LOGGER.info(
                "HiBerNET SSO server-bound payload handlers registered");
    }
}
