package com.nd.sv.parser.xml;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class XMLMappingDenormalizer {
    private LinkedHashMap<String, String> dataXMLMapping;
    private LinkedHashMap<String, String> rootMapping = new LinkedHashMap<String, String>();

    public XMLMappingDenormalizer(LinkedHashMap<String, String> dataXMLMapping) {
        this.dataXMLMapping = dataXMLMapping;
    }

    LinkedHashMap<String, String> denormalizedMap() {
        rootMapping.clear();
        LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
        for(Map.Entry<String, String> entry: dataXMLMapping.entrySet()) {
            if(!entry.getValue().startsWith("..")) {
                result.put(entry.getKey(), entry.getValue());
                addToRootMapping(dataPath(entry.getKey()), xmlPathWithoutAttribute(entry.getValue()));
            } else {
                String fullXMLPath = getFullPathForXMLElement(getParentDataObject(entry.getKey()),entry.getValue());
                result.put(entry.getKey(), fullXMLPath);
                addToRootMapping(dataPath(entry.getKey()), xmlPathWithoutAttribute(fullXMLPath));
            }
        }
        return result;
    }

    private void addToRootMapping(String dataPath, String xmlPath) {
        if(rootMapping.containsValue(dataPath) && !rootMapping.get(dataPath).equals(xmlPath)) {
            throw new IllegalStateException("More than one xml path found for data path -> " + dataPath);
        }
        rootMapping.put(dataPath, xmlPath);
    }

    private String getParentDataObject(String dataPathWithAttribute) {
        return dataPath(dataPath(dataPathWithAttribute));
    }

    private String getFullPathForXMLElement(String parentDataPath, String shortHandXML) {
        return rootMapping.get(parentDataPath) + "." + shortHandXML.replaceAll("\\.\\.","");
    }

    private String dataPath(String dataPathPlusAttribute) {
        List<String> pieces = toList(Splitter.on(".").split(dataPathPlusAttribute));
        return Joiner.on(".").join(pieces.subList(0, pieces.size() -1));
    }

    private String xmlPathWithoutAttribute(String xmlPath) {
        return xmlPath.replaceAll("[@].*","");
    }

    private List<String> toList(Iterable<String> iterable) {
        List<String> result = new ArrayList<String>();
        for(String s: iterable) {
            result.add(s);
        }
        return result;
    }
}
