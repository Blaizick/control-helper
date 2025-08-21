package controlhelper.modules;

import java.util.HashMap;

import arc.Events;
import arc.struct.Queue;
import arc.struct.Seq;
import arc.util.Nullable;
import controlhelper.core.Vec2Int;
import controlhelper.core.events.CHEventType.PlayerPlansChangeEvent;
import controlhelper.utils.ArrayUtils;
import controlhelper.utils.GeneralUtils;
import controlhelper.utils.GeometryUtils;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.entities.units.BuildPlan;
import mindustry.world.Block;

public class DistributionAlternator {
    public Seq<Block> replaceBlocks = new Seq<>();
    public boolean enabled = false;

    public DistributionAlternator() {
        replaceBlocks.add(new Block[] {
                Blocks.conveyor,
                Blocks.titaniumConveyor,
                Blocks.router,
                Blocks.junction
        });
    }

    public void Init() {
        Events.on(PlayerPlansChangeEvent.class, e -> {
            if (!enabled)
                return;
            if (!Vars.state.isGame())
                return;
            if (e.added == null || e.added.size == 0 || e.added.indexOf(p -> !p.breaking) == -1)
                return;

            var tmp = ArrayUtils.Copy(e.added);
            ArrayUtils.RemoveAll(tmp, p -> p == null || p.block == null || p.breaking);
            if (tmp.size < 5)
                return;
            if (tmp.indexOf(p -> p.block != tmp.first().block || !replaceBlocks.contains(p.block)) != -1)
                return;
            if (GetEdges(tmp).size > 2)
                return;

            var sorted = SortPlans(tmp);
            var replaceMap = GetReplaceMap(sorted);
            if (replaceMap == null)
                return;
            var unit = Vars.player.unit();
            for (var curPlan : replaceMap.keySet()) {
                var replacePlan = replaceMap.get(curPlan);
                ArrayUtils.Replace(unit.plans, curPlan, replacePlan);
            }
        });
    }

    public Queue<BuildPlan> SortPlans(Queue<BuildPlan> plans) {
        if (plans == null || plans.size == 0)
            return plans;
        BuildPlan edge = GetEdges(plans).first();

        if (edge == null)
            return plans;

        Queue<BuildPlan> sorted = new Queue<>();
        sorted.add(edge);
        int iter = 0, max = plans.size;
        BuildPlan cur = edge, last = null;
        while (iter <= max) {
            if (cur.block == null)
                break;
            boolean found = false;
            for (BuildPlan plan : GetCollisions(cur, plans)) {
                if (last == plan)
                    continue;
                sorted.add(plan);
                last = cur;
                cur = plan;
                found = true;
                break;
            }
            if (!found)
                break;
            iter++;
        }
        return sorted;
    }

    public @Nullable HashMap<BuildPlan, BuildPlan> GetReplaceMap(Queue<BuildPlan> plans) {
        HashMap<BuildPlan, BuildPlan> replaceMap = new HashMap<>();

        if (plans.size < 5)
            return null;
        int counter = 1;
        for (int i = 2; i < plans.size - 2; i++, counter++) {
            var cur = plans.get(i);
            var prev = plans.get(i - 1);
            var next = plans.get(i + 1);

            if (counter != 3) {
                if (prev.x != next.x && prev.y != next.y) {
                    replaceMap.put(cur, new BuildPlan(cur.x, cur.y, cur.rotation, Blocks.sorter, "none"));
                } else {
                    replaceMap.put(cur, new BuildPlan(cur.x, cur.y, cur.rotation, Blocks.invertedSorter, "none"));
                }
            } else {
                counter = 0;
            }
        }
        return replaceMap;
    }

    public @Nullable Seq<BuildPlan> GetEdges(Queue<BuildPlan> plans) {
        if (plans == null || plans.size == 0)
            return null;
        Seq<BuildPlan> edges = new Seq<>();
        for (BuildPlan plan : plans) {
            if (plan == null || plan.block == null)
                continue;
            if (GetCollisions(plan, plans).size < 2)
                edges.add(plan);
        }
        return edges;
    }

    public Queue<BuildPlan> GetCollisions(BuildPlan plan, Queue<BuildPlan> plans) {
        Queue<BuildPlan> collisions = new Queue<>();
        if (plan == null || plan.block == null)
            return new Queue<>();

        Seq<Vec2Int> positions = GeometryUtils.GetCollisions(new Vec2Int(plan.x, plan.y), plan.block.size);
        for (Vec2Int pos : positions) {
            var p = GeneralUtils.GetPlanAt(pos, plans);
            if (p != null)
                collisions.add(p);
        }
        return collisions;
    }
}
