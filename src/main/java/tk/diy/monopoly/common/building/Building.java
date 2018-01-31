
package tk.diy.monopoly.common.building;

import tk.diy.monopoly.common.Player;

public abstract class Building {
    protected Player owner;
    protected int index;

    public abstract String name();
    public abstract int rent();
    public abstract int cost();
    public abstract boolean canBuild();
    public abstract void build();
    public abstract int getStage();

    public void setIndex(int index) {
        this.index = index;
    }

    public int index() {
        return this.index;
    }

    public void own(Player owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        return this.owner;
    }
}
