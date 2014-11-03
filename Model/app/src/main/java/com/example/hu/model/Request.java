package com.example.hu.model;

import com.google.gson.JsonElement;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

/**
 * Created by hu on 14/10/31.
 */
public class Request{

    final static String packageName = "com.example.hu.model.";

    public Request(){

    }

    public RestAdapter adapter(){
        return new RestAdapter.Builder()
                .setEndpoint("http://huhuanming.github.io/images/test_data")
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        if(User.isLogin()){
                            Realm realm = Realm.getInstance(ApplicationRunTime.getAppContext());
                            request.addQueryParam("Authorization", realm.where(User.class).findAll().first().getAuthorization());
                         }
                    }
                })
                .build();
    }

    public static Object toObject(String className, JsonElement element, Realm realm) throws InvocationTargetException, NoSuchMethodException, ClassNotFoundException, IllegalAccessException, NoSuchFieldException {
        if (element.isJsonArray()){
            List list = new ArrayList();
            for (JsonElement jsonElement:element.getAsJsonArray()){
                list.add(jsonToObject(className,jsonElement, realm));
            }
        }else{
            return jsonToObject(className,element, realm);
        }
        return null;
    }

    public static RealmObject jsonToObject(String className, JsonElement element, Realm realm) throws NoSuchFieldException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        Class cls = Class.forName(className);
        RealmObject object = realm.createObject(cls);
        Set<Map.Entry<String,JsonElement>> entrySet = element.getAsJsonObject().entrySet();
        for (Map.Entry<String, JsonElement> map : entrySet){
            String key = map.getKey();
            Field field = cls.getDeclaredField(key);
            String type = field.getType().toString();
            String value = map.getValue().toString();
            String propertyName = new StringBuilder("set").append(Character.toUpperCase(key.charAt(0))).append(key.substring(1)).toString();
            Method methodSet = object.getClass().getDeclaredMethod(propertyName, field.getType());

            if (type.endsWith("String")){
                methodSet.invoke(object, value);
            }else if(type.endsWith("double")){

            }else if(type.endsWith("int")){
                int x = Integer.parseInt(value);
                methodSet.invoke(object, x);
            }else if(type.endsWith("RealmList")){
                String elementClassName = key;
                propertyName = new StringBuilder("get").append(new StringBuilder().append(Character.toUpperCase(key.charAt(0))).append(key.substring(1)).toString()).toString();
                methodSet = object.getClass().getDeclaredMethod(propertyName);
                RealmList list =  (RealmList)methodSet.invoke(object);

                if (elementClassName.endsWith("s")) {
                    elementClassName = elementClassName.substring(0, elementClassName.length()-1);
                } else if(elementClassName.endsWith("es")){
                    elementClassName =  elementClassName.substring(0, elementClassName.length()-2);
                }

                for (JsonElement jsonElementObject : map.getValue().getAsJsonArray()){
                    elementClassName = new StringBuilder().append(Character.toUpperCase(elementClassName.charAt(0))).append(elementClassName.substring(1)).toString();
                    RealmObject elementObject = jsonToObject(getPackageName(elementClassName), jsonElementObject, realm);
                    list.add(elementObject);
                }
            } else {
                Object elementObject = jsonToObject(type.substring(6), map.getValue(), realm);
                methodSet.invoke(object, elementObject);
            }
        }
        return object;
    }

    public static String getPackageName(String className){
        return new StringBuilder(packageName).append(className).toString();
    }


}
