
package tk.diy.monopoly.common.field;

import tk.diy.monopoly.common.Board;
import tk.diy.monopoly.common.Player;
import tk.diy.monopoly.common.building.Building;

public abstract class Field {
    public static enum Visit {
        INCOME,
        PAYED,
        BANKRUPT,
        CANBUY,
        CANBUILD,
        VISITING,
        IN_JAIL,
        GOTO_JAIL,
    }

    protected Building building;

    public abstract String name();

    public Visit visit(Player player) {
        Player owner = this.building.getOwner();
        if (owner == null) {
            if (player.getCash() >= this.building.cost()) {
                // no owner, have enough money to buy
                return Visit.CANBUY;
            } else {
                // no owner, don't have enough money to buy
                return Visit.VISITING;
            }
        } else {
            if (owner == player) {
                if (building.canBuild()
                 && player.getCash() >= this.building.cost()) {
                    // own building, can build
                    return Visit.CANBUILD;
                } else {
                    // own building, can't build
                    return Visit.VISITING;
                }
            } else {
                if (player.pay(owner, this.building.rent())) {
                    // someone else's building, could pay rent
                    return Visit.PAYED;
                } else {
                    // someone else's building, couldn't pay rent
                    return Visit.BANKRUPT;
                }
            }
        }
    }
}
