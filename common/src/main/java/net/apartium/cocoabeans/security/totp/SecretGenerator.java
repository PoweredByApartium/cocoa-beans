package net.apartium.cocoabeans.security.totp;

import net.apartium.cocoabeans.encode.BaseConverter;
import org.jetbrains.annotations.ApiStatus;

import java.util.Random;

@ApiStatus.AvailableSince("0.0.24")
public class SecretGenerator {

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
        return BaseConverter.base32().encode(bytes);
    }

    /**
     * @param numCharacters number of character generated for the secret (mostly it's 32 (160 bit))
     * @return SecretGenerator
     */
    public static SecretGenerator create(int numCharacters) {
        return new SecretGenerator(numCharacters);
    }

}
