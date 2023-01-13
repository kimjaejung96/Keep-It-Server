package com.teamside.project.alpha.common.util;

import com.teamside.project.alpha.common.exception.ApiExceptionCode;
import com.teamside.project.alpha.common.exception.CustomRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@Slf4j
public class CryptUtils {
    // 암호화
    private static final String alg = "AES/CBC/PKCS5Padding";
    private static final String key = "2r5u7x!A%D*G-KaPdSgVkYp3s6v9y/B?"; // 32byte
    private static final String iv = "ak$)@_!DNCkAl!$N"; // 16byte

    // 암호화
    public static String encode(String text)  {
        String returnValue;
        try {
            Cipher cipher = Cipher.getInstance(alg);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);

            byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            returnValue = Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new CustomRuntimeException(ApiExceptionCode.SYSTEM_ERROR);
        }
        return returnValue;
    }

    // 복호화
    public static String decode(String text)  {
        String returnValue;
        try {

        Cipher cipher = Cipher.getInstance(alg);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);

        byte[] decodedBytes = Base64.getDecoder().decode(text);
        byte[] decrypted = cipher.doFinal(decodedBytes);
        returnValue = new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new CustomRuntimeException(ApiExceptionCode.SYSTEM_ERROR);
        }
        return returnValue;
    }



    public static String getMid() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (String) authentication.getPrincipal();
    }

    public static String arrangeBearer(String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token =  token.substring(7);
        }
        return token;
    }
}
