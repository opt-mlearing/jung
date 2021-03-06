package edu.uci.ics.jung.visualization.spatial.rtree;

import java.util.List;

/**
 * interface for a list of Bounded items. The bounding box of the list is the union of the list
 * contents bounding boxes
 *
 * @param <B> the type of item stored in the list
 * @author Tom Nelson
 */
public interface BoundedList<B extends Bounded> extends Bounded, List<B> {

    /**
     * recompute the bounding box for the list
     */
    void recalculateBounds();
}
