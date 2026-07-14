package top.hibernet.sso.network.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import top.hibernet.sso.SSOPluginMessageHandlerMod;

/**
 * Packet sent from the server to the client after successful authentication.
 * The client will display a success GUI with a 3-second countdown that then
 * auto-closes, returning the player to the game.
 * <p>
 * <b>Direction:</b> Server → Client<br>
 * <b>Channel:</b> {@code hibernet_sso:auth_success}<br>
 * <b>Prerequisite:</b> {@link ClientboundOpenAuthGuiPayload} (optional, but recommended)
 * <p>
 * <b>Velocity plugin usage:</b>
 * <pre>{@code
 * // Send to a player after successfully verifying their SSO response
 * }
 * }</pre>
 */
public record ClientboundAuthSuccessPayload() implements CustomPacketPayload {
    public static final Type<ClientboundAuthSuccessPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    SSOPluginMessageHandlerMod.MODID, "auth_success"));
    public static final StreamCodec<ByteBuf, ClientboundAuthSuccessPayload> STREAM_CODEC =
            StreamCodec.unit(new ClientboundAuthSuccessPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
