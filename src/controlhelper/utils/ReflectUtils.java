package controlhelper.utils;

import java.lang.reflect.Field;

public class ReflectUtils {
    public static Field GetField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Field field = clazz.getDeclaredField(fieldName);

        if (field == null) {
            throw new NoSuchFieldException("Field " + fieldName + " not found in class " + clazz.getName());
        }

        field.setAccessible(true);

        return field;
    }
}
