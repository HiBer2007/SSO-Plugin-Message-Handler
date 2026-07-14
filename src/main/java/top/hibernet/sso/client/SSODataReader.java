package top.hibernet.sso.client;

import top.hibernet.sso.SSOData;
import top.hibernet.sso.SSOPluginMessageHandlerMod;

/**
 * Reads SSO authentication data from JVM system properties or environment variables.
 * <p>
 * <b>Priority order:</b>
 * <ol>
 *   <li>If {@code -DHiBerNET.SSO.useenv=true} is set, reads ONLY from environment variables</li>
 *   <li>Otherwise, reads from system properties first, falling back to environment variables</li>
 * </ol>
 * <p>
 * <b>Available properties / environment variables:</b>
 * <table>
 *   <tr><th>Key</th><th>Description</th></tr>
 *   <tr><td>{@code HiBerNET.SSO.username}</td><td>SSO username (e.g. "hiber2007")</td></tr>
 *   <tr><td>{@code HiBerNET.SSO.uuid}</td><td>Minecraft UUID (e.g. "b3492950-2453-40c8-9efb-c63094d271de")</td></tr>
 *   <tr><td>{@code HiBerNET.SSO.accesstoken}</td><td>OAuth2 access token</td></tr>
 *   <tr><td>{@code HiBerNET.SSO.idtoken}</td><td>OpenID Connect id_token (JWT)</td></tr>
 *   <tr><td>{@code HiBerNET.SSO.expiresat}</td><td>Token expiration time as Unix epoch milliseconds</td></tr>
 *   <tr><td>{@code HiBerNET.SSO.useenv}</td><td>(System property only) When {@code "true"}, forces reading from environment variables</td></tr>
 * </table>
 */
public class SSODataReader {

    private static final String PROP_PREFIX = "HiBerNET.SSO.";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_UUID = "uuid";
    private static final String KEY_ACCESSTOKEN = "accesstoken";
    private static final String KEY_IDTOKEN = "idtoken";
    private static final String KEY_EXPIRESAT = "expiresat";
    private static final String KEY_USEENV = "useenv";

    private SSODataReader() {}

    /**
     * Reads SSO data from system properties (or environment variables if
     * {@code -DHiBerNET.SSO.useenv=true} is set).
     *
     * @return a populated {@link SSOData} record
     */
    public static SSOData readSSOData() {
        boolean useEnvOnly = Boolean.parseBoolean(
                System.getProperty(PROP_PREFIX + KEY_USEENV, "false"));

        String username, uuid, accessToken, idToken;
        long expiresAt;

        if (useEnvOnly) {
            username = getEnv(KEY_USERNAME);
            uuid = getEnv(KEY_UUID);
            accessToken = getEnv(KEY_ACCESSTOKEN);
            idToken = getEnv(KEY_IDTOKEN);
            expiresAt = parseLongSafe(getEnv(KEY_EXPIRESAT), 0L);
        } else {
            username = getPropOrEnv(KEY_USERNAME);
            uuid = getPropOrEnv(KEY_UUID);
            accessToken = getPropOrEnv(KEY_ACCESSTOKEN);
            idToken = getPropOrEnv(KEY_IDTOKEN);
            expiresAt = parseLongSafe(getPropOrEnv(KEY_EXPIRESAT), 0L);
        }

        SSOPluginMessageHandlerMod.LOGGER.info(
                "Read SSO data: username={}, uuid={}, useEnvOnly={}",
                username, uuid, useEnvOnly);

        return new SSOData(
                username != null ? username : "",
                uuid != null ? uuid : "",
                accessToken != null ? accessToken : "",
                idToken != null ? idToken : "",
                expiresAt
        );
    }

    private static String getPropOrEnv(String key) {
        String value = System.getProperty(PROP_PREFIX + key);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        return getEnv(key);
    }

    private static String getEnv(String key) {
        return System.getenv(PROP_PREFIX + key);
    }

    private static long parseLongSafe(String value, long defaultValue) {
        if (value == null || value.isEmpty()) return defaultValue;
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            SSOPluginMessageHandlerMod.LOGGER.warn("Failed to parse long value: {}", value);
            return defaultValue;
        }
    }
}
