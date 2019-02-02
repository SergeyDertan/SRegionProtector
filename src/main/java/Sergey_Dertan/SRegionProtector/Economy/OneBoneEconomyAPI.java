package Sergey_Dertan.SRegionProtector.Economy;

import cn.nukkit.Player;
import me.onebone.economyapi.EconomyAPI;

public final class OneBoneEconomyAPI implements AbstractEconomy {

    private EconomyAPI eco = EconomyAPI.getInstance();

    @Override
    public long getMoney(Player player) {
        return (long) this.eco.myMoney(player);
    }

    @Override
    public void addMoney(String player, long amount) {
        this.eco.addMoney(player, (double) amount);
    }

    @Override
    public void reduceMoney(String player, long amount) {
        this.eco.reduceMoney(player, (double) amount);
    }
}
