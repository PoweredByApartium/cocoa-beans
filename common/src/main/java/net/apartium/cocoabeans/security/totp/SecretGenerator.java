package net.apartium.cocoabeans.security.totp;

import org.apache.commons.codec.binary.Base32;

import java.util.Random;

public class SecretGenerator {

    /**
     * @param numCharacters number of character generated for the secret (mostly it's 32 (160 bit))
     * @return SecretGenerator
     */
    public static SecretGenerator create(int numCharacters) {
        return new SecretGenerator(numCharacters);
    }

    private final int numCharacters;

    private SecretGenerator(int numCharacters) {
        this.numCharacters = numCharacters;
    }

    /**
     * @param random random (Recommended: {@link java.security.SecureRandom} or any other cryptographically secure random)
     * @return Base32 encoded secret
     */
    public String generate(Random random) {
        // 5 bits per char in base32
        byte[] bytes = new byte[(numCharacters * 5) / 8];

        random.nextBytes(bytes);
        return new Base32().encodeToString(bytes);
    }

}
