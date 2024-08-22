package dev.thorinwasher.blockanimator.api.util;

import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import org.joml.Vector3d;

import java.util.List;

public class Directions {
    public static final List<ImmutableVector3i> DIRECTIONS = getDirections();

    private static List<ImmutableVector3i> getDirections() {
        return List.of(new ImmutableVector3i(1, 0, 0),
                new ImmutableVector3i(-1, 0, 0),
                new ImmutableVector3i(0, 1, 0),
                new ImmutableVector3i(0, -1, 0),
                new ImmutableVector3i(0, 0, 1),
                new ImmutableVector3i(0, 0, -1));
    }
}
