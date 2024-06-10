package net.apartium.cocoabeans.secuirty.totp;

import net.apartium.cocoabeans.security.totp.CodeGenerator;
import net.apartium.cocoabeans.security.totp.CodeVerifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CodeVerifierTest {

    private final String secret = "IWPMXJ7WLAT2ABCJTUVKCAOY5WFL73DF";

    @Test
    public void testIsValidCode() throws Exception {

        CodeVerifier codeVerifier = CodeVerifier.create(CodeGenerator.create(), () -> 612);
        assertTrue(codeVerifier.isValidCode(secret, CodeGenerator.create().generate(secret, 19)));
        assertTrue(codeVerifier.isValidCode(secret, CodeGenerator.create().generate(secret, 20))); // 364455
        assertTrue(codeVerifier.isValidCode(secret, CodeGenerator.create().generate(secret, 21)));
        assertFalse(codeVerifier.isValidCode(secret, CodeGenerator.create().generate(secret, 22)));
        assertTrue(codeVerifier.isValidCode(secret, "364455"));
        assertFalse(codeVerifier.isValidCode(secret, "214435"));
        assertFalse(codeVerifier.isValidCode(secret, "asd4"));

        codeVerifier = CodeVerifier.create(CodeGenerator.create(), () -> 612, 30, 0);
        assertFalse(codeVerifier.isValidCode(secret, CodeGenerator.create().generate(secret, 19)));
        assertTrue(codeVerifier.isValidCode(secret, CodeGenerator.create().generate(secret, 20))); // 364455
        assertFalse(codeVerifier.isValidCode(secret, CodeGenerator.create().generate(secret, 21)));
    }

}
