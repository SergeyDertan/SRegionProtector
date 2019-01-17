package Sergey_Dertan.SRegionProtector.Region.Chunk;

import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Utils.Utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static Sergey_Dertan.SRegionProtector.Utils.Tags.*;

public final class Chunk {

    public final Object lock = new Object();
    boolean needUpdate;
    private long x;
    private long z;
    private Set<Region> regions;

    public Chunk(long x, long z, Set<Region> regions) {
        this.x = x;
        this.z = z;
        this.regions = regions;
        this.needUpdate = false;
    }

    public Chunk(long x, long z) {
        this(x, z, new HashSet<>());
    }

    public Set<Region> getRegions() {
        return new HashSet<>(this.regions);
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
            this.needUpdate = true;
        }
    }

    public void removeRegion(Region region) {
        synchronized (this.lock) {
            this.regions.remove(region);
            this.needUpdate = true;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Chunk && this.x == ((Chunk) obj).x && this.z == ((Chunk) obj).z;
    }

    public Map<String, Object> toMap() throws RuntimeException {
        Map<String, Object> sec = new HashMap<>();

        Set<String> regions = new HashSet<>();

        this.regions.forEach(region -> regions.add(region.getName()));

        sec.put(X_TAG, this.x);
        sec.put(Z_TAG, this.z);
        sec.put(REGIONS_TAG, Utils.serializeStringArray(regions.toArray(new String[]{})));
        return sec;
    }
}
