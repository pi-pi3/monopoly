
package tk.diy.monopoly.common.building;

import tk.diy.monopoly.common.Player;

public abstract class Building {
    protected Player owner;

    public abstract String name();
    public abstract int rent();
    public abstract int cost();
    public abstract boolean canBuild();
    public abstract void build();
    public abstract int getStage();

    public void own(Player owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        return this.owner;
    }
}
