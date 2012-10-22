package com.nd.sv.parser.xml;

import com.google.common.base.Joiner;
import com.nd.sv.serialization.Attribute;
import com.nd.sv.serialization.Data;
import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.*;

/**
 */
public class FastParser {
    private Logger log = LoggerFactory.getLogger(FastParser.class);

    private AttributeMaps attributeMaps;
    Stack<Data>  dataStack = new Stack<>();
    Stack<String> xmlStack = new Stack<>();

    public FastParser(AttributeMaps attributeMaps) {
        this.attributeMaps = attributeMaps;
    }

    public Data parse(InputStream inputStream) {
        dataStack.clear();
        xmlStack.clear();
        Data result = null;
        try {
            XMLStreamReader2 xmlr = (XMLStreamReader2) createXmlInputFactory2().createXMLStreamReader("inputStream", inputStream);
            int eventType;
            while (xmlr.hasNext()) {
                eventType = xmlr.next();
                switch (eventType) {
                    case XMLEvent.START_ELEMENT:
                        xmlStack.push(xmlr.getLocalName());
                        if(result == null) {
                            result = new Data();
                            dataStack.push(result);
                        } else {
                            if(attributeMaps.listDataVariableFor(currentXMLPath()) != null && !dataStack.peek().attributeNameExists(attributeMaps.listDataVariableFor(currentXMLPath()))) {
                                dataStack.peek().putList(attributeMaps.listDataVariableFor(currentXMLPath()), new ArrayList<Attribute>());
                            }
                            if(attributeMaps.listDataVariableFor(currentXMLPath()) != null) {
                                Data data = new Data();
                                dataStack.peek().getList(attributeMaps.listDataVariableFor(currentXMLPath())).add(new Attribute("", data));
                                dataStack.push(data);
                            }
                        }
                        for(AttributeMap attributeMap: attributeMaps.attributeMapsWithAttributes(currentXMLPath())) {
                            dataStack.peek().getAttributes().add(attributeMap.createAttribute(xmlr.getAttributeValue("", attributeMap.getAttributeName()),attributeMaps.getSimpleDateFormat()));
                        }
                        break;
                    case XMLEvent.CHARACTERS:
                        for(AttributeMap attributeMap: attributeMaps.attributeMapsWithoutAttributes(currentXMLPath())) {
                            dataStack.peek().getAttributes().add(attributeMap.createAttribute(xmlr.getText(),attributeMaps.getSimpleDateFormat()));
                        }
                        break;
                    case XMLEvent.END_ELEMENT:
                        if(attributeMaps.listDataVariableFor(currentXMLPath()) != null) {
                            dataStack.pop();
                        }
                        xmlStack.pop();
                        break;
                    case XMLEvent.END_DOCUMENT:
                }
            }
        } catch (XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
        return  result;
    }



    private String currentXMLPath() {
        return Joiner.on(".").join(xmlStack.listIterator());
    }

    private  XMLInputFactory2 createXmlInputFactory2() {
        XMLInputFactory2 xmlif = (XMLInputFactory2) XMLInputFactory2.newInstance();
        xmlif.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.FALSE);
        xmlif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
        xmlif.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
        xmlif.configureForSpeed();
        return xmlif;
    }
}
