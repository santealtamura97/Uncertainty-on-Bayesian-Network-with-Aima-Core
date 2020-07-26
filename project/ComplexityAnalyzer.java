package project;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;


import aima.core.probability.CategoricalDistribution;
import aima.core.probability.RandomVariable;
import aima.core.probability.bayes.BayesInference;
import aima.core.probability.bayes.BayesianNetwork;
import aima.core.probability.bayes.FiniteNode;
import aima.core.probability.bayes.Node;
import aima.core.probability.bayes.exact.EliminationAsk;
import aima.core.probability.bayes.exact.EnumerationAsk;
import aima.core.probability.bayes.impl.CPT;
import aima.core.probability.proposition.AssignmentProposition;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import aima.core.search.csp.Assignment;
import bifreader.BifReader;
import org.apache.commons.lang3.ArrayUtils;

public class ComplexityAnalyzer {
    private List<RandomVariable> allrv;
    private List<Node> allNodes = new ArrayList<Node>();
    private BayesianNetwork bn;
    private CategoricalDistribution cd;
    private BayesInference[] allbi = new BayesInference[] {new EnumerationAsk(), new EliminationAsk() , new EliminationDarwicheAsk()};
    private ResultsInfo resultsInfo = new ResultsInfo();
    private int maxDomainSize = 0;

    private final static String TOPOLOGICAL_ORDER = "Topological_Order";
    private final static String MIN_DEGREE_ORDER = "Min_Degree_Order";
    private final static String MIN_FILL_ORDER = "Min_Fill_Order";

    private final static String NODE_PRUNING = "Node_Pruning";
    private final static String M_SEPARATION = "M_Separation";
    private final static String EDGE_PRUNING = "Edge_Pruning";

    //QUERY
    private List<RandomVariable> qrv;
    private List<AssignmentProposition> ap;

    //PREPROCESSING INFO
    private PreprocessingInfo preproInfo = new PreprocessingInfo();


    private BayesianNetwork createNet(String BNxml) {

        HashMap<String, RandomVariable> rvsmap = new HashMap<>();

        bn = BifReader.readBIF(BNxml);
        allrv = bn.getVariablesInTopologicalOrder();
        resultsInfo.setNumNodi(allrv.size());


        for (RandomVariable rv : allrv) {
            rvsmap.put(rv.getName(), rv);
            allNodes.add(bn.getNode(rv));
            if (rv.getDomain().size() >= maxDomainSize) {
                maxDomainSize = rv.getDomain().size();
            }
        }
        System.out.println("Max Domain size: " + maxDomainSize);

        System.out.println("NETWORK: " + BNxml);

        if (BNxml.contains("asia")) {
            qrv = new ArrayList<RandomVariable>(3);
            qrv.add(0, rvsmap.get("Xray"));
            ap = new ArrayList<AssignmentProposition>(1);
            ap.add(0, new AssignmentProposition(rvsmap.get("Lung"), "Yes"));
            //ap.add(1, new AssignmentProposition(rvsmap.get("Either"), "Yes"));
        }else if (BNxml.contains("cow")) {
            //query, distribuzione a posteriori di Scan dato Pregnancy:
            //P(Blood|Progesterone = P)
            qrv = new ArrayList<RandomVariable>(1);
            qrv.add(0, rvsmap.get("Scan"));
            ap = new ArrayList<AssignmentProposition>(1);
            ap.add(0, new AssignmentProposition(rvsmap.get("Pregnancy"), "True"));
        }else if (BNxml.contains("sachs")) {
            qrv = new ArrayList<RandomVariable>(1);
            qrv.add(0, rvsmap.get("Akt"));
            //qrv.add(1, rvsmap.get("Erk"));
            ap = new ArrayList<AssignmentProposition>(1);
            ap.add(0, new AssignmentProposition(rvsmap.get("Raf"), "LOW"));
            //ap.add(1, new AssignmentProposition(rvsmap.get("PIP3"), "AVG"));
        }else if (BNxml.contains("alarm")) {
            qrv = new ArrayList<RandomVariable>(1);
            qrv.add(0, rvsmap.get("PAP"));
            //qrv.add(1, rvsmap.get("HISTORY"));
            ap = new ArrayList<AssignmentProposition>(1);
            ap.add(0, new AssignmentProposition(rvsmap.get("CATECHOL"), "NORMAL"));
            //ap.add(1, new AssignmentProposition(rvsmap.get("PVSAT"), "NORMAL"));
        }else if (BNxml.contains("insurance")) {
            qrv = new ArrayList<RandomVariable>(1);
            //qrv.add(0, rvsmap.get("GoodStudent"));
            //qrv.add(1, rvsmap.get("SeniorTrain"));
            qrv.add(0, rvsmap.get("Accident"));
            //qrv.add(3, rvsmap.get("PropCost"));
            ap = new ArrayList<AssignmentProposition>(1);
            ap.add(0, new AssignmentProposition(rvsmap.get("Age"), "Adult"));
            //ap.add(0, new AssignmentProposition(rvsmap.get("Accident"), "Moderate"));
        }else if (BNxml.contains("hailfinder")) {
            qrv = new ArrayList<RandomVariable>(1);
            qrv.add(0, rvsmap.get("Boundaries"));
            //qrv.add(1, rvsmap.get("Scenario"));
            ap = new ArrayList<AssignmentProposition>(1);
            ap.add(0, new AssignmentProposition(rvsmap.get("Scenario"), "B"));
        }else if (BNxml.contains("hepar2")) {
            qrv = new ArrayList<RandomVariable>(1);
            qrv.add(0, rvsmap.get("Alcohol"));
            qrv.add(0, rvsmap.get("Nausea"));
            qrv.add(0, rvsmap.get("Hepatalgia"));
            qrv.add(0, rvsmap.get("Proteins"));
            qrv.add(0, rvsmap.get("Platelet"));
            //qrv.add(0, rvsmap.get("Anorexia"));
            qrv.add(0, rvsmap.get("Hbsag"));
            ap = new ArrayList<AssignmentProposition>(1);
            ap.add(0, new AssignmentProposition(rvsmap.get("Obesity"), "Present"));
            ap.add(0, new AssignmentProposition(rvsmap.get("Hepatotoxic"), "Present"));
            ap.add(0, new AssignmentProposition(rvsmap.get("Hospital"), "Present"));
            ap.add(0, new AssignmentProposition(rvsmap.get("Surgery"), "Present"));
            ap.add(0, new AssignmentProposition(rvsmap.get("Diabetes"), "Present"));
            ap.add(0, new AssignmentProposition(rvsmap.get("Fibrosis"), "Present"));
        }else if (BNxml.contains("andes")) {
            qrv = new ArrayList<RandomVariable>(1);
            qrv.add(0, rvsmap.get("TRY13"));
            ap = new ArrayList<AssignmentProposition>(1);
            ap.add(0, new AssignmentProposition(rvsmap.get("GOAL_126"), "True"));
        }else if (BNxml.contains("link")) {
            qrv = new ArrayList<RandomVariable>(1);
            qrv.add(0, rvsmap.get("D0_14_d_p"));
            ap = new ArrayList<AssignmentProposition>(1);
            ap.add(0, new AssignmentProposition(rvsmap.get("Z_30_d_f"), "F"));
        }
        resultsInfo.setNumQuery(qrv.size());
        resultsInfo.setNumEvidences(ap.size());
        printQuery();
        return bn;
    }

    //CREATE ANY BN from XML
    public ComplexityAnalyzer(String BNxml, String orderType, String pruningType) {
        resultsInfo.setNetworkName(BNxml);
        resultsInfo.setOrderType(orderType);
        if (pruningType != "")
            resultsInfo.setPruningType(pruningType);
        else
            resultsInfo.setPruningType("NONE");

        createNet(BNxml);
        preproInfo.copyInitialBN(bn,ap);
        if (pruningType.equals(""))
            System.out.println("PRUNING: NONE");
        else
            System.out.println("PRUNING: " + pruningType);
        preprocessing(pruningType);


        switch(orderType) {
            case TOPOLOGICAL_ORDER:
                System.out.print("ORDER: " + orderType + ": ");
                System.out.print(bn.getVariablesInTopologicalOrder() + "\n");
                ComputeCategoricalDistributionAndTime(bn.getVariablesInTopologicalOrder(),allbi[1],TOPOLOGICAL_ORDER);
                break;
            case MIN_DEGREE_ORDER:
                List<RandomVariable> minDeegreeOrder = new ArrayList<RandomVariable>();
                minDegreeOrder();
                for (Node node : preproInfo.getMinDegreeOrder()) {
                    minDeegreeOrder.add(node.getRandomVariable());
                }
                System.out.print("ORDER: " + orderType + ": ");
                System.out.print(minDeegreeOrder + "\n");
                ComputeCategoricalDistributionAndTime(minDeegreeOrder,allbi[2],MIN_DEGREE_ORDER);
                break;
            case MIN_FILL_ORDER:
                List<RandomVariable> minFillOrder = new ArrayList<>();
                MinFillOrder();
                for (Node node : preproInfo.getMinFillOrder()) {
                    minFillOrder.add(node.getRandomVariable());
                }
                System.out.print("ORDER: " + orderType + ": ");
                System.out.print(minFillOrder + "\n");
                ComputeCategoricalDistributionAndTime(minFillOrder,allbi[2],MIN_FILL_ORDER);
                break;
        }

        System.out.println(Lists.reverse(bn.getVariablesInTopologicalOrder()));
        System.out.println(preproInfo.getMinDegreeOrder());
        System.out.println(preproInfo.getMinFillOrder());
    }

    /**
     * Esegue il preprocessing sulla BN in base al tipo di pruning adottato(NODE_PRUNING o M_SEPARATION o EDGE_PRUNING)
     * @param pruningType
     */
    private void preprocessing(String pruningType) {
        switch (pruningType) {
            case NODE_PRUNING:
                pruningFirst();
                break;
            case M_SEPARATION:
                pruningSecond();
                break;
            case EDGE_PRUNING:
                pruneEdges();
                break;
        }
        modifyBN();
    }

    private void printQuery() {
        System.out.print("QUERY: P(");
        for (RandomVariable rv : qrv) {
            System.out.print(rv + " ");
        }
        System.out.print("| ");
        for (AssignmentProposition app : ap) {
            System.out.print(app + " ");
        }
        System.out.print(")\n");
    }

    /**
     * Calcola la distribuzione a posteriori della query data l'evidenza
     * e il tempo di esecuzione della Variable Elimination a seconda
     * del tipo di inferenza utilizzata
     */
    private void ComputeCategoricalDistributionAndTime(List<RandomVariable> order, BayesInference inferenceType, String orderType) {

        RandomVariable[] query = new RandomVariable[qrv.size()];
        query = qrv.toArray(query);
        AssignmentProposition[] ap2 = new AssignmentProposition[ap.size()];
        ap2 = ap.toArray(ap2);

        //compute and get execution time
        //se il numero di nodi è inferiore a 20, calcola il tempo in nanosecondi
        //altrimenti lo calcola in millisecondi
        if (resultsInfo.getNumNodi() <= 20) {
            long startTime = System.nanoTime();
            if (orderType.equals(TOPOLOGICAL_ORDER))
                cd = inferenceType.ask(query,ap2,bn);
            else if (orderType.equals(MIN_DEGREE_ORDER) || orderType.equals(MIN_FILL_ORDER)) {
                cd = inferenceType.new_ask(query,ap2,bn,order);
            }
            long endTime = System.nanoTime();
            long timeElapsed = endTime - startTime;
            printCategoricalDistribution();
            System.out.println("Execution time in nanoseconds: " + timeElapsed);
            resultsInfo.setExecutionTime(timeElapsed);
        }else {
            long startTime = System.currentTimeMillis();
            if (orderType.equals(TOPOLOGICAL_ORDER))
                cd = inferenceType.ask(query,ap2,bn);
            else if (orderType.equals(MIN_DEGREE_ORDER) || orderType.equals(MIN_FILL_ORDER)) {
                cd = inferenceType.new_ask(query,ap2,bn,order);
            }
            long endTime = System.currentTimeMillis();
            long timeElapsed = endTime - startTime;
            printCategoricalDistribution();
            System.out.println("Execution time in milliseconds: " + timeElapsed);
            resultsInfo.setExecutionTime(timeElapsed);
        }
        writeFile();
    }

    /**
     * Modifica la BN a seguito degli algoritmi di pruning dei nodi o degli archi
     */
    private void modifyBN() {

        /**
         * Aggiorna le CPT dei figli che hanno l'evidenza come solo padre
         */
        for (RandomVariable rv : preproInfo.getIrrelevantEdges().keySet()) {
            AssignmentProposition app = getAssignmentProposition(rv);
            for (RandomVariable rvs : preproInfo.getIrrelevantEdges().get(rv)) {
                if(!preproInfo.getIrrelevantVariables().contains(rvs)){
                    FiniteNode fn = (FiniteNode) bn.getNode(rvs);
                    if (bn.getNode(rvs).getParents().size() == 1){
                        String[] stringValues = fn.getCPD().getConditioningCase(app.getValue()).toString().replace("<","").replace(">","").replace(",","").split(" ");
                        double[] values = new double[stringValues.length];
                        for (int i = 0; i < values.length; i++){
                            values[i] = Double.parseDouble(stringValues[i]);
                        }
                        RandomVariable[] conditionedOn = new RandomVariable[bn.getNode(rvs).getParents().size() - 1];
                        CPT cpt = new CPT(rvs,values,conditionedOn);
                        ((FiniteNode) bn.getNode(rvs)).setCPT(cpt);
                    }
                }
            }
        }

        /**
         * Rimuove gli archi irrilevanti tra le evidenze e i figli
         */
        for (RandomVariable rv : preproInfo.getIrrelevantEdges().keySet()) {
            for (RandomVariable rvs : preproInfo.getIrrelevantEdges().get(rv)) {
                bn.getNode(rv).getChildren().remove(bn.getNode(rvs));

                bn.getNode(rvs).getParents().remove(bn.getNode(rv));
                FiniteNode fn = (FiniteNode) bn.getNode(rvs);
                fn.getCPT().getParents().remove(rv);
            }
        }


        /**
         * Aggiorna le CPT dei figli che hanno perso l'evidenza ma hanno anche altri padri
         */
        for (RandomVariable rv : preproInfo.getIrrelevantEdges().keySet()) {
            AssignmentProposition app = getAssignmentProposition(rv);
            for (RandomVariable rvs : preproInfo.getIrrelevantEdges().get(rv)) {
                if(!preproInfo.getIrrelevantVariables().contains(rvs)){
                    FiniteNode fn = (FiniteNode) bn.getNode(rvs);
                    if(bn.getNode(rvs).getParents().size() >= 1) {
                        String[] stringValues = null;
                        for (List<Object> combination : getCartesianProductDomains(getAllDomainsValues(rvs))) {
                            String[] sValues = fn.getCPD().getConditioningCase(combination.toArray(new Object[combination.size()])).toString().replace("<","").replace(">","").replace(",","").split(" ");
                            stringValues = ArrayUtils.addAll(stringValues,sValues);
                        }
                        double[] values = new double[stringValues.length];
                        for (int i = 0; i < values.length; i++){
                            values[i] = Double.parseDouble(stringValues[i]);
                        }
                        RandomVariable[] conditionedOn = new RandomVariable[bn.getNode(rvs).getParents().size()];
                        int j = 0;
                        for (Node p : bn.getNode(rvs).getParents()) {
                            conditionedOn[j++] = p.getRandomVariable();
                        }
                        CPT cpt = new CPT(rvs,values,conditionedOn);
                        ((FiniteNode) bn.getNode(rvs)).setCPT(cpt);
                    }
                }
            }
        }


        /**
         * Rimuove i nodi irrilevanti della BN
         */
        for (RandomVariable rv : preproInfo.getIrrelevantVariables()) {
            bn.removeNode(rv);
        }

    }

    /**
     * Restituisce tutti i domini di tutti i padri della variabile rvs
     * @param rvs
     * @return
     */
    private List<Set<Object>> getAllDomainsValues (RandomVariable rvs) {
        List<Set<Object>> allDomainsValues = new ArrayList<>();
        for (Node parent : bn.getNode(rvs).getParents()) {
            String [] domainValues = null;
            if (checkIsEvidence(parent.getRandomVariable())) {
                domainValues = new String[1];
                domainValues[0] = getAssignmentProposition(parent.getRandomVariable()).getValue().toString();
            }else {
                domainValues = parent.getRandomVariable().getDomain().toString().replace("[","").replace("]","").replace(",","").split(" ");
            }


            Set<Object> domainValuesSet = new HashSet<>();
            for (String string : domainValues) {
                if(string.equals("true")) {
                    domainValuesSet.add(true);
                }else if (string.equals("false")) {
                    domainValuesSet.add(false);
                }else {
                    domainValuesSet.add(string);
                }
            }
            allDomainsValues.add(domainValuesSet);
        }
        return allDomainsValues;
    }

    private boolean checkIsEvidence(RandomVariable rv) {
        for (AssignmentProposition app : ap) {
            if (app.getTermVariable() == rv) {
                return  true;
            }
        }
        return false;
    }

    /**
     * Restituisce il prodotto cartesiano di tutti i domini passati come argomento
     * @param allDomainsValues
     * @return
     */
    private Set<List<Object>> getCartesianProductDomains(List<Set<Object>> allDomainsValues) {
        Set<List<Object>> cartesianProduct = Sets.cartesianProduct(allDomainsValues);
        return cartesianProduct;
    }

    /**
     * Restituisce l'evidenza per una Random Variable
     * @param rv
     * @return
     */
    private AssignmentProposition getAssignmentProposition(RandomVariable rv) {
        for (AssignmentProposition app : ap){
            if (app.getTermVariable() == rv){
                return app;
            }
        }
        return null;
    }

    /**
     * Elimina le variabili che non sono nell'insieme degli ancestor
     * delle variabili di query e delle variabili di evidenza
     */
    private void pruningFirst() {
        //get query-ancestors
        List<Node> queryAncestorsNode = new ArrayList<Node>();
        List<Node> initialParentsNode = new ArrayList<Node>();
        List<RandomVariable> queryAncestorsVar = new ArrayList<RandomVariable>();
        List<RandomVariable> initialParents = new ArrayList<RandomVariable>();

        initialParents.addAll(qrv);
        for (AssignmentProposition a : ap) {
            initialParents.add(a.getTermVariable());
        }
        for (RandomVariable rv : initialParents) {
            initialParentsNode.add(bn.getNode(rv));
        }
        queryAncestorsVar = getAncestors(initialParentsNode);
        for (Node node : queryAncestorsNode) {
            queryAncestorsVar.add(node.getRandomVariable());
        }

        //get irrelavant nodes
        for (RandomVariable rv : allrv) {
            if (!queryAncestorsVar.contains(rv)) {
                preproInfo.addIrrelevantVariable(rv);
            }
        }
        System.out.println("IRRELEVANT VARIABLES: " + preproInfo.getIrrelevantVariables());
    }


    /**
     * Le variabili irrilevanti sono quelle che non posso
     * collegare direttamente alle variabili di query
     * senza passare per le evidenze
     */
    private void pruningSecond() {
        MoralGraph<Node> M = new MoralGraph<Node>();

        //build moralGraph
        for (Node node : allNodes) {
            for (Node children : node.getChildren()) {
                if (!preproInfo.getIrrelevantVariables().contains(node.getRandomVariable())
                        && !preproInfo.getIrrelevantVariables().contains(children.getRandomVariable())){
                    M.addEdge(node, children, true);
                }
            }
        }

        //get nodes to marry
        Map<Node,Node> toMarry = toMarry();
        M.marryAllParentsOfSameVariable(toMarry);
        M.addEvidences(bn, ap);

        List<Node> qrvNodes = new ArrayList<>();
        for (RandomVariable rv : qrv) {
            qrvNodes.add(bn.getNode(rv));
        }

        for (Node rv : qrvNodes) {
            M.addQuery(rv);
        }



        //get irrelevant variables
        for (Node node : allNodes) {
            if(!preproInfo.getIrrelevantVariables().contains(node.getRandomVariable()) &&
                    M.getMap().keySet().contains(node)) {
                if (!M.targetIsReachableFrom(node) && !qrv.contains(node.getRandomVariable())) {
                    preproInfo.addIrrelevantVariable(node.getRandomVariable());
                    M.deleteNode(node);
                }
            }
        }
        System.out.println("IRRELEVANT VARIABLES: " +  preproInfo.getIrrelevantVariables());
    }


    private boolean haveCommonChildren(Node parent1, Node parent2) {
        for (Node children : parent1.getChildren()) {
            if (parent2.getChildren().contains(children)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Rimuove gli archi irrilevanti.
     * Gli archi irrilevanti sono quelli che escono da una variabile di evidenza.
     * Attenzione: i nodi che perdono il proprio padre, cambieranno CPT.
     */
    private void pruneEdges() {
        for (AssignmentProposition app : ap) {
            Set<Node> childrenNodes = bn.getNode(app.getTermVariable()).getChildren();
            List<RandomVariable> childrenVariables = new ArrayList<RandomVariable>();
            for (Node children : childrenNodes) {
                //verifico che tra i figli che children non sia una variabile di query
                if (!qrv.contains(children.getRandomVariable())) {
                    childrenVariables.add(children.getRandomVariable());
                }
            }
            if(childrenVariables.size() != 0) {
                preproInfo.addIrrelevantEdges(bn,app,app.getTermVariable(), childrenVariables);
            }
        }
        preproInfo.printIrrelevantEdges();
    }

    /**
     * Inserisce a coppie in una HashMap, i nodi che devono essere sposati
     * perchè hanno un figlio in comune.
     * @return
     */
    private Map<Node,Node> toMarry() {
        //get nodes to marry
        Map<Node,Node> toMarry = new HashMap<Node,Node>();
        for (Node parent1 : allNodes) {
            for (Node parent2 : allNodes) {
                if( parent1 != parent2 && haveCommonChildren(parent1,parent2) && !preproInfo.getIrrelevantVariables().contains(parent1.getRandomVariable())
                        && !preproInfo.getIrrelevantVariables().contains(parent2.getRandomVariable())) {
                    toMarry.put(parent1, parent2);
                }
            }
        }
        return toMarry;
    }


    private void minDegreeOrder() {
        //build InteractionGraph G
        //without irrelevant nodes
        InteractionGraph<Node> G = new InteractionGraph<Node>(allNodes,preproInfo);
        //get nodes to marry
        Map<Node,Node> toMarry = toMarry();
        G.marryAllParentsOfSameVariable(toMarry);
        for (int i = 0; i < allrv.size(); i++) {
            Node node = G.getSmallestNumNeighborsNode();
            if(node == null)
                break;
            preproInfo.getMinDegreeOrder().add(node);
            List<Node> neighbors = G.getNeighBors(node);
            for (Node nb1 : neighbors) {
                for (Node nb2 : neighbors) {
                    if(nb1 != nb2 && G.getMap().keySet().contains(nb1)) {
                        if (!G.adjacent(nb1, nb2)) {
                            G.addEdge(nb1, nb2, true);
                        }
                    }
                }
            }
            G.deleteNode(node);
        }
    }

    private void MinFillOrder() {
        //build InteractionGraph G
        //without irrelevant nodes
        InteractionGraph<Node> G = new InteractionGraph<Node>(allNodes,preproInfo);
        //get nodes to marry
        Map<Node,Node> toMarry = toMarry();
        G.marryAllParentsOfSameVariable(toMarry);
        for (int i = 0; i < allrv.size(); i++) {
            Node node = G.getMinCountNumberAddEdgesNode();
            if(node == null)
                break;
            preproInfo.getMinFillOrder().add(node);
            List<Node> neighbors = G.getNeighBors(node);
            for (Node nb1 : neighbors) {
                for (Node nb2 : neighbors) {
                    if(nb1 != nb2 && G.getMap().keySet().contains(nb1)) {
                        if (!G.adjacent(nb1, nb2)) {
                            G.addEdge(nb1, nb2, true);
                        }
                    }
                }
            }
            G.deleteNode(node);
        }
    }

    /**
     * Restituisce una lista di variabili che fanno parte degli antenati(ancestor)
     * di una lista di Nodi della BN
     * @param initialList
     * @return
     */
    private List<RandomVariable> getAncestors(List<Node> initialList) {
        List<Node> ancestors = new ArrayList<Node>();
        List<RandomVariable> ancestorsVar = new ArrayList<RandomVariable>();
        int i = 0;
        while(!initialList.isEmpty()) {
            Node n = initialList.get(i);
            initialList.remove(i);
            if (!ancestors.contains(n)) {
                Set<Node> tempSet = n.getParents();
                for (Node nd : tempSet) {
                    initialList.add(nd);
                }
            }
            if(!ancestors.contains(n)) {
                ancestors.add(n);
            }
        }

        for (Node node : ancestors) {
            ancestorsVar.add(node.getRandomVariable());
        }
        return ancestorsVar;
    }

    private void printCategoricalDistribution() {
        System.out.print("<");
        for (int i = 0; i < cd.getValues().length; i++) {
            System.out.print(cd.getValues()[i]);
            if (i < (cd.getValues().length - 1)) {
                System.out.print(", ");
            } else {
                System.out.println(">");
            }
        }
    }

    private void writeFile() {
        File fout = new File("out.txt");
        try {
            FileWriter writer = new FileWriter(fout, true);
            writer.write(String.valueOf(resultsInfo));
            writer.write("\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void startComplexityAnalyzers(String networkName, List<String> orders, List<String> pruning) {
        for (String orderType : orders) {
            for (String pruningType : pruning) {
                ComplexityAnalyzer CA = new ComplexityAnalyzer(networkName,orderType,pruningType);
                System.out.println("\n");
            }
        }
    }


    public static void main(String[] args) {

        List<String> orders = new ArrayList<>();
        List<String> pruning = new ArrayList<>();
        orders.add(TOPOLOGICAL_ORDER);
        orders.add(MIN_DEGREE_ORDER);
        orders.add(MIN_FILL_ORDER);
        pruning.add("");
        //pruning.add(NODE_PRUNING);
        //pruning.add(EDGE_PRUNING);
        ///pruning.add(M_SEPARATION);
        //-----------------------------------------ASIA.XML---------------------------------------------------------------
        startComplexityAnalyzers("asia.xml",orders,pruning);
        //startComplexityAnalyzers("asia.xml",orders,pruning);
        //-----------------------------------------SACHS.XML---------------------------------------------------------------

        //startComplexityAnalyzers("sachs.xml",orders,pruning);

        //-----------------------------------------ALARM.XML---------------------------------------------------------------
        //startComplexityAnalyzers("alarm.xml",orders,pruning);

        //-----------------------------------------INSURANCE.XML-----------------------------------------------------------

        //startComplexityAnalyzers("insurance.xml",orders,pruning);
        //startComplexityAnalyzers("insurance.xml",orders,pruning);

        //------------------------------------------HAILFINDER.XML----------------------------------------------------------------

        //startComplexityAnalyzers("hailfinder.xml",orders,pruning);

        //------------------------------------------HEPAR2.XML----------------------------------------------------------------

        //startComplexityAnalyzers("hepar2.xml",orders,pruning);
        //startComplexityAnalyzers("hepar2.xml",orders,pruning);

        //-------------------------------------------ANDES.XML----------------------------------------------------------------
        //startComplexityAnalyzers("andes.xml",orders,pruning);

        //-------------------------------------------LINK.XML----------------------------------------------------------------
        //startComplexityAnalyzers("link.xml",orders,pruning);


    }



}