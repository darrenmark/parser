package com.nd.sv.parser.xml;

import org.junit.Test;

import java.util.LinkedHashMap;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 */
public class XMLMappingDenormalizerTest {

    @Test
    public void testDenormalization() {
        LinkedHashMap<String, String> input = new LinkedHashMap<>();
        input.put("root.name_string","county@name");
        input.put("root.city.name_string","..city@name");
        LinkedHashMap<String, String> output = new XMLMappingDenormalizer(input).denormalizedMap();
        assertThat(output.get("root.name_string"), is(equalTo("county@name")));
        assertThat(output.get("root.city.name_string"), is(equalTo("county.city@name")));
    }

    @Test
    public void testDenormalization_1() {
        LinkedHashMap<String, String> input = new LinkedHashMap<>();
        input.put("root.name_string","county@name");
        input.put("root.city.name_string","..city@name");
        input.put("root.city.school.address_string","..school@name");
        LinkedHashMap<String, String> output = new XMLMappingDenormalizer(input).denormalizedMap();
        assertThat(output.get("root.city.school.address_string"), is(equalTo("county.city.school@name")));
    }

}
