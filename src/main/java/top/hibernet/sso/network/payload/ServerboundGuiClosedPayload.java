package top.hibernet.sso.network.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import top.hibernet.sso.SSOPluginMessageHandlerMod;

/**
 * Packet sent from the client to the server when a GUI screen (browser overlay
 * or other modal) is closed by the player.
 * <p>
 * <b>Direction:</b> Client → Server<br>
 * <b>Channel:</b> {@code hibernet_sso:gui_closed}<br>
 * <b>Triggered by:</b> Various GUI close events
 * <p>
 * <b>Possible reason values:</b>
 * <ul>
 *   <li>{@code "browser_closed"} – The browser was closed normally</li>
 *   <li>{@code "browser_overlay_closed"} – The Minecraft overlay was closed</li>
 *   <li>{@code "exit_server_button"} – The player clicked the "Exit Server" button</li>
 *   <li>{@code "auth_screen_done"} – The auth screen completed and auto-closed</li>
 * </ul>
 *
 * @param reason a human-readable reason string identifying what was closed and how
 */
public record ServerboundGuiClosedPayload(String reason) implements CustomPacketPayload {
    public static final Type<ServerboundGuiClosedPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    SSOPluginMessageHandlerMod.MODID, "gui_closed"));

    public static final StreamCodec<ByteBuf, ServerboundGuiClosedPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, ServerboundGuiClosedPayload::reason,
                    ServerboundGuiClosedPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
