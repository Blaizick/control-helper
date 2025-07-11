package controlhelper.core;

import static arc.Core.bundle;
import static arc.Core.settings;

import arc.Events;
import arc.struct.ObjectIntMap;
import arc.struct.Queue;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.game.EventType.Trigger;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.production.Drill;

public class DrillsValidator 
{
    public float drillsThreashold = 0.6f;

    public Queue<BuildPlan> deltaPlans = new Queue<>();

    public void Init()
    {
        Events.run(Trigger.update, () -> 
        {
            if (!IsEnabled()) return;
            if (!Vars.state.isGame()) return;

            Queue<BuildPlan> plans = Vars.player.unit().plans;
            Queue<BuildPlan> newPlans = new Queue<>();

            for (BuildPlan plan : plans) 
            {
                if (deltaPlans.contains(plan)) continue;
                newPlans.add(plan);
            }
            if (newPlans.size == 0) return;

            Queue<BuildPlan> tmpPlans = new Queue<>();
            tmpPlans = ValidatePlans(newPlans);

            for (BuildPlan plan : deltaPlans) 
            {
                if (plans.contains(plan))
                {
                    tmpPlans.add(plan);
                }
            }

            Vars.player.unit().plans = tmpPlans;

            deltaPlans.clear();
            for (BuildPlan plan : tmpPlans) 
            {
                deltaPlans.add(plan);
            }
        });
    }    


    public Queue<BuildPlan> ValidatePlans(Queue<BuildPlan> plans)
    {
        Queue<BuildPlan> anotherBlocks = new Queue<>();
        Queue<DVDrill> drills = new Queue<>();

        for (BuildPlan plan : plans) 
        {
            if (plan.breaking) continue;

            if (!(plan.block instanceof Drill)) 
            {
                anotherBlocks.add(plan);
                continue;
            }

            Drill drill = (Drill)plan.block;
            Item returnItem = GetDrillReturnItem(drill, plan.tile());
            int id = drills.indexOf(i -> i.returnItem == returnItem);
            if (id == -1)
            {
                DVDrill dvDrill = new DVDrill(returnItem);
                dvDrill.plans.add(plan);
                drills.add(dvDrill);
            }
            else
            {
                drills.get(id).plans.add(plan);
            }
        }

        int drillsCount = 0;
        for (DVDrill dvDrill : drills) 
        {
            drillsCount += dvDrill.plans.size;
        } 

        if (drillsCount == 0) return plans;
        for (DVDrill dvDrill : drills) 
        {
            dvDrill.relativeReturnItem = (float)dvDrill.plans.size / (float)drillsCount;
        }

        int id = drills.indexOf(i -> i.relativeReturnItem > drillsThreashold);
        if (id == -1) return plans;

        Queue<BuildPlan> newPlans = anotherBlocks;
        drills.get(id).plans.each(i -> newPlans.add(i));
        return newPlans;
    }

    public Item GetDrillReturnItem(Drill drill, Tile tile)
    {
        ObjectIntMap<Item> oreCount = new ObjectIntMap<>();
        Seq<Item> itemArray = new Seq<>();

        Seq<Tile> temp = new Seq<>();
        for (Tile other : tile.getLinkedTilesAs(drill, temp))
        {
            if (drill.canMine(other))
            {
                oreCount.increment(other.drop(), 0, 1);
            }
        }

        for (Item item : oreCount.keys()) 
        {
            itemArray.add(item);
        }

        itemArray.sort((item1, item2) ->
        {
            int type = Boolean.compare(!item1.lowPriority, !item2.lowPriority);
            if (type != 0) return type;
            int amounts = Integer.compare(oreCount.get(item1, 0), oreCount.get(item2, 0));
            if (amounts != 0) return amounts;
            return Integer.compare(item1.id, item2.id);
        });

        return itemArray.peek();
    }

    public boolean IsEnabled()
    {
        return settings.getBool(bundle.get("settings.drillsValidator.name"));
    }



    public class DVDrill 
    {
        public Item returnItem;
        public Queue<BuildPlan> plans = new Queue<>();
        public float relativeReturnItem = 0f;

        public DVDrill(Item returnItem)
        {
            this.returnItem = returnItem;
        }
    }
}