package controlhelper.core.requestexecutor;

import arc.math.geom.Vec2;
import arc.struct.Seq;
import controlhelper.utils.ArrayUtils;
import mindustry.Vars;
import mindustry.ai.UnitCommand;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Unit;

public interface IRequest extends IUnmergableRequest {
    public abstract boolean AreSimiliar(IRequest request);

    public abstract void MergeRequest(IRequest request);

    public static class MoveRequest implements IRequest {
        public int[] unitIds;
        public Building building;
        public Unit unit;
        public Vec2 target;
        public Seq<Runnable> callbacks = new Seq<>();

        private boolean executed = false;

        public MoveRequest(int[] unitIds, Building building, Unit unit, Vec2 target) {
            this.unitIds = unitIds;
            this.building = building;
            this.unit = unit;
            this.target = target;
        }

        public MoveRequest(int[] unitIds, Building building, Unit unit, Vec2 target, Runnable callback) {
            this.unitIds = unitIds;
            this.building = building;
            this.unit = unit;
            this.target = target;
            this.callbacks.add(callback);
        }

        @Override
        public void Execute() {
            Call.commandUnits(Vars.player, unitIds, building, unit, target, false, true);
            for (Runnable callback : callbacks) {
                if (callback == null)
                    continue;
                callback.run();
            }
        }

        @Override
        public boolean AreSimiliar(IRequest request) {
            if (!(request instanceof MoveRequest))
                return false;
            MoveRequest moveRequest = (MoveRequest) request;
            if (moveRequest.building != building || moveRequest.unit != unit)
                return false;
            if (this.target == null || moveRequest.target == null)
                return false;
            if (!moveRequest.target.within(target, 0.2f))
                return false;
            return true;
        }

        @Override
        public void MergeRequest(IRequest request) {
            if (!(request instanceof MoveRequest))
                return;
            MoveRequest moveRequest = (MoveRequest) request;

            unitIds = ArrayUtils.Concatenate(unitIds, moveRequest.unitIds);
            callbacks.add(moveRequest.callbacks);
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

    public static class UnitCommandRequest implements IRequest {
        public int[] unitIds;
        public UnitCommand command;
        public Seq<Runnable> callbacks = new Seq<>();

        private boolean executed = false;

        public UnitCommandRequest(int[] unitIds, UnitCommand command) {
            this.unitIds = unitIds;
            this.command = command;
        }

        public UnitCommandRequest(int[] unitIds, UnitCommand command, Runnable callback) {
            this.unitIds = unitIds;
            this.command = command;
            callbacks.add(callback);
        }

        @Override
        public void Execute() {
            Call.setUnitCommand(Vars.player, unitIds, command);
            for (Runnable callback : callbacks) {
                if (callback == null)
                    continue;
                callback.run();
            }
        }

        @Override
        public boolean AreSimiliar(IRequest request) {
            if (!(request instanceof UnitCommandRequest))
                return false;
            UnitCommandRequest commandRequest = (UnitCommandRequest) request;
            if (commandRequest.command != command)
                return false;
            return true;
        }

        @Override
        public void MergeRequest(IRequest request) {
            if (!(request instanceof UnitCommandRequest))
                return;
            UnitCommandRequest commandRequest = (UnitCommandRequest) request;

            unitIds = ArrayUtils.Concatenate(unitIds, commandRequest.unitIds);
            callbacks.add(commandRequest.callbacks);
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
