/*
 * Copyright (c) 2003, The JUNG Authors
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or https://github.com/jrtom/jung/blob/master/LICENSE for a description.
 *
 */
package edu.uci.ics.jung.visualization.control;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public abstract class AbstractPopupGraphMousePlugin extends AbstractGraphMousePlugin
        implements MouseListener {

    public AbstractPopupGraphMousePlugin() {
        this(MouseEvent.BUTTON3_MASK);
    }

    public AbstractPopupGraphMousePlugin(int modifiers) {
        super(modifiers);
    }

    public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
            handlePopup(e);
            e.consume();
        }
    }

    /**
     * if this is the popup trigger, process here, otherwise defer to the superclass
     */
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            handlePopup(e);
            e.consume();
        }
    }

    protected abstract void handlePopup(MouseEvent e);

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}
