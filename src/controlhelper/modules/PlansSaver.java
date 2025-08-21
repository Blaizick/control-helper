package controlhelper.modules;

import arc.Events;
import arc.struct.Queue;
import controlhelper.utils.ArrayUtils;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.game.EventType.Trigger;
import mindustry.input.Binding;

import static arc.Core.input;
import static arc.Core.settings;

public class PlansSaver 
{
    protected Queue<BuildPlan> plans = new Queue<>();
    protected boolean resetPlans = false;

    public boolean enabled;

    public long maxResetTime = 2000;
    protected long resetTime = 0;

    public void Init()
    {
        Events.run(Trigger.update, () -> 
        {
            if (!IsEnabled()) return;
            if (!Vars.state.isGame()) return;

            if (input.keyTap(Binding.respawn) || Vars.player.dead())
            {
                if (resetPlans == true) return;
                if (plans.size == 0) return;
                resetPlans = true;
                resetTime = System.currentTimeMillis();
            }

            if (resetPlans && (Vars.player.unit().plans.size == 0 || !ArrayUtils.AreSame(Vars.player.unit().plans, plans)))
            {
                resetPlans = false;
                if (System.currentTimeMillis() - resetTime > maxResetTime) return;

                Queue<BuildPlan> newPlans = new Queue<>();
                plans.each(i -> newPlans.add(i));
                Vars.player.unit().plans = newPlans;
            }

            if (!resetPlans)
            {
                plans = ArrayUtils.Copy(Vars.player.unit().plans);
            }
        });
    }

    public boolean IsEnabled()
    {
        return settings.getBool("plansSaver");
    }
}
