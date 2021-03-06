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

import com.google.common.graph.MutableNetwork;
import edu.uci.ics.jung.io.GraphIOException;
import edu.uci.ics.jung.io.graphml.DataMetadata;
import edu.uci.ics.jung.io.graphml.EdgeMetadata;
import edu.uci.ics.jung.io.graphml.ExceptionConverter;
import edu.uci.ics.jung.io.graphml.GraphMLConstants;

import java.util.Iterator;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * Parses an edge element.
 *
 * @author Nathan Mittler - nathan.mittler@gmail.com
 */
public class EdgeElementParser<G extends MutableNetwork<N, E>, N, E>
        extends AbstractElementParser<G, N, E> {

    public EdgeElementParser(ParserContext<G, N, E> parserContext) {
        super(parserContext);
    }

    public EdgeMetadata parse(XMLEventReader xmlEventReader, StartElement start)
            throws GraphIOException {

        try {
            // Create the new edge.
            EdgeMetadata edge = new EdgeMetadata();

            // Parse the attributes.
            @SuppressWarnings("unchecked")
            Iterator<Attribute> iterator = start.getAttributes();
            while (iterator.hasNext()) {
                Attribute attribute = iterator.next();
                String name = attribute.getName().getLocalPart();
                String value = attribute.getValue();
                if (edge.getId() == null && GraphMLConstants.ID_NAME.equals(name)) {
                    edge.setId(value);
                } else if (edge.isDirected() == null && GraphMLConstants.DIRECTED_NAME.equals(name)) {
                    edge.setDirected(("true".equals(value)));
                } else if (edge.getSource() == null && GraphMLConstants.SOURCE_NAME.equals(name)) {
                    edge.setSource(value);
                } else if (edge.getTarget() == null && GraphMLConstants.TARGET_NAME.equals(name)) {
                    edge.setTarget(value);
                } else if (edge.getSourcePort() == null && GraphMLConstants.SOURCEPORT_NAME.equals(name)) {
                    edge.setSourcePort(value);
                } else if (edge.getTargetPort() == null && GraphMLConstants.TARGETPORT_NAME.equals(name)) {
                    edge.setTargetPort(value);
                } else {
                    edge.setProperty(name, value);
                }
            }

            // Make sure the source and target have been been set.
            if (edge.getSource() == null || edge.getTarget() == null) {
                throw new GraphIOException("Element 'edge' is missing attribute 'source' or 'target'");
            }

            while (xmlEventReader.hasNext()) {

                XMLEvent event = xmlEventReader.nextEvent();
                if (event.isStartElement()) {
                    StartElement element = (StartElement) event;

                    String name = element.getName().getLocalPart();
                    if (GraphMLConstants.DESC_NAME.equals(name)) {
                        String desc = (String) getParser(name).parse(xmlEventReader, element);
                        edge.setDescription(desc);
                    } else if (GraphMLConstants.DATA_NAME.equals(name)) {
                        DataMetadata data = (DataMetadata) getParser(name).parse(xmlEventReader, element);
                        edge.addData(data);
                    } else {

                        // Treat anything else as unknown
                        getUnknownParser().parse(xmlEventReader, element);
                    }
                }
                if (event.isEndElement()) {
                    EndElement end = (EndElement) event;
                    verifyMatch(start, end);
                    break;
                }
            }

            // Apply the keys to this object.
            applyKeys(edge);

            return edge;

        } catch (Exception e) {
            ExceptionConverter.convert(e);
        }

        return null;
    }
}
