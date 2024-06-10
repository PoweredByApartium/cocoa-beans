package net.apartium.cocoabeans.security;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.24")
public enum HashingAlgorithm {

    SHA1("HmacSHA1", "SHA1"),
    SHA256("HmacSHA256", "SHA256"),
    SHA512("HmacSHA512", "SHA512");

    private final String hmacAlgorithm;
    private final String friendlyName;

    HashingAlgorithm(String hmacAlgorithm, String friendlyName) {
        this.hmacAlgorithm = hmacAlgorithm;
        this.friendlyName = friendlyName;
    }

    public String getHmacAlgorithm() {
        return hmacAlgorithm;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public static HashingAlgorithm of(String friendlyName) {
        for (HashingAlgorithm algorithm : HashingAlgorithm.values()) {
            if (algorithm.getFriendlyName().equalsIgnoreCase(friendlyName)) {
                return algorithm;
            }
        }
        return null;
    }

}
