package Sergey_Dertan.SRegionProtector.Region.Selector;

import cn.nukkit.Player;

import java.util.HashMap;
import java.util.Map;

public final class RegionSelector {

    private Map<String, SelectorSession> sessions;

    public RegionSelector() {
        this.sessions = new HashMap<>();
    }

    public void removeSession(Player player) {
        this.sessions.remove(player.getName());
    }

    public SelectorSession getSession(Player player) {
        if (!this.sessions.containsKey(player.getName())) this.sessions.put(player.getName(), new SelectorSession());
        return this.sessions.get(player.getName());
    }

    public void clear() {
        int currentTime = (int) System.currentTimeMillis() / 1000;

        for (Map.Entry<String, SelectorSession> sessionData : this.sessions.entrySet()) {
            if (sessionData.getValue().getExpirationTime() < currentTime) this.sessions.remove(sessionData.getKey());
        }
    }
}