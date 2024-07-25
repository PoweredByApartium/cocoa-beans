package net.apartium.cocoabeans.secuirty.totp;

import net.apartium.cocoabeans.security.totp.SecretGenerator;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SecretGeneratorTest {

    @Test
    public void testGenerate() {
        SecretGenerator secretGenerator = SecretGenerator.create(32);

        assertEquals(32, secretGenerator.generate(new Random()).length());
        assertEquals(32, secretGenerator.generate(ThreadLocalRandom.current()).length());
        assertEquals(32, secretGenerator.generate(ThreadLocalRandom.current()).length());
        assertEquals("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", secretGenerator.generate(new DummyRandom()));
        assertEquals("AAAQAAIAAEAACAABAAAQAAIAAEAACAAB", secretGenerator.generate(new DummyRandom2()));

        Random random = new Random();
        random.setSeed(1359);
        String secret = secretGenerator.generate(random);
        random.setSeed(1359);
        String secret2 = secretGenerator.generate(random);
        assertEquals(secret, secret2);
    }

    private static class DummyRandom extends Random {
        @Override
        public int nextInt() {
            return 0;
        }
    }

    private static class DummyRandom2 extends Random {

        @Override
        public void nextBytes(byte[] bytes) {
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte) (i % 2);
            }
        }
    }


}
