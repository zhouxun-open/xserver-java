package com.xuexibao.xserver.service.db;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.xuexibao.xserver.service.XFieldType;

public class XPojoExplain {
    public Map<String, String> fieldToDBField;
    public Map<String, String> dbFieldToPojoField;
    public Map<String, XFieldType> fieldType;
    public String tableName;
    public String identifier;

    public static XPojoExplain compile(Class<?> pojoClazz) throws Exception {
        XPojoExplain explain = new XPojoExplain();
        explain.fieldToDBField = new HashMap<String, String>();
        explain.dbFieldToPojoField = new HashMap<String, String>();
        explain.fieldType = new HashMap<String, XFieldType>();
        XPojo pojoAnno = pojoClazz.getDeclaredAnnotation(XPojo.class);
        if (null == pojoAnno) {
            throw new Exception("class " + pojoClazz.getPackage().getName() + "." + pojoClazz.getName()
                    + " doesn't have XPojo Annotation !");
        }
        explain.identifier = pojoAnno.identifier();
        if (null == explain.identifier || "".equals(explain.identifier)) {
            throw new Exception("class " + pojoClazz.getPackage().getName() + "." + pojoClazz.getName()
                    + " XPojo Annotation doesn't have identifier! ");
        }
        explain.tableName = pojoAnno.value();
        Field[] fields = pojoClazz.getDeclaredFields();
        if (null != fields) {
            int length = fields.length;
            for (int i = 0; i < length; i++) {
                Field field = fields[i];
                XPojoVariable varAnno = field.getDeclaredAnnotation(XPojoVariable.class);
                String fieldName = field.getName();
                if (null == varAnno) {
                    explain.fieldToDBField.put(fieldName, fieldName);
                    explain.dbFieldToPojoField.put(fieldName, fieldName);
                } else {
                    String dbName = varAnno.value();
                    explain.fieldToDBField.put(fieldName, dbName);
                    explain.dbFieldToPojoField.put(dbName, fieldName);
                }
                explain.fieldType.put(fieldName, XPojoExplain.getFieldType(field));
            }
        }
        return explain;
    }

    public static XFieldType getFieldType(Field field) {
        String type = field.getGenericType().toString().toLowerCase();
        if (type.contains("int")) {
            return XFieldType.INTEGER;
        }
        if (type.contains("long")) {
            return XFieldType.LONG;
        }
        if (type.contains("float")) {
            return XFieldType.FLOAT;
        }
        if (type.contains("double")) {
            return XFieldType.DOUBLE;
        }
        if (type.contains("string")) {
            return XFieldType.STRING;
        }
        return null;
    }
}