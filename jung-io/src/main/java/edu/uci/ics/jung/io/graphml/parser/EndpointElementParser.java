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
import edu.uci.ics.jung.io.graphml.EndpointMetadata;
import edu.uci.ics.jung.io.graphml.EndpointMetadata.EndpointType;
import edu.uci.ics.jung.io.graphml.ExceptionConverter;
import edu.uci.ics.jung.io.graphml.GraphMLConstants;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * Parses endpoint elements.
 *
 * @author Nathan Mittler - nathan.mittler@gmail.com
 */
public class EndpointElementParser<G extends MutableNetwork<N, E>, N, E>
        extends AbstractElementParser<G, N, E> {

    private static final Map<String, EndpointType> endpointTypeMap =
            new HashMap<String, EndpointType>();

    static {
        endpointTypeMap.put(GraphMLConstants.IN_NAME, EndpointType.IN);
        endpointTypeMap.put(GraphMLConstants.OUT_NAME, EndpointType.OUT);
        endpointTypeMap.put(GraphMLConstants.UNDIR_NAME, EndpointType.UNDIR);
    }

    public EndpointElementParser(ParserContext<G, N, E> parserContext) {
        super(parserContext);
    }

    public EndpointMetadata parse(XMLEventReader xmlEventReader, StartElement start)
            throws GraphIOException {

        try {
            // Create the new endpoint.
            EndpointMetadata endpoint = new EndpointMetadata();

            // Parse the attributes.
            @SuppressWarnings("unchecked")
            Iterator<Attribute> iterator = start.getAttributes();
            while (iterator.hasNext()) {
                Attribute attribute = iterator.next();
                String name = attribute.getName().getLocalPart();
                String value = attribute.getValue();
                if (endpoint.getId() == null && GraphMLConstants.ID_NAME.equals(name)) {
                    endpoint.setId(value);
                }
                if (endpoint.getPort() == null && GraphMLConstants.PORT_NAME.equals(name)) {
                    endpoint.setPort(value);
                }
                if (endpoint.getNode() == null && GraphMLConstants.NODE_NAME.equals(name)) {
                    endpoint.setNode(value);
                }
                if (GraphMLConstants.TYPE_NAME.equals(name)) {
                    EndpointType t = endpointTypeMap.get(value);
                    if (t == null) {
                        t = EndpointType.UNDIR;
                    }
                    endpoint.setEndpointType(t);
                } else {
                    endpoint.setProperty(name, value);
                }
            }

            // Make sure the node has been set.
            if (endpoint.getNode() == null) {
                throw new GraphIOException("Element 'endpoint' is missing attribute 'node'");
            }

            while (xmlEventReader.hasNext()) {

                XMLEvent event = xmlEventReader.nextEvent();
                if (event.isStartElement()) {
                    StartElement element = (StartElement) event;

                    String name = element.getName().getLocalPart();
                    if (GraphMLConstants.DESC_NAME.equals(name)) {
                        String desc = (String) getParser(name).parse(xmlEventReader, element);
                        endpoint.setDescription(desc);
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
            applyKeys(endpoint);

            return endpoint;

        } catch (Exception e) {
            ExceptionConverter.convert(e);
        }

        return null;
    }
}
