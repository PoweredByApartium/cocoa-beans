package net.apartium.cocoabeans.security.totp;

import net.apartium.cocoabeans.utils.TimeProvider;

/* package-private */ class CodeVerifierImpl implements CodeVerifier {

    private final CodeGenerator codeGenerator;
    private final TimeProvider timeProvider;
    private final int timePeriod;
    private final int allowedTimePeriodDiscrepancy;

    /* package-private */ CodeVerifierImpl(CodeGenerator codeGenerator, TimeProvider timeProvider) {
        this(codeGenerator, timeProvider, 30, 1);
    }

    /* package-private */ CodeVerifierImpl(CodeGenerator codeGenerator, TimeProvider timeProvider, int timePeriod, int allowedTimePeriodDiscrepancy) {
        this.codeGenerator = codeGenerator;
        this.timeProvider = timeProvider;
        this.timePeriod = timePeriod;
        this.allowedTimePeriodDiscrepancy = allowedTimePeriodDiscrepancy;
    }

    @Override
    public boolean isValidCode(String secret, String attemptCode) {
        long currentBucket = Math.floorDiv(timeProvider.getTime(), timePeriod);

        // to avoid timing attack we will check all time periods in allowedTimePeriodDiscrepancy (even if it found)
        boolean found = false;
        for (int i = -allowedTimePeriodDiscrepancy; i <= allowedTimePeriodDiscrepancy; i++) {
            found = checkCode(secret, attemptCode, currentBucket + i) || found;
        }
        return found;
    }

    private boolean checkCode(String secret, String attemptCode, long counter) {
        try {
            return equal(codeGenerator.generate(secret, counter), attemptCode);
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Compare two strings for equality without leaking timing information
     * @param a a
     * @param b b
     * @return true if equal else false
     */
    private boolean equal(String a, String b) {
        if (a == null || b == null)
            return false;

        byte[] aBytes = a.getBytes();
        byte[] bBytes = b.getBytes();

        if (aBytes.length != bBytes.length)
            return false;


        int result = 0;
        for (int i = 0; i < aBytes.length; i++)
            result |= aBytes[i] ^ bBytes[i];


        return result == 0;
    }
}
