/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.metatype.sxc.jaxb.xmllist;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import org.metatype.sxc.jaxb.JAXBContextImpl;
import org.metatype.sxc.util.XoTestCase;
import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.List;

public class XmlListTest extends XoTestCase {
    protected JAXBContext ctx;

    public void testElement() throws Exception {
        XmlListElement element = (XmlListElement) ctx.createUnmarshaller().unmarshal(getClass().getResource("xmlList.xml"));
        assertNotNull(element);

        assertValues(element.booleanArray);
        assertValues(element.shortArray);
        assertValues(element.intArray);
        assertValues(element.longArray);
        assertValues(element.floatArray);
        assertValues(element.doubleArray);
        assertValues(element.stringArray, "stringArray");

        assertBooleanList(element.booleanList);
        assertShortList(element.shortList);
        assertIntegerList(element.intList);
        assertLongList(element.longList);
        assertFloatList(element.floatList);
        assertDoubleList(element.doubleList);
        assertStringList(element.stringList, "stringList");

        assertValues(element.booleanArrayAttribute);
        assertValues(element.shortArrayAttribute);
        assertValues(element.intArrayAttribute);
        assertValues(element.longArrayAttribute);
        assertValues(element.floatArrayAttribute);
        assertValues(element.doubleArrayAttribute);
        assertValues(element.stringArrayAttribute, "stringArrayAttribute");

        assertBooleanList(element.booleanListAttribute);
        assertShortList(element.shortListAttribute);
        assertIntegerList(element.intListAttribute);
        assertLongList(element.longListAttribute);
        assertFloatList(element.floatListAttribute);
        assertDoubleList(element.doubleListAttribute);
        assertStringList(element.stringListAttribute, "stringListAttribute");

        Marshaller marshaller = ctx.createMarshaller();
        assertNotNull(marshaller);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshaller.marshal(element, bos);

        Document d = readDocument(bos.toByteArray());
        assertValid("/xmlListElement/booleanArray[text()='" + arrayToString(element.booleanArray) + "']", d);
        assertValid("/xmlListElement/shortArray[text()='" + arrayToString(element.shortArray) + "']", d);
        assertValid("/xmlListElement/intArray[text()='" + arrayToString(element.intArray) + "']", d);
        assertValid("/xmlListElement/longArray[text()='" + arrayToString(element.longArray) + "']", d);
        assertValid("/xmlListElement/floatArray[text()='" + arrayToString(element.floatArray) + "']", d);
        assertValid("/xmlListElement/doubleArray[text()='" + arrayToString(element.doubleArray) + "']", d);
        assertValid("/xmlListElement/stringArray[text()='" + arrayToString(element.stringArray) + "']", d);

        assertValid("/xmlListElement/booleanList[text()='" + toString(element.booleanList) + "']", d);
        assertValid("/xmlListElement/shortList[text()='" + toString(element.shortList) + "']", d);
        assertValid("/xmlListElement/intList[text()='" + toString(element.intList) + "']", d);
        assertValid("/xmlListElement/longList[text()='" + toString(element.longList) + "']", d);
        assertValid("/xmlListElement/floatList[text()='" + toString(element.floatList) + "']", d);
        assertValid("/xmlListElement/doubleList[text()='" + toString(element.doubleList) + "']", d);
        assertValid("/xmlListElement/stringList[text()='" + toString(element.stringList) + "']", d);

        assertValid("/xmlListElement[@booleanArrayAttribute='" + arrayToString(element.booleanArrayAttribute) + "']", d);
        assertValid("/xmlListElement[@shortArrayAttribute='" + arrayToString(element.shortArrayAttribute) + "']", d);
        assertValid("/xmlListElement[@intArrayAttribute='" + arrayToString(element.intArrayAttribute) + "']", d);
        assertValid("/xmlListElement[@longArrayAttribute='" + arrayToString(element.longArrayAttribute) + "']", d);
        assertValid("/xmlListElement[@floatArrayAttribute='" + arrayToString(element.floatArrayAttribute) + "']", d);
        assertValid("/xmlListElement[@doubleArrayAttribute='" + arrayToString(element.doubleArrayAttribute) + "']", d);
        assertValid("/xmlListElement[@stringArrayAttribute='" + arrayToString(element.stringArrayAttribute) + "']", d);

        assertValid("/xmlListElement[@booleanListAttribute='" + toString(element.booleanListAttribute) + "']", d);
        assertValid("/xmlListElement[@shortListAttribute='" + toString(element.shortListAttribute) + "']", d);
        assertValid("/xmlListElement[@intListAttribute='" + toString(element.intListAttribute) + "']", d);
        assertValid("/xmlListElement[@longListAttribute='" + toString(element.longListAttribute) + "']", d);
        assertValid("/xmlListElement[@floatListAttribute='" + toString(element.floatListAttribute) + "']", d);
        assertValid("/xmlListElement[@doubleListAttribute='" + toString(element.doubleListAttribute) + "']", d);
        assertValid("/xmlListElement[@stringListAttribute='" + toString(element.stringListAttribute) + "']", d);

    }

    public void testValue() throws Exception {
        ListValues element = (ListValues) ctx.createUnmarshaller().unmarshal(getClass().getResource("listValues.xml"));
        assertNotNull(element);

        assertValues(element.booleanArrayValue.value);
        assertValues(element.shortArrayValue.value);
        assertValues(element.intArrayValue.value);
        assertValues(element.longArrayValue.value);
        assertValues(element.floatArrayValue.value);
        assertValues(element.doubleArrayValue.value);
        assertValues(element.stringArrayValue.value, "stringArray");

        assertBooleanList(element.booleanListValue.value);
        assertShortList(element.shortListValue.value);
        assertIntegerList(element.intListValue.value);
        assertLongList(element.longListValue.value);
        assertFloatList(element.floatListValue.value);
        assertDoubleList(element.doubleListValue.value);
        assertStringList(element.stringListValue.value, "stringListValue");

        Marshaller marshaller = ctx.createMarshaller();
        assertNotNull(marshaller);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshaller.marshal(element, bos);

        Document d = readDocument(bos.toByteArray());
        assertValid("/listValues/booleanArrayValue[text()='" + arrayToString(element.booleanArrayValue.value) + "']", d);
        assertValid("/listValues/shortArrayValue[text()='" + arrayToString(element.shortArrayValue.value) + "']", d);
        assertValid("/listValues/intArrayValue[text()='" + arrayToString(element.intArrayValue.value) + "']", d);
        assertValid("/listValues/longArrayValue[text()='" + arrayToString(element.longArrayValue.value) + "']", d);
        assertValid("/listValues/floatArrayValue[text()='" + arrayToString(element.floatArrayValue.value) + "']", d);
        assertValid("/listValues/doubleArrayValue[text()='" + arrayToString(element.doubleArrayValue.value) + "']", d);
        assertValid("/listValues/stringArrayValue[text()='" + arrayToString(element.stringArrayValue.value) + "']", d);

        assertValid("/listValues/booleanListValue[text()='" + toString(element.booleanListValue.value) + "']", d);
        assertValid("/listValues/shortListValue[text()='" + toString(element.shortListValue.value) + "']", d);
        assertValid("/listValues/intListValue[text()='" + toString(element.intListValue.value) + "']", d);
        assertValid("/listValues/longListValue[text()='" + toString(element.longListValue.value) + "']", d);
        assertValid("/listValues/floatListValue[text()='" + toString(element.floatListValue.value) + "']", d);
        assertValid("/listValues/doubleListValue[text()='" + toString(element.doubleListValue.value) + "']", d);
        assertValid("/listValues/stringListValue[text()='" + toString(element.stringListValue.value) + "']", d);

    }

    private void assertBooleanList(List<Boolean> array) {
        assertNotNull("array is null", array);
        for (int i = 0; i < 5; i++) {
            assertEquals(i % 2 == 0, (boolean) array.get(i));
        }
    }

    private void assertShortList(List<Short> array) {
        assertNotNull("array is null", array);
        for (int i = 0; i < 5; i++) {
            assertEquals(i, (short) array.get(i));
        }
    }

    private void assertIntegerList(List<Integer> array) {
        assertNotNull("array is null", array);
        for (int i = 0; i < 5; i++) {
            assertEquals(11 * i, (int) array.get(i));
        }
    }

    private void assertLongList(List<Long> array) {
        assertNotNull("array is null", array);
        for (int i = 0; i < 5; i++) {
            assertEquals(111 * i, (long) array.get(i));
        }
    }

    private void assertFloatList(List<Float> array) {
        assertNotNull("array is null", array);
        for (int i = 0; i < 5; i++) {
            assertEquals((float) (1.1 * i), array.get(i));
        }
    }

    private void assertDoubleList(List<Double> array) {
        assertNotNull("array is null", array);
        for (int i = 0; i < 5; i++) {
            assertEquals(1.11 * i, array.get(i));
        }
    }

    private void assertStringList(List<String> array, String name) {
        assertNotNull("array is null", array);
        for (int i =0; i < 5; i++) {
            assertEquals(name + i, array.get(i));
        }
    }

    private void assertValues(boolean[] array) {
        assertNotNull("array is null", array);
        for (int i = 0; i < 5; i++) {
            assertEquals(i % 2 == 0, array[i]);
        }
    }

    private void assertValues(short[] array) {
        assertNotNull("array is null", array);
        for (int i = 0; i < 5; i++) {
            assertEquals(i, array[i]);
        }
    }

    private void assertValues(int[] array) {
        assertNotNull("array is null", array);
        for (int i = 0; i < 5; i++) {
            assertEquals(11 * i, array[i]);
        }
    }

    private void assertValues(long[] array) {
        assertNotNull("array is null", array);
        for (int i = 0; i < 5; i++) {
            assertEquals(111 * i, array[i]);
        }
    }

    private void assertValues(float[] array) {
        assertNotNull("array is null", array);
        for (int i = 0; i < 5; i++) {
            assertEquals((float) (1.1 * i), array[i]);
        }
    }

    private void assertValues(double[] array) {
        assertNotNull("array is null", array);
        for (int i = 0; i < 5; i++) {
            assertEquals(1.11 * i, array[i]);
        }
    }

    private void assertValues(String[] array, String name) {
        assertNotNull("array is null", array);
        for (int i =0; i < 5; i++) {
            assertEquals(name + i, array[i]);
        }
    }

    private String toString(List list) {
        StringBuilder builder = new StringBuilder();
        for (Object o : list) {
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(o);
        }
        return builder.toString();
    }

    private String arrayToString(Object array) {
        StringBuilder builder = new StringBuilder();
        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                builder.append(" ");
            }
            builder.append(Array.get(array, i));
        }
        return builder.toString();
    }

    public void setUp() throws Exception {
        super.setUp();
        System.setProperty("org.metatype.sxc.output.directory", "target/tmp-jaxb");
        ctx = JAXBContextImpl.newSxcInstance(XmlListElement.class, ListValues.class);
    }
}
