package dev.thorinwasher.blockanimator.minestomtest.command;

import dev.thorinwasher.blockanimator.minestom.SchemBlockSupplier;
import dev.thorinwasher.blockanimator.minestomtest.Registry;
import dev.thorinwasher.blockanimator.minestomtest.TestSupplier;
import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import net.hollowcube.schem.Rotation;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;

public class BlockSupplierUtil {

    public static BlockSupplier<Block> getBlockSupplier(Player player, int size) {
        Point corner = player.getPosition().add(20, 0, 0);
        if (Registry.playerSchematicRegistry.containsKey(player.getUuid())) {
            return new SchemBlockSupplier(Registry.playerSchematicRegistry.get(player.getUuid()), Rotation.NONE, corner);
        }
        return new TestSupplier(Block.DIAMOND_BLOCK, size, corner);
    }
}
