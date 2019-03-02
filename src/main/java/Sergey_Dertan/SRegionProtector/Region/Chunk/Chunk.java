package Sergey_Dertan.SRegionProtector.Region.Chunk;

import Sergey_Dertan.SRegionProtector.Region.Region;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;

import java.util.Set;

public final class Chunk {

    public final Object lock = new Object();
    public final long x;
    public final long z;
    private final Set<Region> regions;

    public Chunk(long x, long z) {
        this.x = x;
        this.z = z;
        this.regions = new ObjectArraySet<>();
    }

    public Set<Region> getRegions() {
        return new ObjectArraySet<>(this.regions);
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

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
}
