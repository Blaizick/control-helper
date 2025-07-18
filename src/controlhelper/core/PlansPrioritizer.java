package controlhelper.core;

import static arc.Core.settings;

import arc.Events;
import arc.math.geom.Geometry;
import arc.math.geom.Vec2;
import arc.struct.Queue;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.entities.Fires;
import mindustry.entities.Units;
import mindustry.entities.units.BuildPlan;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.blocks.ConstructBlock.ConstructBuild;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.world.blocks.defense.turrets.Turret;

public class PlansPrioritizer 
{
    public Seq<PriorityFilter> filters = new Seq<>();
    public Seq<BuildPlan> prioritizedPlans = new Seq<>();

    public PlansPrioritizer()
    {
        filters.add(new PriorityFilter[]
        {
            new LiquidsFilter(),
            new TurretsFilter()
        });
    }

    public void Init()
    {
        Events.run(Trigger.update, () -> 
        {
            if (!IsEnabled()) return;
            boolean infinite = Vars.state.rules.infiniteResources || Vars.player.unit().team.rules().infiniteResources;
            if (Vars.player.unit().core() == null && !infinite) return;
            
            Queue<BuildPlan> plans = Vars.player.unit().plans;
            prioritizedPlans.removeAll(plan -> plan == null || (plan.build() != null && !(plan.build() instanceof ConstructBuild)) || plan.block == Blocks.air || !plans.contains(plan));

            Seq<BuildPlan> prioritize = new Seq<>();

            for (BuildPlan plan : plans) 
            {
                if (prioritizedPlans.contains(plan)) continue;
                for (PriorityFilter filter : filters)
                {
                    if (!filter.ShouldPreoritize(plan)) continue;
                    prioritize.add(plan);
                    prioritizedPlans.add(plan);
                    break;
                }
            }

            for (BuildPlan plan : prioritize) 
            {
                plans.remove(plan);
                plans.addFirst(plan);
            }
        });
    }

    public boolean HasEnoughResources(BuildPlan plan)
    {
        var requirements = plan.block.requirements;
        for (int i = 0; i < requirements.length; i++)
        {
            var itemsStack = requirements[i];
            var coreAmount = GetAmountInCore(itemsStack.item);
            if (itemsStack.amount > coreAmount)
            {
                return false;
            }
        }
        return true;
    }

    public int GetAmountInCore(Item item)
    {
        var core = Vars.player.unit().core();
        return core.items.get(item);
    }

    public boolean IsEnabled()
    {
        return settings.getBool("prioritizePlans");
    }


    public interface PriorityFilter
    {
        public boolean ShouldPreoritize(BuildPlan plan); 
    }

    public class LiquidsFilter implements PriorityFilter
    {
        public Seq<Block> pumpBlocks = new Seq<>(new Block[]
        {
            Blocks.mechanicalPump,
            Blocks.rotaryPump,
            Blocks.impulsePump,
            Blocks.waterExtractor
        });

        public Seq<Block> distributionBlocks = new Seq<>(new Block[]
        {
            Blocks.conduit,
            Blocks.pulseConduit,
            Blocks.platedConduit,
            Blocks.bridgeConduit
        });

        public Seq<Block> stewerBlocks = new Seq<>(new Block[]
        {
            Blocks.wave,
            Blocks.tsunami
        });

        
        protected boolean foundFire = false;
        @Override
        public boolean ShouldPreoritize(BuildPlan plan) 
        {
            if (plan.breaking) return false;
            
            if (!stewerBlocks.contains(plan.block) && !pumpBlocks.contains(plan.block) && !distributionBlocks.contains(plan.block)) return false;
            if (!HasEnoughResources(plan)) return false;
            if (!IsFireInRange(new Vec2(plan.getX(), plan.getY()), GetMaxRange())) return false;

            _BuildPlan buildPlan = new _BuildPlan();
            buildPlan.plan = plan;
            if (!LeadsToStewer(buildPlan)) return false;
            return false;
        }

        public boolean IsFireInRange(Vec2 pos, float range)
        {
            foundFire = false;
            Vars.indexer.eachBlock(Vars.player.team(), pos.x, pos.y, range, ez -> true, b -> 
            {
                if (Fires.has(b.tileX(), b.tileY()))
                {
                    foundFire = true;
                }
            });
            return foundFire;
        }

        public float GetMaxRange()
        {
            float maxRange = 0;
            for (Block block : stewerBlocks) 
            {
                if (!(block instanceof LiquidTurret)) continue;
                LiquidTurret turret = (LiquidTurret)block;
                if (turret.range > maxRange) maxRange = turret.range;
            }

            return maxRange * 1.5f;
        }

        
        public boolean LeadsToStewer(_BuildPlan buildPlan)
        {
            if (buildPlan.IsNull()) return false;
            if (IsPump(buildPlan))
            {
                Seq<_BuildPlan> outputs = GetPumpOutputs(buildPlan);
                for (_BuildPlan output : outputs)
                {
                    if (LeadsToStewer(output)) return true;
                }
                return false;
            }

            _BuildPlan cur = buildPlan;
            while (IsDistribution(cur))
            {
                cur = GetNext(cur);
                if (cur.IsNull()) return false;
            }

            return IsStewer(cur);
        }

        public boolean IsPump(_BuildPlan buildPlan)
        {
            if (buildPlan.plan != null && pumpBlocks.contains(buildPlan.plan.block)) return true;
            if (buildPlan.build != null && pumpBlocks.contains(buildPlan.build.block)) return true;
            return false;
        }

        public boolean IsDistribution(_BuildPlan buildPlan)
        {
            if (buildPlan.plan != null && distributionBlocks.contains(buildPlan.plan.block)) return true;
            if (buildPlan.build != null && distributionBlocks.contains(buildPlan.build.block)) return true;
            return false;
        }

        public boolean IsStewer(_BuildPlan buildPlan)
        {
            if (buildPlan.plan != null && stewerBlocks.contains(buildPlan.plan.block)) return true;
            if (buildPlan.build != null && stewerBlocks.contains(buildPlan.build.block)) return true;
            return false;
        }

        public _BuildPlan GetNext(_BuildPlan buildPlan)
        {
            int x = 0, y = 0;
            int trns = 0;
            int rot = 0;
            if (buildPlan.plan != null)
            {
                trns = buildPlan.plan.block.size / 2;
                rot = buildPlan.plan.rotation;
                x = buildPlan.plan.x;
                y = buildPlan.plan.y;
            } 
            else if (buildPlan.build != null)
            {
                trns = buildPlan.build.block.size / 2;
                rot = buildPlan.build.rotation;
                x = buildPlan.build.tileX();
                y = buildPlan.build.tileY();
            }
            else
            {
                return null;
            }

            int nextX = x + (Geometry.d4(rot).x * trns);
            int nextY = y + (Geometry.d4(rot).y * trns);
            return GetAt(nextX, nextY);
        }

        public Seq<_BuildPlan> GetPumpOutputs(_BuildPlan buildPlan)
        {
            if (buildPlan.IsNull()) return null;

            int trns = 0;
            int _x = 0, _y = 0;
            if (buildPlan.plan != null)
            {
                trns = buildPlan.plan.block.size / 2;
                _x = buildPlan.plan.x;
                _y = buildPlan.plan.y;
            } 
            else if (buildPlan.build != null)
            {
                trns = buildPlan.build.block.size / 2;
                _x = buildPlan.build.tileX();
                _y = buildPlan.build.tileY();
            }

            Seq<_BuildPlan> out = new Seq<>();
            for (int x = _x - trns - 1; x <= _x + trns + 1; x++)
            {
                for (int y = _y - trns - 1; y <= _y + trns + 1; y++)
                {
                    if (x <= _x + trns && x >= _x - trns && y <= _y + trns && y >= _y - trns) continue;

                    _BuildPlan cur = GetAt(x, y);
                    if (cur.IsNull()) continue;
                    if (IsDistribution(cur) || IsStewer(cur)) out.add(cur); 
                }
            }

            return out;
        }

        public _BuildPlan GetAt(Vec2 pos)
        {
            return GetAt((int)pos.x, (int)pos.y);
        }

        public _BuildPlan GetAt(int x, int y)
        {
            _BuildPlan out = new _BuildPlan();
            out.build = Vars.world.build(x, y);
            for (BuildPlan plan : Vars.player.unit().plans)
            {
                if (plan.within(x, y, 0.1f))
                {
                    out.plan = plan;
                    break;
                }
            }
            
            return out;
        }

        public class _BuildPlan
        {
            public BuildPlan plan;
            public Building build;

            public boolean IsNull()
            {
                return plan == null && build == null;
            }
        }
    }

    public class TurretsFilter implements PriorityFilter
    {
        public Seq<Block> priorityBlocks = new Seq<>(new Block[]
        {
            Blocks.scatter,
            Blocks.lancer,
            Blocks.arc,
            Blocks.swarmer,
            Blocks.salvo,
            Blocks.fuse,
            Blocks.cyclone
        });


        boolean foundEnemy = false;
        @Override
        public boolean ShouldPreoritize(BuildPlan plan) 
        {
            if (plan.breaking) return false;
            if (!priorityBlocks.contains(plan.block)) return false;
            if (!HasEnoughResources(plan)) return false;

            foundEnemy = false;
            Units.nearbyEnemies(Vars.player.team(), plan.getX(), plan.getY(), GetMaxRange(), u -> foundEnemy = true);
            return foundEnemy;
        }

        public float GetMaxRange()
        {
            float maxRange = 0;
            for (Block block : priorityBlocks) 
            {
                if (!(block instanceof Turret)) continue;
                Turret turret = (Turret)block;
                if (turret.range > maxRange) maxRange = turret.range;
            }

            return maxRange * 1.5f;
        }
    }
}
