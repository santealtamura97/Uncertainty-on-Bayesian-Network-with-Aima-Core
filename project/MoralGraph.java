package project;
import java.util.*;

import aima.core.probability.RandomVariable;
import aima.core.probability.bayes.BayesianNetwork;
import aima.core.probability.bayes.Node;
import aima.core.probability.proposition.AssignmentProposition;

public class MoralGraph<T> extends Graph<T> {
    List<T> evidences  = new ArrayList<T>();
    ArrayList<LinkedList<T>> paths = new ArrayList<LinkedList<T>>();
    List<T> target = new ArrayList<T>();
    int V;//number of node
    boolean pathExist;


    public void marryAllParentsOfSameVariable(Map<T,T> toMarry) {
        for (T parent1 : toMarry.keySet()) {
            if(this.getMap().keySet().contains(parent1) && !this.getMap().get(parent1).contains(toMarry.get(parent1))) {
                this.getMap().get(parent1).add(toMarry.get(parent1));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void addEvidences(BayesianNetwork bn, List <AssignmentProposition> evidences) {
        for (AssignmentProposition app : evidences) {
            this.evidences.add((T) bn.getNode(app.getTermVariable()));
            deleteNode((T) bn.getNode(app.getTermVariable()));
        }
        V = bn.getVariablesInTopologicalOrder().size();
    }

    public void addQuery(T queryVariable) {
        target.add(queryVariable);
    }

    /*private void allPathsUtil(T v1, T target, HashMap<T,Boolean> visited, List<T> path,int index){
        visited.put(v1, true);
        path.add(index, v1);
        index++;

        //if current node is same as v2, then print
        //current path
        if(v1 == target) {
            int i;
            pathExist = true;
            LinkedList<T> pathToSave = new LinkedList<T>();
            for (i = 0; i<index-1;i++)
                pathToSave.add(path.get(i));
            pathToSave.add(path.get(i));
            paths.add(pathToSave);
        }else {
            for (T adj : getNeighBors(v1)) {
                if (!visited.get(adj)) {
                    allPathsUtil(adj,target,visited,path,index);
                }
            }
        }
        index--;
        visited.put(v1, false);
    }*/

    /**
     * Salva tutti i path tra il nodo considerato e il target
     * @param v1
     */
    /*public T saveAllPaths(T v1) {
        boolean irrelevant = true;
        HashMap<T,Boolean> visited = new HashMap<T,Boolean>(V);

        for(T key : this.getMap().keySet()) {
            visited.put(key, false);
        }

        List<T> path = new ArrayList<T>(V);
        int index = 0;
        pathExist = false;
        allPathsUtil(v1,target,visited,path,index);

        //verifies that at least one path for v1 to target doesn't contains evidence
        //if not verified v1 is irrelevant
        for (LinkedList<T> list : paths) {
            if (list.contains(target)) {
                irrelevant = false;
            }
        }
        if (irrelevant && !evidences.contains(v1)) {
            return v1;
        }
        paths.clear();
        return null;
    }*/

    /**
     * Verifica se la variabile di query è raggiungibile da var
     * @param var variabile random
     * @return
     */
    public Boolean targetIsReachableFrom(T var) {
        LinkedList<T> temp;
        HashMap<T,Boolean> visited = new HashMap<>(V);
        for(T key : this.getMap().keySet()) {
            visited.put(key, false);
        }

        //Create a queue for BFS
        LinkedList<T> queue = new LinkedList<>();

        visited.put(var,true);
        queue.add(var);

        while (queue.size()!=0) {
            var = queue.poll();
            for (T adj : getNeighBors(var)) {
                if (target.contains(adj)) {
                    return true;
                }

                if(visited.get(adj) == false) {
                    visited.put(adj,true);
                    queue.add(adj);
                }
            }
        }
        return false;
    }



    /**
     * Restituisce i vicini (neighbors) di un nodo
     * @param node
     * @return
     */
    public List<T> getNeighBors(T node) {
        return this.getMap().get(node);
    }

}