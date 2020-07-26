package bifreader.bif;


import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import bifreader.csv.CSVFormat;

/**
 * Holds a BIF definition.
 */
public class BIFDefinition {
    private String forDefinition;
    private final List<String> givenDefinitions = new ArrayList<String>();
    private double[] table;

    /**
     * @return the forDefinition
     */
    public String getForDefinition() {
        return forDefinition;
    }
    /**
     * @param forDefinition the forDefinition to set
     */
    public void setForDefinition(String forDefinition) {

        forDefinition = upperize(forDefinition);
        this.forDefinition = forDefinition;
    }
    /**
     * @return the probability
     */
    public double[] getTable() {
        return table;
    }
    /**
     * @param s the probability to set
     */
    public void setTable(String s) throws Exception {

        // parse a space separated list of numbers
        StringTokenizer tok = new StringTokenizer(s);
        List<Double> list = new ArrayList<Double>();
        while(tok.hasMoreTokens()) {
            String str = tok.nextToken();
            // support both radix formats
            if( str.indexOf("," )!=-1 ) {
                list.add(CSVFormat.DECIMAL_COMMA.parse(str));
            } else {
                list.add(CSVFormat.DECIMAL_POINT.parse(str));
            }
        }

        // now copy to regular array
        this.table = new double[list.size()];
        for(int i=0;i<this.table.length;i++) {
            this.table[i] = list.get(i);
        }
    }
    /**
     * @return the givenDefinitions
     */
    public List<String> getGivenDefinitions() {
        return givenDefinitions;
    }
    public void addGiven(String s) {
        s=upperize(s);
        this.givenDefinitions.add(s);

    }


    private String upperize(String name){
        return name.substring(0,1).toUpperCase()+name.substring(1);
    }
}
