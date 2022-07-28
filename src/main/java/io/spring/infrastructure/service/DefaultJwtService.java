package io.spring.infrastructure.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import io.spring.core.service.JwtService;
import io.spring.core.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.Optional;

@Component
public class DefaultJwtService implements JwtService {
    //一个秘密（对称）密钥。这个接口的目的是将所有秘密密钥接口分组（并为其提供类型安全）
    private final SecretKey signingKey;
    //对JSON网络算法规范中定义的标准JWT签名算法名称的类型安全表示。
    private final SignatureAlgorithm signatureAlgorithm;
    private int sessionTime;

    @Autowired
    public DefaultJwtService(@Value("${jwt.secret}") String secret,
                             @Value("${jwt.sessionTime}") int sessionTime) {
        signatureAlgorithm = SignatureAlgorithm.HS512;
        this.signingKey = new SecretKeySpec(secret.getBytes(),signatureAlgorithm.getJcaName());
        this.sessionTime = sessionTime;
    }


    @Override
    public Optional<String> getSubFromToken(String token) {
        try {
            Jws<Claims> claimsJws =
                    Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
            return Optional.ofNullable(claimsJws.getBody().getSubject());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    //生成jwts的token，根据用户的id生成token
    @Override
    public String toToken(User user) {
        return Jwts.builder()
                .setSubject(user.getId())
                .setExpiration(expireTimeFromNow())
                .signWith(signingKey)
                .compact();
    }

    private Date expireTimeFromNow() {
        return new Date(System.currentTimeMillis() + sessionTime + sessionTime*1000L  );
        }
}
