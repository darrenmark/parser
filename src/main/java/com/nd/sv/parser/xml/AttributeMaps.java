package com.nd.sv.parser.xml;

import com.google.common.base.Joiner;
import com.nd.sv.serialization.AttributeType;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: darrenm
 * Date: 10/16/12
 * Time: 3:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class AttributeMaps {

    private List<AttributeMap> attributeMaps = new ArrayList<>();
    private SimpleDateFormat simpleDateFormat = null;
    public AttributeMaps(LinkedHashMap<String, String> mappings, String dateFormat) {
        initialize(mappings);
        simpleDateFormat = new SimpleDateFormat(dateFormat);
    }

    private void initialize(LinkedHashMap<String, String> mappings) {
        for(Map.Entry<String, String> entry: mappings.entrySet()) {
            AttributeMap attributeMap = new AttributeMap();
            attributeMap.setDataPath(dataPath(entry.getKey()));
            attributeMap.setDataVariable(variableName(entry.getKey()));
            attributeMap.setAttributeType(attributeType(entry.getKey()));
            attributeMap.setXmlPath(xmlPath(entry.getValue()));
            attributeMap.setAttributeName(attributeName(entry.getValue()));
            attributeMaps.add(attributeMap);
        }
    }

    public List<AttributeMap> attributeMapsWithXMLPath(String xmlPath) {
        List<AttributeMap> result = new ArrayList<>();
        for(AttributeMap attributeMap: attributeMaps) {
            if(attributeMap.getXmlPath().equals(xmlPath)) {
                result.add(attributeMap);
            }
        }
        return result;
    }

    public List<AttributeMap> attributeMapsWithAttributes(String xmlPath) {
        List<AttributeMap> result = new ArrayList<>();
        for(AttributeMap attributeMap: attributeMapsWithXMLPath(xmlPath)) {
            if(attributeMap.hasAttribute()) {
                result.add(attributeMap);
            }
        }
        return result;
    }

    public List<AttributeMap> attributeMapsWithoutAttributes(String xmlPath) {
        List<AttributeMap> result = new ArrayList<>();
        for(AttributeMap attributeMap: attributeMapsWithXMLPath(xmlPath)) {
            if(!attributeMap.hasAttribute()) {
                result.add(attributeMap);
            }
        }
        return result;
    }

    public SimpleDateFormat getSimpleDateFormat() {
        return simpleDateFormat;
    }

    private String dataPath(String dataPathWithVariable) {
        String[] pieces = dataPathWithVariable.split("[.]");
        return Joiner.on(".").join(Arrays.asList(pieces).subList(0, pieces.length -1));
    }

    private String variableName(String dataPathWithVariable) {
        String[] pieces = dataPathWithVariable.split("[.]");
        return pieces[pieces.length - 1].replaceAll("[_][A-Za-z]+$","");
    }

    private AttributeType attributeType(String dataPathWithVariable) {
        String[] pieces = dataPathWithVariable.split("[_]");
        String attributeType = pieces[pieces.length - 1];
        if(attributeType.equals("string")) {
            return AttributeType.STRING;
        }
        if(attributeType.equals("int")) {
            return AttributeType.INTEGER;
        }
        if(attributeType.equals("long")) {
            return AttributeType.LONG;
        }
        if(attributeType.equals("double")) {
            return AttributeType.DOUBLE;
        }
        if(attributeType.equals("date")) {
            return AttributeType.DATE;
        }
        if(attributeType.equals("boolean")) {
            return AttributeType.BOOLEAN;
        }
        throw new UnsupportedOperationException("Attribute type " + attributeType + " is not supported.");
    }

    private String xmlPath(String xmlPathWithAttributes) {
        return xmlPathWithAttributes.replaceAll("@.*","");
    }

    private String attributeName(String xmlPathWithAttributes) {
        if(!xmlPathWithAttributes.contains("@")) {
            return "";
        }
        return xmlPathWithAttributes.replaceAll(".*[@]","");
    }

    public String listDataVariableFor(String xmlPath) {
        for(AttributeMap attributeMap: attributeMaps) {
            if(attributeMap.getXmlPath().equals(xmlPath)) {
                String[] pieces = attributeMap.getDataPath().split("[.]");
                return pieces[pieces.length - 1];
            }
        }
        return null;
    }
}
