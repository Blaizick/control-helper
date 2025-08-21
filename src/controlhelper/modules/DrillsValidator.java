package controlhelper.modules;

import static arc.Core.settings;

import arc.Events;
import arc.struct.ObjectIntMap;
import arc.struct.Queue;
import arc.struct.Seq;
import controlhelper.core.events.CHEventType.PlayerPlansChangeEvent;
import controlhelper.utils.ArrayUtils;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.production.Drill;

public class DrillsValidator 
{
    public float drillsThreashold = 0.6f;

    public void Init()
    {
        Events.on(PlayerPlansChangeEvent.class, e -> 
        {
            if (!IsEnabled()) return;
            if (!Vars.state.isGame()) return;
            if (e.added == null || e.added.size == 0) return;
            if (Vars.player == null || Vars.player.unit() == null || Vars.player.unit().plans == null);

            var plansToRemove = GetPlansToRemove(e.added);
            ArrayUtils.RemoveAll(Vars.player.unit().plans, plan -> plan != null && plansToRemove.contains(plan));
        });
    }    


    public Queue<BuildPlan> GetPlansToRemove(Queue<BuildPlan> plans)
    {
        Queue<DVDrill> drills = new Queue<>();
        var tmp = ArrayUtils.Copy(plans);
        var drillsCount = 0;

        ArrayUtils.RemoveAll(tmp, plan -> plan != null && plan.block != null && !(plan.block instanceof Drill));

        for (BuildPlan plan : tmp)
        {
            if (plan == null || plan.block == null || plan.breaking) continue;

            Drill drill = (Drill)plan.block;
            if (drill == null) continue;
            Item returnItem = GetDrillReturnItem(drill, plan.tile());
            if (returnItem == null) continue;
            var id = drills.indexOf(i -> i.returnItem == returnItem);
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
            drillsCount++;
        }

        if (drillsCount == 0) return new Queue<>();
        for (DVDrill dvDrill : drills)
        {
            var relativeReturnItem = (float)dvDrill.plans.size / (float)drillsCount;
            if (relativeReturnItem > drillsThreashold)
            {

                ArrayUtils.RemoveAll(tmp, plan -> plan != null && plan.block != null && dvDrill.plans.contains(plan));
                return tmp;
            }
        }

        return new Queue<>();
    }

    public Item GetDrillReturnItem(Drill drill, Tile tile)
    {
        if (tile == null || drill == null) return null;

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

        if (itemArray.size == 0) return null;

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
        return settings.getBool("drillsValidator");
    }


    public class DVDrill 
    {
        public Item returnItem;
        public Queue<BuildPlan> plans = new Queue<>();

        public DVDrill(Item returnItem)
        {
            this.returnItem = returnItem;
        }
    }
}