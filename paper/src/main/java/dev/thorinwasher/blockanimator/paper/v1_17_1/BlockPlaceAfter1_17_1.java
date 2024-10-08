package dev.thorinwasher.blockanimator.paper.v1_17_1;

import dev.thorinwasher.blockanimator.api.animator.BlockAnimator;
import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockPlaceAfter1_17_1 implements BlockAnimator<BlockData> {
    private final World world;
    private final int maxEntities;
    private final Map<ImmutableVector3i, BlockDisplayEquivalent> fallingBlocks = new HashMap<>();
    private final List<ImmutableVector3i> blocksToRemove = new ArrayList<>();
    private final ArmorStandPool pool;

    public BlockPlaceAfter1_17_1(World world, int maxEntities) {
        this.world = world;
        this.pool = new ArmorStandPool(world);
        this.maxEntities = maxEntities;
    }

    @Override
    public void blockMove(ImmutableVector3i identifier, Vector3d to, BlockSupplier<BlockData> blockSupplier, Matrix4f transform) {
        BlockDisplayEquivalent blockDisplayEquivalent = spawnOrGetFallingBlock(identifier, to, blockSupplier);
        Vector3f scale = new Vector3f();
        transform.getScale(scale);
        blockDisplayEquivalent.move(to, scale.get(scale.maxComponent()));
    }

    @Override
    public void blockPlace(ImmutableVector3i identifier, BlockSupplier<BlockData> blockSupplier) {
        BlockDisplayEquivalent fallingBlock = fallingBlocks.get(identifier);
        fallingBlock.move(identifier.asVector3d().add(0.5, 0, 0.5), 1f);
        fallingBlock.freeze();
        blocksToRemove.add(identifier);
        if (blocksToRemove.size() > maxEntities) {
            finishAnimation(blockSupplier);
        }
    }

    @Override
    public void blockDestroy(ImmutableVector3i identifier) {
        world.getBlockAt(identifier.x(), identifier.y(), identifier.z()).setType(Material.AIR);
    }

    @Override
    public void finishAnimation(BlockSupplier<BlockData> blockSupplier) {
        blocksToRemove.forEach(identifier -> {
            BlockDisplayEquivalent fallingBlock = fallingBlocks.remove(identifier);
            fallingBlock.remove();
        });
        blockSupplier.placeBlocks(blocksToRemove);
        blocksToRemove.clear();
        pool.clean();
    }

    @Override
    public void tick() {
        pool.tick();

        for (BlockDisplayEquivalent blockDisplayEquivalent : this.fallingBlocks.values()) {
            blockDisplayEquivalent.tick();
        }
    }

    private BlockDisplayEquivalent spawnOrGetFallingBlock(ImmutableVector3i identifier, Vector3d position, BlockSupplier<BlockData> blockSupplier) {
        BlockDisplayEquivalent blockEquivalent = fallingBlocks.get(identifier);
        if (blockEquivalent == null) {
            blockEquivalent = new BlockDisplayEquivalent(blockSupplier.getBlock(identifier), position, world, 0.25F, pool);
            fallingBlocks.put(identifier, blockEquivalent);
        }
        return blockEquivalent;
    }
}
