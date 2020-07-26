package project;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import aima.core.probability.RandomVariable;
import aima.core.probability.bayes.BayesianNetwork;
import aima.core.probability.bayes.FiniteNode;
import aima.core.probability.bayes.Node;
import aima.core.probability.bayes.impl.BayesNet;
import aima.core.probability.bayes.impl.FullCPTNode;
import aima.core.probability.proposition.AssignmentProposition;
import aima.core.probability.util.RandVar;


public class PreprocessingInfo {

    //information for new bn
    private List<RandomVariable> irrelevantVariables = new ArrayList<RandomVariable>();
    private HashMap<RandomVariable,List<RandomVariable>> irrelevantEdges = new HashMap<RandomVariable,List<RandomVariable>>();
    private LinkedList<Node> minDegreeOrder = new LinkedList<Node>();
    private LinkedList<Node> minFillOrder = new LinkedList<Node>();
    private HashMap<RandomVariable, double[]> newNodesCTP = new HashMap<RandomVariable, double[]>();
    private HashMap<RandomVariable,List<RandomVariable>> edges = new HashMap<RandomVariable,List<RandomVariable>>();
    private List<AssignmentProposition> evidences = new ArrayList<AssignmentProposition>();

    //pre-processed bayesNet
    private BayesianNetwork newBN;

    public HashMap<RandomVariable, double[]> getNodesCTP() {
        return this.newNodesCTP;
    }

    public void addIrrelevantVariable(RandomVariable rv) {
        if (!irrelevantVariables.contains(rv)) {
            this.irrelevantVariables.add(rv);
            //remove rv from the graph
            edges.remove(rv);
            for (RandomVariable var : edges.keySet()) {
                if (edges.get(var).contains(rv)) {
                    edges.get(var).remove(rv);
                }
            }
        }
    }

    /**
     * Aggiunge un arco irrilevante. Modifica la CPT dei figli dell'evidenza.
     * Rimuove gli archi dall'evidenza verso i figli.
     * @param bn
     * @param app
     * @param evidence
     * @param rvs
     */
    public void addIrrelevantEdges(BayesianNetwork bn,AssignmentProposition app,RandomVariable evidence, List<RandomVariable> rvs) {
        irrelevantEdges.put(evidence, rvs);
        //elimino il nodo dai figli di evidence
        edges.get(evidence).removeAll(rvs);
    }

    private void getEvidenceVariables(List<AssignmentProposition> ap) {
        evidences.addAll(ap);
    }

    public List<RandomVariable> getIrrelevantVariables() {
        return this.irrelevantVariables;
    }

    public HashMap<RandomVariable,List<RandomVariable>> getIrrelevantEdges(){
        return this.irrelevantEdges;
    }

    public LinkedList<Node> getMinDegreeOrder() {
        return this.minDegreeOrder;
    }

    public LinkedList<Node> getMinFillOrder() {
        return this.minFillOrder;
    }

    public void printIrrelevantEdges() {
        System.out.print("Irrelevant Edges: ");
        for (RandomVariable rv : irrelevantEdges.keySet()) {
            System.out.print(rv + "->" + irrelevantEdges.get(rv) + " ");
        }
        System.out.print("\n");
    }

    public void printEdges() {
        System.out.println("\n");
        for (RandomVariable rv : edges.keySet()) {
            System.out.println(rv + "->" + edges.get(rv));
        }
    }

    public void copyInitialBN(BayesianNetwork oldBN,List<AssignmentProposition> ap) {
        List<RandomVariable> allrv = oldBN.getVariablesInTopologicalOrder();

        for(RandomVariable rv : allrv) {
            Set<Node> children = new HashSet<Node>();
            children = oldBN.getNode(rv).getChildren();
            List<RandomVariable> childrenVar = new ArrayList<RandomVariable>();

            for (Node child : children) {
                childrenVar.add(child.getRandomVariable());
            }
            edges.put(rv, childrenVar);
        }
        //printEdges();
        getEvidenceVariables(ap);
    }


}