package Sergey_Dertan.SRegionProtector.Economy;

import cn.nukkit.Player;

@SuppressWarnings("unused")
public interface AbstractEconomy {

    long getMoney(Player player);

    default void addMoney(Player player, long amount) {
        this.addMoney(player.getName(), amount);
    }

    default void reduceMoney(Player player, long amount) {
        this.reduceMoney(player.getName(), amount);
    }

    void reduceMoney(String player, long amount);

    void addMoney(String player, long amount);
}
