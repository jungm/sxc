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
package org.metatype.sxc.jaxb.model;

import javax.xml.namespace.QName;
import java.util.LinkedHashMap;
import java.util.Map;

public class EnumInfo {
    /**
     * The model that owns this enum.
     */
    private final Model model;

    /**
     * The enum class.
     */
    private final Class<?> type;

    /**
     * Used for xsi:type checks.
     */
    private QName schemaTypeName;

    /**
     * If this bean can be a root element, this is the name.
     */
    private QName rootElementName;

    /**
     * Map from enum constant to xml representation
     */
    private final Map<Enum, String> enumMap = new LinkedHashMap<Enum, String>();

    public EnumInfo(Model model, Class<?> type) {
        if (model == null) throw new NullPointerException("model is null");
        if (type == null) throw new NullPointerException("type is null");
        this.model = model;
        this.type = type;
    }

    public Model getModel() {
        return model;
    }

    public Class<?> getType() {
        return type;
    }

    public QName getSchemaTypeName() {
        return schemaTypeName;
    }

    public void setSchemaTypeName(QName schemaTypeName) {
        this.schemaTypeName = schemaTypeName;
    }

    public QName getRootElementName() {
        return rootElementName;
    }

    public void setRootElementName(QName rootElementName) {
        this.rootElementName = rootElementName;
    }

    public Map<Enum, String> getEnumMap() {
        return enumMap;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EnumInfo enumInfo = (EnumInfo) o;

        return type.equals(enumInfo.type);
    }

    public int hashCode() {
        return type.hashCode();
    }

    public String toString() {
        return type.getName();
    }
}