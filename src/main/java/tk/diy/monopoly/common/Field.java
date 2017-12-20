
package tk.diy.monopoly.common;

import tk.diy.monopoly.common.Board;
import tk.diy.monopoly.common.Player;

public abstract class Field {
    public static enum Visit {
        PAYED,
        CANBUY,
        CANBUILD,
    }

    protected Building building;

    public Visit visit(Player player) {
        Player owner = this.building.getOwner();
        if (owner == null) {
            return Visit.CANBUY;
        } else {
            if (owner == player) {
                return Visit.CANBUILD;
            } else {
                player.pay(owner, this.building.rent());
                return Visit.PAYED;
            }
        }
    }
}
