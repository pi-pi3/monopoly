
package tk.diy.monopoly.common;

import tk.diy.monopoly.common.field.*;
import tk.diy.monopoly.common.building.*;

public class Board {
    private Field[] fields;

    // Fields are taken from the original UK edition. Costs are multiplied by 4,
    // taxes by 5.
    public Board() {
        this.fields = new Field[40];
        // this.fields[0] = new StartField(); TODO
        this.fields[1] = new SimpleField(new SimpleBuilding("Old Kent Road", 240));
        this.fields[2] = new ParkingLot(); // community chests are replaced by parking lots
        this.fields[3] = new SimpleField(new SimpleBuilding("Whitechapel Road", 240));
        // this.fields[4] = new Tax("Income tax", 500); TODO
        // this.fields[5] = new TrainStation("Kings Cross Station"); TODO
        this.fields[6] = new SimpleField(new SimpleBuilding("The Angel Islington", 400));
        this.fields[7] = new ParkingLot(); // chances are replaced by parking lots
        this.fields[8] = new SimpleField(new SimpleBuilding("Euston Road", 400));
        this.fields[9] = new SimpleField(new SimpleBuilding("Pentonville Road", 480));
        // this.fields[10] = new Jail(); TODO
        this.fields[11] = new SimpleField(new SimpleBuilding("Pall Mall", 560));
        // this.fields[12] = new Business("Electric Company");  TODO
        this.fields[13] = new SimpleField(new SimpleBuilding("Whitehall", 560));
        this.fields[14] = new SimpleField(new SimpleBuilding("Northumberland Avenue", 640));
        // this.fields[15] = new TrainStation("Marylebone Station"); TODO
        this.fields[16] = new SimpleField(new SimpleBuilding("Bow Street", 720));
        this.fields[17] = new ParkingLot(); // community chests are replaced by parking lots
        this.fields[18] = new SimpleField(new SimpleBuilding("Marlborough Street", 720));
        this.fields[19] = new SimpleField(new SimpleBuilding("Vine Street", 800));
        this.fields[20] = new ParkingLot();
        this.fields[21] = new SimpleField(new SimpleBuilding("The Strand", 880));
        this.fields[22] = new ParkingLot(); // chances are replaced by parking lots
        this.fields[23] = new SimpleField(new SimpleBuilding("Fleet Street", 880));
        this.fields[24] = new SimpleField(new SimpleBuilding("Trafalgar Square", 960));
        // this.fields[25] = new TrainStation("Fenchurch St Station"); TODO
        this.fields[26] = new SimpleField(new SimpleBuilding("Leicester Square", 1040));
        this.fields[27] = new SimpleField(new SimpleBuilding("Coventry Street", 1040));
        // this.fields[28] = new Business("Waterworks");  TODO
        this.fields[29] = new SimpleField(new SimpleBuilding("Piccadilly", 1120));
        // this.fields[30] = new GotoJail();
        this.fields[31] = new SimpleField(new SimpleBuilding("Regent Street", 1120));
        this.fields[32] = new SimpleField(new SimpleBuilding("Oxford Street", 1200));
        this.fields[33] = new ParkingLot(); // community chests are replaced by parking lots
        this.fields[34] = new SimpleField(new SimpleBuilding("Bond Street", 1280));
        // this.fields[35] = new TrainStation("Liverpool Street Station"); TODO
        this.fields[36] = new ParkingLot(); // chances are replaced by parking lots
        this.fields[37] = new SimpleField(new SimpleBuilding("Park Lane", 1400));
        // this.fields[38] = new Tax("Super tax", 1000); TODO
        this.fields[39] = new SimpleField(new SimpleBuilding("Mayfair", 1600));
    }

    public Field get(int i) {
        return this.fields[i];
    }
}
