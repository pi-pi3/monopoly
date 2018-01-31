
package tk.diy.monopoly.common.field;

import tk.diy.monopoly.common.Player;

public class Tax extends Field {
    private final String name;
    private final int tax;

    public Tax(int index, String name, int tax) {
        this.index = index;
        this.name = name;
        this.tax = tax;
    }

    public String name() {
        return this.name;
    }

    public Visit visit(Player player) {
        if (player.receive(-this.tax)) {
            return Visit.PAYED;
        } else {
            return Visit.BANKRUPT;
        }
    }
}
