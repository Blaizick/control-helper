/*package controlhelper.core;

import static controlhelper.ControlHelper.requestExecutor;
import static controlhelper.ControlHelper.unitMinerWindow;

import arc.Core;
import arc.Events;
import arc.func.Cons;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Timer;
import controlhelper.Utils.ArrayUtils;
import controlhelper.Utils.GeneralUtils;
import controlhelper.core.requestexecutor.IRequest;
import controlhelper.core.requestexecutor.IUnitsRequest;
import mindustry.Vars;
import mindustry.ai.UnitCommand;
import mindustry.content.Items;
import mindustry.game.EventType.Trigger;
import mindustry.game.EventType.UnitCreateEvent;
import mindustry.gen.Unit;
import mindustry.type.Item;
import mindustry.type.UnitType;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock.CoreBuild;

public class UnitMiner 
{
    public Seq<UMItem> items = new Seq<>();
    public Seq<UnitType> unitTypes = new Seq<>();

    public Seq<UnitBatch> batches = new Seq<>();
    public Seq<Unit> undistributedUnits = new Seq<>();

    public int batchSize = 24;

    public boolean mining = false;

    public void Init()
    {
        items.add(new UMItem(Items.titanium));
        items.add(new UMItem(Items.coal));
        items.add(new UMItem(Items.sand));
        items.add(new UMItem(Items.lead));
        items.add(new UMItem(Items.copper));

        Events.on(UnitCreateEvent.class, e -> 
        {
            if (!mining || !unitTypes.contains(e.unit.type)) return;
            undistributedUnits.add(e.unit);
            DistributeUnits();
        });

        Events.run(Trigger.update, () -> 
        {
            DeselectUnits();
            if (!Vars.state.isGame() && mining)
            {
                unitMinerWindow.UncheckUMCheckBoxes();
                IterruptMining();
            }
            if (Vars.state.isGame() && !mining && unitTypes.size > 0 && items.size > 0)
            {
                RefreshMining();
            }
        });
    }


    public void AddUnitType(UnitType unitType)
    {
        if (unitTypes.contains(unitType)) return;
        unitTypes.add(unitType);
        mining = true;
    }

    public void RemoveUnitType(UnitType unitType)
    {
        unitTypes.remove(unitType);
        if (unitTypes.size == 0 && mining) mining = false;
    }


    public void DeselectUnits()
    {
        if (unitTypes.size == 0) return;

        Seq<Unit> selectedUnits = new Seq<>();

        for (Unit unit : Vars.control.input.selectedUnits) 
        {
            if (unitTypes.contains(unit.type)) continue;
            selectedUnits.add(unit);
        }

        Vars.control.input.selectedUnits = selectedUnits;
    }


    public void RefreshMining()
    {
        if (!mining) return;

        RefreshUnitPool();
        DistributeUnits();

        DeselectUnits();
    }

    public void IterruptMining()
    {
        mining = false;
        ResetMiner();
    }


    public void RefreshUnitPool()
    {
        if (!mining) return;

        ResetMiner();

        Seq<Unit> allUnits = Vars.player.team().data().units;
        for (Unit unit : allUnits) 
        {
            if (!unit.isCommandable() || !unit.canMine() || !unitTypes.contains(unit.type))
            {
                continue;
            }

            undistributedUnits.add(unit);
        }
    } 

    public void ResetMiner()
    {
        for (UnitBatch batch : batches) 
        {
            batch.InterruptMining();
        }
        
        batches.clear();
        undistributedUnits.clear();

        for (UMItem item : items)
        {
            item.income = 0;
        }
    }


    public void DistributeUnits()
    {
        if (!mining) return;
        if (undistributedUnits.size == 0 || items.size == 0) return;

        Seq<UMItem> locItems = items.copy();
        RefreshRelativeDeficits();

        while (undistributedUnits.size > 0 && locItems.size > 0) 
        {
            UMItem item = FindMostDeficitetItem(locItems);
            Unit unit = FindWorstUndistrUnitCanMine(item.item);

            if (unit == null)
            {
                locItems.remove(item);
                continue;
            }

            int unitIncome = unit.type.itemCapacity;
            item.income += unitIncome;

            int globalDeficit = GetGlobalDeficit();
            float relativeIncome = 0;
            if (globalDeficit != 0)
            {
                relativeIncome = (float)unitIncome / (float)GetGlobalDeficit();
            }
            item.relatimeDeficit -= relativeIncome;
            undistributedUnits.remove(unit);
            
            UnitBatch batch = GetBatch(item);
            batches.add(batch);
            batch.delayedUnits.add(unit);
        }
    }

    protected void RefreshRelativeDeficits()
    {
        int globalDeficit = GetGlobalDeficit();

        for (UMItem item : items)
        {
            int itemDeficit = item.GetDeficit();
            float relativeDeficit = 0;
            if (globalDeficit != 0)
            {
                relativeDeficit = (float)itemDeficit / (float)globalDeficit;
            }

            item.relatimeDeficit = relativeDeficit;
        }
    }

    public int GetGlobalDeficit()
    {
        int globalDeficit = 0;
        for (UMItem item : items)
        {
            globalDeficit += item.GetDeficit();
        }
        return globalDeficit;
    }

    public UMItem FindMostDeficitetItem(Seq<UMItem> items)
    {
        if (items.size == 0) return null;

        UMItem mostDeficited = items.first();
        for (UMItem item : items) 
        {
            if (item.relatimeDeficit > mostDeficited.relatimeDeficit)
            {
                mostDeficited = item;
            }
        }

        return mostDeficited;
    }

    public Unit FindWorstUndistrUnitCanMine(Item item)
    {
        if (undistributedUnits.size == 0 || item == null) return null;

        Unit unit = null;
        for (Unit curUnit : undistributedUnits) 
        {
            if (!curUnit.canMine(item)) continue;
            if (unit == null) unit = curUnit;
            if (curUnit.type.mineTier >= unit.type.mineTier) continue;
            unit = curUnit;
        }

        return unit;
    }


    public UnitBatch GetBatch(UMItem item)
    {
        UnitBatch batch = null;
        for (UnitBatch b : batches)
        {
            if (b.item != item.item || b.delayedUnits.size >= batchSize) continue;
            batch = b;
        }

        if (batch == null)
        {
            batch = new UnitBatch(item.item);
            batch.StartMining(u -> 
            {
                item.income -= u.type.itemCapacity;
                undistributedUnits.add(u);
                Log.info(undistributedUnits.size);
                DistributeUnits();
                Log.info(undistributedUnits.size);
            });
        }

        return batch;
    }

    
    protected class UMItem
    {
        public Item item;
        public int income;
        public float relatimeDeficit;

        public UMItem(Item item)
        {
            this.item = item;
            income = 0;
        }

        public int GetAmountInCore()
        {
            if (Vars.player.team().cores().size == 0) return 0;
            return Vars.player.team().core().items.get(item);
        }

        public int GetDeficit()
        {
            if (Vars.player.team().cores().size == 0) return 0;

            int maxItems = Vars.player.team().core().storageCapacity;
            int amount = GetAmountInCore();
            int predicted = amount + income;
            int deficit = maxItems - predicted;

            return deficit;
        }
    }

    protected class UnitBatch
    {
        public Item item;
        protected Seq<Unit> units = new Seq<>();
        public Seq<Unit> delayedUnits = new Seq<>();

        protected Seq<Unit> returnUnits = new Seq<>();
        protected boolean mined = false;

        public boolean mining = false;
        protected Cons<Unit> callback;
        
        protected Tile ore;
        protected Vec2 target;
        protected CoreBuild core;

        public float flyRequestDelay = 3f;
        public float targetCompletion = 0.9f;

        public float itemsMined = 0;

        public UnitBatch(Item item)
        {
            this.item = item;

            Events.run(Trigger.update, () ->
            {
                if (!mining) return;
                Log.info(batches.size);
                UpdateMining();
            });

            Timer.schedule(() -> 
            {
                if (!mining) return;
                BoostUnits();
            }, 0f, flyRequestDelay);
        }

        public void StartMining(Cons<Unit> callback)
        {
            this.callback = callback;
            mining = true;
            target = null;
        }

        public void InterruptMining()
        {
            mining = false;
            item = null;
            ore = null;
            target = null;
            core = null;
            callback = null;
        }

        public void UpdateMining()  
        {
            if (!mining || !Vars.state.isGame()) 
            {
                return;
            }
            if (item == null || Vars.player.team().cores().size == 0 || 
            Vars.indexer.findClosestOre(Vars.player.unit(), item) == null)
            {
                return;
            }

            if (units.size == 0 && returnUnits.size == 0)
            {
                DistributeUnits();
                mined = false;
                return;
            }

            if (core == null || core.dead)
            {
                core = units.first().closestCore();
            }
            if (ore == null)
            {
                ore = Vars.indexer.findClosestOre(units.first(), item);
            }

            for (Unit unit : units) 
            {
                if (!unit.dead) continue;
                units.remove(unit);
            }

            if (returnUnits.size > 0)
            {
                if (target == null || !target.within(core, 1f))
                {
                    target = new Vec2(core.x, core.y);
                    requestExecutor.AddRequest(new IUnitsRequest.MoveRequest(GeneralUtils.GetUnitIds(returnUnits), null, null, target));
                }
                
                for (Unit unit : returnUnits)
                {
                    if (!target.within(unit, unit.type.range - 2f)) continue;
                    requestExecutor.AddRequest(new IRequest.TransferItemsTo(unit, unit.stack.amount, core, () ->
                    {
                        returnUnits.remove(unit);
                        if (returnUnits.size == 0) target = null;
                        if (mined || unit.stack.amount > 0)
                        {
                            units.remove(unit);
                            if (callback != null) callback.get(unit);
                        }
                    }));
                }

                return;
            }

            if (GetRelativeCompletion() > targetCompletion)
            {
                units.each(unit -> returnUnits.add(unit));
                mined = true;
            }
            else
            {
                if (target == null || !target.within(ore, 1f))
                {
                    target = new Vec2(ore.worldx(), ore.worldy());
                    requestExecutor.AddRequest(new IUnitsRequest.MoveRequest(GeneralUtils.GetUnitIds(units), null, null, target));
                }

                for (Unit unit : units)
                {
                    if (unit.mineTile != null && unit.mineTile == ore)
                    {
                        if (unit.stack.amount < unit.type.itemCapacity) itemsMined += Core.graphics.getDeltaTime() * unit.type.mineSpeed;
                        continue;
                    }
                    if (unit.within(target, unit.type.mineRange - 2f)) unit.mineTile = ore;
                }
            }
        }

        public void DistributeUnits()
        {
            units.clear();
            for (Unit unit : delayedUnits)
            {
                if (unit.dead)
                {
                    callback.get(unit);
                    continue;
                }
                units.add(unit);
                if (unit.stack.amount > 0 && unit.stack.item != item)
                {
                    returnUnits.add(unit);
                }
            }

            delayedUnits.clear();
        }

        public void BoostUnits()
        {
            Seq<Integer> unitIds = new Seq<>();

            for (Unit unit : units)
            {
                if (!unit.isFlying() && !unit.type.flying) 
                {
                    unitIds.add(unit.id);
                }
            }

            if (unitIds.size == 0) return;
            requestExecutor.AddRequest(new IUnitsRequest.UnitCommandRequest(ArrayUtils.ToArray(unitIds), UnitCommand.boostCommand));
        }

        public float GetRelativeCompletion()
        {
            int globalCapacity = 0;
            int globalFilling = 0;
            for (Unit unit : units)
            {
                globalCapacity += unit.type.itemCapacity;
                globalFilling += unit.stack.amount;
            }

            float itemsMined = 0;
            if (globalFilling > this.itemsMined) itemsMined = globalFilling;
            else itemsMined = this.itemsMined;

            return itemsMined / globalCapacity;
        }
    }
}*/