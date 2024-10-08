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
package org.metatype.sxc.jaxb.root;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.UnmarshalException;
import org.metatype.node.NamedNode;
import org.metatype.sxc.jaxb.JAXBContextImpl;
import org.metatype.sxc.util.XoTestCase;
import org.w3c.dom.Document;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static java.beans.Introspector.decapitalize;

public class RootTest extends XoTestCase {
    public void setUp() throws Exception {
        super.setUp();
        System.setProperty("org.metatype.sxc.output.directory", "target/tmp-jaxb");
    }

    /**
     * NoRoot has no element declaration so xsi:type must be used for JaxB to recognize the type.
     */
    public void testNoRoot() throws Exception {
        JAXBContext ctx = JAXBContextImpl.newSxcInstance(NoRoot.class);

        JAXBElement<NoRoot> element = asJAXBElement(ctx.createUnmarshaller().unmarshal(getClass().getResource("noRoot.xml")));

        assertNotNull(element);
        NoRoot noRoot = element.getValue();
        assertEquals("no root", noRoot.getName());

        Marshaller marshaller = ctx.createMarshaller();
        assertNotNull(marshaller);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshaller.marshal(element, bos);

        Document d = readDocument(bos.toByteArray());
        addNamespace("n", "http://metatype.org/root");
        addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        assertValid("/n:NoRoot", d);
        assertValid("/n:NoRoot[@xsi:type='NoRootType']", d);
        assertValid("/n:NoRoot/n:name", d);
        assertValid("/n:NoRoot/n:name[text()='no root']", d);
    }

    /**
     * AnnotatedRoot has @XmlRootElement annotation
     */
    public void testAnnotatedRoot() throws Exception {
        JAXBContext ctx = JAXBContextImpl.newSxcInstance(AnnotatedRoot.class);

        AnnotatedRoot annotatedRoot = (AnnotatedRoot) ctx.createUnmarshaller().unmarshal(getClass().getResource("annotatedRoot.xml"));

        assertEquals("annotated root", annotatedRoot.getName());

        Marshaller marshaller = ctx.createMarshaller();
        assertNotNull(marshaller);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshaller.marshal(annotatedRoot, bos);

        Document d = readDocument(bos.toByteArray());
        addNamespace("n", "http://metatype.org/root");
        assertValid("/n:annotated-root", d);
        assertValid("/n:annotated-root/n:name", d);
        assertValid("/n:annotated-root/n:name[text()='annotated root']", d);
    }

    /**
     * ObjectFactoryRoot has @XmlElementDecl annotatated method in ObjectFactory.
     * Test is initalized with packakage name.
     */
    public void testObjectFactoryRootPackage() throws Exception {
        JAXBContext ctx = JAXBContextImpl.newSxcInstance("org.metatype.sxc.jaxb.root");
        testObjectFactoryRootContext(ctx, "object-factory-root", "objectFactoryRoot.xml");
    }

    /**
     * ObjectFactoryRoot has @XmlElementDecl annotatated method in ObjectFactory.
     * Test is initalized with ObjectFactory class.
     */
    public void testObjectFactoryRootObjectFactory() throws Exception {
        JAXBContext ctx = JAXBContextImpl.newSxcInstance(ObjectFactory.class);
        testObjectFactoryRootContext(ctx, "object-factory-root", "objectFactoryRoot.xml");
    }

    /**
     * ObjectFactoryRoot has @XmlElementDecl annotatated method in ObjectFactory.
     * Test is initalized with ObjectFactoryRoot class which does not have @XmlRootElement annotation.
     * Since the JAXBContext does not load the package ObjectFactory, the ObjectFactoryRoot class
     * has no root elements and test should throw an exception.
     */
    public void testObjectFactoryRootClass() throws Exception {
        JAXBContext ctx = JAXBContextImpl.newSxcInstance(ObjectFactoryRoot.class);
        try {
            testObjectFactoryRootContext(ctx, "object-factory-root", "objectFactoryRoot.xml");
            fail("excpected UnmarshalException");
        } catch (UnmarshalException expected) {
            // expected
        }
    }

    /**
     * Alternate root element name.
     * Test is initalized with packakage name.
     */
    public void testAlternateRootNamePackage() throws Exception {
        JAXBContext ctx = JAXBContextImpl.newSxcInstance("org.metatype.sxc.jaxb.root");
        testObjectFactoryRootContext(ctx, "alternate-root-name", "alternateRootName.xml");
    }

    /**
     * Alternate root element name
     * Test is initalized with ObjectFactory class.
     */
    public void testAlternateRootNameObjectFactory() throws Exception {
        JAXBContext ctx = JAXBContextImpl.newSxcInstance(ObjectFactory.class);
        testObjectFactoryRootContext(ctx, "alternate-root-name", "alternateRootName.xml");
    }

    private void testObjectFactoryRootContext(JAXBContext ctx, String rootName, String fileName) throws Exception {
        JAXBElement<ObjectFactoryRoot> element = asJAXBElement(ctx.createUnmarshaller().unmarshal(getClass().getResource(fileName)));

        assertNotNull(element);
        ObjectFactoryRoot objectFactoryRoot = element.getValue();
        assertEquals(rootName.replace('-', ' '), objectFactoryRoot.getName());

        Marshaller marshaller = ctx.createMarshaller();
        assertNotNull(marshaller);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshaller.marshal(element, bos);

        Document d = readDocument(bos.toByteArray());
        addNamespace("n", "http://metatype.org/root");
        String rootPath = "/n:" + rootName;
        assertValid(rootPath, d);
        assertValid(rootPath + "/n:name", d);
        assertValid(rootPath +"/n:name[text()='" + rootName.replace('-', ' ') + "']", d);
    }

    /**
     * Root element from another package
     */
    public void testExternalRoot() throws Exception {
        JAXBContext ctx = JAXBContextImpl.newSxcInstance(ObjectFactory.class);

        JAXBElement<NamedNode> element = asJAXBElement(ctx.createUnmarshaller().unmarshal(getClass().getResource("externalRoot.xml")));

        assertNotNull(element);
        NamedNode namedNode = element.getValue();
        assertEquals("external root", namedNode.getName());

        Marshaller marshaller = ctx.createMarshaller();
        assertNotNull(marshaller);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshaller.marshal(element, bos);

        Document d = readDocument(bos.toByteArray());
        addNamespace("n", "http://metatype.org/node");
        assertValid("/n:external-root", d);
        assertValid("/n:external-root/n:name", d);
        assertValid("/n:external-root/n:name[text()='external root']", d);
    }

    /**
     * NoEnumRoot has no element declaration so xsi:type must be used for JaxB to recognize the type.
     */
    public void testNoEnumRoot() throws Exception {
        JAXBContext ctx = JAXBContextImpl.newSxcInstance(NoEnumRoot.class);

        JAXBElement<NoEnumRoot> element = asJAXBElement(ctx.createUnmarshaller().unmarshal(getClass().getResource("noEnumRoot.xml")));

        assertNotNull(element);
        NoEnumRoot noEnumRoot = element.getValue();
        assertNotNull(noEnumRoot);
        assertSame(NoEnumRoot.TRUE, noEnumRoot);

        Marshaller marshaller = ctx.createMarshaller();
        assertNotNull(marshaller);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshaller.marshal(element, bos);

        Document d = readDocument(bos.toByteArray());
        addNamespace("n", "http://metatype.org/root");
        addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        assertValid("/n:NoEnumRoot", d);
        assertValid("/n:NoEnumRoot[@xsi:type='NoEnumRootType']", d);
        assertValid("/n:NoEnumRoot[text()='no enum root']", d);
    }

    /**
     * AnnotatedEnumRoot has @XmlRootElement annotation
     */
    public void testAnnotatedEnumRoot() throws Exception {
        JAXBContext ctx = JAXBContextImpl.newSxcInstance(AnnotatedEnumRoot.class);

        AnnotatedEnumRoot annotatedEnumRoot = (AnnotatedEnumRoot) ctx.createUnmarshaller().unmarshal(getClass().getResource("annotatedEnumRoot.xml"));

        assertNotNull(annotatedEnumRoot);
        assertSame(AnnotatedEnumRoot.TRUE, annotatedEnumRoot);

        Marshaller marshaller = ctx.createMarshaller();
        assertNotNull(marshaller);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshaller.marshal(annotatedEnumRoot, bos);

        Document d = readDocument(bos.toByteArray());
        addNamespace("n", "http://metatype.org/root");
        assertValid("/n:annotated-enum-root", d);
        assertValid("/n:annotated-enum-root[text()='annotated enum root']", d);
    }

    /**
     * ObjectFactoryEnumRoot has @XmlElementDecl annotatated method in ObjectFactory.
     * Test is initalized with packakage name.
     */
    public void testObjectFactoryEnumRootPackage() throws Exception {
        JAXBContext ctx = JAXBContextImpl.newSxcInstance("org.metatype.sxc.jaxb.root");
        testObjectFactoryEnumRootContext(ctx, "object-factory-enum-root", "objectFactoryEnumRoot.xml");
    }

    /**
     * ObjectFactoryEnumRoot has @XmlElementDecl annotatated method in ObjectFactory.
     * Test is initalized with ObjectFactory class.
     */
    public void testObjectFactoryEnumRootObjectFactory() throws Exception {
        JAXBContext ctx = JAXBContextImpl.newSxcInstance(ObjectFactory.class);
        testObjectFactoryEnumRootContext(ctx, "object-factory-enum-root", "objectFactoryEnumRoot.xml");
    }

    /**
     * ObjectFactoryEnumRoot has @XmlElementDecl annotatated method in ObjectFactory.
     * Test is initalized with ObjectFactoryEnumRoot class which does not have @XmlRootElement annotation.
     * Since the JAXBContext does not load the package ObjectFactory, the ObjectFactoryEnumRoot class
     * has no root elements and test should throw an exception.
     */
    public void testObjectFactoryEnumRootClass() throws Exception {
        JAXBContext ctx = JAXBContextImpl.newSxcInstance(ObjectFactoryEnumRoot.class);
        try {
            testObjectFactoryEnumRootContext(ctx, "object-factory-enum-root", "objectFactoryEnumRoot.xml");
            fail("excpected UnmarshalException");
        } catch (UnmarshalException expected) {
            // expected
        }
    }

    /**
     * Alternate enum root element name.
     * Test is initalized with packakage name.
     */
    public void testAlternateEnumRootNamePackage() throws Exception {
        JAXBContext ctx = JAXBContextImpl.newSxcInstance("org.metatype.sxc.jaxb.root");
        testObjectFactoryEnumRootContext(ctx, "alternate-enum-root-name", "alternateEnumRootName.xml");
    }

    /**
     * Alternate enum root element name
     * Test is initalized with ObjectFactory class.
     */
    public void testAlternateEnumRootNameObjectFactory() throws Exception {
        JAXBContext ctx = JAXBContextImpl.newSxcInstance(ObjectFactory.class);
        testObjectFactoryEnumRootContext(ctx, "alternate-enum-root-name", "alternateEnumRootName.xml");
    }

    private void testObjectFactoryEnumRootContext(JAXBContext ctx, String rootName, String fileName) throws Exception {
        JAXBElement<ObjectFactoryEnumRoot> element = asJAXBElement(ctx.createUnmarshaller().unmarshal(getClass().getResource(fileName)));

        assertNotNull(element);
        ObjectFactoryEnumRoot objectFactoryEnumRoot = element.getValue();

        assertNotNull(objectFactoryEnumRoot);
        assertEquals(ObjectFactoryEnumRoot.TRUE, objectFactoryEnumRoot);

        Marshaller marshaller = ctx.createMarshaller();
        assertNotNull(marshaller);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshaller.marshal(element, bos);

        Document d = readDocument(bos.toByteArray());
        addNamespace("n", "http://metatype.org/root");
        assertValid("/n:" + rootName, d);
        assertValid("/n:" + rootName +"[text()='object factory enum root']", d);
    }

    public void testStringRoot() throws Exception {
        JAXBContext ctx = JAXBContextImpl.newSxcInstance(ObjectFactory.class);
        testStandardTypeRootContext(ctx, String.class, "test string");
    }

    public void testShortRoot() throws Exception {
        JAXBContext ctx = JAXBContextImpl.newSxcInstance(ObjectFactory.class);
        testStandardTypeRootContext(ctx, Short.class, (short) 42);
    }

    public void testIntegerRoot() throws Exception {
        JAXBContext ctx = JAXBContextImpl.newSxcInstance(ObjectFactory.class);
        testStandardTypeRootContext(ctx, Integer.class, 42);
    }

    public void testLongRoot() throws Exception {
        JAXBContext ctx = JAXBContextImpl.newSxcInstance(ObjectFactory.class);
        testStandardTypeRootContext(ctx, Long.class, (long) 42);
    }

    public void testBigintegerRoot() throws Exception {
        JAXBContext ctx = JAXBContextImpl.newSxcInstance(ObjectFactory.class);
        testStandardTypeRootContext(ctx, BigInteger.class, new BigInteger("12345678901234567890"));
    }

    public void testFloatRoot() throws Exception {
        JAXBContext ctx = JAXBContextImpl.newSxcInstance(ObjectFactory.class);
        testStandardTypeRootContext(ctx, Float.class, (float) 42.42);
    }

    public void testDoubleRoot() throws Exception {
        JAXBContext ctx = JAXBContextImpl.newSxcInstance(ObjectFactory.class);
        testStandardTypeRootContext(ctx, Double.class, 42.0042);
    }

    public void testBigdecimalRoot() throws Exception {
        JAXBContext ctx = JAXBContextImpl.newSxcInstance(ObjectFactory.class);
        testStandardTypeRootContext(ctx, BigDecimal.class, new BigDecimal("12345678901234567890.01234567890123456789"));
    }

    public void testBooleanRoot() throws Exception {
        JAXBContext ctx = JAXBContextImpl.newSxcInstance(ObjectFactory.class);
        testStandardTypeRootContext(ctx, Boolean.class, true);
    }

    public void testCalendarRoot() throws Exception {
        JAXBContext ctx = JAXBContextImpl.newSxcInstance(ObjectFactory.class);
        GregorianCalendar calendar = new GregorianCalendar();

        String xmlValue = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar).toString();
        String xml = "<calendar-root xmlns=\"http://metatype.org/root\">\n" +
                "    " + xmlValue + "\n" +
                "</calendar-root>";

        JAXBElement<Calendar> element = asJAXBElement(ctx.createUnmarshaller().unmarshal(new ByteArrayInputStream(xml.getBytes())));

        assertNotNull(element);
        Calendar actualValue = element.getValue();

        assertNotNull(actualValue);
        // compare using java.util.Date
        assertEquals(calendar.getTime(), actualValue.getTime());

        Marshaller marshaller = ctx.createMarshaller();
        assertNotNull(marshaller);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshaller.marshal(new JAXBElement<Calendar>(element.getName(), Calendar.class, actualValue), bos);

        Document d = readDocument(bos.toByteArray());
        addNamespace("n", "http://metatype.org/root");
        assertValid("/n:calendar-root", d);
        assertValid("/n:calendar-root[text()='" + xmlValue + "']", d);
    }

    public void testDateRoot() throws Exception {
        JAXBContext ctx = JAXBContextImpl.newSxcInstance(ObjectFactory.class);
        Date date = new Date();
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        testStandardTypeRootContext(ctx, Date.class, date, DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar).toString());
    }

    public void testQNameRoot() throws Exception {
        JAXBContext ctx = JAXBContextImpl.newSxcInstance(ObjectFactory.class);

        QName expectedValue = new QName(ObjectFactory.ROOT_URI, "qname", "");
        String xml = "<QName-root xmlns=\"http://metatype.org/root\" xmlns:x=\"http://metatype.org/root\">\n" +
                "    x:" + expectedValue.getLocalPart() + "\n" +
                "</QName-root>";

        JAXBElement<QName> element = asJAXBElement(ctx.createUnmarshaller().unmarshal(new ByteArrayInputStream(xml.getBytes())));

        assertNotNull(element);
        QName actualValue = element.getValue();

        assertNotNull(actualValue);
        assertEquals(expectedValue, actualValue);

        Marshaller marshaller = ctx.createMarshaller();
        assertNotNull(marshaller);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshaller.marshal(element, bos);

        Document d = readDocument(bos.toByteArray());
        addNamespace("n", "http://metatype.org/root");
        assertValid("/n:QName-root", d);
        assertValid("/n:QName-root[text()='x:" + expectedValue.getLocalPart() + "']", d);
    }

    public void testURIRoot() throws Exception {
        JAXBContext ctx = JAXBContextImpl.newSxcInstance(ObjectFactory.class);
        testStandardTypeRootContext(ctx, URI.class, new URI("scheme", "userInfo", "host", 42, "/path", "query", "fragment"));
    }

    public void testDurationRoot() throws Exception {
        JAXBContext ctx = JAXBContextImpl.newSxcInstance(ObjectFactory.class);
        testStandardTypeRootContext(ctx, Duration.class, DatatypeFactory.newInstance().newDuration(4200));
    }

    public void testXMLGregorianCalendarRoot() throws Exception {
        JAXBContext ctx = JAXBContextImpl.newSxcInstance(ObjectFactory.class);
        testStandardTypeRootContext(ctx, XMLGregorianCalendar.class, DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
    }

    private <T> void testStandardTypeRootContext(JAXBContext ctx, Class<T> type, T expectedValue) throws Exception {
        testStandardTypeRootContext(ctx, type, expectedValue, expectedValue.toString());
    }

    private <T> void testStandardTypeRootContext(JAXBContext ctx, Class<T> type, T expectedValue, String xmlValue) throws Exception {
        String elementName = decapitalize(type.getSimpleName()) + "-root";
        String xml = "<" + elementName + " xmlns=\"http://metatype.org/root\">" + xmlValue + "</" + elementName + ">";

        JAXBElement<T> element = asJAXBElement(ctx.createUnmarshaller().unmarshal(new ByteArrayInputStream(xml.getBytes())));

        assertNotNull(element);
        T actualValue = element.getValue();

        assertNotNull(actualValue);
        assertEquals(expectedValue, actualValue);

        Marshaller marshaller = ctx.createMarshaller();
        assertNotNull(marshaller);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        marshaller.marshal(new JAXBElement<T>(element.getName(), type, actualValue), bos);

        Document d = readDocument(bos.toByteArray());
        addNamespace("n", "http://metatype.org/root");
        assertValid("/n:" + elementName, d);
        assertValid("/n:" + elementName +"[text()='" + xmlValue + "']", d);
    }

    @SuppressWarnings("unchecked")
    private <T> JAXBElement<T> asJAXBElement(Object object) {
        assertTrue("object should be an instance of JAXBElement", object instanceof JAXBElement);
        return (JAXBElement<T>) object;
    }
}