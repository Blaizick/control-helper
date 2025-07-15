package controlhelper.Utils;

import arc.struct.Seq;
import mindustry.gen.Groups;
import mindustry.gen.Unit;

public class GeneralUtils 
{
    public static Seq<Unit> GetUnitByIds(int[] ids)
    {
        Seq<Unit> units = new Seq<>();
        for (int id : ids) 
        {
            units.add(Groups.unit.getByID(id));    
        }
        return units;
    }

    public static int[] GetUnitIds(Seq<Unit> units)
    {
        int[] ids = new int[units.size];
        for (int i = 0; i < units.size; i++)
        {
            ids[i] = units.get(i).id;
        }

        return ids;
    }
}
