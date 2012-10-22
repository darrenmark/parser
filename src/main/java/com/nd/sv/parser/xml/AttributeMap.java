package com.nd.sv.parser.xml;

import com.nd.sv.serialization.Attribute;
import com.nd.sv.serialization.AttributeType;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created with IntelliJ IDEA.
 * User: darrenm
 * Date: 10/16/12
 * Time: 3:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class AttributeMap {

    private String dataPath;
    private String dataVariable;
    private AttributeType attributeType;
    private String xmlPath;
    private String attributeName;


    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public String getDataVariable() {
        return dataVariable;
    }

    public void setDataVariable(String dataVariable) {
        this.dataVariable = dataVariable;
    }

    public AttributeType getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(AttributeType attributeType) {
        this.attributeType = attributeType;
    }

    public String getXmlPath() {
        return xmlPath;
    }

    public void setXmlPath(String xmlPath) {
        this.xmlPath = xmlPath;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public boolean hasAttribute() {
        return !attributeName.isEmpty();
    }

    public Attribute createAttribute(String valueAsString, SimpleDateFormat dateFormat) {
        if(attributeType == AttributeType.STRING) {
            return new Attribute(dataVariable, valueAsString);
        }
        if(attributeType == AttributeType.INTEGER) {
            return new Attribute(dataVariable, Integer.parseInt(valueAsString));
        }
        if(attributeType == AttributeType.LONG) {
            return new Attribute(dataVariable, Long.parseLong(valueAsString));
        }
        if(attributeType == AttributeType.DOUBLE) {
            return new Attribute(dataVariable, Double.parseDouble(valueAsString));
        }
        if(attributeType == AttributeType.BOOLEAN) {
            return new Attribute(dataVariable, valueAsString.equalsIgnoreCase("Y") || valueAsString.equals("1") || valueAsString.equalsIgnoreCase("true"));
        }
        if(attributeType == AttributeType.DATE) {
            try {
                return new Attribute(dataVariable, dateFormat.parse(valueAsString));
            }catch (ParseException parseException) {
                throw new RuntimeException(parseException);
            }
        }
        throw new IllegalStateException(attributeType + " is not supported");
    }
}
