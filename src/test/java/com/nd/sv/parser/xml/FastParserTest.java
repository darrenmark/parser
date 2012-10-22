package com.nd.sv.parser.xml;

import com.nd.sv.serialization.Data;
import com.nd.sv.serialization.Message;
import com.nd.sv.serialization.MessageSerializer;
import com.nd.sv.serialization.protobuf.ProtobufMessageSerializer;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
/**
 */
public class FastParserTest {

    @Test
    public void testParser() throws Exception {
        String xml ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<county name=\"Santa Clara\">\n" +
                "    <city name=\"San Jose\">\n" +
                "        <school address=\"121 Fist Street\" maxStudent=\"230\">\n" +
                "            <class grade=\"5\">\n" +
                "                <student age=\"21\">\n" +
                "                    <firstName>Darren</firstName>\n" +
                "                    <lastName>Mark</lastName>\n" +
                "                </student>\n" +
                "            </class>\n" +
                "        </school>\n" +
                "    </city>\n" +
                "    <city name=\"Santa Clara\">\n" +
                "        <school address=\"232 Jackson St.\" maxStudent=\"531\">\n" +
                "            <class grade=\"5\">\n" +
                "                <student age=\"24\">\n" +
                "                    <firstName>Robin</firstName>\n" +
                "                    <lastName>Hood</lastName>\n" +
                "                </student>\n" +
                "            </class>\n" +
                "        </school>\n" +
                "        <school address=\"1001 Erie Cir\" maxStudent=\"3451\">\n" +
                "            <class grade=\"5\">\n" +
                "                <student age=\"15\">\n" +
                "                    <firstName>James</firstName>\n" +
                "                    <lastName>Cook</lastName>\n" +
                "                </student>\n" +
                "            </class>\n" +
                "        </school>\n" +
                "    </city>\n" +
                "</county>";

        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("root.name_string","county@name");
        map.put("root.cities.name_string","..city@name");
        map.put("root.cities.name_string","..city@name");
        map.put("root.cities.schools.address_string","..school@address");
        map.put("root.cities.schools.maxStudent_long","..school@maxStudent");
        map.put("root.cities.schools.classes.grade_int","..class@grade");
        map.put("root.cities.schools.classes.students.firstName_string","..student.firstName");
        map.put("root.cities.schools.classes.students.lastName_string","..student.lastName");

        XMLMappingDenormalizer denormalizer = new XMLMappingDenormalizer(map);
        AttributeMaps  attributeMaps = new AttributeMaps(denormalizer.denormalizedMap(), "");
        Data data =  new FastParser(attributeMaps).parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
        assertThat(data.getString("name"), is(equalTo("Santa Clara")));
        assertThat(data.getList("cities").size(), is(equalTo(2)));
        assertThat(data.getList("cities").get(1).getObjectString("name"), is(equalTo("Santa Clara")));
        assertThat(data.getList("cities").get(1).getObjectList("schools").size(), is(equalTo(2)));
        assertThat(data.getList("cities").get(1).getObjectList("schools").get(0).getObjectString("address"), is(equalTo("232 Jackson St.")));
        assertThat(data.getList("cities").get(1).getObjectList("schools").get(0).getObjectList("classes").size(), is(equalTo(1)));
        assertThat(data.getList("cities").get(1).getObjectList("schools").get(0).getObjectList("classes").get(0).getObjectList("students").get(0).getObjectString("firstName"), is(equalTo("Robin")));
    }
}
