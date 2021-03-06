/*
 * Copyright (c) 2003, The JUNG Authors
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or https://github.com/jrtom/jung/blob/master/LICENSE for a description.
 */
/*
 * Created on Jul 2, 2003
 *
 */
package edu.uci.ics.jung.graph.util;

import com.google.common.graph.MutableNetwork;
import com.google.common.graph.Network;
import com.google.common.graph.NetworkBuilder;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Provides generators for several different test graphs.
 */
public class TestGraphs {

    /**
     * A series of pairs that may be useful for generating graphs. The miniature graph consists of 8
     * edges, 10 nodes, and is formed of two connected components, one of 8 nodes, the other of 2.
     */
    public static String[][] pairs = {
            {"a", "b", "3"},
            {"a", "c", "4"},
            {"a", "d", "5"},
            {"d", "c", "6"},
            {"d", "e", "7"},
            {"e", "f", "8"},
            {"f", "g", "9"},
            {"h", "i", "1"}
    };

    /**
     * Creates a small sample graph that can be used for testing purposes. The graph is as described
     * in the section on {@link #pairs pairs}.
     *
     * @param directed true iff the graph created is to have directed edges
     * @return a graph consisting of eight edges and ten nodes.
     */
    public static Network<String, Number> createTestGraph(boolean directed) {
        MutableNetwork<String, Number> graph;
        if (directed) {
            graph = NetworkBuilder.directed().allowsParallelEdges(true).build();
        } else {
            graph = NetworkBuilder.undirected().allowsParallelEdges(true).build();
        }

        for (int i = 0; i < pairs.length; i++) {
            String[] pair = pairs[i];
            graph.addEdge(pair[0], pair[1], Integer.parseInt(pair[2]));
        }
        return graph;
    }

    /**
     * @param chain_length  the length of the chain of nodes to add to the returned graph
     * @param isolate_count the number of isolated nodes to add to the returned graph
     * @return a graph consisting of a chain of {@code chain_length} nodes and {@code isolate_count}
     * isolated nodes.
     */
    public static Network<String, Number> createChainPlusIsolates(
            int chain_length, int isolate_count) {
        MutableNetwork<String, Number> g =
                NetworkBuilder.undirected().allowsParallelEdges(true).build();
        if (chain_length > 0) {
            String[] v = new String[chain_length];
            v[0] = "v" + 0;
            g.addNode(v[0]);
            for (int i = 1; i < chain_length; i++) {
                v[i] = "v" + i;
                g.addNode(v[i]);
                g.addEdge(v[i], v[i - 1], new Double(Math.random()));
            }
        }
        for (int i = 0; i < isolate_count; i++) {
            String v = "v" + (chain_length + i);
            g.addNode(v);
        }
        return g;
    }

    /**
     * Creates a sample directed acyclic graph by generating several "layers", and connecting nodes
     * (randomly) to nodes in earlier (but never later) layers. The number of nodes in each layer is a
     * random value in the range [1, maxNodesPerLayer].
     *
     * @param layers           the number of layers of nodes to create in the graph
     * @param maxNodesPerLayer the maximum number of nodes to put in any layer
     * @param linkprob         the probability that this method will add an edge from a node in layer <i>k</i>
     *                         to a node in layer <i>k+1</i>
     * @return the created graph
     */
    public static Network<String, Number> createDirectedAcyclicGraph(
            int layers, int maxNodesPerLayer, double linkprob) {

        MutableNetwork<String, Number> dag =
                NetworkBuilder.directed().allowsParallelEdges(true).build();
        Set<String> previousLayers = new HashSet<String>();
        Set<String> inThisLayer = new HashSet<String>();
        for (int i = 0; i < layers; i++) {

            int nodesThisLayer = (int) (Math.random() * maxNodesPerLayer) + 1;
            for (int j = 0; j < nodesThisLayer; j++) {
                String v = i + ":" + j;
                dag.addNode(v);
                inThisLayer.add(v);
                // for each previous node...
                for (String v2 : previousLayers) {
                    if (Math.random() < linkprob) {
                        Double de = new Double(Math.random());
                        dag.addEdge(v, v2, de);
                    }
                }
            }

            previousLayers.addAll(inThisLayer);
            inThisLayer.clear();
        }
        return dag;
    }

    private static void createEdge(
            MutableNetwork<String, Number> g, String v1Label, String v2Label, int weight) {
        g.addEdge(v1Label, v2Label, new Double(Math.random()));
    }

    /**
     * Returns a bigger, undirected test graph with a just one component. This graph consists of a
     * clique of ten edges, a partial clique (randomly generated, with edges of 0.6 probability), and
     * one series of edges running from the first node to the last.
     *
     * @return the testgraph
     */
    public static Network<String, Number> getOneComponentGraph() {
        MutableNetwork<String, Number> g =
                NetworkBuilder.undirected().allowsParallelEdges(true).build();

        // let's throw in a clique, too
        for (int i = 1; i <= 10; i++) {
            for (int j = i + 1; j <= 10; j++) {
                String i1 = "" + i;
                String i2 = "" + j;
                g.addEdge(i1, i2, Math.pow(i + 2, j));
            }
        }

        // and, last, a partial clique
        for (int i = 11; i <= 20; i++) {
            for (int j = i + 1; j <= 20; j++) {
                if (Math.random() > 0.6) {
                    continue;
                }
                String i1 = "" + i;
                String i2 = "" + j;
                g.addEdge(i1, i2, Math.pow(i + 2, j));
            }
        }

        Iterator<String> nodeIt = g.nodes().iterator();
        String current = nodeIt.next();
        int i = 0;
        while (nodeIt.hasNext()) {
            String next = nodeIt.next();
            g.addEdge(current, next, new Integer(i++));
        }

        return g;
    }

    /**
     * Returns a bigger test graph with a clique, several components, and other parts.
     *
     * @return a demonstration graph of type <tt>UndirectedSparseMultiNetwork</tt> with 28 nodes.
     */
    public static Network<String, Number> getDemoGraph() {
        MutableNetwork<String, Number> g =
                NetworkBuilder.undirected().allowsParallelEdges(true).build();

        for (int i = 0; i < pairs.length; i++) {
            String[] pair = pairs[i];
            createEdge(g, pair[0], pair[1], Integer.parseInt(pair[2]));
        }

        // let's throw in a clique, too
        for (int i = 1; i <= 10; i++) {
            for (int j = i + 1; j <= 10; j++) {
                String i1 = "c" + i;
                String i2 = "c" + j;
                g.addEdge(i1, i2, Math.pow(i + 2, j));
            }
        }

        // and, last, a partial clique
        for (int i = 11; i <= 20; i++) {
            for (int j = i + 1; j <= 20; j++) {
                if (Math.random() > 0.6) {
                    continue;
                }
                String i1 = "p" + i;
                String i2 = "p" + j;
                g.addEdge(i1, i2, Math.pow(i + 2, j));
            }
        }
        return g;
    }
}
