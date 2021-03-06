/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util;

import junit.framework.TestCase;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Enumeration;

public class ClassLoaderUtilTest extends TestCase {

    public void testGetResources() throws IOException {
        Iterator<URL> i = ClassLoaderUtil.getResources("xwork-sample.xml", ClassLoaderUtilTest.class, false);
        assertNotNull(i);
        
        assertTrue(i.hasNext());
        URL url = i.next();
        assertTrue(url.toString().endsWith("xwork-sample.xml"));
        assertTrue(!i.hasNext());
    }
    
    public void testGetResources_Multiple() throws IOException {
        Iterator<URL> i = ClassLoaderUtil.getResources("xwork-1.0.dtd", ClassLoaderUtilTest.class, false);
        assertNotNull(i);
        
        assertTrue(i.hasNext());
        URL url = i.next();
        assertTrue(url.toString().endsWith("xwork-1.0.dtd"));
        url = i.next();
        assertTrue(url.toString().endsWith("xwork-1.0.dtd"));
        assertTrue(!i.hasNext());
    }

    public void testGetResources_Aggregate() throws IOException {
        Iterator<URL> i = ClassLoaderUtil.getResources("xwork-1.0.dtd", ClassLoaderUtilTest.class, true);
        assertNotNull(i);

        assertTrue(i.hasNext());
        URL url = i.next();
        assertTrue(url.toString().endsWith("xwork-1.0.dtd"));
        url = i.next();
        assertTrue(url.toString().endsWith("xwork-1.0.dtd"));
        assertTrue(!i.hasNext());
    }

    public void testGetResources_None() throws IOException {
        Iterator<URL> i = ClassLoaderUtil.getResources("asdfasdf.html", ClassLoaderUtilTest.class, false);
        assertNotNull(i);
        
        assertTrue(!i.hasNext());
    }

    public void testGetResource() {
        URL url = ClassLoaderUtil.getResource("xwork-sample.xml", ClassLoaderUtilTest.class);
        assertNotNull(url);
        
        assertTrue(url.toString().endsWith("xwork-sample.xml"));
    }
    
    public void testGetResource_None() {
        URL url = ClassLoaderUtil.getResource("asf.xml", ClassLoaderUtilTest.class);
        assertNull(url);
    }

    public void testAggregateIterator() {
       ClassLoaderUtil.AggregateIterator<String> aggr = new ClassLoaderUtil.AggregateIterator<String>();

       Enumeration en1 = new Enumeration() {
           private Iterator itt = Arrays.asList("str1", "str1", "str3", "str1").iterator();
           public boolean hasMoreElements() {
               return itt.hasNext();
           }

           public Object nextElement() {
               return itt.next();
           }
       };

       Enumeration en2 = new Enumeration() {
           private Iterator itt = Arrays.asList("str4", "str5").iterator();
           public boolean hasMoreElements() {
               return itt.hasNext();
           }

           public Object nextElement() {
               return itt.next();
           }
       };


       aggr.addEnumeration(en1);
       aggr.addEnumeration(en2);

       assertTrue(aggr.hasNext());
       assertEquals("str1", aggr.next());

       assertTrue(aggr.hasNext());
       assertEquals("str3", aggr.next());

       assertTrue(aggr.hasNext());
       assertEquals("str4", aggr.next());

       assertTrue(aggr.hasNext());
       assertEquals("str5", aggr.next());

       assertFalse(aggr.hasNext());
    }
}
