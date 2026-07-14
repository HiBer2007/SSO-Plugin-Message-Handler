package top.hibernet.sso.network.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import top.hibernet.sso.SSOPluginMessageHandlerMod;

/**
 * Packet sent from the server to the client instructing it to open an embedded
 * browser (JavaFX WebView) displaying the specified URL.
 * <p>
 * <b>Direction:</b> Server → Client<br>
 * <b>Channel:</b> {@code hibernet_sso:open_browser}<br>
 * <b>Close instruction:</b> {@link ClientboundCloseBrowserPayload}
 * <p>
 * <b>Velocity plugin usage:</b>
 * <pre>{@code
 * // Open a browser for OAuth/SSO login
 * // Parameters:
 * // - url (String): The URL to open
 * // - fullscreen (boolean): If true, the browser window opens in fullscreen
 * // - allowExit (boolean): If false, the user cannot close the browser
 * //                        and an "Exit Server" button is shown
 * }</pre>
 *
 * @param url        the URL to load in the embedded browser
 * @param fullscreen whether the browser should be displayed in fullscreen mode
 * @param allowExit  whether the user is allowed to close the browser by themselves
 */
public record ClientboundOpenBrowserPayload(String url, boolean fullscreen, boolean allowExit) implements CustomPacketPayload {
    public static final Type<ClientboundOpenBrowserPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    SSOPluginMessageHandlerMod.MODID, "open_browser"));

    public static final StreamCodec<ByteBuf, ClientboundOpenBrowserPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, ClientboundOpenBrowserPayload::url,
                    ByteBufCodecs.BOOL, ClientboundOpenBrowserPayload::fullscreen,
                    ByteBufCodecs.BOOL, ClientboundOpenBrowserPayload::allowExit,
                    ClientboundOpenBrowserPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
