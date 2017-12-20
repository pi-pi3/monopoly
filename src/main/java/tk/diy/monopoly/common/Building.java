
package tk.diy.monopoly.common;

public abstract class Building {
    protected Player owner;
    protected int stage;

    public abstract String name();
    public abstract int income();
    public abstract int rent();
    public abstract int cost();

    public void own(Player owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        return this.owner;
    }
}
