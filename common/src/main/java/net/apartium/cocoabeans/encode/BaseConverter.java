package net.apartium.cocoabeans.encode;

import org.jetbrains.annotations.ApiStatus;

/**
 * BaseConverter interface for encoding and decoding data
 *
 * @author Thebotgame (Kfir b.)
 */
@ApiStatus.AvailableSince("0.0.25")
public interface BaseConverter {

    /**
     * Returns a Base 32 alphabet converter
     * [A-Z, 2-7, = for padding]
     * @see <a href="https://en.wikipedia.org/wiki/Base32">Base32</a>
     * @return Base32 converter instance
     */
    static BaseConverter base32() {
        return Base32Converter.INSTANCE;
    }

    /**
     * Encode data to string
     * @param data data to encode
     * @return encoded data
     */
    String encode(byte[] data);

    /**
     * Decodes string to data
     * @param data data to decode
     * @return decoded data
     */
    byte[] decode(String data);

}
