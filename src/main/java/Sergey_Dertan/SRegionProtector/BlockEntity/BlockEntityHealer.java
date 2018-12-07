package Sergey_Dertan.SRegionProtector.BlockEntity;

import Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

public final class BlockEntityHealer extends BlockEntitySpawnable {

    public static final String TAG_REGION = "region";
    public static final String TAG_ID = "id";
    public static final String TAG_X = "x";
    public static final String TAG_Y = "y";
    public static final String TAG_Z = "z";
    public static final String BLOCK_ENTITY_HEALER = "RegionHealer";
    public static int HEAL_DELAY;
    public static int HEAL_AMOUNT;
    public static boolean FLAG_ENABLED;
    private RegionManager regionManager;
    private AxisAlignedBB bb;
    private String region;
    private int delay;

    public BlockEntityHealer(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.region = nbt.getString(TAG_REGION);
        this.regionManager = SRegionProtectorMain.getInstance().getRegionManager();
        this.delay = HEAL_DELAY;
        this.bb = this.regionManager.getRegion(this.region).getBoundingBox();
    }

    public static CompoundTag getDefaultNBT(Vector3 pos, String region) {
        return new CompoundTag("")
                .putString(TAG_ID, BLOCK_ENTITY_HEALER)
                .putInt(TAG_X, pos.getFloorX())
                .putInt(TAG_Y, pos.getFloorY())
                .putInt(TAG_Z, pos.getFloorZ())
                .putString(TAG_REGION, region);
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
        return new CompoundTag("")
                .putString(TAG_ID, BLOCK_ENTITY_HEALER)
                .putInt(TAG_X, this.getFloorX())
                .putInt(TAG_Y, this.getFloorY())
                .putInt(TAG_Z, this.getFloorZ())
                .putString(TAG_REGION, region);
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putString(TAG_ID, BLOCK_ENTITY_HEALER);
        this.namedTag.putString(TAG_REGION, this.region);
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.regionManager.regionExists(this.region);
    }

    @Override
    public boolean onUpdate() { //TODO
        if (!FLAG_ENABLED) return true;
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