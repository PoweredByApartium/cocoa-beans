package net.apartium.cocoabeans.security.totp;

public class CodeGenerationException extends RuntimeException {

    public CodeGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

}
