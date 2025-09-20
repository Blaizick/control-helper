package controlhelper.modules;

import static arc.Core.settings;

import arc.Core;
import arc.Events;
import arc.input.KeyCode;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.content.UnitTypes;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Unit;
import mindustry.input.Binding;
import mindustry.type.UnitType;

public class SupportsIgnorer {
    public long resetDelay = 500l, lastTapTime = 0l;
    public boolean deselectNextFrame = false;

    public boolean deselected = false;

    public Seq<UnitType> unitsToIgnore = new Seq<>(new UnitType[] {
            UnitTypes.poly,
            UnitTypes.mega
    });

    public void Init() {
        Events.run(Trigger.update, () -> {
            if (!IsEnabled())
                return;
            if (!Vars.state.isGame() || !Vars.control.input.commandMode || Vars.player == null)
                return;

            if (deselectNextFrame) {
                if (!deselected)
                    DeselectUnits();
                deselectNextFrame = false;
            }

            if (Core.input.keyTap(Binding.selectAllUnits)) {
                if (System.currentTimeMillis() - lastTapTime <= (long) resetDelay) {
                    deselectNextFrame = true;
                    deselected = !deselected;
                } else {
                    deselected = true;
                }
                lastTapTime = System.currentTimeMillis();
                return;
            }
        });
    }

    public void DeselectUnits() {
        try {
            Seq<Unit> units = new Seq<>();
            for (Unit unit : Vars.control.input.selectedUnits) {
                if (!unitsToIgnore.contains(unit.type)) {
                    units.add(unit);
                }
            }
            Vars.control.input.selectedUnits = units;
        } catch (Exception e) {
            Log.info(e);
        }

    }

    public boolean IsEnabled() {
        return settings.getBool("ignoreSupportUnits");
    }
}
