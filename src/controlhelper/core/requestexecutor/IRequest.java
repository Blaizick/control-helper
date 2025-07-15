package controlhelper.core.requestexecutor;

import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Unit;

public interface IRequest 
{
    public void Execute();


    public class TransferItemsTo implements IRequest
    {
        public Unit unit;
        public int amount;
        public Building building;
        public Runnable callback;

        public TransferItemsTo(Unit unit, int amount, Building building, Runnable callback)
        {
            this.unit = unit;
            this.amount = amount;
            this.building = building;
            this.callback = callback;
        }

        public TransferItemsTo(Unit unit, int amount, Building building)
        {
            this.unit = unit;
            this.amount = amount;
            this.building = building;
        }

        @Override
        public void Execute() 
        {
            if (unit.dead || building.dead) return;
            int accepted = building.acceptStack(unit.stack.item, amount, unit);
            Call.transferItemTo(unit, unit.stack.item, accepted, unit.x, unit.y, building);
            if (callback != null) callback.run();
        }
    }

    public class TileConfig implements IRequest
    {
        public Building building;
        public Object value;
        public Runnable callback;

        public TileConfig(Building building, Object value, Runnable callback)
        {
            this.building = building;
            this.value = value;
            this.callback = callback;
        }

        public TileConfig(Building building, Object value)
        {
            this.building = building;
            this.value = value;
        }

        @Override
        public void Execute() 
        {
            if (building == null || building.dead) return;
            Call.tileConfig(Vars.player, building, value);
            if (callback != null) return;
        }
    }
}