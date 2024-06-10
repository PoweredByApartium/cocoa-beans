package net.apartium.cocoabeans.security.totp;

import net.apartium.cocoabeans.codec.BaseConverter;
import net.apartium.cocoabeans.security.HashingAlgorithm;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;

/* package-private */ class CodeGeneratorImpl implements CodeGenerator {

    private final HashingAlgorithm algorithm;
    private final int digits;

    /* package-private */ CodeGeneratorImpl() {
        this(HashingAlgorithm.SHA1, 6);
    }

    /* package-private */ CodeGeneratorImpl(HashingAlgorithm algorithm, int digits) throws InvalidParameterException {
        if (algorithm == null)
            throw new InvalidParameterException("HashingAlgorithm must not be null.");

        if (digits < 1)
            throw new InvalidParameterException("Number of digits must be larger than 0.");


        this.algorithm = algorithm;
        this.digits = digits;
    }

    @Override
    public String generate(String secret, long counter) throws Exception {
        byte[] hash = generateHash(secret, counter);
        return getDigitsFromHash(hash);
    }

    private byte[] generateHash(String secret, long counter) throws InvalidKeyException, NoSuchAlgorithmException {
        byte[] data = new byte[8];
        long value = counter;
        for (int i = 8; i-- > 0; value >>>= 8) {
            data[i] = (byte) value;
        }

        // Create a HMAC-SHA1 signing key from the shared key
        byte[] decodedKey = BaseConverter.base32().decode(secret);
        SecretKeySpec signKey = new SecretKeySpec(decodedKey, algorithm.getHmacAlgorithm());
        Mac mac = Mac.getInstance(algorithm.getHmacAlgorithm());
        mac.init(signKey);

        return mac.doFinal(data);
    }

    private String getDigitsFromHash(byte[] hash) {
        int offset = hash[hash.length - 1] & 0xF;

        long truncatedHash = 0;

        for (int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            truncatedHash |= (hash[offset + i] & 0xFF);
        }

        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= (long) Math.pow(10, digits);

        return String.format("%0" + digits + "d", truncatedHash);
    }

}
