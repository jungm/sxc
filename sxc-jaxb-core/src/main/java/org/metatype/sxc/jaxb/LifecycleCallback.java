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

import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class LifecycleCallback {
    public final Method beforeUnmarshal;
    public final Method afterUnmarshal;
    public final Method beforeMarshal;
    public final Method afterMarshal;

    public static final LifecycleCallback NONE = new LifecycleCallback(null, null, null, null);

    public LifecycleCallback(Method beforeUnmarshal, Method afterUnmarshal, Method beforeMarshal, Method afterMarshal) {
        this.beforeUnmarshal = beforeUnmarshal;
        this.afterUnmarshal = afterUnmarshal;
        this.beforeMarshal = beforeMarshal;
        this.afterMarshal = afterMarshal;
    }

    public LifecycleCallback(Class beanType) {
        if (beanType == null) throw new NullPointerException("clazz is null");

        beforeUnmarshal = getDeclaredMethod(beanType, "beforeUnmarshal", Unmarshaller.class, Object.class);
        afterUnmarshal = getDeclaredMethod(beanType, "afterUnmarshal", Unmarshaller.class, Object.class);
        beforeMarshal = getDeclaredMethod(beanType, "beforeMarshal", Marshaller.class);
        afterMarshal = getDeclaredMethod(beanType, "afterMarshal", Marshaller.class);
    }

    private static Method getDeclaredMethod(Class type, String name, Class ... parameterTypes) {
        Method method;
        try {
            method = type.getDeclaredMethod(name, parameterTypes);
        } catch (Exception ignored) {
            return null;
        }

        if (isPublic(method)) {
            try {
                method.setAccessible(true);
            } catch (Exception e) {
                throw new IllegalStateException("Unable to access non-public methods");
            }
        }
        return method;
    }

    public static boolean isPublic(Member member) {
        return member != null && (!Modifier.isPublic(member.getDeclaringClass().getModifiers()) || !Modifier.isPublic(member.getModifiers()));
    }

    public void beforeUnmarshal(Object bean, Unmarshaller unmarshaller, Object parent) throws Exception {
        if (beforeUnmarshal != null) {
            beforeUnmarshal.invoke(bean, unmarshaller, parent);
        }
    }

    public void afterUnmarshal(Object bean, Unmarshaller unmarshaller, Object parent) throws Exception {
        if (afterUnmarshal != null) {
            afterUnmarshal.invoke(bean, unmarshaller, parent);
        }
    }

    public void beforeMarshal(Object bean, Marshaller marshaller) throws Exception {
        if (beforeMarshal != null) {
            beforeMarshal.invoke(bean, marshaller);
        }
    }

    public void afterMarshal(Object bean, Marshaller marshaller) throws Exception {
        if (afterMarshal != null) {
            afterMarshal.invoke(bean, marshaller);
        }
    }

    public Method getBeforeUnmarshal() {
        return beforeUnmarshal;
    }

    public Method getAfterUnmarshal() {
        return afterUnmarshal;
    }

    public Method getBeforeMarshal() {
        return beforeMarshal;
    }

    public Method getAfterMarshal() {
        return afterMarshal;
    }
}