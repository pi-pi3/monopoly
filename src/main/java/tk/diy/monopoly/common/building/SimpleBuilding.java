
package tk.diy.monopoly.common.building;

import tk.diy.monopoly.common.Player;

public class SimpleBuilding extends Building {
    public static final int MAX_STAGE = 5; // stages 1-4 are number of houses, stage 5 is hotel
    public static final double RENT_MULTIPLIER = 0.125;

    private String name;
    private int stage;
    private int cost;
    private int rent;

    public SimpleBuilding(String name, int cost) {
        this.owner = null;
        this.stage = 0;

        this.name = name;
        this.cost = cost;
        this.rent = (int) ((double) cost * RENT_MULTIPLIER);
    }

    public String name() {
        return this.name;
    }

    public int rent() {
        return this.rent * this.stage;
    }

    public int cost() {
        return this.cost * this.stage;
    }

    public boolean canBuild() {
        return this.stage < MAX_STAGE;
    }

    public void build() {
        this.stage += 1;
    }

    public void own(Player owner) {
        this.owner = owner;
        this.build();
    }
}
