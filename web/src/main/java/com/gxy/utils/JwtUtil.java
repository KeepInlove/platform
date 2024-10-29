package com.gxy.utils;

/**
 * @Classname JwtUtil
 * @Date 2024/10/29
 * @Created by guoxinyu
 */
/**
 * @author guo
 * @version 1.0
 * @description: TODO
 * @date 2024-06-15 0:02
 */


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.apache.tomcat.util.codec.binary.Base64;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;



public class JwtUtil {
    //创建默认的密钥与加密算法，提供给空参构造器调用
    private static final String defaultBase64EncodingSecretKey = "eb^29*be";

    //有效期为
    public static final Long JWT_TTL = 60 * 60 *1000L;// 60 * 60 *1000  一个小时
    /**
     * 生成jwtToken的方法，jwtToken中包含了三部分：Header、PayLoad、Signature
     * - Header：
     *      当前字符串的类型，一般是"JWT"
     *      使用的加密算法，可以是"HS256"或其他算法
     * - Payload：
     *      一般有四种常见的标准字段：
     *          jat：签发时间，即jwt的生成时间
     *          jti：JWT的唯一标识
     *          iss：签发人，一般是username或userId
     *          exp：过期时间
     * - Signature：签名
     * @param ttlMillis
     * @param claims
     * @return
     */
    public static String encoding(Integer ttlMillis, Map<String, Object> claims){
        if(claims == null){
            claims = new HashMap<>();
        }
        return JWT.create()
                .withClaim("claims",claims)//添加载荷
                .withExpiresAt(new Date(System.currentTimeMillis() + ttlMillis * JWT_TTL))//配置过期时间
                .sign(Algorithm.HMAC256(Base64.encodeBase64String(defaultBase64EncodingSecretKey.getBytes())));//指定算法，配置密钥
    }

    /**
     * 解密jwtToken，并获取jwt载荷内容的方法。
     * Claims就是一个map，里面包含了jwt载荷部分的所有键值对
     * @param token
     * @return
     */
    public static Map<String, Object> decoding(String token){
        return JWT.require(Algorithm.HMAC256(Base64.encodeBase64String(defaultBase64EncodingSecretKey.getBytes())))
                .build()
                .verify(token)//验证token，生成一个解析后的jwt对
                .getClaim("claims").asMap();
    }

    // 判断jwtToken是否合法
    public static boolean isVerify(String token) {
        // 这个是官方的校验规则，这里只写了一个”校验算法“，可以自己加
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(Base64.encodeBase64String(defaultBase64EncodingSecretKey.getBytes()))).build();
        verifier.verify(token);
        // 校验不通过会抛出异常
        // 判断合法的标准：1. 头部和荷载部分没有篡改过。2. 没有过期
        return true;
    }



}