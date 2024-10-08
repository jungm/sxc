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
package org.metatype.sxc.jaxb.collection;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.metatype.sxc.jaxb.JAXBContextImpl;
import org.metatype.sxc.util.XoTestCase;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collection;

public class CollectionsTest extends XoTestCase {
    protected JAXBContextImpl ctx;

    public void testFields() throws Exception {
        // load collections object
        Collections collections = (Collections) ctx.createUnmarshaller().unmarshal(getClass().getResourceAsStream("collections.xml"));
        assertNotNull(collections);

        // verify all fields loaded correctly
        assertValues(collections.collectionField, "collection-field");
        assertValues(collections.listField, "listField");
        assertValues(collections.setField, "setField");
        assertValues(collections.sortedSetField, "sortedSetField");
        assertValues(collections.queueField, "queueField");
        assertValues(collections.linkedHashSetField, "linkedHashSetField");
        assertValues(collections.linkedListField, "linkedListField");
        assertValues(collections.customCollectionField, "customCollectionField");
        assertValues(collections.initializedField, "initializedField");
        assertValues(collections.finalField, "finalField");

        // verify all properties loaded correctly
        assertValues(collections.getCollectionProperty(), "collection-property");
        assertValues(collections.getListProperty(), "listProperty");
        assertValues(collections.getSetProperty(), "setProperty");
        assertValues(collections.getSortedSetProperty(), "sortedSetProperty");
        assertValues(collections.getQueueProperty(), "queueProperty");
        assertValues(collections.getLinkedHashSetProperty(), "linkedHashSetProperty");
        assertValues(collections.getLinkedListProperty(), "linkedListProperty");
        assertValues(collections.getCustomCollectionProperty(), "customCollectionProperty");
        assertValues(collections.getInitializedProperty(), "initializedProperty");
        assertValues(collections.getFinalProperty(), "finalProperty");

        // verify initialized instances didn't change
        assertSame(collections.initializedField, Collections.INITIALIZED_FIELD);
        assertSame(collections.getInitializedProperty(), Collections.INITIALIZED_PROPERTY);
        assertSame(collections.getFinalProperty(), Collections.FINAL_PROPERTY);

        // Fill the unknown and uncreatable collections
        collections.uncreatableCollectionField = new Collections.UncreatableCollection<String>(42);
        addValues(collections.uncreatableCollectionField, "uncreatableCollectionField");
        collections.unknownCollectionField = new Collections.UnknownCollectionImpl<String>();
        addValues(collections.unknownCollectionField, "unknownCollectionField");
        collections.setUncreatableCollectionProperty(new Collections.UncreatableCollection<String>(42));
        addValues(collections.getUncreatableCollectionProperty(), "uncreatableCollectionProperty");
        collections.setUnknownCollectionProperty(new Collections.UnknownCollectionImpl<String>());
        addValues(collections.getUnknownCollectionProperty(), "unknownCollectionProperty");

        Marshaller marshaller = ctx.createMarshaller();
        assertNotNull(marshaller);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshaller.marshal(collections, bos);

        Document d = readDocument(bos.toByteArray());
        assertValues(d, "collection-field");
        assertValues(d, "listField");
        assertValues(d, "setField");
        assertValues(d, "sortedSetField");
        assertValues(d, "queueField");
        assertValues(d, "linkedHashSetField");
        assertValues(d, "linkedListField");
        assertValues(d, "customCollectionField");
        assertValues(d, "initializedField");
        assertValues(d, "finalField");
        assertValues(d, "uncreatableCollectionField");
        assertValues(d, "unknownCollectionField");

        assertValues(d, "collection-property");
        assertValues(d, "listProperty");
        assertValues(d, "setProperty");
        assertValues(d, "sortedSetProperty");
        assertValues(d, "queueProperty");
        assertValues(d, "linkedHashSetProperty");
        assertValues(d, "linkedListProperty");
        assertValues(d, "customCollectionProperty");
        assertValues(d, "initializedProperty");
        assertValues(d, "finalProperty");
        assertValues(d, "uncreatableCollectionProperty");
        assertValues(d, "unknownCollectionProperty");

    }

    public void testUncreatableCollection() throws Exception {
        assertLoad("collection-field", true); // just verify the code works
        assertLoad("uncreatableCollectionField", false);
        assertLoad("uncreatableCollectionProperty", false);
    }

    public void testUnknownCollection() throws Exception {
        assertLoad("collection-field", true); // just verify the code works
        assertLoad("unknownCollectionField", false);
        assertLoad("unknownCollectionProperty", false);
    }

    private void assertValues(Collection<String> collection, String name) {
        assertNotNull("collection is null", collection);
        for (int i =0; i < 5; i++) {
            assertTrue("Expected collection " + name + " to contain value " + name + i, collection.contains(name + i));
        }
    }

    private void assertValues(Document d, String name) throws Exception {
        for (int i =0; i < 5; i++) {
            assertValid("/collections/" + name + "[text()='" + name + i + "']", d);
        }
    }

    private void addValues(Collection<String> c, String name) throws Exception {
        for (int i =0; i < 5; i++) {
            c.add(name + i);
        }
    }

    public void setUp() throws Exception {
        super.setUp();
        System.setProperty("org.metatype.sxc.output.directory", "target/tmp-jaxb");
        ctx = new JAXBContextImpl(Collections.class);
    }

    private void assertLoad(String name, boolean shouldLoad) {
        try {
            String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<collections>" +
                    "    <" + name + ">value</" + name + ">" +
                    "</collections>";

            ctx.createUnmarshaller().unmarshal(new ByteArrayInputStream(xml.getBytes()));
            if (!shouldLoad) fail("Expected to NOT be able to load xml containing a " + name + " element");
        } catch (JAXBException e) {
            if (shouldLoad) fail("Expected to be able to load xml containing a " + name + " element");
        }
    }
}