package com.finx.security;

import com.finx.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class JwtService {

    private final String secret;
    private final long accessTokenMinutes;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.access-token-minutes}") long accessTokenMinutes) {
        this.secret = secret;
        this.accessTokenMinutes = accessTokenMinutes;
    }

    public String generateAccessToken(User user) {
        long now = Instant.now().getEpochSecond();
        Map<String, String> claims = new LinkedHashMap<>();
        claims.put("sub", String.valueOf(user.getId()));
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole().name());
        claims.put("iat", String.valueOf(now));
        claims.put("exp", String.valueOf(now + accessTokenMinutes * 60));
        String header = base64Url("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        String payload = base64Url(toJson(claims));
        String signature = sign(header + "." + payload);
        return header + "." + payload + "." + signature;
    }

    public Long parseUserId(String token) {
        Map<String, String> claims = parseClaims(token);
        long exp = Long.parseLong(claims.get("exp"));
        if (Instant.now().getEpochSecond() > exp) {
            throw new IllegalArgumentException("JWT expired");
        }
        return Long.parseLong(claims.get("sub"));
    }

    private Map<String, String> parseClaims(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT");
        }
        String expected = sign(parts[0] + "." + parts[1]);
        if (!constantTimeEquals(expected, parts[2])) {
            throw new IllegalArgumentException("Invalid JWT signature");
        }
        String json = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        return fromJson(json);
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot sign JWT", ex);
        }
    }

    private String base64Url(String value) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String toJson(Map<String, String> claims) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, String> entry : claims.entrySet()) {
            if (!first) {
                json.append(',');
            }
            first = false;
            json.append('"').append(entry.getKey()).append('"').append(':');
            if ("iat".equals(entry.getKey()) || "exp".equals(entry.getKey()) || "sub".equals(entry.getKey())) {
                json.append(entry.getValue());
            } else {
                json.append('"').append(entry.getValue().replace("\"", "\\\"")).append('"');
            }
        }
        return json.append('}').toString();
    }

    private Map<String, String> fromJson(String json) {
        Map<String, String> claims = new LinkedHashMap<>();
        String body = json.trim();
        if (body.startsWith("{")) {
            body = body.substring(1);
        }
        if (body.endsWith("}")) {
            body = body.substring(0, body.length() - 1);
        }
        if (body.trim().isEmpty()) {
            return claims;
        }
        for (String pair : body.split(",")) {
            String[] parts = pair.split(":", 2);
            String key = strip(parts[0]);
            String value = strip(parts[1]);
            claims.put(key, value);
        }
        return claims;
    }

    private String strip(String value) {
        String trimmed = value.trim();
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
            return trimmed.substring(1, trimmed.length() - 1);
        }
        return trimmed;
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
