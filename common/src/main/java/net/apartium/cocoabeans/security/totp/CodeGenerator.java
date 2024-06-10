package net.apartium.cocoabeans.security.totp;

import net.apartium.cocoabeans.security.HashingAlgorithm;
import org.jetbrains.annotations.ApiStatus;

import java.security.InvalidParameterException;

@ApiStatus.AvailableSince("0.0.24")
public interface CodeGenerator {

    /**
     * @return The created code generator
     */
    static CodeGenerator create() {
        return new CodeGeneratorImpl();
    }

    /**
     * @param algorithm The hashing algorithm to use
     * @param digits The number of digits to generate (for example 6 means 000000 and 999999)
     * @return The created code generator
     * @throws InvalidParameterException If the algorithm or the number of digits is invalid
     */
    static CodeGenerator create(HashingAlgorithm algorithm, int digits) throws InvalidParameterException {
        return new CodeGeneratorImpl(algorithm, digits);
    }

    /**
     * @param secret The shared secret key to generate the code with
     * @param counter The current time bucket number (Number of seconds since epoch / bucket period)
     * @return The n-digit code for the secret/counter.
     */
    String generate(String secret, long counter) throws Exception;

}
