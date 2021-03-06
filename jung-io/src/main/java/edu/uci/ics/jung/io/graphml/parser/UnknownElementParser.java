/*
 * Copyright (c) 2008, The JUNG Authors
 *
 * All rights reserved.
 *
 * This software is open-source under the BSD license; see either
 * "license.txt" or
 * https://github.com/jrtom/jung/blob/master/LICENSE for a description.
 */

package edu.uci.ics.jung.io.graphml.parser;

import edu.uci.ics.jung.io.GraphIOException;
import edu.uci.ics.jung.io.graphml.ExceptionConverter;

import java.util.ArrayDeque;
import java.util.Deque;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * Skips an entire unknown subtree of the XML
 *
 * @author Nathan Mittler - nathan.mittler@gmail.com
 */
public class UnknownElementParser implements ElementParser {

    /**
     * Skips an entire subtree starting with the provided unknown element.
     *
     * @param xmlEventReader the event reader
     * @param start          the unknown element to be skipped.
     * @return null
     */
    public Object parse(XMLEventReader xmlEventReader, StartElement start) throws GraphIOException {

        try {
            Deque<String> skippedElements = new ArrayDeque<String>();
            skippedElements.add(start.getName().getLocalPart());

            while (xmlEventReader.hasNext()) {

                XMLEvent event = xmlEventReader.nextEvent();
                if (event.isStartElement()) {

                    String name = event.asStartElement().getName().getLocalPart();

                    // Push the name of the unknown element.
                    skippedElements.push(name);
                }
                if (event.isEndElement()) {

                    String name = event.asEndElement().getName().getLocalPart();

                    if (skippedElements.size() == 0 || !skippedElements.peek().equals(name)) {
                        throw new GraphIOException(
                                "Failed parsing GraphML document - startTag/endTag mismatch");
                    }

                    // Pop the stack.
                    skippedElements.pop();
                    if (skippedElements.isEmpty()) {
                        break;
                    }
                }
            }

            return null;

        } catch (Exception e) {
            ExceptionConverter.convert(e);
        }

        return null;
    }
}
