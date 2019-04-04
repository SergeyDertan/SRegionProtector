package Sergey_Dertan.SRegionProtector.Provider;

import cn.nukkit.utils.Logger;

public abstract class DataBaseDataProvider extends DataProvider {

    public DataBaseDataProvider(Logger logger) {
        super(logger);
    }

    public abstract boolean checkConnection();
}
