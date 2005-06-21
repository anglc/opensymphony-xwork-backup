/*
 * Created on Nov 3, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.opensymphony.xwork.util;

import com.opensymphony.xwork.ObjectFactory;
import ognl.MapPropertyAccessor;
import ognl.OgnlException;

import java.util.Collection;
import java.util.Map;

/**
 * Implementation of PropertyAccessor that sets and gets properties by storing and looking
 * up values in Maps.
 *
 * @author Gabriel Zimmerman
 */
public class XWorkMapPropertyAccessor extends MapPropertyAccessor {
    private static final String[] INDEX_ACCESS_PROPS = new String[]
    {"size", "isEmpty", "keys", "values"};

    private static final XWorkConverter _converter = XWorkConverter.getInstance();
    private static final XWorkCollectionPropertyAccessor _cAcc = new XWorkCollectionPropertyAccessor();

    public Object getProperty(Map context, Object target, Object name) throws OgnlException {
        if (target instanceof Collection) {
            return _cAcc.getProperty(context, target, name);
        }
        OgnlContextState.updateCurrentPropertyPath(context, name);
        // if this is one of the regular index access
        // properties then just let the superclass deal with the
        // get.
        if (name instanceof String && contains(INDEX_ACCESS_PROPS, (String) name)) {
            return super.getProperty(context, target, name);
        }

        Object result = super.getProperty(context, target, name);

        if (result == null) {
            //find the key class and convert the name to that class
            Class lastClass = (Class) context.get(XWorkConverter.LAST_BEAN_CLASS_ACCESSED);

            String lastProperty = (String) context.get(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED);
            if (lastClass == null || lastProperty == null) {
                return super.getProperty(context, target, name);
            }
            Class keyClass = _converter.getObjectTypeDeterminer()
                    .getKeyClass(lastClass, lastProperty);

            if (keyClass == null) {

                keyClass = java.lang.String.class;
            }
            Object key = getKey(context, name);
            Map map = (Map) target;
            result = map.get(key);

            if (result == null &&
                    context.get(InstantiatingNullHandler.CREATE_NULL_OBJECTS) != null) {
                Class valueClass = _converter.getObjectTypeDeterminer().getElementClass(lastClass, lastProperty, key);

                try {
                    result = ObjectFactory.getObjectFactory().buildBean(valueClass);
                    map.put(key, result);
                } catch (Exception exc) {

                }

            }
        }
        return result;
    }

    /**
     * @param array
     * @param name
     * @return
     */
    private boolean contains(String[] array, String name) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(name)) {
                return true;
            }
        }

        return false;
    }

    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        Object key = getKey(context, name);
        Map map = (Map) target;
        map.put(key, value);
    }

    private Object getKey(Map context, Object name) {
        Class lastClass = (Class) context.get(XWorkConverter.LAST_BEAN_CLASS_ACCESSED);
        String lastProperty = (String) context.get(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED);
        if (lastClass == null || lastProperty == null) {
            return java.lang.String.class;
        }
        Class keyClass = _converter.getObjectTypeDeterminer()
                .getKeyClass(lastClass, lastProperty);
        if (keyClass == null) {
            keyClass = java.lang.String.class;
        }

        return _converter.convertValue(context, name, keyClass);

    }
}
