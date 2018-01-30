
package tk.diy.monopoly.common.field;

import tk.diy.monopoly.common.Board;
import tk.diy.monopoly.common.Player;

public class Jail extends Field {
    public static final String NAME = "Jail";

    public String name() {
        return NAME;
    }

    public Visit visit(Player _player) {
        return Visit.VISITING;
    }
}
