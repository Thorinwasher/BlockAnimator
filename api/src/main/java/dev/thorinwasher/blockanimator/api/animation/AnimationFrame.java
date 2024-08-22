package dev.thorinwasher.blockanimator.api.animation;

import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;

import java.util.Map;

public record AnimationFrame(Map<ImmutableVector3i, BlockAnimationFrame> currentToDestination) {
}
