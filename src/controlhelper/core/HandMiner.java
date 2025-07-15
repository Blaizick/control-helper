package controlhelper.core;

import static arc.Core.graphics;
import static arc.Core.settings;

import arc.Core;
import arc.Events;
import arc.struct.Queue;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.game.EventType.Trigger;
import mindustry.input.Binding;
import mindustry.type.Item;
import mindustry.type.ItemStack;

public class HandMiner
{
    public float delayBeforeStart = 0.5f;
    protected float curDelay;

    public void Init()
    {
        Events.run(Trigger.update, () -> 
        {
            if (!IsEnabled()) return; 
            if (!Vars.state.isGame()) return;
            if (Vars.player == null || Vars.player.unit() == null || Vars.player.dead()) return;
            if (Vars.player.team().cores().size == 0) return;
            if (!Vars.player.within(Vars.player.unit().closestCore(), Vars.player.unit().range() - 1f)) return;
            
            if (Core.input.keyDown(Binding.pause_building))
            {
                if (curDelay > 0)
                {
                    curDelay -= graphics.getDeltaTime();
                    return;
                }
            }
            else
            {
                curDelay = delayBeforeStart;
                return;
            }

            if (Vars.player.unit().mineTile == null) return;
            Item mineItem = Vars.player.unit().mineTile.drop();
            int neededAmount = GetNeededAmount(mineItem);

            if (Vars.control.input.isBuilding && GetCoreAmount(mineItem) > 0)
            {
                return;
            }

            if (neededAmount > 0)
            {
                if (Vars.control.input.isBuilding)
                {
                    Vars.control.input.isBuilding = false;
                }
            }
            else
            {
                if (!Vars.control.input.isBuilding)
                {
                    Vars.control.input.isBuilding = true;
                }
            }
        });
    }


    public int GetNeededAmount(Item targetItem)
    {
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