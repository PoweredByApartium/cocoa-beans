package net.apartium.cocoabeans.secuirty.totp;

import net.apartium.cocoabeans.security.HashingAlgorithm;
import net.apartium.cocoabeans.security.totp.CodeGenerator;
import org.junit.jupiter.api.Test;

import java.security.InvalidParameterException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CodeGeneratorTest {

    @Test
    public void testGenerate() throws Exception {
        assertEquals(13, CodeGenerator.create(HashingAlgorithm.SHA256, 13).generate("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", 3).length());
        assertThrows(IllegalArgumentException.class, () -> CodeGenerator.create().generate("{ASD%R@#", 3));
        assertThrows(IllegalArgumentException.class, () -> CodeGenerator.create().generate("asd", 3));
        assertThrows(InvalidParameterException.class, () -> CodeGenerator.create(null, 4));
        assertThrows(InvalidParameterException.class, () -> CodeGenerator.create(HashingAlgorithm.SHA1, 0));
        assertThrows(InvalidParameterException.class, () -> CodeGenerator.create(HashingAlgorithm.SHA1, -3));
    }

}
