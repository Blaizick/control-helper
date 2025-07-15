package controlhelper.core;

import static arc.Core.settings;

import arc.Events;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.entities.Fires;
import mindustry.entities.Units;
import mindustry.entities.units.BuildPlan;
import mindustry.game.EventType.Trigger;
import mindustry.world.Block;
import mindustry.world.blocks.ConstructBlock.ConstructBuild;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.world.blocks.defense.turrets.Turret;

public class PlansPrioritizer 
{
    public Seq<PriorityFilter> filters = new Seq<>();
    public Seq<BuildPlan> prioritizedPlans = new Seq<>();

    public void Init()
    {
        filters.add(new PriorityFilter[]
        {
            new TurretsFilter()
        });

        Events.run(Trigger.update, () -> 
        {
            if (!IsEnabled()) return;
            
            prioritizedPlans.removeAll(plan -> plan == null || (plan.build() != null && !(plan.build() instanceof ConstructBuild)) || plan.block == Blocks.air);

            Seq<BuildPlan> prioritize = new Seq<>();
            var plans = Vars.player.unit().plans;

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

    public boolean IsEnabled()
    {
        return settings.getBool("prioritizePlans");
    }


    public interface PriorityFilter
    {
        public boolean ShouldPreoritize(BuildPlan plan); 
    }

    //todo Добавить проверку на то, ведёт ли трубопровод в тушилку
    public class LiquidsFilter implements PriorityFilter
    {
        public Seq<Block> distributionBlocks = new Seq<>(new Block[]
        {
            Blocks.mechanicalPump,
            Blocks.rotaryPump,
            Blocks.impulsePump,
            Blocks.waterExtractor,
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
            if (!distributionBlocks.contains(plan.block) && !stewerBlocks.contains(plan.block)) return false;
            if (!IsFireInRange(new Vec2(plan.getX(), plan.getY()), GetMaxRange())) return false;
            return true;
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


        /*
        public Building GetConduitOut(Building build)
        {
            int dx = Geometry.d4x(build.rotation);
            int dy = Geometry.d4y(build.rotation);

            Tile outTile = Vars.world.tile(build.tileX() + dx, build.tileY() + dy);
            if (outTile == null || outTile.build == null) return null;
            return outTile.build;
        }

        public Seq<Building> GetPumpOutputs(Building build)
        {
            int size = build.block.size;
            Liquid liquid = build.liquids.current();

            Seq<Building> outputs = new Seq<>();

            for (int dx = -size / 2; dx <= size / 2; dx++)
            {
                for (int dy = -size / 2; dy <= size / 2; dy++)
                {
                    if (dx >= 0 && dx < size && dy >= 0 && dy < size) continue;

                    int tx = build.tileX() + dx;
                    int ty = build.tileY() + dy;

                    Tile neighbor = Vars.world.tile(tx, ty);
                    if (neighbor == null || neighbor.build == null) continue;

                    Building other = neighbor.build;
                    if (other.acceptLiquid(other, liquid))
                }
            }
        }

        */
        //Здесь должна была быть проверка, на то ведёт ли путь к тушилке, но нужно проверять и планы и мир
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
