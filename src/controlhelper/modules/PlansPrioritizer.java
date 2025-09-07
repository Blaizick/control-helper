package controlhelper.modules;

import static arc.Core.settings;

import arc.Events;
import arc.struct.Queue;
import arc.struct.Seq;
import controlhelper.core.events.CHEventType.PlayerPlansChangeEvent;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.entities.Units;
import mindustry.entities.units.BuildPlan;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.Turret;

public class PlansPrioritizer {
    public Seq<PriorityFilter> filters = new Seq<>();

    public PlansPrioritizer() {
        filters.add(new PriorityFilter[] {
                new TurretsFilter()
        });
    }

    public void Init() {
        Events.on(PlayerPlansChangeEvent.class, e -> {
            if (!IsEnabled())
                return;
            boolean infinite = Vars.state.rules.infiniteResources || Vars.player.unit().team.rules().infiniteResources;
            if (Vars.player.unit().core() == null && !infinite)
                return;

            Queue<BuildPlan> plans = e.added;
            Seq<BuildPlan> prioritize = new Seq<>();

            for (BuildPlan plan : plans) {
                for (PriorityFilter filter : filters) {
                    if (!filter.ShouldPreoritize(plan))
                        continue;
                    prioritize.add(plan);
                    break;
                }
            }

            for (BuildPlan plan : prioritize) {
                plans.remove(plan);
                plans.addFirst(plan);
            }
        });
    }

    public boolean HasEnoughResources(BuildPlan plan) {
        var requirements = plan.block.requirements;
        for (int i = 0; i < requirements.length; i++) {
            var itemsStack = requirements[i];
            var coreAmount = GetAmountInCore(itemsStack.item);
            if (itemsStack.amount > coreAmount) {
                return false;
            }
        }
        return true;
    }

    public int GetAmountInCore(Item item) {
        var core = Vars.player.unit().core();
        if (core == null)
            return 0;
        if (!core.items.has(item))
            return 0;
        return core.items.get(item);
    }

    public boolean IsEnabled() {
        return settings.getBool("prioritizePlans");
    }

    public interface PriorityFilter {
        public boolean ShouldPreoritize(BuildPlan plan);
    }

    public class TurretsFilter implements PriorityFilter {
        public Seq<Block> priorityBlocks = new Seq<>(new Block[] {
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
        public boolean ShouldPreoritize(BuildPlan plan) {
            if (plan.breaking)
                return false;
            if (!priorityBlocks.contains(plan.block))
                return false;
            if (!HasEnoughResources(plan))
                return false;

            foundEnemy = false;
            Units.nearbyEnemies(Vars.player.team(), plan.getX(), plan.getY(), GetMaxRange(), u -> foundEnemy = true);
            return foundEnemy;
        }

        public float GetMaxRange() {
            float maxRange = 0;
            for (Block block : priorityBlocks) {
                if (!(block instanceof Turret))
                    continue;
                Turret turret = (Turret) block;
                if (turret.range > maxRange)
                    maxRange = turret.range;
            }

            return maxRange * 1.5f;
        }
    }
}
