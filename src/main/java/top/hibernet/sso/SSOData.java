package top.hibernet.sso;

/**
 * Immutable record holding all SSO authentication data received from JVM
 * system properties or environment variables.
 */
public record SSOData(
        String username,
        String uuid,
        String accessToken,
        String idToken,
        long expiresAt
) {}
