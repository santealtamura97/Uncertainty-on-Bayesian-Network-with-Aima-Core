package project;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Graph<T> {
    private Map<T, List<T> > map = new HashMap<>();

    public void addVertex(T s) {
        map.put(s, new LinkedList<T>());
    }

    public void addEdge(T source, T destination, boolean bidirectional) {
        if (!map.containsKey(source))
            addVertex(source);

        if (!map.containsKey(destination))
            addVertex(destination);

        map.get(source).add(destination);
        if (bidirectional == true) {
            map.get(destination).add(source);
        }
    }

    public void getVertexCount()
    {
        System.out.println("The graph has "
                + map.keySet().size()
                + " vertex");
    }

    public void hasVertex(T s)
    {
        if (map.containsKey(s)) {
            System.out.println("The graph contains "
                    + s + " as a vertex.");
        }
        else {
            System.out.println("The graph does not contain "
                    + s + " as a vertex.");
        }
    }

    public void getEdgesCount(boolean bidirection)
    {
        int count = 0;
        for (T v : map.keySet()) {
            count += map.get(v).size();
        }
        if (bidirection == true) {
            count = count / 2;
        }
        System.out.println("The graph has "
                + count
                + " edges.");
    }

    public Map<T, List<T> > getMap() {
        return this.map;
    }

    public void deleteNode(T node) {
        //remove node from vertex
        this.map.keySet().remove(node);

        //remove node mentions
        for (T n : this.map.keySet()) {
            if (this.map.get(n).contains(node)) {
                this.map.get(n).remove(node);
            }
        }
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();

        for (T v : map.keySet()) {
            builder.append(v.toString() + ": ");
            for (T w : map.get(v)) {
                builder.append(w.toString() + " ");
            }
            builder.append("\n");
        }

        return (builder.toString());
    }



}
