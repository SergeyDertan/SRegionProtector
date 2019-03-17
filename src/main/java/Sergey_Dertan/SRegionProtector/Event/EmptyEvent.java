package Sergey_Dertan.SRegionProtector.Event;

import cn.nukkit.event.Event;
import cn.nukkit.event.entity.EntitySpawnEvent;

/**
 * special for
 *
 * @see RegionEventsHandler#entitySpawn(EntitySpawnEvent)
 * because EntitySpawnEvent can`t be cancelled
 */
@SuppressWarnings("WeakerAccess")
public final class EmptyEvent extends Event {

    private boolean isCancelled;

    @Override
    public void setCancelled() {
        this.isCancelled = true;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }
}
