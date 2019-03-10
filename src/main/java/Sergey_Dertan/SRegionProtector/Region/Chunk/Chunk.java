package Sergey_Dertan.SRegionProtector.Region.Chunk;

import Sergey_Dertan.SRegionProtector.Region.Region;
import it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

public final class Chunk {

    public final Object lock = new Object();
    public final long x;
    public final long z;
    private final SortedSet<Region> regions;

    public Chunk(long x, long z) {
        this.x = x;
        this.z = z;
        this.regions = new ObjectAVLTreeSet<>((r, r2) -> r2.getPriority() - r.getPriority());
    }

    /**
     * do not modify this without synchronization on
     *
     * @see Chunk#lock
     * @see Chunk#addRegion(Region)
     * @see Chunk#removeRegion(Region)
     */
    public Set<Region> getRegions() {
        return this.regions;
    }

    public int getRegionsAmount() {
        return this.regions.size();
    }

    public long getX() {
        return this.x;
    }

    public long getZ() {
        return this.z;
    }

    public void addRegion(Region region) {
        synchronized (this.lock) {
            this.regions.add(region);
        }
    }

    public void removeRegion(Region region) {
        synchronized (this.lock) {
            this.regions.remove(region);
        }
    }

    public void updatePriorities() {
        synchronized (this.lock) {
            List<Region> list = new ObjectArrayList<>(this.regions);
            this.regions.clear();
            this.regions.addAll(list);
        }
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
}
