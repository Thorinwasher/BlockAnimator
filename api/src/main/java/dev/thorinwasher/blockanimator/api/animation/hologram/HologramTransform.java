package dev.thorinwasher.blockanimator.api.animation.hologram;

import org.joml.Matrix4d;

public interface HologramTransform {

    Matrix4d getTransform(long ticksFromStart);
}
