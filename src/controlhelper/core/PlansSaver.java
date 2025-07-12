package controlhelper.core;

import arc.Events;
import arc.struct.Queue;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.game.EventType.Trigger;
import mindustry.input.Binding;

import static arc.Core.bundle;
import static arc.Core.input;
import static arc.Core.settings;

public class PlansSaver 
{
    protected Queue<BuildPlan> plans = new Queue<>();
    protected boolean resetPlans = false;

    public boolean enabled;

    public void Init()
    {
        Events.run(Trigger.update, () -> 
        {
            if (!IsEnabled()) return;
            if (!Vars.state.isGame()) return;

            if (input.keyTap(Binding.respawn) || Vars.player.dead())
            {
                if (resetPlans == true) return;
                if (Vars.player.unit().plans.size == 0) return;
                resetPlans = true;

                plans.clear();
                Vars.player.unit().plans.each(i -> plans.add(i));
            }

            if (resetPlans && Vars.player.unit().plans.size == 0)
            {
                Queue<BuildPlan> newPlans = new Queue<>();
                plans.each(i -> newPlans.add(i));
                Vars.player.unit().plans = newPlans;
                resetPlans = false;
            }
        });
    }

    public boolean IsEnabled()
    {
        return settings.getBool(bundle.get("settings.plansSaver.name"));
    }
}
