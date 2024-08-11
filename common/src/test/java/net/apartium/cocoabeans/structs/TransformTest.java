package net.apartium.cocoabeans.structs;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.Rotation;
import net.apartium.cocoabeans.space.Transform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransformTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testConstructorAndGetters() {
        Position position = new Position(1, 2, 3);
        Rotation rotation = new Rotation(45, 30);
        Transform transform = new Transform(position, rotation);

        assertEquals(position, transform.getPosition());
        assertEquals(rotation, transform.getRotation());
    }

    @Test
    void testConstructorWithCoordinates() {
        Transform transform = new Transform(1, 2, 3, 45, 30);

        assertEquals(1, transform.getPosition().getX(), 0.001);
        assertEquals(2, transform.getPosition().getY(), 0.001);
        assertEquals(3, transform.getPosition().getZ(), 0.001);
        assertEquals(45, transform.getRotation().getYaw(), 0.001);
        assertEquals(30, transform.getRotation().getPitch(), 0.001);
    }

    @Test
    void testSetters() {
        Transform transform = new Transform(0, 0, 0, 0, 0);
        Position newPosition = new Position(1, 2, 3);
        Rotation newRotation = new Rotation(45, 30);

        transform.setPosition(newPosition).setRotation(newRotation);

        assertEquals(newPosition, transform.getPosition());
        assertEquals(newRotation, transform.getRotation());
    }

    @Test
    void testTranslate() {
        Transform transform = new Transform(1, 1, 1, 0, 0);
        Position translation = new Position(2, 3, 4);

        transform.translate(translation);

        assertEquals(3, transform.getPosition().getX(), 0.001);
        assertEquals(4, transform.getPosition().getY(), 0.001);
        assertEquals(5, transform.getPosition().getZ(), 0.001);
    }

    @Test
    void testRotate() {
        Transform transform = new Transform(0, 0, 0, 10, 20);
        Rotation deltaRotation = new Rotation(5, 10);

        transform.rotate(deltaRotation);

        assertEquals(15, transform.getRotation().getYaw(), 0.001);
        assertEquals(30, transform.getRotation().getPitch(), 0.001);
    }

    @Test
    void testGetDirectionVector() {
        Transform transform = new Transform(0, 0, 0, 90, 0);
        Position directionVector = transform.getDirectionVector();

        assertEquals(1, directionVector.getX(), 0.001);
        assertEquals(0, directionVector.getY(), 0.001);
        assertEquals(0, directionVector.getZ(), 0.001);
    }

    @Test
    void testToString() {
        Transform transform = new Transform(1, 2, 3, 45, 30);
        String expected = "Transform(position=Position(x=1.0, y=2.0, z=3.0), rotation=Rotation(yaw=45.0, pitch=30.0))";
        assertEquals(expected, transform.toString());
    }

    @Test
    void testJsonSerialization() throws Exception {
        Transform transform = new Transform(1, 2, 3, 45, 30);
        String json = objectMapper.writeValueAsString(transform);
        String expected = "{\"position\":{\"x\":1.0,\"y\":2.0,\"z\":3.0},\"rotation\":{\"yaw\":45.0,\"pitch\":30.0}}";
        assertEquals(expected, json);
    }

    @Test
    void testJsonDeserialization() throws Exception {
        String json = "{\"x\":1.0,\"y\":2.0,\"z\":3.0,\"yaw\":45.0,\"pitch\":30.0}";
        Transform transform = objectMapper.readValue(json, Transform.class);

        assertEquals(1, transform.getPosition().getX(), 0.001);
        assertEquals(2, transform.getPosition().getY(), 0.001);
        assertEquals(3, transform.getPosition().getZ(), 0.001);
        assertEquals(45, transform.getRotation().getYaw(), 0.001);
        assertEquals(30, transform.getRotation().getPitch(), 0.001);
    }

    @Test
    void testJsonSerializationDeserialization() throws Exception {
        Transform original = new Transform(1, 2, 3, 45, 30);
        String json = objectMapper.writeValueAsString(original);
        Transform deserialized = objectMapper.readValue(json, Transform.class);

        assertEquals(original.getPosition().getX(), deserialized.getPosition().getX(), 0.001);
        assertEquals(original.getPosition().getY(), deserialized.getPosition().getY(), 0.001);
        assertEquals(original.getPosition().getZ(), deserialized.getPosition().getZ(), 0.001);
        assertEquals(original.getRotation().getYaw(), deserialized.getRotation().getYaw(), 0.001);
        assertEquals(original.getRotation().getPitch(), deserialized.getRotation().getPitch(), 0.001);
    }
}