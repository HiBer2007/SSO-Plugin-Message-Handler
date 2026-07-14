package top.hibernet.sso.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Full-screen GUI shown while the server is performing authentication.
 * This is a simple waiting screen with no auto-close timer.
 * It is dismissed when the server sends an auth_success message.
 */
public class AuthWaitScreen extends Screen {

    private int tickCounter = 0;

    public AuthWaitScreen() {
        super(Component.translatable("gui.hbnssopmhm.auth.wait_title"));
    }

    @Override
    public void tick() {
        super.tick();
        tickCounter++;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Title
        guiGraphics.drawCenteredString(font,
                Component.translatable("gui.hbnssopmhm.auth.wait_title"),
                centerX, centerY - 30, 0xFFFFFF);

        // Subtitle with animated dots
        int dots = (tickCounter / 20) % 4;
        String dotStr = ".".repeat(dots);
        guiGraphics.drawCenteredString(font,
                Component.translatable("gui.hbnssopmhm.auth.wait_subtitle", dotStr),
                centerX, centerY, 0xAAAAAA);
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
