package controlhelper.modules;

import static arc.Core.settings;

import arc.Events;
import arc.math.Mathf;
import arc.struct.Seq;
import controlhelper.core.inputs.CHInput;
import controlhelper.core.inputs.CHKeybind;
import mindustry.Vars;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Unit;

public class UnitSplitter {
    public void Init() {
        Events.run(Trigger.update, () -> {
            if (CHKeybind.split.KeyTap()) {
                Split(0.5f);
            }
            if (CHKeybind.splitAdd1.KeyTap()) {
                Split((float) settings.getInt("splitAdd1.size", 0) / 100f);
            }
            if (CHKeybind.splitAdd2.KeyTap()) {
                Split((float) settings.getInt("splitAdd2.size", 0) / 100f);
            }
            if (CHKeybind.splitAdd3.KeyTap()) {
                Split((float) settings.getInt("splitAdd3.size", 0) / 100f);
            }
        });
    }

    public void Split(float percent) {
        if (!Vars.control.input.commandMode) {
            return;
        }

        // Seq<Unit> selectedUnits = Vars.control.input.selectedUnits;
        Seq<Unit> selectedUnits = CHInput.GetSelectedUnits();
        if (selectedUnits == null || selectedUnits.size == 0)
            return;
        Seq<Unit> validUnits = new Seq<>();
        selectedUnits.each(u -> {
            if (u.isValid() && u.isCommandable())
                validUnits.add(u);
        });
        if (validUnits.size == 0)
            return;

        Seq<Unit> units = new Seq<Unit>();
        int targetCount = Mathf.clamp((int) Math.round((float) validUnits.size * percent), 1, validUnits.size);

        validUnits.shuffle();

        for (int i = 0; i < targetCount; i++) {
            units.add(validUnits.get(i));
        }

        CHInput.SetSelectedUnits(units);
        // Vars.control.input.selectedUnits = units;
    }
}
