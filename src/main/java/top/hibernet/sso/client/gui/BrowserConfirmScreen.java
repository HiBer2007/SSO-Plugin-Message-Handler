package top.hibernet.sso.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Confirmation screen shown before opening the URL in the system browser.
 * <p>
 * Requires the user to explicitly click "Open Browser" to proceed.
 * This prevents accidental browser popups and gives the player control.
 */
public class BrowserConfirmScreen extends Screen {

    private final String url;
    private final boolean fullscreen;
    private final boolean allowExit;

    public BrowserConfirmScreen(String url, boolean fullscreen, boolean allowExit) {
        super(Component.translatable("gui.hbnssopmhm.browser.title"));
        this.url = url;
        this.fullscreen = fullscreen;
        this.allowExit = allowExit;
    }

    @Override
    protected void init() {
        super.init();

        int cx = this.width / 2;
        int cy = this.height / 2;

        // "Open Browser" button
        this.addRenderableWidget(
                Button.builder(
                        Component.translatable("gui.hbnssopmhm.browser.confirm_open"),
                        btn -> {
                            BrowserWindow.launchBrowser(url, fullscreen, allowExit);
                        })
                        .bounds(cx - 75, cy + 10, 150, 16)
                        .build()
        );

        if (!allowExit) {
            // Forced mode: show red "Exit Server" button instead of Cancel
            this.addRenderableWidget(
                    Button.builder(
                            Component.translatable("gui.hbnssopmhm.browser.exit_server")
                                    .withStyle(s -> s.withColor(net.minecraft.ChatFormatting.RED)),
                            btn -> {
                                var player = Minecraft.getInstance().player;
                                if (player != null) {
                                    player.connection.getConnection().disconnect(
                                            Component.literal("Disconnected by user"));
                                }
                            })
                            .bounds(cx - 75, cy + 32, 150, 16)
                            .build()
            );
        } else {
            // Normal mode: simple Cancel button
            this.addRenderableWidget(
                    Button.builder(
                            Component.translatable("gui.hbnssopmhm.browser.confirm_cancel"),
                            btn -> onClose())
                            .bounds(cx - 75, cy + 32, 150, 16)
                            .build()
            );
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // NO renderBackground() — avoids double-blur with button backgrounds

        // Draw buttons first
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Draw ALL text on TOP of buttons — same visual layer, no sandwiching
        int cx = this.width / 2;
        int cy = this.height / 2;

        guiGraphics.drawCenteredString(font,
                Component.translatable("gui.hbnssopmhm.browser.confirm_title"),
                cx, cy - 50, 0xFFFFFF);
        guiGraphics.drawCenteredString(font,
                Component.translatable("gui.hbnssopmhm.browser.confirm_prompt"),
                cx, cy - 30, 0xAAAAAA);

        String displayUrl = font.width(url) > this.width - 40
                ? font.plainSubstrByWidth(url, this.width - 40)
                : url;
        guiGraphics.drawCenteredString(font,
                Component.literal("§b" + displayUrl),
                cx, cy + 80, 0x5555FF);
    }

    @Override
    public void onClose() {
        super.onClose();
        // If we never opened the browser, close the overlay silently
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
