package Sergey_Dertan.SRegionProtector.Region.Chunk;

import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Utils.Utils;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;

import java.util.Map;
import java.util.Set;

import static Sergey_Dertan.SRegionProtector.Utils.Tags.*;

public final class Chunk {

    public final Object lock = new Object();
    public final long x;
    public final long z;
    private Set<Region> regions;

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

    public Map<String, Object> toMap() throws RuntimeException {
        Map<String, Object> sec = new Object2ObjectArrayMap<>();

        Set<String> regions = new ObjectArraySet<>();

        this.regions.forEach(region -> regions.add(region.getName()));

        sec.put(X_TAG, this.x);
        sec.put(Z_TAG, this.z);
        sec.put(REGIONS_TAG, Utils.serializeStringArray(regions.toArray(new String[]{})));
        return sec;
    }
}
