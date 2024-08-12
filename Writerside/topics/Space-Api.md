# 🚀 Space api

## Position

The Position class represents a position in 3D space. It provides various methods for manipulating and querying positions.

```java
Position position = new Position(1.0, 2.0, 3.0);

// Add another position
position.add(new Position(0.5, -1.0, 0.0));
System.out.println(position); 

// Get the distance to another position
Position otherPosition = new Position(2.0, 2.0, 0.0);
double distance = position.distance(otherPosition);
System.out.println(distance); 

// Normalize the position
position.normalize();
System.out.println(position); 
```

## Rotation

Represents a rotation in 3D space using Euler angles (yaw and pitch). This class is suitable for representing orientations where gimbal lock is not a significant concern.

```java
// Create a new rotation with 45 degrees yaw and 30 degrees pitch
Rotation rotation1 = new Rotation(45f, 30f);
System.out.println(rotation1);

float yaw = rotation1.getYaw();
float pitch = rotation1.getPitch();

// Modify the yaw and pitch
rotation1.setYaw(90f);
rotation1.setPitch(-60f);

// Create another rotation
Rotation rotation2 = new Rotation(20f, 10f);

// Add the rotations
rotation1.add(rotation2);

// Normalize the rotation values
rotation1.normalize();
```

## Transform
The Transform class represents a transform in 3D space, combining position and rotation. It is part of the net.apartium.cocoabeans.space package.

```java
// Create a new transform
Transform transform = new Transform(10.0, 20.0, 30.0, 45.0f, 30.0f);

// Modify the transform
transform.setX(15.0).setYaw(60.0f);

// Translate and rotate
Position translation = new Position(5.0, 5.0, 5.0);
Rotation rotation = new Rotation(10.0f, 5.0f);
transform.translate(translation).rotate(rotation);

// Get direction vector
Position direction = transform.getDirectionVector();
```

## BoxRegion

The `BoxRegion` class is part of the `net.apartium.cocoabeans.space` package. It implements the `Region` interface and represents a three-dimensional rectangular region in space.

### Usage Example

```java
// Create a BoxRegion
BoxRegion region = new BoxRegion(0, 10, 0, 5, 0, 8);

// Check if a position is inside the region
Position pos = new Position(5, 3, 4);
boolean isInside = region.contains(pos);

// Calculate distance to a point
Position outsidePos = new Position(15, 15, 15);
double distance = region.distance(outsidePos);

// Modify the region
region.setX(1, 11);
region.setY(1, 6);
region.setZ(1, 9);
```