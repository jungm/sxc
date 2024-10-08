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
package org.metatype.sxc.jaxb;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.UnmarshallerHandler;
import org.metatype.sxc.jaxb.StaxContentHandler.StaxParser;

import javax.xml.stream.XMLEventReader;

public class UnmarshallerHandlerImpl extends StaxContentHandler implements UnmarshallerHandler, StaxParser {
    private final ExtendedUnmarshaller extendedUnmarshaller;
    private Class<?> type;
    private Object result;
    private JAXBException jaxbException;

    public UnmarshallerHandlerImpl(ExtendedUnmarshaller extendedUnmarshaller) {
        super();
        this.extendedUnmarshaller = extendedUnmarshaller;
        setStaxParser(this);
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public Object getResult() throws JAXBException, IllegalStateException {
        // cleanup worker thread
        destroy();

        // if we got a JAXBException, throw it
        if (jaxbException != null) {
            throw new JAXBException(jaxbException);
        }

        // if there is no result, we were never called in the first place (or an error occured)
        if (result == null) {
            throw new IllegalStateException("No result");
        }
        
        return result;
    }

    public void parse(XMLEventReader reader) {
        try {
            if (type == null) {
                result = extendedUnmarshaller.unmarshal(reader);
            } else {
                result = extendedUnmarshaller.unmarshal(reader, type);
            }
        } catch (JAXBException e) {
            jaxbException = e;
        }
    }
}
