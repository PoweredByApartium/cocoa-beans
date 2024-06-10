package net.apartium.cocoabeans.codec;

/**
 * BaseConverter interface for encoding and decoding data
 *
 * @author Thebotgame (Kfir b.)
 */
public interface BaseConverter {

    static BaseConverter base32() {
        return new Base32Converter();
    }

    String encode(byte[] data);
    byte[] decode(String data);

}
