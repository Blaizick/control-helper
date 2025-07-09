package controlhelper.core;

import arc.Events;
import arc.struct.Seq;
import controlhelper.inputs.Keybind;
import mindustry.Vars;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Unit;

public class UnitSplitter 
{
    public void Init()
    {
        Events.run(Trigger.update, () ->
        {
            if (Keybind.split.KeyTap())
            {
                Split();
            }
        });
    }

    public void Split()
    {
        if (!Vars.control.input.commandMode)
        {
            return;
        }

        Seq<Unit> units = new Seq<Unit>();
        Seq<Unit> selectedUnits = Vars.control.input.selectedUnits;

        for (int i = 0; i < selectedUnits.size; i++)
        {
            Unit unit = selectedUnits.get(i);
            if (!unit.isValid() || !unit.isCommandable()) continue;

            if (i % 2 == 0)
            {
                units.add(unit);
            }
        }

        Vars.control.input.selectedUnits = units;
    }
}
