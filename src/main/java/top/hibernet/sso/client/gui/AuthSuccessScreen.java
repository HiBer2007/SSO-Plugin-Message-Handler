package top.hibernet.sso.client.gui;

import top.hibernet.sso.SSOPluginMessageHandlerMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

/**
 * Full-screen GUI shown after successful authentication.
 * Auto-closes after 3 seconds (60 ticks at 20 TPS) with a visible countdown.
 * Once closed, the player returns to the game.
 */
public class AuthSuccessScreen extends Screen {

    private static final int TOTAL_TICKS = 60;
    private int ticksRemaining = TOTAL_TICKS;

    public AuthSuccessScreen() {
        super(Component.translatable("gui.hbnssopmhm.auth.success_title"));
    }

    @Override
    public void tick() {
        super.tick();
        ticksRemaining--;
        if (ticksRemaining <= 0) {
            SSOPluginMessageHandlerMod.LOGGER.info("Auth success GUI auto-closed after 3 seconds");
            // Notify server the auth success screen auto-closed
            Minecraft.getInstance().setScreen(null);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Success title in green
        guiGraphics.drawCenteredString(font,
                Component.translatable("gui.hbnssopmhm.auth.success_title"),
                centerX, centerY - 30, 0x55FF55);

        // Success subtitle
        guiGraphics.drawCenteredString(font,
                Component.translatable("gui.hbnssopmhm.auth.success_subtitle"),
                centerX, centerY - 10, 0xAAAAAA);

        // Countdown
        int seconds = Mth.ceil(ticksRemaining / 20.0f);
        Component countdown = Component.translatable("gui.hbnssopmhm.auth.countdown", seconds);
        guiGraphics.drawCenteredString(font, countdown, centerX, centerY + 15, 0xFF5555);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
