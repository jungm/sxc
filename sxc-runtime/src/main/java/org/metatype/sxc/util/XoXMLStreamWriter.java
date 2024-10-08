package org.metatype.sxc.util;

import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Collection;

public interface XoXMLStreamWriter extends XMLStreamWriter {
    void writeString(String s) throws XMLStreamException;
    void writeInt(int i) throws XMLStreamException;
    void writeBoolean(boolean b) throws XMLStreamException;
    void writeLong(long b) throws XMLStreamException;
    void writeFloat(float b) throws XMLStreamException;
    void writeShort(short b) throws XMLStreamException;
    void writeDouble(double b) throws XMLStreamException;
    void writeByte(byte b) throws XMLStreamException;
    void writeQName(QName q) throws XMLStreamException;
    String getQNameAsString(QName q) throws XMLStreamException;
    void writeDomElement(Element element, boolean writeTag) throws XMLStreamException;

    void writeAttribute(QName name, String value) throws XMLStreamException;
    void writeStartElementWithAutoPrefix(String namespaceURI, String localName) throws XMLStreamException;

    void writeXsiNil() throws XMLStreamException; 
    void writeXsiType(String namespace, String local) throws XMLStreamException;
    
    void writeAndDeclareIfUndeclared(String prefix, String namespace) throws XMLStreamException ;

    String getUniquePrefix(String namespaceURI) throws XMLStreamException;

    void writeAsXmlList(Collection<Object> values) throws XMLStreamException;
    void writeAsXmlList(Object[] values) throws XMLStreamException;
    void writeAsXmlList(boolean[] values) throws XMLStreamException;
    void writeAsXmlList(short[] values) throws XMLStreamException;
    void writeAsXmlList(int[] values) throws XMLStreamException;
    void writeAsXmlList(long[] values) throws XMLStreamException;
    void writeAsXmlList(float[] values) throws XMLStreamException;
    void writeAsXmlList(double[] values) throws XMLStreamException;
}
