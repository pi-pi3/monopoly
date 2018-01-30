
package tk.diy.monopoly.common.building;

import tk.diy.monopoly.common.Player;

public class Business extends Building {
    public static final int BASE_COST = 750;
    public static final int BASE_RENT = 100;
    public static final int HIGH_RENT = 250;

    private String name;

    public Business(String name) {
        this.name = name;
    }

    public String name() {
        return this.name;
    }

    public int rent() {
        if (this.owner.getBusinesses() == 1) {
            return BASE_RENT;
        } else {
            return HIGH_RENT;
        }
    }

    public int cost() {
        return BASE_COST;
    }

    public boolean canBuild() { return false; }
    public void build() {}

    public void own(Player owner) {
        this.owner = owner;
        owner.addBusiness();
    }
}
