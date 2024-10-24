package dev.thorinwasher.blockanimator.minestomtest.command;

import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.minestom.SchemBlockSupplier;
import dev.thorinwasher.blockanimator.minestomtest.Registry;
import dev.thorinwasher.blockanimator.minestomtest.TestSupplier;
import net.hollowcube.schem.Rotation;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

public class BlockSupplierUtil {

    public static BlockSupplier<Block> getBlockSupplier(Player player, int size, Instance instance) {
        Point corner = player.getPosition().add(2, 0, 0);
        if (Registry.playerSchematicRegistry.containsKey(player.getUuid())) {
            return new SchemBlockSupplier(Registry.playerSchematicRegistry.get(player.getUuid()), Rotation.NONE, corner, instance);
        }
        return new TestSupplier(Block.DIAMOND_BLOCK, size, corner, instance);
    }
}
