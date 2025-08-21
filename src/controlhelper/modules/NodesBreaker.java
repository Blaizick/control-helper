package controlhelper.modules;

import arc.Core;
import arc.Events;
import arc.struct.ObjectMap;
import arc.struct.Queue;
import arc.struct.Seq;
import arc.util.Nullable;
import controlhelper.core.Vec2Int;
import controlhelper.core.events.CHEventType;
import controlhelper.core.events.CHEventType.PlayerPlansChangeEvent;
import controlhelper.utils.ArrayUtils;
import controlhelper.utils.GeneralUtils;
import controlhelper.utils.GeometryUtils;
import java.util.Iterator;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.logic.LExecutor.Var;
import mindustry.world.Block;

public class NodesBreaker {
    public Block plastConveyor;
    public Seq<Block> breakBlocks;
    public ObjectMap<Building, BuildPlan> replacePlans;

    public NodesBreaker() {
        this.plastConveyor = Blocks.plastaniumConveyor;
        this.breakBlocks = new Seq(new Block[] { Blocks.powerNode });
        this.replacePlans = new ObjectMap();
    }

    public void Init() {
        Events.on(CHEventType.PlayerPlansChangeEvent.class, (e) -> {
            if (!IsEnabled() || !Vars.state.isGame()) {
                replacePlans.clear();
                return;
            }

            if (e.added.indexOf((planx) -> {
                return planx.block != ((BuildPlan) e.added.first()).block || this.plastConveyor != planx.block;
            }) != -1)
                return;

            Queue<BuildPlan> tmp = ArrayUtils.Copy(e.added);
            Queue<BuildPlan> edges = this.GetEdges(tmp);

            if (edges.size < 4)
                return;

            for (BuildPlan edge : edges) {
                Building front = GetFrontBuild(edge);
                if (front == null || !breakBlocks.contains(front.block))
                    continue;
                Queue<BuildPlan> collisions = this.GetCollisions(new Vec2Int(front.tileX(), front.tileY()),
                        front.block.size, tmp);
                if (collisions.isEmpty() || collisions.size > 2)
                    continue;
                collisions.remove(edge);

                Vars.player.unit().addBuild(new BuildPlan(front.tileX(), front.tileY()));

                int rot = collisions.size == 1
                        ? GeometryUtils.GetDirRotation(new Vec2Int(front.tileX(), front.tileY()),
                                new Vec2Int(collisions.first().x, collisions.first().y))
                        : edge.rotation;
                BuildPlan plan = new BuildPlan(front.tileX(), front.tileY(), rot, plastConveyor);
                replacePlans.put(front, plan);
            }
        });

        Events.on(PlayerPlansChangeEvent.class, e -> {
            for (Building build : replacePlans.keys()) {
                if (build.dead || (build.tile != null && build.tile.build == null)) {
                    Vars.player.unit().plans.add(replacePlans.get(build));
                    replacePlans.remove(build);
                }
            }
        });
    }

    public Queue<BuildPlan> GetEdges(Queue<BuildPlan> plans) {
        Queue<BuildPlan> edges = new Queue();

        for (BuildPlan plan : plans) {
            if (plan == null || plan.block == null) continue;

            var collisions = GetCollisions(plan, plans);
            if (collisions.size < 2) edges.add(plan);
        }
        
        return edges;
    }

    public Queue<BuildPlan> GetCollisions(BuildPlan plan, Queue<BuildPlan> plans) {
        return plan != null && plan.block != null
                ? this.GetCollisions(new Vec2Int(plan.x, plan.y), plan.block.size, plans)
                : new Queue();
    }



    public Queue<BuildPlan> GetCollisions(Vec2Int pos, int size, Queue<BuildPlan> plans) {
        Queue<BuildPlan> collisions = new Queue();
        Seq<Vec2Int> positions = GeometryUtils.GetCollisions(pos, size);
        Iterator it = positions.iterator();

        while (it.hasNext()) {
            Vec2Int p = (Vec2Int) it.next();
            BuildPlan collision = GeneralUtils.GetPlanAt(p, plans);
            if (collision != null) {
                collisions.add(collision);
            }
        }

        return collisions;
    }

    @Nullable
    public BuildPlan GetFront(BuildPlan plan, Queue<BuildPlan> plans) {
        Vec2Int frontPos = GeometryUtils.GetFront(new Vec2Int(plan.x, plan.y), plan.block.size, plan.rotation);
        return GeneralUtils.GetPlanAt(frontPos, plans);
    }

    @Nullable
    public Building GetFrontBuild(BuildPlan plan) {
        Vec2Int frontPos = GeometryUtils.GetFront(new Vec2Int(plan.x, plan.y), plan.block.size, plan.rotation);
        return Vars.world.build(frontPos.x, frontPos.y);
    }


    public boolean IsEnabled() {
        return Core.settings.getBool("nodesBreaker", true);
    }
}