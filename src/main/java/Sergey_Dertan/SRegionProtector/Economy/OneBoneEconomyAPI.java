package Sergey_Dertan.SRegionProtector.Economy;

import cn.nukkit.Player;
import me.onebone.economyapi.EconomyAPI;

public final class OneBoneEconomyAPI implements AbstractEconomy {

    @Override
    public long getMoney(Player player) {
        return (int) EconomyAPI.getInstance().myMoney(player);
    }

    @Override
    public void addMoney(String player, long amount) {
        EconomyAPI.getInstance().addMoney(player, (double) amount);
    }

    @Override
    public void reduceMoney(String player, long amount) {
        EconomyAPI.getInstance().reduceMoney(player, (double) amount);
    }
}
