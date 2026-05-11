package me.danilux.simplesteal;

public class Utils {

    private final SimpleSteal plugin;

    public Utils(SimpleSteal plugin) {
        this.plugin = plugin;
    }

    public int getMaxHearts() {
        int maxHearts = this.plugin.getConfig().getInt("max-hearts", 30);
        return maxHearts == 0 ? Integer.MAX_VALUE : maxHearts;
    }
}
