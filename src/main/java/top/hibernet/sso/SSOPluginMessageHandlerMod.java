package top.hibernet.sso;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(SSOPluginMessageHandlerMod.MODID)
public class SSOPluginMessageHandlerMod {
    public static final String MODID = "hbnssopmhm";
    public static final Logger LOGGER = LoggerFactory.getLogger(SSOPluginMessageHandlerMod.class);
    public static final String SSO_CHANNEL = "HiBerNET.SSO";

    public SSOPluginMessageHandlerMod(IEventBus modBus) {
        LOGGER.info("HiBerNET SSO Plugin Message Handler initialized");
    }
}
