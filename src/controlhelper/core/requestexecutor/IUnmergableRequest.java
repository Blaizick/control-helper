package controlhelper.core.requestexecutor;

import arc.math.geom.Vec2;
import arc.util.Log;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Unit;

public interface IUnmergableRequest {
    public void Execute();

    public boolean IsExecuted();

    public void SetExecuted(boolean executed);

    public static class TransferItemsTo implements IUnmergableRequest {
        public Unit unit;
        public int amount;
        public Building building;
        public Runnable callback;

        private boolean executed = false;

        public TransferItemsTo(Unit unit, int amount, Building building, Runnable callback) {
            this.unit = unit;
            this.amount = amount;
            this.building = building;
            this.callback = callback;
        }

        public TransferItemsTo(Unit unit, int amount, Building building) {
            this.unit = unit;
            this.amount = amount;
            this.building = building;
        }

        @Override
        public void Execute() {
            if (unit.dead || building.dead)
                return;
            int accepted = building.acceptStack(unit.stack.item, amount, unit);
            Call.transferItemTo(unit, unit.stack.item, accepted, unit.x, unit.y, building);
            if (callback != null)
                callback.run();
        }

        @Override
        public boolean IsExecuted() {
            return executed;
        }

        @Override
        public void SetExecuted(boolean executed) {
            this.executed = executed;
        }

    }

    public class TileConfig implements IUnmergableRequest {
        public Building building;
        public Object value;
        public Runnable callback;

        private boolean executed = false;

        public TileConfig(Building building, Object value) {
            this.building = building;
            this.value = value;
        }

        public TileConfig(Building building, Object value, Runnable callback) {
            this(building, value);
            this.callback = callback;
        }

        @Override
        public void Execute() {
            if (building == null || building.dead)
                return;
            Call.tileConfig(Vars.player, building, value);
            if (callback != null)
                callback.run();
        }

        @Override
        public boolean IsExecuted() {
            return executed;
        }

        @Override
        public void SetExecuted(boolean executed) {
            this.executed = executed;
        }

    }

    public static class MoveRequest implements IUnmergableRequest {
        public int[] unitIds;
        public Building building;
        public Unit unit;
        public Vec2 target;
        public Runnable callback;

        private boolean executed = false;

        public MoveRequest(int[] unitIds, Building building, Unit unit, Vec2 target, Runnable callback) {
            this(unitIds, building, unit, target);
            this.callback = callback;
        }

        public MoveRequest(int[] unitIds, Building building, Unit unit, Vec2 target) {
            this.unitIds = unitIds;
            this.building = building;
            this.unit = unit;
            this.target = target;
        }

        @Override
        public void Execute() {
            Call.commandUnits(Vars.player, unitIds, building, unit, target);
            if (callback != null) {
                callback.run();
            }
        }

        @Override
        public boolean IsExecuted() {
            return executed;
        }

        @Override
        public void SetExecuted(boolean executed) {
            this.executed = executed;
        }
    }
}