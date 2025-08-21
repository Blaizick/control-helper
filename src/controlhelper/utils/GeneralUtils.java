package controlhelper.utils;

import arc.func.Boolf;
import arc.math.geom.Rect;
import arc.struct.Queue;
import arc.struct.Seq;
import arc.util.Nullable;
import controlhelper.core.Vec2Int;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Groups;
import mindustry.gen.Unit;

public class GeneralUtils {
    public static Seq<Unit> GetUnitByIds(int[] ids) {
        Seq<Unit> units = new Seq();
        int[] var2 = ids;
        int var3 = ids.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            int id = var2[var4];
            units.add((Unit) Groups.unit.getByID(id));
        }

        return units;
    }

    public static int[] GetUnitIds(Seq<Unit> units) {
        int[] ids = new int[units.size];

        for (int i = 0; i < units.size; ++i) {
            ids[i] = ((Unit) units.get(i)).id;
        }

        return ids;
    }

    @Nullable
    public static BuildPlan GetPlanAt(Vec2Int pos, Queue<BuildPlan> plans) {
        return GetPlanAt(pos, 1, (Queue) plans);
    }

    @Nullable
    public static BuildPlan GetPlanAt(Vec2Int pos, int size, Queue<BuildPlan> plans) {
        return GetPlanAt(pos, size, (BuildPlan) null, (Queue) plans);
    }

    @Nullable
    public static BuildPlan GetPlanAt(Vec2Int pos, int size, BuildPlan skip, Queue<BuildPlan> plans) {
        Rect r1 = new Rect();
        Rect r2 = new Rect();
        r2.setSize((float) size);
        r2.setCenter((float) pos.x, (float) pos.y);
        Boolf<BuildPlan> test = (plan) -> {
            if (plan != null && plan != skip) {
                r1.setSize((float) plan.block.size);
                r1.setCenter((float) plan.x, (float) plan.y);
                return r2.overlaps(r1);
            } else {
                return false;
            }
        };

        if (plans == null || plans.isEmpty())
            return null;
        int id = plans.indexOf(test);
        return id == -1 ? null : (BuildPlan) plans.get(id);
    }

    @Nullable
    public static BuildPlan GetPlanAt(Vec2Int pos, Seq<BuildPlan> plans) {
        return GetPlanAt(pos, 1, (Seq) plans);
    }

    @Nullable
    public static BuildPlan GetPlanAt(Vec2Int pos, int size, Seq<BuildPlan> plans) {
        return GetPlanAt(pos, size, (BuildPlan) null, (Seq) plans);
    }

    @Nullable
    public static BuildPlan GetPlanAt(Vec2Int pos, int size, BuildPlan skip, Seq<BuildPlan> plans) {
        Rect r1 = new Rect();
        Rect r2 = new Rect();
        r2.setSize((float) size);
        r2.setCenter((float) pos.x, (float) pos.y);
        Boolf<BuildPlan> test = (plan) -> {
            if (plan != null && plan != skip) {
                r1.setSize((float) plan.block.size);
                r1.setCenter((float) plan.x, (float) plan.y);
                return r2.overlaps(r1);
            } else {
                return false;
            }
        };

        if (plans == null || plans.isEmpty())
            return null;
        int id = plans.indexOf(test);
        return id == -1 ? null : (BuildPlan) plans.get(id);
    }
}
