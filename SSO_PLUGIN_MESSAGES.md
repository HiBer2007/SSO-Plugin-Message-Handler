# HiBerNET SSO Plugin Message Protocol

This document describes the plugin message protocol used by the **HiBerNET SSO Plugin Message Handler** mod for Minecraft NeoForge 1.21.1.

This mod communicates through the Minecraft plugin message channel system. When used behind a **Velocity** proxy, the Velocity plugin must forward and intercept packets on the channel `HiBerNET.SSO`.

---

## Channel

| Property | Value |
|----------|-------|
| **Velocity channel** | `HiBerNET.SSO` |
| **Minecraft Identifier** | `hbnssopmhm:query_sso`, `hbnssopmhm:sso_response`, etc. (see per-packet below) |
| **Direction** | Bidirectional (Server ↔ Client) |

On a Velocity proxy, you must register the channel in your `plugin.yml`:

```yaml
name: HiBerNetSSOVelocity
version: 1.0.0
main: com.example.HiBerNetSSOVelocity
```

And in your Velocity plugin Java code:

```java
// Register the plugin message channel on proxy init
proxy.getChannelRegistrar().register("HiBerNET.SSO");
```

---

## Packet Reference

### 1. Server → Client: `hbnssopmhm:query_sso`

**Purpose:** Server requests the client's SSO login information.

**Payload format:** Empty (no data fields)

```java
// Velocity — forward query to a specific player
// ByteArrayDataOutput out = ByteStreams.newDataOutput();
// out.writeUTF("forward");
// out.writeUTF(playerName); // empty string = all players
// out.writeUTF("HiBerNET.SSO");
// Actually, with Velocity you can use:
```

**Velocity example:**
```java
// Send query_sso to a player
// Using Velocity's modern API (1.0.0+):
player.sendPluginMessage(ChannelIdentifier.of("HiBerNET.SSO"), 
    packetBytes("hbnssopmhm:query_sso"));
```

**Client response:** The client will respond with `hbnssopmhm:sso_response`.

---

### 2. Client → Server: `hbnssopmhm:sso_response`

**Purpose:** Client returns SSO authentication data to the server.

**Payload fields (in order):**

| # | Type | Name | Description |
|---|------|------|-------------|
| 1 | `UTF-8 String` | `username` | SSO username (e.g. `"hiber2007"`) |
| 2 | `UTF-8 String` | `uuid` | Player UUID (e.g. `"b3492950-2453-40c8-9efb-c63094d271de"`) |
| 3 | `UTF-8 String` | `accessToken` | OAuth2 access token |
| 4 | `UTF-8 String` | `idToken` | OpenID Connect id_token (JWT) |
| 5 | `VarLong` | `expiresAt` | Token expiration as Unix epoch milliseconds |

**Velocity example (listening):**
```java
// Listen for responses on the backend server channel
// The actual forwarding is done by Velocity automatically if configured
public void onPluginMessage(PluginMessageEvent event) {
    if (!event.getTag().equals("HiBerNET.SSO")) return;
    
    // Parse the payload...
    // Check the first identifier to determine sub-channel
}
```

**Expected JSON-like data structure:**
```json
{
  "username": "hiber2007",
  "uuid": "b3492950-2453-40c8-9efb-c63094d271de",
  "accessToken": "J-BK4ObPF9JwcNZLT2_a2QhBQldGd3M-GbdriuxeZZz",
  "idToken": "eyJhbGciOiJSUzI1NiIsImtpZCI6...",
  "expiresAt": 1783908334563
}
```

---

### 3. Server → Client: `hbnssopmhm:open_auth_gui`

**Purpose:** Server instructs the client to open a full-screen GUI showing an "authenticating..." message. This GUI remains visible until `auth_success` is received.

**Payload format:** Empty (no data fields)

**Velocity example:**
```java
player.sendPluginMessage(ChannelIdentifier.of("HiBerNET.SSO"), 
    packetBytes("hbnssopmhm:open_auth_gui"));
```

---

### 4. Server → Client: `hbnssopmhm:auth_success`

**Purpose:** Server informs the client that authentication succeeded. The client will display a green success screen with a 3-second countdown, then auto-close and return to the game.

**Payload format:** Empty (no data fields)

**Velocity example:**
```java
player.sendPluginMessage(ChannelIdentifier.of("HiBerNET.SSO"), 
    packetBytes("hbnssopmhm:auth_success"));
```

---

### 5. Server → Client: `hbnssopmhm:open_browser`

**Purpose:** Server tells the client to open an embedded JavaFX WebView browser at the specified URL for OAuth/SSO login flows.

**Payload fields (in order):**

| # | Type | Name | Description |
|---|------|------|-------------|
| 1 | `UTF-8 String` | `url` | URL to open in the browser (e.g. `"https://sso.hibernet.top/auth"`) |
| 2 | `Boolean` | `fullscreen` | `true` → open in fullscreen mode; `false` → windowed (1024×768) |
| 3 | `Boolean` | `allowExit` | `true` → user can close the browser; `false` → user cannot close it, and an "Exit Server" button is shown |

**Velocity example:**
```java
// Open OAuth login page with fullscreen, no manual exit allowed
sendOpenBrowser(player, "https://sso.hibernet.top/oauth/authorize", true, false);

// Helper method
void sendOpenBrowser(Player player, String url, boolean fullscreen, boolean allowExit) {
    ByteBuf buf = Unpooled.buffer();
    writeString(buf, "hbnssopmhm:open_browser");
    writeString(buf, url);
    buf.writeBoolean(fullscreen);
    buf.writeBoolean(allowExit);
    player.sendPluginMessage(ChannelIdentifier.of("HiBerNET.SSO"), buf.array());
}
```

---

### 6. Server → Client: `hbnssopmhm:close_browser`

**Purpose:** Server tells the client to close the currently open embedded browser.

**Payload format:** Empty (no data fields)

**Velocity example:**
```java
// Close browser after OAuth callback is received
player.sendPluginMessage(ChannelIdentifier.of("HiBerNET.SSO"), 
    packetBytes("hbnssopmhm:close_browser"));
```

---

### 7. Client → Server: `hbnssopmhm:gui_closed`

**Purpose:** Client notifies the server that a GUI was closed, along with a reason.

**Payload fields (in order):**

| # | Type | Name | Description |
|---|------|------|-------------|
| 1 | `UTF-8 String` | `reason` | Reason for closing. Possible values: `"browser_closed"`, `"browser_overlay_closed"`, `"exit_server_button"`, `"auth_success_done"` |

---

## Velocity Plugin Workflow Example

Below is a typical authentication flow using all the messages:

```
┌─────────┐         ┌──────────┐         ┌─────────┐
│  Client  │         │ Velocity │         │ Backend │
│  (Mod)   │         │ (Proxy)  │         │ Server  │
└────┬─────┘         └────┬─────┘         └────┬─────┘
     │                    │                    │
     │   Connect          │                    │
     │───────────────────>│                    │
     │                    │   Login            │
     │                    │───────────────────>│
     │                    │                    │
     │                    │  query_sso         │
     │                    │<───────────────────│
     │                    │                    │
     │                    │  query_sso         │
     │<───────────────────│                    │
     │                    │                    │
     │  open_auth_gui     │                    │
     │<───────────────────│                    │
     │                    │                    │
     │  sso_response      │                    │
     │───────────────────>│                    │
     │                    │  sso_response      │
     │                    │───────────────────>│
     │                    │                    │
     │                    │  auth_success      │
     │                    │<───────────────────│
     │                    │                    │
     │  auth_success      │                    │
     │<───────────────────│                    │
     │  [3s countdown]    │                    │
     │  [auto-close]      │                    │
     │                    │                    │
```

### For OAuth flows requiring user interaction:

```
     │                    │                    │
     │  open_browser      │                    │
     │  (url,full,noexit) │                    │
     │<───────────────────│                    │
     │                    │                    │
     │  [User logs in via │                    │
     │   embedded browser]│                    │
     │                    │                    │
     │  close_browser     │                    │
     │<───────────────────│                    │
     │                    │                    │
     │  [Repeat auth flow │                    │
     │   or continue]     │                    │
     │                    │                    │
```

---

## Implementation Notes for Velocity Plugin

### Reading SSO Data on Client (JVM Properties)

The mod reads SSO data from JVM system properties set by the launcher:

```bash
-DHiBerNET.SSO.username=hiber2007
-DHiBerNET.SSO.uuid=b3492950-2453-40c8-9efb-c63094d271de
-DHiBerNET.SSO.accesstoken=J-BK4ObPF9JwcNZLT2_a2QhBQldGd3M-GbdriuxeZZz
-DHiBerNET.SSO.idtoken=eyJhbGciOiJSUzI1NiIsImtpZCI6...
-DHiBerNET.SSO.expiresat=1783908334563
```

Alternatively, if `-DHiBerNET.SSO.useenv=true` is set, the mod reads from environment variables instead:

```
HiBerNET.SSO.username=hiber2007
HiBerNET.SSO.uuid=b3492950-2453-40c8-9efb-c63094d271de
HiBerNET.SSO.accesstoken=J-BK4ObPF9JwcNZLT2_a2QhBQldGd3M-GbdriuxeZZz
HiBerNET.SSO.idtoken=eyJhbGciOiJSUzI1NiIsImtpZCI6...
HiBerNET.SSO.expiresat=1783908334563
```

### Sending Plugin Messages from Velocity

To send a plugin message from a Velocity plugin to a client connected to your proxy, use `player.sendPluginMessage()` with the channel name **`HiBerNET.SSO`**. The first bytes of the payload should be the Minecraft identifier string (e.g., `hbnssopmhm:query_sso`) encoded as UTF-8, followed by the data fields in the order specified above.

**Important:** Ensure your Velocity plugin registers the channel on initialization:

```java
@Override
public void proxyInitialize(ProxyInitializeEvent event) {
    ProxyServer proxy = event.getProxyServer();
    proxy.getChannelRegistrar().register("HiBerNET.SSO");
}
```
