package controlhelper.core;

import arc.Events;
import arc.func.Cons;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.ai.UnitCommand;
import mindustry.content.Items;
import mindustry.game.EventType.Trigger;
import mindustry.game.EventType.UnitCreateEvent;
import mindustry.gen.Call;
import mindustry.gen.Unit;
import mindustry.type.Item;
import mindustry.type.UnitType;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock.CoreBuild;

public class UnitMiner 
{
    public Seq<UMItem> items = new Seq<>();

    public Seq<UnitType> unitTypes = new Seq<>();
    public Seq<UMUnit> units = new Seq<>();
    public Seq<UMUnit> undistributedUnits = new Seq<>();

    public void Init()
    {
        items.add(new UMItem(Items.titanium));
        items.add(new UMItem(Items.coal));
        items.add(new UMItem(Items.sand));
        items.add(new UMItem(Items.lead));
        items.add(new UMItem(Items.copper));

        for (UMItem umItem : items) 
        {
            Log.info(umItem.item.name);    
        }

        Events.on(UnitCreateEvent.class, e -> 
        {
            if (!IsMining() || !unitTypes.contains(e.unit.type)) return;
            undistributedUnits.add(new UMUnit(e.unit));
            DistributeUnits();
        });

        Events.run(Trigger.update, () -> 
        {
            DeselectUnits();
        });
    }


    public void AddUnitType(UnitType unitType)
    {
        if (unitTypes.contains(unitType)) return;
        unitTypes.add(unitType);
    }

    public void RemoveUnitType(UnitType unitType)
    {
        unitTypes.remove(unitType);
        if (unitTypes.size == 0) return;
    }

    public void RefreshUnitPool()
    {
        for (UMItem item : items) 
        {
            if (item == null) continue;
            item.income = 0;
        }

        for (UMUnit unit : units) 
        {
            if (unit == null) continue;
            unit.FinishMining();    
        }

        units.clear();
        undistributedUnits.clear();

        Seq<Unit> allUnits = Vars.player.team().data().units;
        for (Unit unit : allUnits) 
        {
            if (!unit.isCommandable() || !unit.canMine() || !unitTypes.contains(unit.type))
            {
                continue;
            }

            undistributedUnits.add(new UMUnit(unit));
        }
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
        RefreshUnitPool();
        DistributeUnits();

        DeselectUnits();
    }

    public boolean IsMining()
    {
        return (unitTypes.size > 0) && (items.size > 0);
    }


    public void DistributeUnits()
    {
        if (undistributedUnits.size == 0 || items.size == 0) return;

        Seq<UMItem> locItems = items.copy();
        RefreshRelativeDeficits();

        while (undistributedUnits.size > 0 && locItems.size > 0) 
        {
            UMItem item = FindMostDeficitetItem(locItems);
            UMUnit unit = FindWorstUndistrUnitCanMine(item.item);

            if (unit == null)
            {
                locItems.remove(item);
                continue;
            }

            int unitIncome = unit.GetCapacity();
            item.income += unitIncome;

            int globalDeficit = GetGlobalDeficit();
            float relativeIncome = 0;
            if (globalDeficit != 0)
            {
                relativeIncome = (float)unitIncome / (float)GetGlobalDeficit();
            }
            item.relatimeDeficit -= relativeIncome;
            Log.info(item.relatimeDeficit);
            undistributedUnits.remove(unit);
            units.add(unit);

            unit.StartMining(item.item, _unit ->
            {
                item.income -= unitIncome;
                if (!unitTypes.contains(_unit.unit.type)) return;
                units.remove(_unit);
                undistributedUnits.add(_unit);
                DistributeUnits();
            });
        }

        for (UMItem umItem : items) 
        {
            Log.info(umItem.item.name);
            Log.info(umItem.GetAmountInCore() + umItem.income);
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

    public UMUnit FindWorstUndistrUnitCanMine(Item item)
    {
        if (undistributedUnits.size == 0 || item == null) return null;

        UMUnit unit = null;
        for (UMUnit curUnit : undistributedUnits) 
        {
            if (!curUnit.unit.canMine(item)) continue;
            if (unit == null) unit = curUnit;
            if (curUnit.unit.type.mineTier >= unit.unit.type.mineTier) continue;
            unit = curUnit;
        }

        return unit;
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

    protected class UMUnit
    {
        public Item item;
        public Unit unit;

        public boolean mining = false;
        protected Cons<UMUnit> callback;
        
        protected Tile ore;
        protected Vec2 target;
        protected CoreBuild core;

        public UMUnit(Unit unit)
        {
            this.unit = unit;

            Events.run(Trigger.update, () ->
            {
                if (!mining) return;
                UpdateMining();
            });
        }

        public void StartMining(Item item, Cons<UMUnit> callback)
        {
            this.item = item;
            this.callback = callback;
            mining = true;
            ore = null;
            target = null;
            core = null;
        }

        public void FinishMining()
        {
            mining = false;
            item = null;
            ore = null;
            target = null;
            core = null;
            if (callback != null ) callback.get(this);
        }

        public void UpdateMining()  
        {
            if (!mining) 
            {
                return;
            }
            if (item == null || unit == null || Vars.player.team().cores().size == 0 || 
            Vars.indexer.findClosestOre(unit, item) == null || !Vars.state.isGame())
            {
                FinishMining();
                return;
            }

            if (unit.stack.amount == 0)
            {
                MovingToOre(() -> 
                {
                    unit.mineTile = ore;
                });
            }
            else
            {
                if (unit.stack.item == item)
                {
                    if (unit.stack.amount < GetCapacity())
                    {
                        MovingToOre(() -> 
                        {
                            unit.mineTile = ore;
                        });
                    }
                    else
                    {
                        MovingToCore(() -> 
                        {
                            TransferItemsToCore();
                            FinishMining();
                        });
                    }
                }
                else
                {
                    MovingToCore(() -> 
                    {
                        TransferItemsToCore();
                    });
                }
            }
        }

        public void MovingToOre(Runnable callback)
        {
            if (ore != null && unit.mineTile == ore) return;

            if (target == null || ore == null || target != new Vec2(ore.worldx(), ore.worldy()))
            {
                unit.mineTile = null;
                ore = Vars.indexer.findClosestOre(unit, item);
                target = new Vec2(ore.worldx(), ore.worldy());
                GoTo(target);
            }

            if (unit.within(target, GetMineRange()))
            {
                target = null;
                callback.run();
            }
        }

        public void MovingToCore(Runnable callback)
        {
            if (target == null || core == null || target != new Vec2(core.x, core.y))
            {
                unit.mineTile = null;
                core = unit.closestCore();
                target = new Vec2(core.x, core.y);
                GoTo(target);
            }

            if (unit.within(target, GetRange()))
            {
                target = null;
                callback.run();
            }
        }

        public void TransferItemsToCore()
        {
            if (core == null || unit.stack.amount == 0 || !unit.within(core, GetRange())) return;

            int accepted = core.acceptStack(unit.item(), unit.stack.amount, unit);
            Call.transferItemTo(unit, unit.item(), accepted, unit.x, unit.y, core);
            if (unit.stack.amount > 0)
            {
                Call.dropItem(0);
            }
        }

        public void GoTo(Vec2 pos)
        {
            if (!unit.type.flying && !unit.isFlying())
            {
                Call.setUnitCommand(Vars.player, new int[] {unit.id}, UnitCommand.boostCommand);
            } 
            Call.commandUnits(Vars.player, new int[] {unit.id}, null, null, pos);
        }

        public int GetCapacity()
        {
            return unit.type.itemCapacity - 1;
        }

        public float GetRange()
        {
            return unit.type.range / 1.8f;
        }
        
        public float GetMineRange()
        {
            return unit.type.mineRange / 1.8f;
        }
    }
}