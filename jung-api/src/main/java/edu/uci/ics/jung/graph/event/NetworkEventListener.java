package edu.uci.ics.jung.graph.event;

import java.util.EventListener;

/**
 * An interface for classes that listen for graph events.
 */
public interface NetworkEventListener<N, E> extends EventListener {
    /**
     * Method called by the process generating a graph event to which this instance is listening. The
     * implementor of this interface is responsible for deciding what behavior is appropriate.
     *
     * @param evt the graph event to be handled
     */
    void handleGraphEvent(NetworkEvent<N, E> evt);
}
