package net.apartium.cocoabeans.codec;

import org.apache.commons.codec.binary.Base32;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseConverterTest {

    @Test
    public void test() {
        BaseConverter baseConverter = BaseConverter.base32();
        Base32 base32 = new Base32();

        byte[] data = new byte[10];
        for (int i = 0; i < 128; i++) {
            ThreadLocalRandom.current().nextBytes(data);
            String actual = base32.encodeToString(data);
            assertEquals(actual, baseConverter.encode(data));
            equal(data, baseConverter.decode(actual));
        }

        assertEquals(0, baseConverter.decode("").length);
    }

    public void equal(byte[] a, byte[] b) {
        assertEquals(a.length, b.length);
        for (int i = 0; i < a.length; i++)
            assertEquals(a[i], b[i]);
    }

}
