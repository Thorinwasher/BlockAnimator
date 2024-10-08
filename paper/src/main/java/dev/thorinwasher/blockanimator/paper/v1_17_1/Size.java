package dev.thorinwasher.blockanimator.paper.v1_17_1;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public enum Size {
    LARGE,
    MEDIUM,
    SMALL,
    NOTHING;

    static Size fromFloat(float size) {
        if (size >= 0.75f) {
            return LARGE;
        }
        if (size >= 0.5f) {
            return MEDIUM;
        }
        if (size >= 0.25) {
            return SMALL;
        }
        return NOTHING;
    }
}