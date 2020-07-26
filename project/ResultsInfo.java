package project;

import java.io.Serializable;

/**
 * Rappresenta le informazioni di preprocessing ed esecuzione di una rete
 */
public class ResultsInfo implements Serializable {
    private String networkName,orderType,pruningType;
    private long executionTime;
    private int numQuery,numEvidences,numNodi;

    public ResultsInfo(String networkName, String orderType, String pruningType, Long executionTime,
                        int numQuery, int numEvidences, int numNodi) {
        this.networkName = networkName;
        this.orderType = orderType;
        this.pruningType = pruningType;
        this.executionTime = executionTime;
        this.numQuery = numQuery;
        this.numEvidences = numEvidences;
        this.numNodi = numNodi;

    }

    public ResultsInfo() {

    }

    public void setNumNodi(int numNodi) {
        this.numNodi = numNodi;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public void setPruningType(String pruningType) {
        this.pruningType = pruningType;
    }

    public void setNumEvidences(int numEvidences) {
        this.numEvidences = numEvidences;
    }

    public void setNumQuery(int numQuery) {
        this.numQuery = numQuery;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public int getNumEvidences() {
        return numEvidences;
    }

    public int getNumNodi() {
        return numNodi;
    }

    public String getNetworkName() {
        return networkName;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public String getOrderType() {
        return orderType;
    }

    public String getPruningType() {
        return pruningType;
    }

    public String toString() {
        return networkName + " " + orderType + " " + executionTime + " " + pruningType + " " +
                numQuery + " " + numEvidences + " " + numNodi;
    }
}
