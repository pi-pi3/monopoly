
package tk.diy.monopoly.common;

import tk.diy.monopoly.common.field.Field;

public class Board {
    private Field[] fields;

    public Board() {
        /* TODO */
    }

    public Field get(int i) {
        return this.fields[i];
    }
}
