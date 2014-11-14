package com.example.hu.model;

import com.google.gson.JsonElement;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import retrofit.RestAdapter;

/**
 * version 2.0
 *
 * changes:
 * 1. add reflection
 *
 * Updated by hu on 14/11/14
 *
 * version 1.0
 *
 * Created by hu on 14/10/31.
 */
public class Request{

    public RestAdapter adapter(){
        return new RestAdapter.Builder()
                .setEndpoint("http://huhuanming.github.io/images/test_data")
                .build();
    }

    public static Object toObject(Class cls, JsonElement element, Realm realm){
        if (element.isJsonArray()){
            List list = new ArrayList();
            for (JsonElement jsonElement:element.getAsJsonArray()){
                list.add(jsonToObject(cls,jsonElement, realm));
            }
            return list;
        }else{
            return jsonToObject(cls,element, realm);
        }

    }

    public static RealmObject jsonToObject(Class cls, JsonElement element, Realm realm)  {
        RealmObject object = null;
        try {
            object = realm.createObject(cls);

            Set<Map.Entry<String,JsonElement>> entrySet = element.getAsJsonObject().entrySet();
            for (Map.Entry<String, JsonElement> map : entrySet){
                String key = map.getKey();
                Field field = cls.getDeclaredField(key);
                Type type = field.getType();
                String typeString = type.toString();
                JsonElement value = map.getValue();
                String propertyName = new StringBuilder("set").append(Character.toUpperCase(key.charAt(0))).append(key.substring(1)).toString();
                Method methodSet = object.getClass().getDeclaredMethod(propertyName, field.getType());

                if (typeString.endsWith("String")){
                    methodSet.invoke(object, value.getAsString());
                }else if(typeString.endsWith("double")){
                    methodSet.invoke(object,  value.getAsDouble());
                }else if(typeString.endsWith("int")){
                    methodSet.invoke(object,  value.getAsInt());
                }else if(typeString.endsWith("RealmList")){
                    ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                    Type modelType = parameterizedType.getActualTypeArguments()[0];
                    propertyName = new StringBuilder("get").append(new StringBuilder().append(Character.toUpperCase(key.charAt(0))).append(key.substring(1)).toString()).toString();
                    methodSet = object.getClass().getDeclaredMethod(propertyName);
                    RealmList list =  (RealmList)methodSet.invoke(object);

                    String elementClassName = modelType.toString().substring(6);

                    for (JsonElement jsonElementObject : map.getValue().getAsJsonArray()){
                        RealmObject elementObject = jsonToObject(Class.forName(elementClassName), jsonElementObject, realm);
                        list.add(elementObject);
                    }
                } else {
                    String className = typeString.substring(6);
                    Object elementObject = jsonToObject(Class.forName(className), value, realm);
                    methodSet.invoke(object, elementObject);
                }
            }
            return object;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return object;
    }

}
