package project2;

import aima.core.probability.Factor;
import aima.core.probability.bayes.DynamicBayesianNetwork;

import java.util.*;

import aima.core.probability.RandomVariable;
import aima.core.probability.bayes.DynamicBayesianNetwork;
import aima.core.probability.bayes.FiniteNode;
import aima.core.probability.bayes.Node;
import aima.core.probability.bayes.impl.BayesNet;
import aima.core.probability.bayes.impl.DynamicBayesNet;
import aima.core.probability.bayes.impl.FullCPTNode;
import aima.core.probability.example.ExampleRV;
import aima.core.probability.proposition.AssignmentProposition;
import aima.core.probability.util.ProbabilityTable;
import project.ComplexityAnalyzer;
import project.PreprocessingInfo;


public class ComlexityAnalyzer2 {
    private DynamicBayesianNetwork DBN ;
    private List<RandomVariable> allrv;
    private List<Node> allNodes = new ArrayList<Node>();
    //QUERY
    private List<RandomVariable> qrv;
    private List<AssignmentProposition> ap;

    /**
     * Return a Dynamic Bayesian Network of the Umbrella World Network.
     *
     * @return a Dynamic Bayesian Network of the Umbrella World Network.
     */
    private static DynamicBayesianNetwork getUmbrellaWorldNetwork() {
        FiniteNode prior_rain_tm1 = new FullCPTNode(ExampleRV.RAIN_tm1_RV,
                new double[] { 0.5, 0.5 });

        BayesNet priorNetwork = new BayesNet(prior_rain_tm1);

        // Prior belief state
        FiniteNode rain_tm1 = new FullCPTNode(ExampleRV.RAIN_tm1_RV,
                new double[] { 0.5, 0.5 });
        // Transition Model
        FiniteNode rain_t = new FullCPTNode(ExampleRV.RAIN_t_RV, new double[] {
                // R_t-1 = true, R_t = true
                0.7,
                // R_t-1 = true, R_t = false
                0.3,
                // R_t-1 = false, R_t = true
                0.3,
                // R_t-1 = false, R_t = false
                0.7 }, rain_tm1);
        // Sensor Model
        @SuppressWarnings("unused")
        FiniteNode umbrealla_t = new FullCPTNode(ExampleRV.UMBREALLA_t_RV,
                new double[] {
                        // R_t = true, U_t = true
                        0.9,
                        // R_t = true, U_t = false
                        0.1,
                        // R_t = false, U_t = true
                        0.2,
                        // R_t = false, U_t = false
                        0.8 }, rain_t);

        Map<RandomVariable, RandomVariable> X_0_to_X_1 = new HashMap<RandomVariable, RandomVariable>();
        X_0_to_X_1.put(ExampleRV.RAIN_tm1_RV, ExampleRV.RAIN_t_RV);
        Set<RandomVariable> E_1 = new HashSet<RandomVariable>();
        E_1.add(ExampleRV.UMBREALLA_t_RV);

        return new DynamicBayesNet(priorNetwork, X_0_to_X_1, E_1, rain_tm1);
    }
    private PreprocessingInfo preproInfo = new PreprocessingInfo();

    public void UmbrellaWorld(){
        HashMap<String, RandomVariable> rvsmap = new HashMap<>();
        UmbrellaDBN unb = new UmbrellaDBN();
        DBN = unb.getUmbrellaWorldNetworkExt();
        allrv = DBN.getVariablesInTopologicalOrder();

        for (RandomVariable rv : allrv) {
            rvsmap.put(rv.getName(), rv);
            allNodes.add(DBN.getNode(rv));
        }
        qrv = new ArrayList<RandomVariable>(1);
        qrv.add(0,rvsmap.get("Rain_t2"));
        ap = new ArrayList<AssignmentProposition>(1);
        Set<RandomVariable> evidence = new HashSet<RandomVariable>();
        evidence = DBN.getE_1();
        for(RandomVariable rv : evidence){
            ap.add(new AssignmentProposition(rvsmap.get(rv.getName()), "false"));
        }
        System.out.println(ap);
    }

    public ComlexityAnalyzer2() {

        UmbrellaWorld();
        RollupFiltering RF = new RollupFiltering();
        ArrayList<Factor> factors = new ArrayList<Factor>();
        RF.rollupFiltering(qrv.toArray(new RandomVariable[qrv.size()]),ap.toArray(new AssignmentProposition[ap.size()]),DBN, factors);
        System.out.println(factors);
    }

    public static void main(String[] args) {

        ComlexityAnalyzer2 complexityAnalyzer = new ComlexityAnalyzer2();
    }

}
