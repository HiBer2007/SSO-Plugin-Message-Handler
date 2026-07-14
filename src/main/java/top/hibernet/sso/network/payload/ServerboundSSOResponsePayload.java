package top.hibernet.sso.network.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import top.hibernet.sso.SSOPluginMessageHandlerMod;

/**
 * Packet sent from the client to the server in response to
 * {@link ClientboundQuerySSOPayload}, containing the player's SSO credentials.
 * <p>
 * <b>Direction:</b> Client → Server<br>
 * <b>Channel:</b> {@code hibernet_sso:sso_response}<br>
 * <b>Triggered by:</b> {@link ClientboundQuerySSOPayload}
 * <p>
 * <b>Velocity plugin usage:</b>
 * <pre>{@code
 * // Listen for this response on the "HiBerNET.SSO" channel
 * // The payload is sent as a custom payload on the Minecraft channel
 * // "hibernet_sso:sso_response" with the following fields:
 * // - username (UTF-8 String)
 * // - uuid (UTF-8 String)
 * // - accessToken (UTF-8 String)
 * // - idToken (UTF-8 String)
 * // - expiresAt (VAR_LONG)
 * }</pre>
 *
 * @param username    the SSO username (e.g. "hiber2007")
 * @param uuid        the player's UUID (e.g. "b3492950-2453-40c8-9efb-c63094d271de")
 * @param accessToken the SSO access token
 * @param idToken     the OpenID Connect id_token (JWT)
 * @param expiresAt   Unix epoch milliseconds when the tokens expire
 */
public record ServerboundSSOResponsePayload(
        String username,
        String uuid,
        String accessToken,
        String idToken,
        long expiresAt
) implements CustomPacketPayload {
    public static final Type<ServerboundSSOResponsePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    SSOPluginMessageHandlerMod.MODID, "sso_response"));

    public static final StreamCodec<ByteBuf, ServerboundSSOResponsePayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, ServerboundSSOResponsePayload::username,
                    ByteBufCodecs.STRING_UTF8, ServerboundSSOResponsePayload::uuid,
                    ByteBufCodecs.STRING_UTF8, ServerboundSSOResponsePayload::accessToken,
                    ByteBufCodecs.STRING_UTF8, ServerboundSSOResponsePayload::idToken,
                    ByteBufCodecs.VAR_LONG, ServerboundSSOResponsePayload::expiresAt,
                    ServerboundSSOResponsePayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
