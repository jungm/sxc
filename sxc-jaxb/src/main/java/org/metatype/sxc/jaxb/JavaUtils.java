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

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class JavaUtils {

    /** Use this character as suffix */
    static final char KEYWORD_PREFIX = '_';

    /**
     * These are java keywords as specified at the following URL.
     * http://java.sun.com/docs/books/jls/second_edition/html/lexical.doc.html#229308
     * Note that false, true, and null are not strictly keywords; they are
     * literal values, but for the purposes of this array, they can be treated
     * as literals.
     */
    private static final Set<String> KEYWORDS = new HashSet<String>(Arrays
        .asList("abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
                "continue", "default", "do", "double", "else", "enum", "extends", "false", "final",
                "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int",
                "interface", "long", "native", "new", "null", "package", "private", "protected", "public",
                "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw",
                "throws", "transient", "true", "try", "void", "volatile", "while"));

    private JavaUtils() {
    }

    /**
     * checks if the input string is a valid java keyword.
     * 
     * @return boolean true/false
     */
    public static boolean isJavaKeyword(String keyword) {
        return KEYWORDS.contains(keyword);
    }

    /**
     * Turn a java keyword string into a non-Java keyword string. (Right now
     * this simply means appending an underscore.)
     */
    public static String makeNonJavaKeyword(String keyword) {
        return KEYWORD_PREFIX + keyword;
    }

    public static Class toClass(Type type) {
        // GenericArrayType, ParameterizedType, TypeVariable<D>, WildcardType
        if (type instanceof Class) {
            Class clazz = (Class) type;
            return clazz;
        } else if (type instanceof GenericArrayType) {
            GenericArrayType arrayType = (GenericArrayType) type;
            Class componentType = toClass(arrayType.getGenericComponentType());
            return Array.newInstance(componentType, 0).getClass();
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return toClass(parameterizedType.getRawType());
        } else {
            return Object.class;
        }
    }

    static Class toPrimitiveWrapper(Class type) {
        if (type.equals(boolean.class)) {
            return Boolean.class;
        } else if (type.equals(byte.class)) {
            return Byte.class;
        } else if (type.equals(char.class)) {
            return Character.class;
        } else if (type.equals(short.class)) {
            return Short.class;
        } else if (type.equals(int.class)) {
            return Integer.class;
        } else if (type.equals(long.class)) {
            return Long.class;
        } else if (type.equals(float.class)) {
            return Float.class;
        } else if (type.equals(double.class)) {
            return Double.class;
        }
        return type;
    }

    public static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        if (Character.isUpperCase(name.charAt(0))) {
            return name;
        }
        char chars[] = name.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    public static boolean isPrivate(Member member) {
        return member != null && (Modifier.isPrivate(member.getDeclaringClass().getModifiers()) || Modifier.isPrivate(member.getModifiers()) || Modifier.isFinal(member.getModifiers()));
    }
}
