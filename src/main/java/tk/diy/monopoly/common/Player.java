
package tk.diy.monopoly.common;

import java.util.ArrayList;

import tk.diy.monopoly.common.Building;

public class Player {
    public enum Color {
        RED, GREEN, BLUE, CYAN, MAGENTA, YELLOW, ORANGE, BLACK, GREY, WHITE;

        public int toInt() {
            switch (this) {
                case RED:
                    return 0;
                case GREEN:
                    return 1;
                case BLUE:
                    return 2;
                case CYAN:
                    return 3;
                case MAGENTA:
                    return 4;
                case YELLOW:
                    return 5;
                case ORANGE:
                    return 6;
                case BLACK:
                    return 7;
                case GREY:
                    return 8;
                case WHITE:
                    return 9;
                default:
                    return -1; // unreachable
            }
        }

        public static Color fromInt(int color) throws Exception {
            switch (color) {
                case 0:
                    return RED;
                case 1:
                    return GREEN;
                case 2:
                    return BLUE;
                case 3:
                    return CYAN;
                case 4:
                    return MAGENTA;
                case 5:
                    return YELLOW;
                case 6:
                    return ORANGE;
                case 7:
                    return BLACK;
                case 8:
                    return GREY;
                case 9:
                    return WHITE;
                default:
                    throw new Exception("invalid color code");
            }
        }
    }

    private Color color;
    private ArrayList<Building> owned; // TODO: serialize
    private int position;
    private int money;

    public Player(Color color, int money) {
        this.color = color;
        this.owned = new ArrayList<Building>();
        this.position = 0;
        this.money = money;
    }

    public void move(int rel) {
        this.position += rel;
        if (this.position >= Common.FIELD_COUNT) {
            this.position = 0;
        }
    }

    public void receive(int amount) {
        this.money += amount;
    }

    public void pay(Player other, int amount) {
        this.money -= amount;
        other.money += amount;
    }

    public void buy(Building building) {
        this.money -= building.cost();
        building.own(this);
        this.owned.add(building);
    }
}
