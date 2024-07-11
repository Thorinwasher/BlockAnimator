package dev.thorinwasher.blockanimator.minestomtest;

import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.Generator;
import org.jetbrains.annotations.NotNull;

public class FlatWorldGenerator implements Generator {
    @Override
    public void generate(@NotNull GenerationUnit unit) {
        unit.modifier().fillHeight(-64, -63, Block.GRASS_BLOCK);
    }
}
