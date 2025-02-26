package dev.theturkey.backbones.util

import dev.theturkey.backbones.Config
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import java.security.spec.KeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object EncryptUtil {
    private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    private const val IV_SIZE = 16
    private const val SALT_SIZE: Int = 32

    fun encrypt(strToEncrypt: String?): String? {
        if(strToEncrypt == null)
            return null;
        try {
            val ivspec = generateIv()
            val salt = generateSalt()
            val secretKey = getKeyFromPassword(Config.AES_SECRET, salt)
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec)
            val cypherOutput = cipher.doFinal(strToEncrypt.toByteArray(StandardCharsets.UTF_8))
            return Base64.getEncoder().encodeToString(
                ByteBuffer.allocate(IV_SIZE + SALT_SIZE + cypherOutput.size).put(ivspec.iv).put(salt).put(cypherOutput)
                    .array()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun decrypt(strToDecrypt: String?): String? {
        if(strToDecrypt == null)
            return null;

        try {
            val bb = ByteBuffer.wrap(Base64.getDecoder().decode(strToDecrypt.toByteArray(StandardCharsets.UTF_8)))
            val iv = ByteArray(IV_SIZE)
            bb[iv]
            val salt = ByteArray(SALT_SIZE)
            bb[salt]
            val cipherText = ByteArray(bb.remaining())
            bb[cipherText]
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, getKeyFromPassword(Config.AES_SECRET, salt), IvParameterSpec(iv))
            return String(cipher.doFinal(cipherText))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun getKeyFromPassword(password: String, saltBytes: ByteArray): SecretKey {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec: KeySpec = PBEKeySpec(password.toCharArray(), saltBytes, 65536, 256)
        return SecretKeySpec(factory.generateSecret(spec).encoded, "AES")
    }

    private fun generateIv(): IvParameterSpec {
        val iv = ByteArray(IV_SIZE)
        SecureRandom().nextBytes(iv)
        return IvParameterSpec(iv)
    }

    private fun generateSalt(): ByteArray {
        val salt = ByteArray(SALT_SIZE)
        SecureRandom().nextBytes(salt)
        return salt
    }
}
