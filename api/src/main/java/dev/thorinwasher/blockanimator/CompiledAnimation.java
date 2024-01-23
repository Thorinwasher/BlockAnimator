package dev.thorinwasher.blockanimator;

import dev.thorinwasher.blockanimator.supplier.BlockSupplier;

import java.util.List;

public record CompiledAnimation<B>(List<AnimationFrame> frames, BlockSupplier<B> supplier) {
}
