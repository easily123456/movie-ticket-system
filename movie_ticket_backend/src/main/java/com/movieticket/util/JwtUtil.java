package com.movieticket.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}") //读取application.properties中的值注入到secret变量中
    private String secret;//secret: movie-ticket-system-secret-key-2024-spring-boot-3-jwt-token

    @Value("${jwt.expiration}")//读取application.properties中的值注入到expiration变量中
    private Long expiration;//expiration: 86400000   24小时

    private SecretKey getSigningKey(){ //将字符串类型的 secret 转换为 javax.crypto.SecretKey 对象，供 JWT 库（如 jjwt）使用
        return Keys.hmacShaKeyFor(secret.getBytes());//Keys.hamcShaKeyFor()将原数组转为SecretKey对象，并验证密钥是否满足要求
    }

    public String generateToken(String username, Long userId){
    //生成一个代签名的JWT，包含用户名、用户ID、当前时间和过期时间等信息
        Date now = new Date();//获取当前时间
        Date expiryDate = new Date(now.getTime() + expiration);//获取当前时间加上过期时间
        return Jwts.builder()
                .setSubject(username) // 设置 JWT 的主体（sub 声明），即用户名
                .claim("userId", userId) // 自定义字段（用户ID）
                .setIssuedAt(now) // 签发时间（iat）
                .setExpiration(expiryDate)  // 过期时间（exp）
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)  //签名
                .compact(); //创建并返回 JWT
    }

    public String getUsernameFromToken(String token){
        /*JWT由三部分组成：
          1. 头部（Header）：描述 JWT 的元数据，如 JWT 的类型、签名算法等
          2. 载荷（Payload）：存储用户信息，如用户ID、用户名、权限等
          3. 签名（Signature）：用来验证 JWT 的完整性和真实性
        */
        //Claims 是一个核心类型，它代表了 JWT 的 载荷（Payload）部分 的结构化数据
        Claims claims = Jwts.parserBuilder() //创建一个 JWT 解析器的构建器（Builder），用于配置解析规则
                .setSigningKey(getSigningKey()) //设置签名密钥（Secret Key），用于验证 JWT 的签名是否合法
                .build() //完成解析器的构建，返回一个可用的 JwtParser 对象
                .parseClaimsJws(token) //解析传入的 JWT 字符串，如果解析成功，则返回 Claims 对象，否则抛出异常
                .getBody();//从 Jws<Claims> 中提取 Claims 对象，即 JWT 的 载荷部分（Payload），存储了用户信息
        return claims.getSubject();//获取用户名
    }

    public Long getUserIdFromToken(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("userId", Long.class);//获取的字段名 和 期望的返回类型
    }

    public boolean validateToken(String token){ //验证JWT的签名是否合法
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            //捕获JwtException 和 IllegalArgumentException异常，其中|为或运算符，表示多个异常类型可以同时处理，e为是异常变量的命名
            /*JwtException：JJWT 库定义的异常，包括：
                ExpiredJwtException：token 已过期。
                UnsupportedJwtException：token 格式或加密算法不支持。
                MalformedJwtException：token 结构错误（如不是有效的 JWT 格式）。
                SignatureException：签名验证失败（token 被篡改）。
             IllegalArgumentException：参数错误（如 token 为 null 或空字符串）。
            */
            return false;
        }
    }
}
