/*
 * Copyright 2013 Alexandre Terrasa <alexandre@moz-code.org>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;


/**
 *
 */
public class Solution {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Graph graph = readInput();

        // Find all routes
        Integer count = countAllPath(graph);
        if(count == 0) {
            System.out.println("No Route Available from F to " + graph.end.name);
            System.exit(0);
        }

        // Apply Dijkstra
        DijkstraResult result = dijkstra(graph);

        // Show shortest path
        List<Node> path = new ArrayList<>();
        Integer length = result.dist.get(graph.end);

        Node node = graph.end;
        while(node != graph.start) {
            path.add(node);
            node = result.previous.get(node);
        }

        // Display
        System.out.println("Total Routes: " + count);

        length++;
        System.out.println("Shortest Route Length: " + length);

        Collections.reverse(path);
        String pathStr = graph.start.name + "";
        for(Node n : path) {
            pathStr += " " + n.name;
        }
        System.out.println("Shortest Route after Sorting of Routes of length "+ length +": " + pathStr);
    }

    static Integer countAllPath(Graph graph) {
        Integer count = 0;
        Stack<List<Node>> q = new Stack<>();

        List<Node> tmpPath = new ArrayList<>();
        tmpPath.add(graph.start);
        q.push(tmpPath);

        while(!q.isEmpty()) {
            tmpPath = q.pop();
            Node lastNode = tmpPath.get(tmpPath.size() - 1);
            if(lastNode == graph.end) {
                count++;
            }
            for(Node link : lastNode.edges.values()) {
                if(!tmpPath.contains(link)) {
                    List<Node> newPath = new ArrayList<>();
                    newPath.addAll(tmpPath);
                    newPath.add(link);
                    q.push(newPath);
                }
            }
        }
        return count;
    }

    static DijkstraResult dijkstra(Graph graph) {
        // Init
        Map<Node, Integer> dist = new HashMap<>();
        Map<Node, Node> previous = new HashMap<>();
        for(Node node: graph.nodes.values()) {
            dist.put(node, -1);
            previous.put(node, null);
        }

        dist.put(graph.start, 0);
        List<Node> todo = new ArrayList<>();
        todo.addAll(graph.nodes.values());

        while(!todo.isEmpty()) {
            Node u = minDist(todo, dist);

            //if(dist.get(u) == -1) {
            //    break;
           //}

            for(Node v: u.edges.values()) {
                if(dist.get(v) < 0 || dist.get(v) > dist.get(u) + 1) {
                    dist.put(v, dist.get(u) + 1);
                    previous.put(v, u);
                    todo.add(v);
                }
            }
        }
        return new DijkstraResult(dist, previous);
    }

    static Node minDist(List<Node> nodes, Map<Node, Integer> dist) {
        int min = Integer.MAX_VALUE;
        Node minNode = null;
        for(Node node : nodes) {
            if(dist.get(node) >= 0 && dist.get(node) < min) {
                min = dist.get(node);
                minNode = node;
            }
        }
        nodes.remove(minNode);
        return minNode;
    }

    static Graph readInput() {
        Node end = readStartNode();
        List<Pair> pairs = readPairs();

        Map<Character, Node> nodes = new HashMap<>();
        nodes.put(end.name, end);

        for(Pair pair : pairs) {
            Node first;
            if(nodes.containsKey(pair.first)) {
                first = nodes.get(pair.first);
            } else {
                first = new Node(pair.first);
                nodes.put(first.name, first);
            }

            Node second;
            if(nodes.containsKey(pair.second)) {
                second = nodes.get(pair.second);
            } else {
                second = new Node(pair.second);
                nodes.put(second.name, second);
            }

            first.addEdge(second);
            second.addEdge(first);
        }

        if(!nodes.containsKey('F')) {
            System.out.println("Node start node found (F)!");
            System.exit(0);
        }
        Node start = nodes.get('F');

        return new Graph(start, end, nodes);
    }

    static Node readStartNode() {
        try {
            char start = (char)System.in.read();
            return new Node(start);
        } catch (Exception ex) {
            System.out.println("Cannot read start node!");
            System.exit(0);
        }

        return null;
    }

    static Pair readPair() {
        try {
            int in = System.in.read();
            while(in == 10 || in == 13) {
                in = System.in.read();
            }
            char from = (char)in;
            System.in.read(); // consume space
            char to = (char)System.in.read();
            return new Pair(from, to);
        } catch (Exception ex) {
            System.out.println("Cannot read pair!");
            System.exit(0);
        }
        return null;
    }

    static List<Pair> readPairs() {
        List<Pair> pairs = new ArrayList<>();
        try {
            Pair pair = readPair();
            while(!pair.isAA()) {
                pairs.add(pair);
                pair = readPair();
            }
        } catch (Exception ex) {
            System.out.println("Cannot read pairs!");
            System.exit(0);
        }
        return pairs;
    }
}
class Pair {
    char first;
    char second;

    public Pair(char first, char second) {
        this.first = first;
        this.second = second;
    }

    boolean isAA() {
        return first == 'A' && second == 'A';
    }
}

class DijkstraResult {
     Map<Node, Integer> dist;
     Map<Node, Node> previous;

     public DijkstraResult(Map<Node, Integer> dist, Map<Node, Node> previous) {
         this.dist = dist;
         this.previous = previous;
     }
}

class Node {
    char name;
    Map<Character, Node> edges = new HashMap<>();

    public Node(char name) {
        this.name = name;
    }

    void addEdge(Node to) {
        edges.put(to.name, to);
    }

    @Override
    public String toString() {
        String res = "{";
        res += "node: " + name;
        for(Node node : edges.values()) {
            res += " -> " + node.name;
        }
        res += "}";
        return res;
    }
}

class Graph {
    Node start;
    Node end;
    Map<Character, Node> nodes;

    public Graph(Node start, Node end, Map<Character, Node> nodes) {
        this.start = start;
        this.end = end;
        this.nodes = nodes;
    }

    @Override
    public String toString() {
        String res = "";
        res += "Start: " + start.name + "\n";
        res += "End: " + end.name + "\n";
        for(Node node : nodes.values()) {
            res += node.toString() + "\n";
        }
        return res;
    }
}
