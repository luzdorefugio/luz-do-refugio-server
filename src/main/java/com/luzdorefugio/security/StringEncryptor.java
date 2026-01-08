package com.luzdorefugio.security;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.security.Key;

@Component
@Converter
public class StringEncryptor implements AttributeConverter<String, String> {
    private static final String ALGORITHM = "AES";
    private static final String SECRET = "LuzDoRefugioChaveSecreta2024";
    private final Key key;
    private final Cipher cipher;

    public StringEncryptor() throws Exception {
        byte[] keyBytes = SECRET.getBytes(StandardCharsets.UTF_8);
        byte[] validKeyBytes = Arrays.copyOf(keyBytes, 32);
        this.key = new SecretKeySpec(validKeyBytes, ALGORITHM);
        this.cipher = Cipher.getInstance(ALGORITHM);
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao encriptar dados", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao desencriptar dados", e);
        }
    }
}