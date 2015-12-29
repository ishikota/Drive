package jp.ikota.drive.ui.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PrivateAccessor {

    // reference url : http://www.takaiwa.net/2013/01/javaprivatefieldmethod.html

    /*
        ParentClass parent = new ParentClass();
        System.out.println("private field:" + (String)getPrivateField(parent, "hoge"));
     */
    public static Object getPrivateField(Object target_object, String field_name) throws Exception{
        Class c = target_object.getClass();
        Field fld = c.getDeclaredField(field_name);
        fld.setAccessible(true);

        return fld.get(target_object);
    }

    /*
        ParentClass parent = new ParentClass();
        setPrivateField(parent, "hoge", "from test method");
        parent.dispMsg();
     */
    public static void setPrivateField(Object target_object, String field_name, Object value) throws Exception{
        Class c = target_object.getClass();
        Field fld = c.getDeclaredField(field_name);
        fld.setAccessible(true);
        fld.set(target_object, value);
    }

    /*
        ParentClass parent = new ParentClass();
        Class[] classArray = {String.class};
        String[] param = {"hogehoge"};
        doPrivateMethod(parent, "privateDispMsg", classArray, param);
     */
    public static Object doPrivateMethod(Object target_object, String field_name, Class[] classArray, Object[] args) throws Exception{

        Class c = target_object.getClass();
        Method method = c.getDeclaredMethod(field_name, classArray);
        method.setAccessible(true);

        return method.invoke(target_object, args);
    }

}
