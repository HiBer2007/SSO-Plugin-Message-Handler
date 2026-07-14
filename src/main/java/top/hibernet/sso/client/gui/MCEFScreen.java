package top.hibernet.sso.client.gui;

import com.cinemamod.mcef.MCEF;
import com.cinemamod.mcef.MCEFBrowser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Minecraft Screen that embeds a full-screen MCEF Chromium browser.
 * <p>
 * Simplified version of MCEF's built-in ExampleScreen — no navigation bar,
 * just the browser filling the entire screen with an exit button at top-right.
 */
public class MCEFScreen extends Screen {

    private final String startUrl;
    private final boolean fullscreen;
    private final boolean allowExit;
    private MCEFBrowser browser;
    private Button exitButton;

    public MCEFScreen(String url, boolean fullscreen, boolean allowExit) {
        super(Component.literal("HiBerNET SSO Browser"));
        this.startUrl = url;
        this.fullscreen = fullscreen;
        this.allowExit = allowExit;
    }

    @Override
    protected void init() {
        super.init();
        // Create browser if not yet created
        if (browser == null) {
            boolean transparent = false;
            browser = MCEF.createBrowser(startUrl, transparent);
        }
        resizeBrowser();

        // Close/Exit button at top-right
        int btnX = this.width - 110;
        if (!allowExit) {
            exitButton = addRenderableWidget(
                    Button.builder(Component.literal("Exit Server"),
                            btn -> {
                                var player = Minecraft.getInstance().player;
                                if (player != null) {
                                    player.connection.getConnection().disconnect(
                                            Component.literal("Disconnected by user"));
                                }
                            })
                            .bounds(btnX, 10, 100, 16)
                            .build()
            );
        } else {
            exitButton = addRenderableWidget(
                    Button.builder(Component.literal("Close (Esc)"),
                            btn -> onClose())
                            .bounds(btnX, 10, 100, 16)
                            .build()
            );
        }
    }

    private void resizeBrowser() {
        if (browser != null) {
            var mc = Minecraft.getInstance();
            double scale = mc.getWindow().getGuiScale();
            int w = (int) (width * scale);
            int h = (int) (height * scale);
            browser.resize(w, h);
        }
    }

    @Override
    public void resize(Minecraft mc, int width, int height) {
        super.resize(mc, width, height);
        resizeBrowser();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        super.render(guiGraphics, mouseX, mouseY, partial);

        // Render browser texture
        if (browser != null && browser.getRenderer().isTextureReady()) {
            ResourceLocation tex = browser.getTextureLocation();
            if (tex != null) {
                guiGraphics.blit(tex, 0, 0, 0, 0, width, height, width, height);
            }
        }
    }

    // ---- Mouse forwarding ----
    private int browserMouseX(double mx) {
        return (int) (mx * Minecraft.getInstance().getWindow().getGuiScale());
    }
    private int browserMouseY(double my) {
        return (int) (my * Minecraft.getInstance().getWindow().getGuiScale());
    }

    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        if (super.mouseClicked(mx, my, btn)) return true;
        browser.sendMousePress(browserMouseX(mx), browserMouseY(my), btn);
        browser.setFocus(true);
        return true;
    }

    @Override
    public boolean mouseReleased(double mx, double my, int btn) {
        if (super.mouseReleased(mx, my, btn)) return true;
        browser.sendMouseRelease(browserMouseX(mx), browserMouseY(my), btn);
        return true;
    }

    @Override
    public void mouseMoved(double mx, double my) {
        super.mouseMoved(mx, my);
        browser.sendMouseMove(browserMouseX(mx), browserMouseY(my));
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        if (super.keyPressed(key, scan, mods)) return true;
        browser.sendKeyPress(key, scan, mods);
        return false;
    }

    @Override
    public boolean keyReleased(int key, int scan, int mods) {
        if (super.keyReleased(key, scan, mods)) return true;
        browser.sendKeyRelease(key, scan, mods);
        return false;
    }

    @Override
    public void onClose() {
        if (browser != null) {
            browser.setCloseAllowed();
            browser.close();
            browser = null;
        }
        super.onClose();
    }

    @Override
    public void tick() {
        super.tick();
        if (browser != null) {
            String cur = browser.getURL();
            // Optionally track URL changes
        }
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
