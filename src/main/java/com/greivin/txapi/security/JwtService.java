package com.greivin.txapi.security;

import com.greivin.txapi.config.JwtProperties;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final JwtProperties props;
    private final byte[] secretBytes;

    public JwtService(JwtProperties props) {
        this.props = props;
        this.secretBytes = props.secret().getBytes(StandardCharsets.UTF_8);

        if (secretBytes.length < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 bytes long");
        }
    }

    public String generateToken(String subject, Map<String, Object> extraClaims) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(props.ttlMinutes() * 60);

        JWTClaimsSet.Builder claims = new JWTClaimsSet.Builder()
                .subject(subject)
                .issuer(props.issuer())
                .issueTime(Date.from(now))
                .expirationTime(Date.from(exp));

        if (extraClaims != null) {
            extraClaims.forEach(claims::claim);
        }

        try {
            SignedJWT jwt = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    claims.build()
            );

            JWSSigner signer = new MACSigner(secretBytes);
            jwt.sign(signer);

            return jwt.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Failed to sign JWT", e);
        }
    }

    public JWTClaimsSet validateAndGetClaims(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);

            JWSVerifier verifier = new MACVerifier(secretBytes);
            if (!jwt.verify(verifier)) {
                throw new RuntimeException("Invalid JWT signature");
            }

            JWTClaimsSet claims = jwt.getJWTClaimsSet();

            Date exp = claims.getExpirationTime();
            if (exp == null || exp.before(new Date())) {
                throw new RuntimeException("JWT expired");
            }

            if (claims.getIssuer() == null || !claims.getIssuer().equals(props.issuer())) {
                throw new RuntimeException("Invalid JWT issuer");
            }

            return claims;
        } catch (ParseException | JOSEException e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }
}