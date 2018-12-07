package Sergey_Dertan.SRegionProtector.Task;

import Sergey_Dertan.SRegionProtector.Region.Selector.RegionSelector;
import cn.nukkit.scheduler.AsyncTask;

public final class ClearSessionsTask extends AsyncTask {

    private RegionSelector selector;

    public ClearSessionsTask(RegionSelector selector) {
        this.selector = selector;
    }

    @Override
    public void onRun() {
        this.selector.clear();
    }
}