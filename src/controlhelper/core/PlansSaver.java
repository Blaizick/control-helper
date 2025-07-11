package controlhelper.core;

import arc.Events;
import arc.struct.Queue;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.game.EventType.Trigger;
import mindustry.input.Binding;

import static arc.Core.bundle;
import static arc.Core.graphics;
import static arc.Core.input;
import static arc.Core.settings;

public class PlansSaver 
{
    protected Queue<BuildPlan> plans = new Queue<>();
    protected boolean resetPlans = false;
    protected float curResettingTime = 0;

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
                resetPlans = true;
                curResettingTime = GetResettingTime();

                plans.clear();
                Vars.player.unit().plans.each(i -> plans.add(i));
            }

            if (resetPlans)
            {
                if (curResettingTime > 0)
                {
                    curResettingTime -= graphics.getDeltaTime();
                    if (plans.size == 0) return;
                    Queue<BuildPlan> newPlans = new Queue<>();
                    plans.each(i -> newPlans.add(i));
                    Vars.player.unit().plans = newPlans;
                }
                else
                {
                    resetPlans = false;
                }

            }
        });
    }

    public boolean IsEnabled()
    {
        return settings.getBool(bundle.get("settings.plansSaver.name"));
    }

    public float GetResettingTime()
    {
        return (float)settings.getInt(bundle.get("settings.plansResetMilis.name"), 500) / 1000f;
    }
}
