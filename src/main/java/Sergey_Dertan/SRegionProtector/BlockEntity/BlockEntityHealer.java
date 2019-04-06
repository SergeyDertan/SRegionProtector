package Sergey_Dertan.SRegionProtector.BlockEntity;

import Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain;
import Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

import static Sergey_Dertan.SRegionProtector.Utils.Tags.*;

public final class BlockEntityHealer extends BlockEntitySpawnable {

    public static final String BLOCK_ENTITY_HEALER = "RegionHealer";

    public static int HEAL_DELAY;
    public static int HEAL_AMOUNT;

    private final RegionManager regionManager;
    private final AxisAlignedBB bb;
    private final String region;
    private int delay;

    public BlockEntityHealer(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.region = nbt.getString(REGION_TAG);
        this.regionManager = SRegionProtectorMain.getInstance().getRegionManager();

        if (!this.isBlockEntityValid()) {
            this.closed = true;
            this.bb = null;
            return;
        }

        this.bb = this.regionManager.getRegion(this.region).getBoundingBox();

        this.delay = HEAL_DELAY;
    }

    public static CompoundTag getDefaultNBT(Vector3 pos, String region) {
        return new CompoundTag()
                .putString(ID_TAG, BLOCK_ENTITY_HEALER)
                .putInt(X_TAG, pos.getFloorX())
                .putInt(Y_TAG, pos.getFloorY())
                .putInt(Z_TAG, pos.getFloorZ())
                .putString(REGION_TAG, region);
    }

    @Override
    protected void initBlockEntity() {
        this.scheduleUpdate();
        super.initBlockEntity();
    }

    @Override
    public void spawnTo(Player player) {
    }

    @Override
    public void spawnToAll() {
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return new CompoundTag()
                .putString(ID_TAG, BLOCK_ENTITY_HEALER)
                .putInt(X_TAG, this.getFloorX())
                .putInt(Y_TAG, this.getFloorY())
                .putInt(Z_TAG, this.getFloorZ())
                .putString(REGION_TAG, this.region);
    }

    @Override
    public void saveNBT() {
        this.namedTag.putString(ID_TAG, BLOCK_ENTITY_HEALER);
        this.namedTag.putString(REGION_TAG, this.region);
        this.namedTag.putInt(X_TAG, this.getFloorX());
        this.namedTag.putInt(Y_TAG, this.getFloorY());
        this.namedTag.putInt(Z_TAG, this.getFloorZ());
        this.namedTag.putBoolean(IS_MOVABLE_TAG, false);
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.regionManager.regionExists(this.region);
    }

    @Override
    public boolean onUpdate() {
        if (!RegionFlags.getDefaultFlagState(RegionFlags.FLAG_HEAL)) return true;
        if (this.closed) return false;
        if (--this.delay > 0) return true;
        for (Entity entity : this.level.getNearbyEntities(this.bb)) {
            if (!(entity instanceof Player)) continue;
            entity.heal(HEAL_AMOUNT);
        }
        this.delay = HEAL_DELAY;
        return true;
    }

    @Override
    public boolean isMovable() {
        return false;
    }
}
