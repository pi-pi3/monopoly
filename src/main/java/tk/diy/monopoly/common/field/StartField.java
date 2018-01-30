
package tk.diy.monopoly.common.field;

import tk.diy.monopoly.common.Common;
import tk.diy.monopoly.common.Player;

public class StartField extends Field {
    public static final String NAME = "Start";

    public String name() {
        return NAME;
    }

    public Visit visit(Player player) {
        player.receive(Common.BONUS_CASH);
        return Visit.INCOME;
    }
}
