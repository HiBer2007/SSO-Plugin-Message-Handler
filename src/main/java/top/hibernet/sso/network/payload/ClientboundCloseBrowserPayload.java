package top.hibernet.sso.network.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import top.hibernet.sso.SSOPluginMessageHandlerMod;

/**
 * Packet sent from the server to the client instructing it to close the
 * currently open embedded browser (JavaFX WebView).
 * <p>
 * <b>Direction:</b> Server → Client<br>
 * <b>Channel:</b> {@code hibernet_sso:close_browser}<br>
 * <b>Prerequisite:</b> {@link ClientboundOpenBrowserPayload}
 * <p>
 * <b>Velocity plugin usage:</b>
 * <pre>{@code
 * // Close the browser for a player after OAuth callback is received
 * }</pre>
 */
public record ClientboundCloseBrowserPayload() implements CustomPacketPayload {
    public static final Type<ClientboundCloseBrowserPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    SSOPluginMessageHandlerMod.MODID, "close_browser"));
    public static final StreamCodec<ByteBuf, ClientboundCloseBrowserPayload> STREAM_CODEC =
            StreamCodec.unit(new ClientboundCloseBrowserPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
