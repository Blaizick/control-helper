package controlhelper.core;

import arc.Events;
import arc.struct.Queue;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.game.EventType.Trigger;
import mindustry.input.Binding;

import static arc.Core.input;

public class PlansSaver 
{
    protected Queue<BuildPlan> plans = new Queue<>();
    protected boolean resetPlans = false;

    public void Init()
    {
        Events.run(Trigger.update, () -> 
        {
            if (!Vars.state.isGame()) return;

            if (input.keyTap(Binding.respawn) || Vars.player.dead())
            {
                if (resetPlans == true) return;
                resetPlans = true;

                plans.clear();
                Vars.player.unit().plans.each(i -> plans.add(i));
            }


            if (resetPlans)
            {
                Queue<BuildPlan> newPlans = new Queue<>();
                plans.each(i -> newPlans.add(i));
                Vars.player.unit().plans = newPlans;
                resetPlans = false;
                return;
            }
        });
    }    
}
