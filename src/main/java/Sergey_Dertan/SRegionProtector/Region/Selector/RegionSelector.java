package Sergey_Dertan.SRegionProtector.Region.Selector;

import cn.nukkit.Player;

import java.util.HashMap;
import java.util.Map;

public final class RegionSelector {

    private Map<Integer, SelectorSession> sessions;
    private int sessionLifetime;

    public RegionSelector(int sessionLifetime) {
        this.sessions = new HashMap<>();
        this.sessionLifetime = sessionLifetime;
    }

    public void removeSession(Player player) {
        this.sessions.remove(player.getLoaderId());
    }

    public SelectorSession getSession(Player player) {
        return this.sessions.computeIfAbsent(player.getLoaderId(), s -> new SelectorSession(this.sessionLifetime));
    }

    public void clear() {
        int currentTime = (int) System.currentTimeMillis() / 1000;

        for (Map.Entry<Integer, SelectorSession> sessionData : this.sessions.entrySet()) {
            if (sessionData.getValue().getExpirationTime() > currentTime) continue;
            this.sessions.remove(sessionData.getKey());
        }
    }
}