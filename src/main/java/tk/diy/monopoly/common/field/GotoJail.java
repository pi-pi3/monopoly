
package tk.diy.monopoly.common.field;

import tk.diy.monopoly.common.Common;
import tk.diy.monopoly.common.Board;
import tk.diy.monopoly.common.Player;

public class GotoJail extends Field {
    public static final String NAME = "Go to jail";

    public GotoJail(int index) {
        this.index = index;
    }

    public String name() {
        return NAME;
    }

    public Visit visit(Player player) {
        player.moveTo(Common.JAIL_FIELD);
        player.setJail(Common.JAIL_SENTENCE);
        return Visit.GOTO_JAIL;
    }
}
