package controlhelper.core.events;

import java.util.Iterator;

import arc.Events;
import arc.struct.Queue;
import arc.util.Timer;
import controlhelper.core.events.CHEventType.PlayerPlansChangeEvent;
import controlhelper.utils.ArrayUtils;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Unit;
import mindustry.world.blocks.ConstructBlock;

public class EventsRunner {

    public void Init() {
        Timer.schedule(() -> {
            FilterIgnorePlans();
        }, 0.0F, 0.5F).run();

        Events.run(Trigger.update, () -> {
            CheckForPlansChange();
        });
    }

    public Queue<BuildPlan> ignorePlans = new Queue<>();
    public Queue<BuildPlan> deltaPlans = new Queue<>();

    public void CheckForPlansChange() {
        if (Vars.player == null || Vars.player.unit() == null || Vars.player.unit().plans == null)
            return;
        Unit unit = Vars.player.unit();
        Queue<BuildPlan> tmpD = FilterPlans(ArrayUtils.Copy(deltaPlans));
        Queue<BuildPlan> tmpC = FilterPlans(ArrayUtils.Copy(unit.plans));

        Queue<BuildPlan> removed, added = new Queue<>();

        for (BuildPlan plan : tmpC) {
            if (plan != null && tmpD.contains(plan))
                tmpD.remove(plan);
        }
        removed = ArrayUtils.Copy(tmpD);

        tmpD = FilterPlans(ArrayUtils.Copy(deltaPlans));
        for (BuildPlan plan : tmpC) {
            if (plan != null && !tmpD.contains(plan))
                added.add(plan);
        }

        if (added.size > 0 || removed.size > 0)
            Events.fire(new PlayerPlansChangeEvent(added, removed));

        deltaPlans = ArrayUtils.Copy(unit.plans);
    }

    public Queue<BuildPlan> FilterPlans(Queue<BuildPlan> plans) {
        if (plans == null || plans.size == 0)
            return plans;

        Iterator<BuildPlan> it = plans.iterator();
        while (it.hasNext()) {
            BuildPlan plan = it.next();
            if (plan == null || plan.block == null || this.ignorePlans.contains(plan))
                it.remove();
        }
        return plans;
    }

    public void FilterIgnorePlans() {
        if (!Vars.state.isGame() || Vars.player == null || Vars.player.unit() == null) {
            ignorePlans.clear();
        } else {
            this.ignorePlans.remove(p -> {
                return p == null || p.block == null
                        || p.build() != null && !(p.build() instanceof ConstructBlock.ConstructBuild);
            });
        }
    }
}
