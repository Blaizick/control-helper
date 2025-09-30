package controlhelper.modules;

import static arc.Core.input;
import static arc.Core.settings;
import static controlhelper.ControlHelper.requestExecutor;

import arc.Events;
import arc.math.geom.Vec2;
import arc.struct.Queue;
import arc.struct.Seq;
import arc.util.Log;
import controlhelper.core.inputs.CHKeybind;
import controlhelper.core.inputs.CHInput;
import controlhelper.core.requestexecutor.IUnmergableRequest.MoveRequest;
import controlhelper.utils.ArrayUtils;
import mindustry.Vars;
import mindustry.game.EventType.Trigger;
import mindustry.game.EventType.UnitDestroyEvent;
import mindustry.gen.Building;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;

public class CommandBuffer {
    public Queue<RequestWrapper> requests = new Queue<>();
    public RequestWrapper curRequest = null;
    public MoveRequest curMoveRequest = null;
    public Seq<Unit> units = new Seq<>();

    public final float acceptableError = 150f;

    public void Init() {
        Events.run(Trigger.update, () -> {
            if (!Vars.state.isGame()) {
                Clear();
                return;
            }

            if (Vars.control.input.commandMode) {
                Seq<Unit> selected = CHInput.GetSelectedUnits().copy();

                if (CHKeybind.bufferUnits.KeyTap()) {
                    if (!ArrayUtils.AreSame(units, selected)) {
                        Clear();
                        if (curMoveRequest != null && !curMoveRequest.IsExecuted()) {
                            requestExecutor.RemoveRequest(curMoveRequest);
                        }

                        if (selected.size > 0) {
                            units = selected.copy();
                        }
                    }

                    if (selected.size > 0) {
                        Vec2 target = input.mouseWorld().cpy();

                        Teamc attack = Vars.world.buildWorld(target.x, target.y);

                        if (attack == null || (Vars.player != null && attack.team() == Vars.player.team())) {
                            attack = Vars.control.input.selectedEnemyUnit(target.x, target.y);
                        }

                        Building build = null;
                        Unit unit = null;
                        if (attack instanceof Building) {
                            build = (Building) attack;
                        } else if (attack instanceof Unit) {
                            unit = (Unit) attack;
                        }

                        requests.add(new RequestWrapper(units.copy(), unit, build, target));

                        if (curRequest == null) {
                            NextRequest();
                        }
                    }
                }
                if (CHKeybind.attack.KeyTap()) {
                    if (selected.size > 0) {
                        Clear();
                        if (curMoveRequest != null && !curMoveRequest.IsExecuted()) {
                            requestExecutor.RemoveRequest(curMoveRequest);
                        }
                    }
                }

            }

            if (curRequest != null && curRequest.units.size > 0) {
                Vec2 avgPos = new Vec2();
                int count = 0;
                for (Unit unit : curRequest.units) {
                    if (unit.isValid()) {
                        avgPos.add(unit.getX(), unit.getY());
                        count++;
                    }
                }
                avgPos.div(new Vec2(count, count));

                if (curRequest.unit == null && curRequest.build == null) {
                    if (avgPos.within(curRequest.target, acceptableError)) {
                        NextRequest();
                    }
                } else if (curRequest.unit != null && curRequest.unit.dead()) {
                    NextRequest();
                } else if (curRequest.build != null && !curRequest.build.isValid()) {
                    NextRequest();
                }
            } else {
                Clear();
            }
        });

        Events.on(UnitDestroyEvent.class, e -> {
            var unit = e.unit;

            units.remove(unit);
            if (curRequest != null) {
                curRequest.units.remove(unit);
            }

            for (RequestWrapper request : requests) {
                request.units.remove(unit);
            }
        });
    }

    public void NextRequest() {
        if (requests.size > 0) {
            RequestWrapper request = requests.first();
            MoveRequest unwrapped = request.ToMoveRequest();

            curRequest = request;
            curMoveRequest = unwrapped;

            requestExecutor.AddRequest(unwrapped);

            requests.removeFirst();
        } else {
            Clear();
        }
    }

    public void Clear() {
        curRequest = null;
        curMoveRequest = null;
        requests.clear();
        units.clear();
    }

    public class RequestWrapper {
        public Seq<Unit> units;
        public Unit unit;
        public Building build;
        public Vec2 target;

        public RequestWrapper(Seq<Unit> units, Unit unit, Building build, Vec2 target) {
            this.units = units;
            this.unit = unit;
            this.build = build;
            this.target = target;
        }

        public MoveRequest ToMoveRequest() {
            int[] ids = new int[units.size];
            for (int i = 0; i < units.size; i++) {
                ids[i] = units.get(i).id;
            }
            return new MoveRequest(ids, build, unit, target);
        }
    }
}