package me.about.widget.mybatis.sensitive.spring;


import lombok.AllArgsConstructor;
import lombok.Data;
import me.about.widget.mybatis.sensitive.annotation.Sensitive;
import me.about.widget.mybatis.sensitive.handler.Handler;
import me.about.widget.mybatis.sensitive.util.Creator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.ResolvableType;
import org.springframework.util.StringValueResolver;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Blob;
import java.sql.Clob;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 代理 mapper
 *
 * @author: hugo.zxh
 * @date: 2023/03/21 14:20
 */
public class SensitiveMapperProxy<T> implements InvocationHandler, Serializable {

    private final static Logger LOGGER = LoggerFactory.getLogger(SensitiveMapperProxy.class);

    private static ConcurrentHashMap<Class, List<Field>> classAndSensitiveFields = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<Method, int[]> methodAndSensitiveParameterIndexMap = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<Method, Map<Integer, Sensitive>> methodAndParameterSensitiveMap = new ConcurrentHashMap<>();

    private static Map<Class<? extends Handler>,Handler> handlerInstanceCache = new ConcurrentHashMap<>();

    private Class<T> mapperClass;

    private Object mapper;

    private StringValueResolver resolver;

    public SensitiveMapperProxy(Class<T> mapperClass, Object mapper, StringValueResolver resolver) {
        this.mapperClass = mapperClass;
        this.mapper = mapper;
        this.resolver = resolver;
    }
    
    private Handler getHandler(Class<? extends Handler> handlerClass) {
        if (handlerInstanceCache.containsKey(handlerClass)) {
            return handlerInstanceCache.get(handlerClass);
        }
        Handler handler = Creator.of(handlerClass);
        handlerInstanceCache.put(handlerClass,handler);
        return handler;
    }

    private String encrypt(Sensitive sensitive, String value) {
        Handler handler = getHandler(sensitive.handler());
        return handler.encrypt(value);
    }

    private String decrypt(Sensitive sensitive, String value) {
        Handler handler = getHandler(sensitive.handler());
        return handler.decrypt(value);
    }
    
    private boolean isNotSupport(Object object) {
        return object.getClass().isPrimitive() || object.getClass().equals(Boolean.class)
                || object instanceof Blob || object instanceof Clob
                || object instanceof byte[] ;
    }

    private int[] getSensitiveParameterIndex(Method method) {
        if (!methodAndSensitiveParameterIndexMap.containsKey(method)) {
            int index = 0;
            List<Integer> indexList = new ArrayList<>();
            int parameterLength = method.getParameters().length;
            for (Parameter p : method.getParameters()) {
                //支持 String Map Collection,之所以反向检查因为如果参数是pojo 就不好正向检查,而pojo是需要经过后续逻辑的
                if (!isNotSupport(p.getType())) {
                    Sensitive sensitive = p.getAnnotation(Sensitive.class);
                    if (sensitive != null) {
                        methodAndParameterSensitiveMap.putIfAbsent(method, new HashMap<>(Math.max(1, parameterLength / 2)));
                        methodAndParameterSensitiveMap.get(method).put(index, sensitive);
                    }
                    //特殊情况 Collection里面如果放的是bean 参数是不需要注解的
                    indexList.add(index);
                }
                index++;
            }
            int[] result = new int[indexList.size()];
            index = 0;
            for (int i : indexList) {
                result[index++] = i;
            }
            methodAndSensitiveParameterIndexMap.putIfAbsent(method, result);
            return result;
        }
        return methodAndSensitiveParameterIndexMap.get(method);
    }


    @Data
    @AllArgsConstructor
    class MapEntry {

        private Object key;

        private Object value;
    }

    @Data
    @AllArgsConstructor
    class StringEntry {

        private Integer vIndex;

        private Object value;

    }

    private void handlerMapArg(Map argMap, Method method, Integer pIndex, Map<Integer,List<MapEntry>> resetMapArg,Map<Integer,Object> parameterAndIndexMap) {
        Map<Integer, Sensitive> integerSensitiveMap = methodAndParameterSensitiveMap.get(method);
        if(integerSensitiveMap != null) {
            Sensitive sensitive = integerSensitiveMap.get(pIndex);
            if (sensitive != null) {
                String[] fields = sensitive.fields();
                if (fields != null && fields.length != 0) {
                    List<MapEntry> entryList = new ArrayList<>();
                    List<String> fieldList = Arrays.asList(fields);
                    Iterator<Object> iterator = argMap.keySet().iterator();
                    while (iterator.hasNext()) {
                        Object key = iterator.next();
                        if (key == null) {
                            continue;
                        }
                        if (fieldList.indexOf(key.toString()) != -1) {
                            Object value = argMap.get(key);
                            if (value != null && value instanceof String) {
                                argMap.put(key,encrypt(sensitive,(String)value));
                                entryList.add(new MapEntry(key,value));
                            }
                        }
                    }
                    if (!entryList.isEmpty()) {
                        parameterAndIndexMap.put(pIndex,argMap);
                        resetMapArg.put(pIndex,entryList);
                    }
                }
            }
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (AopUtils.isEqualsMethod(method) || AopUtils.isHashCodeMethod(method) || AopUtils.isToStringMethod(method)) {
            return method.invoke(mapper, args);
        }

        LOGGER.debug("invoke proxy={} method={} args={}", proxy, method, args);

        Map<Integer,Object> parameterAndIndexMap = new HashMap<>();

        Map<Integer,List<MapEntry>> resetMapArg = new HashMap<>();

        Map<Integer,List<StringEntry>> resetCollectStringArg = new HashMap<>();

        Map<Object, Map<Field, String>> resetPojoArgFieldValue = new HashMap<>();

        try {
            if (args != null && args.length > 0) {
                int[] sensitiveParameterIndexArr = getSensitiveParameterIndex(method);
                for (int pIndex : sensitiveParameterIndexArr) {
                    Object arg = args[pIndex];
                    if (arg != null) {
                        if (arg instanceof String) {
                            Map<Integer, Sensitive> integerSensitiveMap = methodAndParameterSensitiveMap.get(method);
                            if (integerSensitiveMap != null) {
                                Sensitive sensitive = integerSensitiveMap.get(pIndex);
                                if (sensitive != null) {
                                    args[pIndex] = encrypt(sensitive, (String) arg);
                                }
                            }
                        } else if(arg instanceof Collection) {
                            Collection collection = (Collection<Object>)arg;
                            int itemIndex = 0;
                            for (Object item : collection) {
                                if (item == null || isNotSupport(item) ) {
                                    continue;
                                }
                                if (item instanceof String) {
                                    handlerCollectStringArg(method,pIndex,itemIndex++,(String)item,collection,resetCollectStringArg,parameterAndIndexMap);
                                } else if(item instanceof Map) {
                                    handlerMapArg((Map)item,method,pIndex,resetMapArg,parameterAndIndexMap);
                                } else {
                                    handlerPojoArg(item,resetPojoArgFieldValue);
                                }
                            }
                        } else if (arg instanceof Map) {
                            handlerMapArg((Map) arg,method,pIndex,resetMapArg,parameterAndIndexMap);
                        } else {
                            handlerPojoArg(arg,resetPojoArgFieldValue);
                        }
                    }
                }
            }
            Object result = method.invoke(mapper, args);
            if ((method.getReturnType().equals(Void.TYPE)) || result == null) {
                return result;
            }
            return handlerResult(method, result);
        } finally {
            resetPojoArgFieldValue(resetPojoArgFieldValue);
            resetMapArgFieldValue(resetMapArg,parameterAndIndexMap);
            resetCollectionStringArg(resetCollectStringArg,parameterAndIndexMap);
        }
    }


    private void handlerPojoArg(Object pojo,Map<Object, Map<Field, String>> resetPojoArgFieldValue) {
        Map<Field, String> fieldAndOldValues = handlerPojoFields(pojo, false, true);
        if (!fieldAndOldValues.isEmpty()) {
            resetPojoArgFieldValue.put(pojo, fieldAndOldValues);
        }
    }

    private void handlerCollectStringArg(Method method, Integer pIndex, Integer itemIndex, String oldValue, Collection currArg,
                                         Map<Integer,List<StringEntry>> resetStringArg, Map<Integer,Object> parameterAndIndexMap) {
        Map<Integer, Sensitive> integerSensitiveMap = methodAndParameterSensitiveMap.get(method);
        if (integerSensitiveMap != null) {
            Sensitive sensitive = integerSensitiveMap.get(pIndex);
            if (sensitive != null) {
                if (currArg instanceof List) {
                    List list = (List)currArg;
                    list.set(itemIndex,encrypt(sensitive,oldValue));
                }
                parameterAndIndexMap.putIfAbsent(pIndex,currArg);
                resetStringArg.putIfAbsent(pIndex,new ArrayList<>());
                StringEntry stringEntry = new StringEntry(itemIndex, oldValue);
                resetStringArg.get(pIndex).add(stringEntry);
            }
        }

    }

    private void resetCollectionStringArg(Map<Integer,List<StringEntry>> resetStringArg,Map<Integer,Object> parameterAndIndexMap) {
        if(!resetStringArg.isEmpty()) {
            Iterator<Integer> iterator = resetStringArg.keySet().iterator();
            while (iterator.hasNext()) {
                Integer index = iterator.next();
                List<StringEntry> stringEntryList = resetStringArg.get(index);
                Object o = parameterAndIndexMap.get(index);
                if (o != null && o instanceof List) {
                    List list = (List) o;
                    for (StringEntry stringEntry : stringEntryList) {
                        list.set(stringEntry.vIndex,stringEntry.value);
                    }
                }
            }
        }
    }

    private void resetMapArgFieldValue(Map<Integer,List<MapEntry>> resetMapArg,Map<Integer,Object> parameterAndIndexMap) {
        if (!resetMapArg.isEmpty()) {
            Iterator<Integer> iterator = resetMapArg.keySet().iterator();
            while (iterator.hasNext()) {
                Integer index = iterator.next();
                List<MapEntry> entryList = resetMapArg.get(index);
                Object o = parameterAndIndexMap.get(index);
                if (o != null && o instanceof Map) {
                    Map map = (Map)o;
                    for (MapEntry entry : entryList) {
                        map.put(entry.key,entry.value);
                    }
                }
            }
        }
    }

    private void resetPojoArgFieldValue(Map<Object, Map<Field, String>> resetArgFieldValue) throws IllegalAccessException {
        if (!resetArgFieldValue.isEmpty()) {
            for (Object object : resetArgFieldValue.keySet()) {
                Map<Field, String> fieldAndOldValues = resetArgFieldValue.get(object);
                if (!fieldAndOldValues.isEmpty()) {
                    for (Field field : fieldAndOldValues.keySet()) {
                        field.setAccessible(true);
                        field.set(object, fieldAndOldValues.get(field));
                        LOGGER.debug("reset class={} field={} value={}", object.getClass().getName(), field.getName(), fieldAndOldValues.get(field));
                    }
                }
            }
        }
    }

    private Object handlerResult(Method method, Object object) {
        if (isNotSupport(object) || object == null) {
            return object;
        }
        Class<?> clazz = method.getReturnType();
        if (clazz.equals(String.class)) {
            Sensitive sensitive = method.getAnnotation(Sensitive.class);
            if (sensitive != null) {
                return decrypt(sensitive, (String) object);
            }
        } else if (object instanceof Map) {
            Sensitive sensitive = method.getAnnotation(Sensitive.class);
            if (sensitive != null) {
                return handlerResultMapField((Map) object,sensitive);
            }
        } else if (object instanceof Collection) {
            return handlerResultCollectionField(method, (Collection) object);
        } else {
            return handlerPojo(object);
        }
        return object;
    }

    private Map handlerResultMapField(Map mapField,Sensitive sensitive) {
        if (mapField == null || mapField.isEmpty()) {
            return mapField;
        }
        String[] fields = sensitive.fields();
        if (fields != null) {
            List<String> fieldList = Arrays.asList(fields);
            Iterator<Object> iterator = mapField.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next().toString();
                if (fieldList.indexOf(key) != -1) {
                    Object o = mapField.get(key);
                    if (o != null) {
                        String v = (String)o;
                        mapField.put(key,decrypt(sensitive,v));
                    }
                }
            }
        }
        return mapField;
    }

    private Collection handlerResultCollectionField(Method method, Collection collection) {
        if (collection == null || collection.isEmpty()) {
            return collection;
        }

        Class typeArgClass = getReturnCollectionGenericType(method);
        LOGGER.debug("collection genericType:{}", typeArgClass);
        if (typeArgClass != null) {
            if (typeArgClass.equals(String.class)) {
                //返回值Collection<String>  方法要有注解没有就不处理
                Sensitive sensitive = method.getAnnotation(Sensitive.class);
                if (sensitive == null) {
                    return collection;
                }
                Collection<String> result;
                if (collection instanceof List) {
                    result = new ArrayList<>(collection.size());
                } else {
                    result = new HashSet<>(collection.size());
                }
                for (Object obj : collection) {
                    if (obj == null) {
                        continue;
                    }
                    result.add(decrypt(sensitive, (String) obj));
                }
                return result;
            } else if (typeArgClass.equals(Map.class)) {
                //返回值Collection<Map> 方法要有注解没有就不处理
                for (Object obj : collection) {
                    Sensitive sensitive = method.getAnnotation(Sensitive.class);
                    if (sensitive == null) {
                        return collection;
                    }
                    handlerResultMapField((Map)obj,sensitive);
                }
            } else {
                for (Object obj : collection) {
                    handlerPojo(obj);
                }
            }
        } else {
            for (Object obj : collection) {
                handlerPojo(obj);
            }
        }
        return collection;
    }

    private Class getReturnCollectionGenericType(Method method) {
        return ResolvableType.forMethodReturnType(method).resolveGeneric(0);
//        Type returnType = method.getGenericReturnType();
//        Class typeArgClass = null;
//        if (returnType instanceof ParameterizedType) {
//            ParameterizedType type = (ParameterizedType) returnType;
//            Type[] typeArguments = type.getActualTypeArguments();
//            if (typeArguments != null && typeArguments.length > 0) {
//                typeArgClass = ((ParameterizedTypeImpl) typeArguments[0]).getRawType();
//            }
////            if (typeArguments[0] instanceof Class) {
////                typeArgClass = (Class) typeArguments[0];
////            }
//        }
//        return typeArgClass;
    }


    private Object handlerPojo(Object object) {
        handlerPojoFields(object, true);
        return object;
    }

    private Map<Field, String> handlerPojoFields(Object object, boolean isDecryption) {
        return this.handlerPojoFields(object, isDecryption, false);
    }

    private Map<Field, String> handlerPojoFields(Object object, boolean isDecryption, boolean needOldValue) {
        List<Field> encryptFields = extractEncryptFields(object.getClass());
        if (encryptFields != null && !encryptFields.isEmpty()) {
            Map<Field, String> result = needOldValue ? new HashMap<>(encryptFields.size()) : Collections.emptyMap();
            for (Field field : encryptFields) {
                Sensitive sensitive = field.getAnnotation(Sensitive.class);
                if (sensitive == null) {
                    continue;
                }
                try {
                    field.setAccessible(true);
                    Object o = field.get(object);
                    if (o instanceof String) {
                        String oldValue = (String) o;
                        String newValue;
                        if (isDecryption) {
                            newValue = decrypt(sensitive, oldValue);
                        } else {
                            newValue = encrypt(sensitive, oldValue);
                        }
                        if (needOldValue) {
                            result.put(field, oldValue);
                        }
                        field.set(object, newValue);
                        LOGGER.debug("{} value object={} filed={} oldValue={} newValue={} ", (isDecryption ? "decryption" : "encryption"), object.getClass().getName(), field.getName(), oldValue, newValue);
                    } else if(o instanceof List) {
                        List<String> list = (List)o;
                        for (int i=0,len = list.size();i < len; i++) {
                            String v = list.get(i);
                            list.set(i,encrypt(sensitive,v));
                        }
                    } else {
                        LOGGER.error("field : {}, class type no support.",field);
                    }
                } catch (IllegalAccessException e) {
                    if (isDecryption) {
                        LOGGER.error("decryption value fail object={} field={}", object, field.getName(), e);
                    } else {
                        LOGGER.error("encryption value fail object={} field={}", object, field.getName(), e);
                    }
                }
            }
            return result;
        }
        return Collections.emptyMap();
    }

    private List<Field> extractEncryptFields(Class<?> clazz) {
        if (!classAndSensitiveFields.containsKey(clazz)) {
            Field[] fields = clazz.getDeclaredFields();
            List<Field> encryptFields = new ArrayList<>(fields.length);
            for (Field field : fields) {
                Sensitive sensitive = field.getAnnotation(Sensitive.class);
                if (sensitive != null) {
                    if (field.getType().equals(String.class) || Collection.class.isAssignableFrom(field.getType())) {
                        encryptFields.add(field);
                        LOGGER.debug("add class={} sensitiveField field={}", clazz.getName(), field.getName());
                    }
                }
            }
            classAndSensitiveFields.putIfAbsent(clazz, encryptFields.size() > 0 ? encryptFields : Collections.emptyList());
        }
        return classAndSensitiveFields.get(clazz);
    }
}

