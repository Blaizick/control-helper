package controlhelper.core.inputs;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import arc.struct.Seq;
import controlhelper.utils.ReflectUtils;
import mindustry.Vars;
import mindustry.gen.Unit;
import mindustry.input.InputHandler;

public class CHInput {
    @SuppressWarnings("unchecked")
    public static Seq<Unit> GetSelectedUnits() {
        try {
            Field field = ReflectUtils.GetField(InputHandler.class, "selectedUnits");

            Object value;
            if (Modifier.isStatic(field.getModifiers())) {
                value = field.get(null);
            } else {
                value = field.get(Vars.control.input);
            }

            return (Seq<Unit>) value;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Seq<Unit>();
    }

    public static void SetSelectedUnits(Seq<Unit> units) {
        try {
            Field field = ReflectUtils.GetField(InputHandler.class, "selectedUnits");

            if (Modifier.isStatic(field.getModifiers())) {
                field.set(null, units);
            } else {
                field.set(Vars.control.input, units);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
