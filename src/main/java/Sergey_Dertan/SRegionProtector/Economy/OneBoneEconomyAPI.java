package Sergey_Dertan.SRegionProtector.Economy;

import cn.nukkit.Player;
import me.onebone.economyapi.EconomyAPI;

public final class OneBoneEconomyAPI implements AbstractEconomy {

    private final EconomyAPI plugin = EconomyAPI.getInstance();

    @Override
    public long getMoney(Player player) {
        return (long) this.plugin.myMoney(player);
    }

    @Override
    public void addMoney(String player, long amount) {
        this.plugin.addMoney(player, (double) amount);
    }

    @Override
    public void reduceMoney(String player, long amount) {
        this.plugin.reduceMoney(player, (double) amount);
    }
}
