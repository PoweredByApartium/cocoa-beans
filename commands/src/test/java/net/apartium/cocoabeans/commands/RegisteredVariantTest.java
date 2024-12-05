package net.apartium.cocoabeans.commands;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class RegisteredVariantTest {

    @Test
    void equalsTest() {
        assertEquals(new RegisteredVariant(
                null,
                new RegisteredVariant.Parameter[0],
                null,
                List.of(),
                0
        ), new RegisteredVariant(
                null,
                new RegisteredVariant.Parameter[0],
                null,
                List.of(),
                0
        ));

        assertNotEquals(new RegisteredVariant(
                null,
                new RegisteredVariant.Parameter[0],
                null,
                List.of(),
                6
        ), new RegisteredVariant(
                null,
                new RegisteredVariant.Parameter[0],
                null,
                List.of(),
                0
        ));
    }

}
