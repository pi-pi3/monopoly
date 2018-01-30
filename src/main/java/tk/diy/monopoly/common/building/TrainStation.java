
package tk.diy.monopoly.common.building;

import tk.diy.monopoly.common.Player;

public class TrainStation extends Building {
    public static final int BASE_COST = 1000;
    public static final int BASE_RENT = 100;

    private String name;

    public TrainStation(String name) {
        this.name = name;
    }

    public String name() {
        return this.name;
    }

    public int rent() {
        return BASE_RENT * (1 << this.owner.getStations());
    }

    public int cost() {
        return BASE_COST;
    }

    public boolean canBuild() { return false; }
    public void build() {}

    public void own(Player owner) {
        this.owner = owner;
        owner.addStation();
    }
}
