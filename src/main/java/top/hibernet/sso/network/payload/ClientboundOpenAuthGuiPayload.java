package top.hibernet.sso.network.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import top.hibernet.sso.SSOPluginMessageHandlerMod;

/**
 * Packet sent from the server to the client instructing it to open a full-screen
 * GUI indicating that the server is currently authenticating the player.
 * <p>
 * <b>Direction:</b> Server → Client<br>
 * <b>Channel:</b> {@code hibernet_sso:open_auth_gui}<br>
 * <b>Expected follow-up:</b> {@link ClientboundAuthSuccessPayload} or
 * the player will eventually be kicked if authentication fails.
 * <p>
 * <b>Velocity plugin usage:</b>
 * <pre>{@code
 * // Send to a specific player to open the auth waiting screen
 * }</pre>
 */
public record ClientboundOpenAuthGuiPayload() implements CustomPacketPayload {
    public static final Type<ClientboundOpenAuthGuiPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    SSOPluginMessageHandlerMod.MODID, "open_auth_gui"));
    public static final StreamCodec<ByteBuf, ClientboundOpenAuthGuiPayload> STREAM_CODEC =
            StreamCodec.unit(new ClientboundOpenAuthGuiPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
