package com.vivafit.vivafit.authentification.services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class EncryptionDataService {

    private String algorithm = "AES";
    private SecretKey secretKey;

    public EncryptionDataService(@Value("${encryption.secret.key}") String base64SecretKey) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(base64SecretKey);
            this.secretKey = new SecretKeySpec(decodedKey, algorithm);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize EncryptionDataService", e);
        }
    }


    public String encrypt(String data) throws NoSuchAlgorithmException,
            InvalidKeyException,
            NoSuchPaddingException,
            IllegalBlockSizeException,
            BadPaddingException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public String decrypt(String encryptedData) throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidKeyException,
            IllegalBlockSizeException,
            BadPaddingException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedData = cipher.doFinal(decodedData);
        return new String(decryptedData);
    }

    public boolean isEncrypted(String data) {
        try {
            String decryptedData = decrypt(data);
            if (decryptedData != null) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
