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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayOutputStream;

public class SimpleGlobalTest extends XoTestCase {
    
    public void testJAXBContextUnmarshal() throws Exception {
        System.setProperty("org.metatype.sxc.output.directory", "target/tmp-jaxb");
        JAXBContext ctx = JAXBContextImpl.newSxcInstance("com.everything", getClass().getClassLoader(), null);
        
        XMLStreamReader reader = getXSR("<string xmlns=\"http://everything.com\">Dan</string>");
        JAXBElement<?> c = (JAXBElement<?>) ctx.createUnmarshaller().unmarshal(reader);
        
        assertNotNull(c);
        assertEquals("Dan", c.getValue());
        assertEquals(new QName("http://everything.com", "string"), c.getName());
        
        Marshaller marshaller = ctx.createMarshaller();
        assertNotNull(marshaller);
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshaller.marshal(c, bos);
        
        Document d = readDocument(bos.toByteArray());
        addNamespace("e", "http://everything.com");
        assertValid("/e:string[text()='Dan']", d);
    }

}
