package dev.thorinwasher.blockanimator.api.supplier;

import org.joml.Vector3d;

public record ImmutableVector3i(int x, int y, int z) {

    public Vector3d asVector3d() {
        return new Vector3d(x, y, z);
    }

    public ImmutableVector3i add(ImmutableVector3i vector3i) {
        return this.add(vector3i.x, vector3i.y, vector3i.z);
    }

    public ImmutableVector3i add(int x, int y, int z) {
        return new ImmutableVector3i(this.x + x, this.y + y, this.z + z);
    }

    public ImmutableVector3i sub(int x, int y, int z) {
        return new ImmutableVector3i(this.x - x, this.y - y, this.z - z);
    }

    public ImmutableVector3i sub(ImmutableVector3i vector3i) {
        return this.sub(vector3i.x, vector3i.y, vector3i.z);
    }

    public ImmutableVector3i mul(int scalar) {
        return new ImmutableVector3i(this.x * scalar, this.y * scalar, this.z * scalar);
    }
}
