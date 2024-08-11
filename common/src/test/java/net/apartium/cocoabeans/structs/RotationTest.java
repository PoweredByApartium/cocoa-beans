package net.apartium.cocoabeans.structs;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.apartium.cocoabeans.space.Rotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class RotationTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testConstructorAndGetters() {
        Rotation rotation = new Rotation(45.0, 30.0);
        assertEquals(45.0, rotation.getYaw(), 0.001);
        assertEquals(30.0, rotation.getPitch(), 0.001);
    }

    @Test
    void testCopyConstructor() {
        Rotation original = new Rotation(60.0, -15.0);
        Rotation copy = new Rotation(original);
        assertEquals(original.getYaw(), copy.getYaw(), 0.001);
        assertEquals(original.getPitch(), copy.getPitch(), 0.001);
    }

    @Test
    void testSetters() {
        Rotation rotation = new Rotation(0.0, 0.0);
        rotation.setYaw(90.0).setPitch(45.0);
        assertEquals(90.0, rotation.getYaw(), 0.001);
        assertEquals(45.0, rotation.getPitch(), 0.001);
    }

    @Test
    void testAdd() {
        Rotation rotation1 = new Rotation(30.0, 20.0);
        Rotation rotation2 = new Rotation(15.0, 10.0);
        Rotation result = rotation1.add(rotation2);
        assertEquals(45.0, result.getYaw(), 0.001);
        assertEquals(30.0, result.getPitch(), 0.001);
        assertSame(rotation1, result, "Add should return the same instance");
    }

    @Test
    void testSubtract() {
        Rotation rotation1 = new Rotation(50.0, 40.0);
        Rotation rotation2 = new Rotation(20.0, 15.0);
        Rotation result = rotation1.subtract(rotation2);
        assertEquals(30.0, result.getYaw(), 0.001);
        assertEquals(25.0, result.getPitch(), 0.001);
        assertSame(rotation1, result, "Subtract should return the same instance");
    }

    @Test
    void testMultiply() {
        Rotation rotation = new Rotation(10.0, 5.0);
        Rotation result = rotation.multiply(2.5);
        assertEquals(25.0, result.getYaw(), 0.001);
        assertEquals(12.5, result.getPitch(), 0.001);
        assertSame(rotation, result, "Multiply should return the same instance");
    }

    @Test
    void testNormalize() {
        Rotation rotation1 = new Rotation(380.0, 100.0);
        Rotation result1 = rotation1.normalize();
        assertEquals(20.0, result1.getYaw(), 0.001);
        assertEquals(90.0, result1.getPitch(), 0.001);

        Rotation rotation2 = new Rotation(-30.0, -100.0);
        Rotation result2 = rotation2.normalize();
        assertEquals(330.0, result2.getYaw(), 0.001);
        assertEquals(-90.0, result2.getPitch(), 0.001);

        assertSame(rotation1, result1, "Normalize should return the same instance");
        assertSame(rotation2, result2, "Normalize should return the same instance");
    }

    @Test
    void testDot() {
        Rotation rotation1 = new Rotation(3.0, 4.0);
        Rotation rotation2 = new Rotation(5.0, 6.0);
        assertEquals(39.0, rotation1.dot(rotation2), 0.001);
    }

    @Test
    void testMagnitude() {
        Rotation rotation = new Rotation(3.0, 4.0);
        assertEquals(5.0, rotation.magnitude(), 0.001);
    }

    @Test
    void testToString() {
        Rotation rotation = new Rotation(30.0, 60.0);
        assertEquals("Rotation(yaw=30.0, pitch=60.0)", rotation.toString());
    }

    @Test
    void testJsonSerialization() throws Exception {
        Rotation rotation = new Rotation(30.0, 45.0);
        String json = objectMapper.writeValueAsString(rotation);
        assertEquals("{\"yaw\":30.0,\"pitch\":45.0}", json);
    }

    @Test
    void testJsonDeserialization() throws Exception {
        String json = "{\"yaw\":60.0,\"pitch\":-15.0}";
        Rotation rotation = objectMapper.readValue(json, Rotation.class);
        assertEquals(60.0, rotation.getYaw(), 0.001);
        assertEquals(-15.0, rotation.getPitch(), 0.001);
    }
}