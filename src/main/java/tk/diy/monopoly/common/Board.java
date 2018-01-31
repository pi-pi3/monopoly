
package tk.diy.monopoly.common;

import tk.diy.monopoly.common.field.*;
import tk.diy.monopoly.common.building.*;

public class Board {
    private Field[] fields;

    // Fields are taken from the original UK edition. Costs are multiplied by 4,
    // taxes by 5.
    public Board() {
        this.fields = new Field[40];
        this.fields[0] = new StartField(0);
        this.fields[1] = new SimpleField(1, new SimpleBuilding("Old Kent Road", 240));
        this.fields[2] = new ParkingLot(2); // community chests are replaced by parking lots
        this.fields[3] = new SimpleField(3, new SimpleBuilding("Whitechapel Road", 240));
        this.fields[4] = new Tax(4, "Income tax", 500);
        this.fields[5] = new SimpleField(5, new TrainStation("Kings Cross Station"));
        this.fields[6] = new SimpleField(6, new SimpleBuilding("The Angel Islington", 400));
        this.fields[7] = new ParkingLot(7); // chances are replaced by parking lots
        this.fields[8] = new SimpleField(8, new SimpleBuilding("Euston Road", 400));
        this.fields[9] = new SimpleField(9, new SimpleBuilding("Pentonville Road", 480));
        this.fields[10] = new Jail(10);
        this.fields[11] = new SimpleField(11, new SimpleBuilding("Pall Mall", 560));
        this.fields[12] = new SimpleField(12, new Business("Electric Company"));
        this.fields[13] = new SimpleField(13, new SimpleBuilding("Whitehall", 560));
        this.fields[14] = new SimpleField(14, new SimpleBuilding("Northumberland Avenue", 640));
        this.fields[15] = new SimpleField(15, new TrainStation("Marylebone Station"));
        this.fields[16] = new SimpleField(16, new SimpleBuilding("Bow Street", 720));
        this.fields[17] = new ParkingLot(17); // community chests are replaced by parking lots
        this.fields[18] = new SimpleField(18, new SimpleBuilding("Marlborough Street", 720));
        this.fields[19] = new SimpleField(19, new SimpleBuilding("Vine Street", 800));
        this.fields[20] = new ParkingLot(20);
        this.fields[21] = new SimpleField(21, new SimpleBuilding("The Strand", 880));
        this.fields[22] = new ParkingLot(22); // chances are replaced by parking lots
        this.fields[23] = new SimpleField(23, new SimpleBuilding("Fleet Street", 880));
        this.fields[24] = new SimpleField(24, new SimpleBuilding("Trafalgar Square", 960));
        this.fields[25] = new SimpleField(25, new TrainStation("Fenchurch St Station"));
        this.fields[26] = new SimpleField(26, new SimpleBuilding("Leicester Square", 1040));
        this.fields[27] = new SimpleField(27, new SimpleBuilding("Coventry Street", 1040));
        this.fields[28] = new SimpleField(28, new Business("Waterworks"));
        this.fields[29] = new SimpleField(29, new SimpleBuilding("Piccadilly", 1120));
        this.fields[30] = new GotoJail(30);
        this.fields[31] = new SimpleField(31, new SimpleBuilding("Regent Street", 1120));
        this.fields[32] = new SimpleField(32, new SimpleBuilding("Oxford Street", 1200));
        this.fields[33] = new ParkingLot(33); // community chests are replaced by parking lots
        this.fields[34] = new SimpleField(34, new SimpleBuilding("Bond Street", 1280));
        this.fields[35] = new SimpleField(35, new TrainStation("Liverpool Street Station"));
        this.fields[36] = new ParkingLot(36); // chances are replaced by parking lots
        this.fields[37] = new SimpleField(37, new SimpleBuilding("Park Lane", 1400));
        this.fields[38] = new Tax(38, "Super tax", 1000);
        this.fields[39] = new SimpleField(39, new SimpleBuilding("Mayfair", 1600));
    }

    public Field get(int i) {
        return this.fields[i];
    }
}
