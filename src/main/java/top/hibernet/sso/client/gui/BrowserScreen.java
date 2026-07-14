package top.hibernet.sso.client.gui;

import top.hibernet.sso.SSOPluginMessageHandlerMod;
import top.hibernet.sso.network.payload.ServerboundGuiClosedPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Minecraft GUI overlay screen shown alongside the JavaFX WebView browser.
 * <p>
 * Provides:
 * <ul>
 *   <li>Information about the active browser session</li>
 *   <li>An "Exit Server" button when the browser does not allow self-exit</li>
 * </ul>
 * The actual web rendering happens in the separate JavaFX Stage managed by
 * {@link BrowserWindow}.
 */
public class BrowserScreen extends Screen {

    private final String url;
    private final boolean fullscreen;
    private final boolean allowExit;

    public BrowserScreen(String url, boolean fullscreen, boolean allowExit) {
        super(Component.literal("HiBerNET SSO Browser"));
        this.url = url;
        this.fullscreen = fullscreen;
        this.allowExit = allowExit;
    }

    @Override
    protected void init() {
        super.init();

        // Right-top exit button (10px from top, 120px from right edge)
        int btnX = this.width - 130;

        if (!allowExit) {
            this.addRenderableWidget(
                    Button.builder(
                            Component.translatable("gui.hbnssopmhm.browser.exit_server"),
                            btn -> {
                                SSOPluginMessageHandlerMod.LOGGER.info("Browser exit server button clicked");
                                PacketDistributor.sendToServer(
                                        new ServerboundGuiClosedPayload("exit_server_button"));
                                var player = Minecraft.getInstance().player;
                                if (player != null) {
                                    player.connection.getConnection().disconnect(
                                            Component.literal("Disconnected by user request"));
                                }
                            })
                            .bounds(btnX, 10, 120, 16)
                            .build()
            );
        } else {
            this.addRenderableWidget(
                    Button.builder(
                            Component.translatable("gui.hbnssopmhm.browser.close_overlay"),
                            btn -> onClose())
                            .bounds(btnX, 10, 120, 16)
                            .build()
            );
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // NO renderBackground() — avoids double-blur with button backgrounds

        // Buttons first
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Text on top
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        guiGraphics.drawCenteredString(font,
                Component.translatable("gui.hbnssopmhm.browser.title"),
                centerX, centerY - 50, 0xFFFFFF);
        guiGraphics.drawCenteredString(font,
                Component.translatable("gui.hbnssopmhm.browser.instruction"),
                centerX, centerY - 30, 0xAAAAAA);

        // URL display
        String displayUrl = font.width(url) > this.width - 40
                ? font.plainSubstrByWidth(url, this.width - 40)
                : url;
        guiGraphics.drawCenteredString(font,
                Component.literal(displayUrl),
                centerX, centerY + 80, 0x5555FF);
    }

    @Override
    public void onClose() {
        super.onClose();
        SSOPluginMessageHandlerMod.LOGGER.info("Browser overlay screen closed by user");
        PacketDistributor.sendToServer(new ServerboundGuiClosedPayload("browser_overlay_closed"));
        // Also close the JavaFX window
        BrowserWindow.close();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return allowExit;
    }

}
