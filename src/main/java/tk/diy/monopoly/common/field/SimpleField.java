
package tk.diy.monopoly.common.field;

import tk.diy.monopoly.common.Player;
import tk.diy.monopoly.common.building.Building;

public class SimpleField extends Field {
    public SimpleField(Building building) {
        this.building = building;
    }

    public String name() {
        return this.building.name();
    }
}
