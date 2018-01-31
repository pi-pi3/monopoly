
package tk.diy.monopoly.common.field;

import tk.diy.monopoly.common.Player;

public class ParkingLot extends Field {
    public final static String NAME = "Parking lot";

    public ParkingLot(int index) {
        this.index = index;
    }

    public String name() {
        return NAME;
    }

    public Visit visit(Player _player) {
        return Visit.VISITING;
    }
}
