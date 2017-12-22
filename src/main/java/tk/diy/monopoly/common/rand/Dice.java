
package tk.diy.monopoly.common.rand;

public class Dice {
    private int count;
    private int min;
    private int max;

    public Dice(int count, int max) {
        this(count, 1, max);
    }

    public Dice(int count, int min, int max) {
        this.count = count;
        this.min = min;
        this.max = max;
    }

    public int rand() {
        int sum = 0;

        for (int i = 0; i < this.count; i++) {
            sum += this.min + (int) (Math.random() * (this.max - this.min));
        }

        return sum;
    }
}
