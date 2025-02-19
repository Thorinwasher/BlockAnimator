package dev.thorinwasher.blockanimator.worldedit;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.transform.BlockTransformExtent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.util.SideEffect;
import com.sk89q.worldedit.util.SideEffectSet;
import com.sk89q.worldedit.world.block.BaseBlock;
import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import dev.thorinwasher.blockanimator.api.supplier.ImmutableVector3i;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class PaperClipboardBlockSupplier implements BlockSupplier<BlockData> {

    private final Clipboard clipboard;
    private final ImmutableVector3i origin;
    private final com.sk89q.worldedit.world.World world;
    private final Transform transform;
    private final Transform transformInverse;
    private static final SideEffectSet SIDE_EFFECT_SET = SideEffectSet.none()
            .with(SideEffect.NETWORK, SideEffect.State.ON)
            .with(SideEffect.LIGHTING, SideEffect.State.ON);

    public PaperClipboardBlockSupplier(Clipboard clipboard, Location origin, Transform transform) throws WorldEditException {
        this.clipboard = clipboard;
        this.origin = new ImmutableVector3i(origin.getBlockX(), origin.getBlockY(), origin.getBlockZ());
        this.world = BukkitAdapter.adapt(origin.getWorld());
        this.transform = transform;
        this.transformInverse = transform.inverse();
    }

    @Override
    public BlockData getBlock(ImmutableVector3i targetPosition) {
        BlockVector3 relativePosition = WEVectorConverter.toBlockVector3(targetPosition.sub(origin));
        return BukkitAdapter.adapt(getBlock(applyTransformInverse(relativePosition).add(clipboard.getOrigin())));
    }

    @Override
    public List<ImmutableVector3i> getPositions() {
        List<ImmutableVector3i> output = new ArrayList<>();
        for (BlockVector3 blockVector3 : clipboard.getRegion()) {
            BlockVector3 relativePosition = applyTransform(blockVector3.subtract(clipboard.getOrigin()));
            output.add(WEVectorConverter.toImmutableVector3i(relativePosition).add(origin));
        }
        return output;
    }

    @Override
    public void placeBlock(ImmutableVector3i identifier) {
        BlockVector3 relativeWorldCoordinate = applyTransformInverse(WEVectorConverter.toBlockVector3(identifier.sub(origin)));
        BlockVector3 regionPosition = relativeWorldCoordinate.add(clipboard.getOrigin());
        BaseBlock baseBlock = getBlock(regionPosition);
        try {
            world.setBlock(WEVectorConverter.toBlockVector3(identifier), baseBlock, SIDE_EFFECT_SET);
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void placeBlocks(List<ImmutableVector3i> identifiers) {
        try {
            for (ImmutableVector3i identifier : identifiers) {
                BlockVector3 relativeWorldCoordinate = applyTransformInverse(WEVectorConverter.toBlockVector3(identifier.sub(origin)));
                BlockVector3 regionPosition = relativeWorldCoordinate.add(clipboard.getOrigin());
                BaseBlock baseBlock = getBlock(regionPosition);
                world.setBlock(WEVectorConverter.toBlockVector3(identifier), baseBlock, SIDE_EFFECT_SET);
            }
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
    }

    private BaseBlock getBlock(BlockVector3 regionPosition) {
        BaseBlock baseBlock = clipboard.getFullBlock(regionPosition);
        return BlockTransformExtent.transform(baseBlock, transform);
    }

    private BlockVector3 applyTransform(BlockVector3 blockVector3) {
        return transform.apply(blockVector3.toVector3()).toBlockPoint();
    }

    private BlockVector3 applyTransformInverse(BlockVector3 blockVector3) {
        return transformInverse.apply(blockVector3.toVector3()).toBlockPoint();
    }
}
