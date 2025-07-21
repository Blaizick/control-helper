package controlhelper.modules;

import static arc.Core.settings;

import arc.Core;
import arc.Events;
import arc.math.Mathf;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.game.EventType.Trigger;
import mindustry.input.Binding;
import mindustry.type.Item;

public class HandMiner
{
    public long delayBeforeStart = 500l;
    public long pressTime = 0l;
    public boolean pressed;
    public boolean active;

    public void Init()
    {
        Events.run(Trigger.update, () -> 
        {
            if (!IsEnabled()) return; 
            if (!Vars.state.isGame()) return;
            if (Vars.player == null || Vars.player.unit() == null || Vars.player.dead()) return;
            if (Vars.player.team().cores().size == 0) return;
            if (!Vars.player.within(Vars.player.unit().closestCore(), Vars.player.unit().range() - 1f)) return;
            boolean infinite = Vars.state.rules.infiniteResources || Vars.player.unit().team.rules().infiniteResources;
            if (Vars.player.unit().core() == null && !infinite) return;
            if (Vars.player.unit().plans == null || Vars.player.unit().plans.size == 0) return;
            
            if (Core.input.keyDown(Binding.pause_building))
            {
                if (!pressed)
                {
                    pressTime = System.currentTimeMillis();
                    pressed = true;
                }

                if (System.currentTimeMillis() - pressTime > delayBeforeStart)
                {
                    active = true;
                }
            }
            if (Core.input.keyRelease(Binding.pause_building))
            {
                pressed = false;
                active = false;
            }

            if (active)
            {
                if (Vars.player.unit().mineTile == null) return;
                Item mineItem = Vars.player.unit().mineTile.drop();
                if (Vars.control.input.isBuilding && GetCoreAmount(mineItem) > 0) return;
                var plan = GetPlan();
                var neededAmount = GetNeededAmount(mineItem, plan);
                var coreAmount = GetCoreAmount(mineItem);
                if (neededAmount > 0)
                {
                    if (Vars.control.input.isBuilding)
                    {
                        Vars.control.input.isBuilding = false;
                    }
                }
                else if (coreAmount > 0)
                {
                    if (!Vars.control.input.isBuilding)
                    {
                        Vars.control.input.isBuilding = true;
                    }
                }
            }
        });
    }


    public BuildPlan GetPlan()
    {
        var unit = Vars.player.unit();
        var plans = unit.plans;
        var core = unit.core();
        float finalPlaceDst = Vars.state.rules.infiniteResources ? Float.MAX_VALUE : unit.type.buildRange;

        if(plans.size > 1){
            int total = 0;
            int size = plans.size;
            BuildPlan plan;
            while((!unit.within((plan = unit.buildPlan()).tile(), finalPlaceDst) || unit.shouldSkip(plan, core)) && total < size){
                plans.removeFirst();
                plans.addLast(plan);
                total++;
            }
            return unit.buildPlan();
        }

        return null;
    }

    public int GetNeededAmount(Item targetItem, BuildPlan plan)
    {
        if (plan == null || plan.block == null) return 0;
        var coreAmount = GetCoreAmount(targetItem);
        int cost = 0;
        var requirements = plan.block.requirements;
        for (int i = 0; i < requirements.length; i++)
        {
            var itemStack = requirements[i];
            var item = itemStack.item;
            if (item != targetItem) continue;
            cost = itemStack.amount;
            break;
        }

        return Mathf.clamp(cost - coreAmount, 0, Integer.MAX_VALUE);

        /*
        Queue<BuildPlan> plans = Vars.player.unit().plans;
    
        int leastAmount = Integer.MAX_VALUE;
        boolean needed = false;
        for (BuildPlan plan : plans) 
        {
            if (plan.breaking) continue;
            if (!plan.within(Vars.player, Vars.player.unit().range())) continue;

            ItemStack[] requirements = plan.block.requirements;

            for (int i = 0; i < requirements.length; i++) 
            {
                ItemStack itemStack = requirements[i];

                Item item = itemStack.item;
                if (item != targetItem) continue;

                needed = true;
                int amount = itemStack.amount;
                if (amount < leastAmount)
                {
                    leastAmount = amount;
                }

                break;
            }
        }

        if (!needed) return 0;

        int coreAmount = GetCoreAmount(targetItem);
        int neededAmount = leastAmount - coreAmount;
        if (neededAmount < 0)
        {
            neededAmount = 0;
        }
        return neededAmount;

        */
    }

    public int GetCoreAmount(Item item)
    {
        if (Vars.player.team().cores().size == 0) return 0;
        return Vars.player.team().core().items.get(item);
    }

    public boolean IsEnabled()
    {
        return settings.getBool("handMiner");
    }
}