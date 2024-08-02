package dev.thorinwasher.blockanimator.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.transform.BlockTransformExtent;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.Transform;
import com.sk89q.worldedit.util.SideEffect;
import com.sk89q.worldedit.util.SideEffectSet;
import com.sk89q.worldedit.world.block.BaseBlock;
import dev.thorinwasher.blockanimator.api.supplier.BlockSupplier;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

import java.util.ArrayList;
import java.util.List;

public class PaperClipboardBlockSupplierV7_2_20 implements BlockSupplier<BlockData> {

    private final Clipboard clipboard;
    private final Vector3D origin;
    private final com.sk89q.worldedit.world.World world;
    private final Transform transform;
    private final Transform transformInverse;

    public PaperClipboardBlockSupplierV7_2_20(Clipboard clipboard, Vector3D origin, World world, Transform transform) throws WorldEditException {
        this.clipboard = clipboard;
        this.origin = new Vector3D(Math.round(origin.getX()), Math.round(origin.getY()), Math.round(origin.getZ()));
        this.world = BukkitAdapter.adapt(world);
        this.transform = transform;
        this.transformInverse = transform.inverse();
    }

    @Override
    public BlockData getBlock(Vector3D targetPosition) {
        BlockVector3 relativePosition = WEVectorConverterV7_2_20.toBlockVector3(targetPosition.subtract(origin));
        return BukkitAdapter.adapt(getBlock(applyTransformInverse(relativePosition).add(clipboard.getOrigin())));
    }

    @Override
    public List<Vector3D> getPositions() {
        List<BlockVector3> output = new ArrayList<>();
        clipboard.getRegion().forEach(output::add);
        return new ArrayList<>(output.stream().map(blockVector3 -> blockVector3.subtract(clipboard.getOrigin()))
                .map(this::applyTransform)
                .map(WEVectorConverterV7_2_20::toVector3D)
                .map(origin::add).toList());
    }

    @Override
    public void placeBlock(Vector3D identifier) {
        BlockVector3 relativeWorldCoordinate = applyTransformInverse(WEVectorConverterV7_2_20.toBlockVector3(identifier.subtract(origin)));
        BlockVector3 regionPosition = relativeWorldCoordinate.add(clipboard.getOrigin());
        BaseBlock baseBlock = getBlock(regionPosition);

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            editSession.setSideEffectApplier(SideEffectSet.none().with(SideEffect.LIGHTING, SideEffect.State.ON));
            editSession.setBlock(WEVectorConverterV7_2_20.toBlockVector3(identifier), baseBlock);
            Operations.complete(editSession.commit());
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
    }

    private BaseBlock getBlock(BlockVector3 regionPosition) {
        return BlockTransformExtent.transform(clipboard.getFullBlock(regionPosition), transform);
    }

    private BlockVector3 applyTransform(BlockVector3 blockVector3) {
        return transform.apply(blockVector3.toVector3()).toBlockPoint();
    }

    private BlockVector3 applyTransformInverse(BlockVector3 blockVector3) {
        return transformInverse.apply(blockVector3.toVector3()).toBlockPoint();
    }
}
