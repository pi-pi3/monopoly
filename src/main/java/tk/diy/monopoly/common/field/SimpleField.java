
package tk.diy.monopoly.common.field;

import tk.diy.monopoly.common.Player;
import tk.diy.monopoly.common.building.Building;

public class SimpleField extends Field {
    public SimpleField(int index, Building building) {
        this.index = index;
        this.building = building;
        building.setIndex(this.index);
    }

    public String name() {
        return this.building.name();
    }
}
