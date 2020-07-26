package project2;


import java.util.*;

import aima.core.probability.CategoricalDistribution;
import aima.core.probability.Factor;
import aima.core.probability.RandomVariable;
import aima.core.probability.bayes.*;
import aima.core.probability.bayes.impl.BayesNet;
import aima.core.probability.bayes.impl.DynamicBayesNet;
import aima.core.probability.proposition.AssignmentProposition;
import aima.core.probability.util.ProbabilityTable;
import aima.core.search.csp.Assignment;


/**
 * Artificial Intelligence A Modern Approach (3rd Edition): Figure 14.11, page
 * 528.<br>
 * <br>
 *
 * <pre>
 * function ELIMINATION-ASK(X, e, bn) returns a distribution over X
 *   inputs: X, the query variable
 *           e, observed values for variables E
 *           bn, a Bayesian network specifying joint distribution P(X<sub>1</sub>, ..., X<sub>n</sub>)
 *
 *   factors <- []
 *   for each var in ORDER(bn.VARS) do
 *       factors <- [MAKE-FACTOR(var, e) | factors]
 *       if var is hidden variable the factors <- SUM-OUT(var, factors)
 *   return NORMALIZE(POINTWISE-PRODUCT(factors))
 * </pre>
 *
 * Figure 14.11 The variable elimination algorithm for inference in Bayesian
 * networks. <br>
 * <br>
 * <b>Note:</b> The implementation has been extended to handle queries with
 * multiple variables. <br>
 *
 * @author Ciaran O'Reilly
 */
public class RollupFiltering implements BayesInference {

    public RollupFiltering() {

    }

    // function ELIMINATION-ASK(X, e, bn) returns a distribution over X
    /**
     * The ELIMINATION-ASK algorithm in Figure 14.11.
     *
     * @param X
     *            the query variables.
     * @param e
     *            observed values for variables E.
     * @param bn
     *            a Bayes net with variables {X} &cup; E &cup; Y /* Y = hidden
     *            variables //
     * @return a distribution over the query variables.
     */
    public List<Factor> eliminationAsk(final RandomVariable[] X,
                                       final AssignmentProposition[] e, final DynamicBayesianNetwork bn, List<Factor> fct) {

        Set<RandomVariable> hidden = new HashSet<RandomVariable>();
        List<RandomVariable> VARS = new ArrayList<RandomVariable>();
        calculateVariablesRollup(X, e, bn, hidden, VARS);
        System.out.println(VARS);
        System.out.println(order(bn, VARS));

        List<Factor> factors = new ArrayList<Factor>();


        for (RandomVariable var : order(bn, VARS)) {
            // factors <- [MAKE-FACTOR(var, e) | factors]
            factors.add(0, makeFactor(var, e, bn));
            // if var is hidden variable then factors <- SUM-OUT(var, factors)
            if (hidden.contains(var)) {
                factors.addAll(fct);
                factors = sumOut(var, factors, bn);
            }
        }
        // return NORMALIZE(POINTWISE-PRODUCT(factors))
        List<Factor> product = new LinkedList<>();
        product.add(pointwiseProduct(factors));
        factors=product;
        // Note: Want to ensure the order of the product matches the
        // query variables
        return product;
    }


    private DynamicBayesianNetwork getCopy(DynamicBayesianNetwork bn){
        DynamicBayesianNetwork dbn = bn;
        return dbn;
    }



    //
    // START-BayesInference
    public List<Factor> rollupFiltering(final RandomVariable[] X,
                                        final AssignmentProposition[] observedEvidence,
                                        final DynamicBayesianNetwork bn, ArrayList<Factor> factors) {
        List<RandomVariable> ancestorVariables = new ArrayList<RandomVariable>();
        List<Node> allNodes = new ArrayList<Node>();
        List<AssignmentProposition> evidence = new LinkedList<AssignmentProposition>();

        getAttualQuery(X,observedEvidence,bn,allNodes,ancestorVariables,evidence);
        System.out.println("Aqua stiamo con "+ancestorVariables);
        if (ancestorVariables.isEmpty())
        { return eliminationAsk(X,evidence.toArray(new AssignmentProposition[evidence.size()]),bn,factors);}
        else
        {
            ArrayList<RandomVariable> queryTm1 = new ArrayList<RandomVariable>(1);
            ArrayList<RandomVariable> evidenceTm1 = new ArrayList<RandomVariable>(1);

            getTm1(X,observedEvidence,bn,queryTm1,evidenceTm1);
            List<Factor> fact = new LinkedList<Factor>();
            fact = rollupFiltering(queryTm1.toArray(new RandomVariable[queryTm1.size()]),observedEvidence,bn,factors);
            factors.clear();
            factors.addAll(fact);
            System.out.println(factors);

               /*
                  Set<RandomVariable> Allevid = bn.getE_1();
                  for (Node node : allNodes) {
                    for (Node children : node.getChildren()) {
                        if (Allevid.contains(children.getRandomVariable())) {
                            evidence.add(children.getRandomVariable());
                        }
                    }
                }
                System.out.println(evidence);
                Set<AssignmentProposition> eviden = new HashSet<AssignmentProposition>();
                for (AssignmentProposition ev : observedEvidence) {
                    if (evidence.contains(ev.getTermVariable())) {
                        eviden.add(new AssignmentProposition(ev.getTermVariable(),"true"));
                    }
                } */
            System.out.println(X);

            return eliminationAsk(X,evidence.toArray(new AssignmentProposition[evidence.size()]),bn,factors);
        }
    }



    // END-BayesInference
    //

    //
    // PROTECTED METHODS
    //
    /**
     * <b>Note:</b>Override this method for a more efficient implementation as
     * outlined in AIMA3e pgs. 527-28. Calculate the hidden variables from the
     * Bayesian Network. The default implementation does not perform any of
     * these.<br>
     * <br>
     * Two calcuations to be performed here in order to optimize iteration over
     * the Bayesian Network:<br>
     * 1. Calculate the hidden variables to be enumerated over. An optimization
     * (AIMA3e pg. 528) is to remove 'every variable that is not an ancestor of
     * a query variable or evidence variable as it is irrelevant to the query'
     * (i.e. sums to 1). 2. The subset of variables from the Bayesian Network to
     * be retained after irrelevant hidden variables have been removed.
     *
     * @param X
     *            the query variables.
     * @param e
     *            observed values for variables E.
     * @param bn
     *            a Bayes net with variables {X} &cup; E &cup; Y /* Y = hidden
     *            variables //
     * @param hidden
     *            to be populated with the relevant hidden variables Y.
     * @param bnVARS
     *            to be populated with the subset of the random variables
     *            comprising the Bayesian Network with any irrelevant hidden
     *            variables removed.
     */
    private void calculateVariablesRollup(final RandomVariable[] X,
                                          final AssignmentProposition[] e, final DynamicBayesianNetwork bn,
                                          Set<RandomVariable> hidden, Collection<RandomVariable> bnVARS) {

        bnVARS.addAll(bn.getVariablesInTopologicalOrder());
        Map<RandomVariable, RandomVariable> mapS = bn.getX_0_to_X_1();
        for(RandomVariable rv: X){
            bnVARS.remove(mapS.get(rv));
            List<Node> tempSet = new LinkedList<Node>();
            tempSet.add(bn.getNode(rv));
            List<RandomVariable> successor = getSuccessorTime(tempSet,e);
            for(RandomVariable succ : successor)
            {
                bnVARS.remove(mapS.get(succ));
                for(RandomVariable r: bn.getE_1()) bnVARS.remove(r);
            }
        }
        Map<RandomVariable, RandomVariable> mapA = bn.getX_1_to_X_0();

        for (RandomVariable rv : X) {
            System.out.println(mapA.get(mapA.get(rv)));
            if (bnVARS.contains(mapA.get(mapA.get(rv)))&&mapA.get(mapA.get(rv))!=null) {
                bnVARS.remove(mapA.get(mapA.get(rv)));
                List<Node> tempSet = new LinkedList<Node>();
                tempSet.add(bn.getNode(rv));

            }
        }


        hidden.addAll(bnVARS);
        for (RandomVariable x : X) {
            hidden.remove(x);
        }
        for (AssignmentProposition ap : e) {
            hidden.removeAll(ap.getScope());
        }

        return;
    }
    /**
     * <b>Note:</b>Override this method for a more efficient implementation as
     * outlined in AIMA3e pgs. 527-28. The default implementation does not
     * perform any of these.<br>
     *
     * @param bn
     *            the Bayesian Network over which the query is being made. Note,
     *            is necessary to provide this in order to be able to determine
     *            the dependencies between variables.
     * @param vars
     *            a subset of the RandomVariables making up the Bayesian
     *            Network, with any irrelevant hidden variables alreay removed.
     * @return a possibly opimal ordering for the random variables to be
     *         iterated over by the algorithm. For example, one fairly effective
     *         ordering is a greedy one: eliminate whichever variable minimizes
     *         the size of the next factor to be constructed.
     */
    protected List<RandomVariable> order(BayesianNetwork bn,
                                         Collection<RandomVariable> vars) {
        // Note: Trivial Approach:
        // For simplicity just return in the reverse order received,
        // i.e. received will be the default topological order for
        // the Bayesian Network and we want to ensure the network
        // is iterated from bottom up to ensure when hidden variables
        // are come across all the factors dependent on them have
        // been seen so far.
        List<RandomVariable> order = new ArrayList<RandomVariable>(vars);
        Collections.reverse(order);

        return order;
    }


    //
    // PRIVATE METHODS
    //
    private Factor makeFactor(RandomVariable var, AssignmentProposition[] e,
                              BayesianNetwork bn) {

        Node n = bn.getNode(var);
        if (!(n instanceof FiniteNode)) {
            throw new IllegalArgumentException(
                    "Elimination-Ask only works with finite Nodes.");
        }
        FiniteNode fn = (FiniteNode) n;
        List<AssignmentProposition> evidence = new ArrayList<AssignmentProposition>();
        for (AssignmentProposition ap : e) {
            if (fn.getCPT().contains(ap.getTermVariable())) {
                evidence.add(ap);
            }
        }

        return fn.getCPT().getFactorFor(
                evidence.toArray(new AssignmentProposition[evidence.size()]));
    }

    private List<Factor> sumOut(RandomVariable var, List<Factor> factors,
                                BayesianNetwork bn) {
        List<Factor> summedOutFactors = new ArrayList<Factor>();
        List<Factor> toMultiply = new ArrayList<Factor>();
        for (Factor f : factors) {
            if (f.contains(var)) {
                toMultiply.add(f);
            } else {
                // This factor does not contain the variable
                // so no need to sum out - see AIMA3e pg. 527.
                summedOutFactors.add(f);
            }
        }

        summedOutFactors.add(pointwiseProduct(toMultiply).sumOut(var));

        return summedOutFactors;
    }

    private Factor pointwiseProduct(List<Factor> factors) {

        Factor product = factors.get(0);
        for (int i = 1; i < factors.size(); i++) {
            product = product.pointwiseProduct(factors.get(i));
        }

        return product;
    }

    private void getTm1(RandomVariable[] X,AssignmentProposition[] observedEvidence,DynamicBayesianNetwork bn,ArrayList<RandomVariable> qrv,ArrayList<RandomVariable> evidence)
    {
        Set<RandomVariable> Allevid = bn.getE_1();
        System.out.println("All Evidence: "+Allevid);
        Map<RandomVariable, RandomVariable> map = bn.getX_1_to_X_0();
        System.out.println("X0_to_x1 <-- :"+map);
        System.out.println();
        for(RandomVariable rv : X){
            qrv.add(map.get(rv));
        }
        for (RandomVariable rv : qrv) {
            if(bn.getNode(rv).getChildren()!=null){
                for (Node children : bn.getNode(rv).getChildren()) {
                    for(AssignmentProposition ap: observedEvidence){
                        if (ap.getTermVariable().equals(children.getRandomVariable())&&Allevid.contains(children.getRandomVariable())){
                            evidence.add(children.getRandomVariable());
                        }

                    }
                }
            }
        }
    }

    private void getAttualQuery(RandomVariable[] X, AssignmentProposition[] evid,DynamicBayesianNetwork bn,List<Node> allNodes,List<RandomVariable> ancestor,List<AssignmentProposition> eviden){
        Set<RandomVariable> evidence = new HashSet<RandomVariable>();
        for (RandomVariable rv : X) {
            allNodes.add(bn.getNode(rv));
        }
        System.out.println("Query nodes: "+ allNodes);
        ancestor.addAll(getAncestors(allNodes));
        for (RandomVariable rv : X) {
            allNodes.add(bn.getNode(rv));
        }
        for(Node nd: allNodes){
            if(ancestor.contains(nd.getRandomVariable())){
                ancestor.remove(nd.getRandomVariable());
            }
        }
        System.out.println("Ancestor Variable: "+ancestor);

        for(RandomVariable rv : bn.getPriorNetwork().getVariablesInTopologicalOrder()){
            ancestor.remove(rv);
        }
        System.out.println("Aqua stiamo con "+ancestor);
        Set<RandomVariable> Allevid = bn.getE_1();
        for (Node node : allNodes) {
            for (Node children : node.getChildren()) {
                if (Allevid.contains(children.getRandomVariable())) {
                    evidence.add(children.getRandomVariable());
                }
            }
        }

        for (AssignmentProposition ev : evid) {
            if (evidence.contains(ev.getTermVariable())) {
                eviden.add(ev);
            }
        }
        for(RandomVariable rv:X) System.out.println(rv);
        System.out.println(eviden);
    }

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

    private List<RandomVariable> getSuccessorTime(List<Node> initialList,AssignmentProposition[] e) {
        List<Node> successor = new ArrayList<Node>();
        List<RandomVariable> successorVar = new ArrayList<RandomVariable>();
        List<RandomVariable> ev = new ArrayList<RandomVariable>();
        for(AssignmentProposition ap:e) ev.add(ap.getTermVariable());
        int i = 0;
        while (!initialList.isEmpty())
        {
            Node n = initialList.get(i);
            initialList.remove(i);
            if (!successor.contains(n)&&!ev.contains(n.getRandomVariable()))
            {
                Set<Node> tempSet = n.getChildren();
                for (Node nd : tempSet) {
                    initialList.add(nd);
                }
                successor.add(n);
            }
        }
        for(Node nd:successor) successorVar.add(nd.getRandomVariable());
        System.out.println(successorVar);
        return successorVar;
    }

    private List<RandomVariable> getAncestorTime(List<Node> initialList,RandomVariable[] rv,DynamicBayesianNetwork bn) {
        List<Node> ancestor = new ArrayList<Node>();
        List<RandomVariable> ancestorVar = new ArrayList<RandomVariable>();
        List<RandomVariable> ac = new ArrayList<RandomVariable>();
        for(RandomVariable r:rv) ac.add(r);
        int i = 0;
        while (!initialList.isEmpty())
        {
            Node n = initialList.get(i);
            initialList.remove(i);
            for(RandomVariable r:rv){
                System.out.println(bn.getX_1_to_X_0().get(r));
                if ((!ancestor.contains(n))&&(ac.contains(bn.getX_1_to_X_0().get(r))));
                {
                    Set<Node> tempSet = n.getParents();
                    for (Node nd : tempSet) {
                        initialList.add(nd);
                    }
                    ancestor.add(n);
                } }
        }
        for(Node nd:ancestor) ancestorVar.add(nd.getRandomVariable());
        System.out.println(ancestorVar);
        return ancestorVar;
    }

    @Override
    public CategoricalDistribution ask(RandomVariable[] X, AssignmentProposition[] observedEvidence, BayesianNetwork bn) {
        return null;
    }

    public CategoricalDistribution new_ask(final RandomVariable[] X,
                                           final AssignmentProposition[] observedEvidence,
                                           final BayesianNetwork bn,
                                           final List<RandomVariable> order) {
        return null;
    }
}