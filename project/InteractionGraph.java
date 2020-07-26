package project;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aima.core.probability.RandomVariable;
import aima.core.probability.bayes.Node;

public class InteractionGraph<T> extends Graph<T>{

    public InteractionGraph(List<Node> allNodes,PreprocessingInfo preproInfo) {
        for (Node node : allNodes) {
            if (!isIrrelevant(node,preproInfo)) {
                for (Node children : node.getChildren()) {
                    if (!isIrrelevant(children,preproInfo)) {
                        this.addEdge((T)node, (T)children, true);
                    }
                }
            }
        }
    }

    public void marryAllParentsOfSameVariable(Map<T,T> toMarry) {
        for (T parent1 : toMarry.keySet()) {
            if(this.getMap().keySet().contains(parent1) && !this.getMap().get(parent1).contains(toMarry.get(parent1))) {
                this.getMap().get(parent1).add(toMarry.get(parent1));
            }
        }
    }

    private boolean isIrrelevant(Node node, PreprocessingInfo preproInfo) {
        if (preproInfo.getIrrelevantVariables().contains(node.getRandomVariable())) {
            return true;
        }
        return false;
    }

    /**
     * Restituisce il nodo dell' IG con il numero più basso di vicini
     * @param
     */
    public T getSmallestNumNeighborsNode() {
        int minNum = Integer.MAX_VALUE; //togliere questa schifezza
        T minNode = null;
        for (T node : this.getMap().keySet()) {
            if (getNumNeighborsNode(node) < minNum) {
                minNum = getNumNeighborsNode(node);
                minNode = node;
            }
        }
        return minNode;
    }

    /**
     * Restituisce il nodo che fa aggiungere il minimo numero di archi
     * @return
     */
    public T getMinCountNumberAddEdgesNode() {
        int minCount = Integer.MAX_VALUE;
        T minNode = null;
        for (T node : this.getMap().keySet()) {
            if (countNumberAddEdges(node) < minCount) {
                minCount = countNumberAddEdges(node);
                minNode = node;
            }
        }
        return minNode;

    }

    /**
     * Restituisce il numero di archi che fa aggiungere un nodo
     * @param node
     * @return
     */
    public int countNumberAddEdges(T node) {
        List<T> neighbors = this.getNeighBors(node);
        HashMap<T,T> linked = new HashMap<>();
        for (T nd1 : neighbors) {
            for (T nd2 : neighbors) {
                if (nd1 != nd2 && !adjacent(nd1,nd2)) {
                    if (linked.get(nd1) != nd2 && linked.get(nd2) != nd1) {
                        linked.put(nd1,nd2);
                    }
                }
            }
        }
        return linked.size();
    }

    /**
     * Restituisce i vicini (neighbors) di un nodo
     * @param node
     * @return
     */
    public List<T> getNeighBors(T node) {
        return this.getMap().get(node);
    }

    /**
     * Verifica se due nodi sono adiacenti
     * @param node1
     * @param node2
     * @return
     */
    public boolean adjacent(T node1, T node2) {
        if (this.getMap().keySet().contains(node1)) {
            if (getNeighBors(node1).contains(node2)) {
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    /**
     * Restituisce il numero di vicini di node
     * @param node il nodo dato come argomento
     * @return
     */
    public int getNumNeighborsNode(T node) {
        return this.getMap().get(node).size();
    }
}