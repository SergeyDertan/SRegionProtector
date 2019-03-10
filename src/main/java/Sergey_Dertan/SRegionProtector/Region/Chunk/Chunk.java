package Sergey_Dertan.SRegionProtector.Region.Chunk;

import Sergey_Dertan.SRegionProtector.Region.Region;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.Comparator;
import java.util.List;

public final class Chunk {

    public final Object lock = new Object();
    public final long x;
    public final long z;
    private static final Comparator<Region> regionComparator = (r, r2) -> r2.getPriority() - r.getPriority();
    private final List<Region> regions;

    public Chunk(long x, long z) {
        this.x = x;
        this.z = z;
        this.regions = new ObjectArrayList<Region>() {
            @Override
            public boolean add(Region region) {
                boolean result = super.add(region);
                this.sort(regionComparator);
                return result;
            }
        };
    }

    /**
     * do not modify this without synchronization on the lock
     *
     * @see Chunk#lock
     * @see Chunk#addRegion(Region)
     * @see Chunk#removeRegion(Region)
     */
    public List<Region> getRegions() {
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
            this.regions.sort(regionComparator);
        }
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
}
