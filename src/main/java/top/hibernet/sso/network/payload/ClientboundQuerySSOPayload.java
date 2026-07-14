package top.hibernet.sso.network.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import top.hibernet.sso.SSOPluginMessageHandlerMod;

/**
 * Packet sent from the server to the client to query SSO login information.
 * <p>
 * <b>Direction:</b> Server → Client<br>
 * <b>Channel:</b> {@code hibernet_sso:query_sso}<br>
 * <b>Response expected:</b> {@link ServerboundSSOResponsePayload}
 * <p>
 * <b>Velocity plugin usage:</b>
 * <pre>{@code
 * // Send to a specific player
 * ByteArrayDataOutput out = ByteStreams.newDataOutput();
 * out.writeUTF("forward");
 * out.writeUTF(ALL_PLAYERS ? "" : targetPlayerName);
 * out.writeUTF("HiBerNET.SSO");
 * out.writeUTF("query_sso");
 * // No payload data needed for this packet
 * player.sendPluginMessage("HiBerNET.SSO", "hibernet_sso:query_sso".getBytes(StandardCharsets.UTF_8));
 * }</pre>
 */
public record ClientboundQuerySSOPayload() implements CustomPacketPayload {
    public static final Type<ClientboundQuerySSOPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    SSOPluginMessageHandlerMod.MODID, "query_sso"));
    public static final StreamCodec<ByteBuf, ClientboundQuerySSOPayload> STREAM_CODEC =
            StreamCodec.unit(new ClientboundQuerySSOPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
