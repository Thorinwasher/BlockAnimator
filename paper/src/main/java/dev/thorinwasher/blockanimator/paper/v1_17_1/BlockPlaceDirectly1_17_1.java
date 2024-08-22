package dev.thorinwasher.blockanimator.paper.v1_17_1;

import dev.thorinwasher.blockanimator.api.animator.BlockAnimator;
import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import dev.thorinwasher.blockanimator.paper.VectorConverter;
import org.joml.Vector3d;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

import java.util.HashMap;
import java.util.Map;

public class BlockPlaceDirectly1_17_1 implements BlockAnimator<BlockData> {

    private final World world;
    private final Map<ImmutableVector3i, BlockDisplayEquivalent> armorStands = new HashMap<>();

    public BlockPlaceDirectly1_17_1(World world) {
        this.world = world;
    }

    @Override
    public void blockMove(ImmutableVector3i identifier, Vector3d to, BlockSupplier<BlockData> blockSupplier) {
        BlockDisplayEquivalent blockEquivalent = spawnOrGetFallingBlock(identifier, to, blockSupplier);
        blockEquivalent.move(to);
    }

    @Override
    public void blockPlace(ImmutableVector3i identifier, BlockSupplier<BlockData> blockSupplier) {
        BlockDisplayEquivalent blockEquivalent = armorStands.remove(identifier);
        if (blockEquivalent != null) {
            blockEquivalent.remove();
        }
        blockSupplier.placeBlock(identifier);
    }

    @Override
    public void blockDestroy(ImmutableVector3i identifier) {
        world.getBlockAt(VectorConverter.toLocation(identifier, world)).setType(Material.AIR);
    }

    @Override
    public void finishAnimation(BlockSupplier<BlockData> blockSupplier) {
        // All blocks are already placed directly, nothing more needs to be done
    }

    private BlockDisplayEquivalent spawnOrGetFallingBlock(ImmutableVector3i identifier, Vector3d position, BlockSupplier<BlockData> blockSupplier) {
        BlockDisplayEquivalent blockEquivalent = armorStands.get(identifier);
        if (blockEquivalent == null) {
            blockEquivalent = new BlockDisplayEquivalent(blockSupplier.getBlock(identifier), position, world, 0.25F);
            blockEquivalent.spawn();
            armorStands.put(identifier, blockEquivalent);
        }
        return blockEquivalent;
    }
}
