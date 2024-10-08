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
package org.metatype.sxc.jaxb.simple;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;
import org.metatype.sxc.jaxb.JAXBContextImpl;
import org.metatype.sxc.util.XoTestCase;
import org.w3c.dom.Document;

import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayOutputStream;

public class SimpleReadWriteTest extends XoTestCase {
    
    public void testJAXBContextUnmarshal() throws Exception {
        System.setProperty("org.metatype.sxc.output.directory", "target/tmp-jaxb");
        JAXBContext ctx = new JAXBContextImpl();
        
        XMLStreamReader reader = getXSR("<name>Dan</name>");
        JAXBElement<?> c = (JAXBElement<?>) ctx.createUnmarshaller().unmarshal(reader, 
                                                                               String.class);
        
        assertNotNull(c);
        assertEquals("Dan", c.getValue());
        
        Marshaller marshaller = ctx.createMarshaller();
        assertNotNull(marshaller);
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshaller.marshal(c, bos);
        
        Document d = readDocument(bos.toByteArray());
        assertValid("/name[text()='Dan']", d);
    }
}
