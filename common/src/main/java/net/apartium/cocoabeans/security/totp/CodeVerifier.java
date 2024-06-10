package net.apartium.cocoabeans.security.totp;

import net.apartium.cocoabeans.utils.TimeProvider;
import org.jetbrains.annotations.ApiStatus;

/**
 * Use this interface to verify if the given totp code is valid (it's safe against timing attacks)
 * @see <a href="https://en.wikipedia.org/wiki/Timing_attack">Timing Attack</a>
 */
@ApiStatus.AvailableSince("0.0.24")
public interface CodeVerifier {

    /**
     * @param codeGenerator which code generator to use
     * @param timeProvider which time provider to use
     * @return the created code verifier
     */
    static CodeVerifier create(CodeGenerator codeGenerator, TimeProvider timeProvider) {
        return new CodeVerifierImpl(codeGenerator, timeProvider);
    }

    /**
     * @param codeGenerator which code generator to use
     * @param timeProvider which time provider to use
     * @param timePeriod the time period in seconds (default is 30 sec)
     * @param allowedTimePeriodDiscrepancy the allowed time period discrepancy in seconds (default is 1)
     *                                     Note if it's 1 it will check 30 seconds before and ahead of the current time)
     * @return the created code verifier
     */
    static CodeVerifier create(CodeGenerator codeGenerator, TimeProvider timeProvider, int timePeriod, int allowedTimePeriodDiscrepancy) {
        return new CodeVerifierImpl(codeGenerator, timeProvider, timePeriod, allowedTimePeriodDiscrepancy);
    }

    /**
     * Use this method to check if the given code is valid (it's safe against timing attacks)
     * @param secret  The shared secret key to generate the code with
     * @param attemptCode The n-digit code given by the user
     * @return True if the code is valid, else false
     */
    boolean isValidCode(String secret, String attemptCode);

}
