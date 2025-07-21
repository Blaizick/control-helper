package controlhelper.core.events;

import arc.Events;
import arc.struct.Queue;
import controlhelper.core.events.CHEventType.PlayerPlansChangeEvent;
import controlhelper.utils.ArrayUtils;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.game.EventType.Trigger;

public class EventsRunner 
{

    public void Init()
    {
        Events.run(Trigger.update, () -> 
        {
            CheckForPlansChange();
        });
    }

    public Queue<BuildPlan> deltaPlans = new Queue<>();
    public void CheckForPlansChange()
    {
        if (Vars.player == null || Vars.player.unit() == null || Vars.player.unit().plans == null) return;
        var unit = Vars.player.unit();
        
        var tmp = ArrayUtils.Copy(deltaPlans);
        for (BuildPlan plan : unit.plans) 
        {
            if (plan == null) continue;
            if (tmp.contains(plan)) tmp.remove(plan);
        }
        Queue<BuildPlan> removed = ArrayUtils.Copy(tmp);

        Queue<BuildPlan> added = new Queue<>();
        for (BuildPlan plan : unit.plans)
        {
            if (plan == null) continue;
            if (!deltaPlans.contains(plan)) added.add(plan);
        }

        if ((added != null && added.size > 0) || (removed != null && removed.size > 0)) 
            Events.fire(new PlayerPlansChangeEvent(added, removed));

        deltaPlans = ArrayUtils.Copy(unit.plans);        
    }
}
