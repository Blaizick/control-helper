// Source code is decompiled from a .class file using FernFlower decompiler.
package controlhelper.modules;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.struct.Queue;
import arc.util.Time;
import arc.util.Timer;
import arc.util.Tmp;
import controlhelper.ControlHelper;
import controlhelper.core.Vec2Int;
import controlhelper.core.events.CHEventType;
import controlhelper.utils.GeneralUtils;
import controlhelper.utils.GeometryUtils;
import java.util.Iterator;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.game.EventType.Trigger;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.input.Binding;
import mindustry.input.DesktopInput;
import mindustry.input.Placement;
import mindustry.type.ItemStack;

public class PlansSkipper {
    public Queue<BuildPlan> skipBuffer = new Queue<>();
    public int maxLength = Integer.MAX_VALUE;
    public float refreshDelay = 0.1F;
    public Vec2Int pos1;
    public Vec2Int pos2;

    public PlansSkipper() {
    }

    public void Init() {
        Events.run(Trigger.update, () -> {
            if (Vars.control.input instanceof DesktopInput && this.IsEnabled() && Vars.state.isGame()
                    && Vars.player != null && Vars.player.unit() != null
                    && !Core.input.keyTap(Binding.clear_building)) {
                DesktopInput input = (DesktopInput) Vars.control.input;
                this.pos1 = new Vec2Int(input.selectX, input.selectY);
                this.pos2 = new Vec2Int(GeometryUtils.TileX((float) Core.input.mouseX()),
                        GeometryUtils.TileY((float) Core.input.mouseY()));
                if (Core.input.keyRelease(Binding.break_block) && Vars.control.input.isBreaking()) {
                    this.RemoveSelection();
                }

            } else {
                this.skipBuffer.clear();
            }
        });
        Timer.schedule(() -> {
            if (Vars.control.input instanceof DesktopInput && this.IsEnabled() && Vars.state.isGame()
                    && Vars.player != null && Vars.player.unit() != null
                    && !Core.input.keyTap(Binding.clear_building)) {
                int counter = 0;

                for (int max = Vars.player.unit().plans.size; counter < max
                        && this.ShouldSkip((BuildPlan) Vars.player.unit().plans.first()); ++counter) {
                    BuildPlan plan = (BuildPlan) Vars.player.unit().plans.first();
                    if (!ControlHelper.eventsRunner.ignorePlans.contains(plan)) {
                        ControlHelper.eventsRunner.ignorePlans.add(plan);
                    }

                    Vars.player.unit().plans.removeFirst();
                    this.skipBuffer.add(plan);
                }

                Iterator<BuildPlan> iterator = this.skipBuffer.iterator();

                while (iterator.hasNext()) {
                    BuildPlan planx = (BuildPlan) iterator.next();
                    if (!this.ShouldSkip(planx)) {
                        iterator.remove();
                        Vars.player.unit().plans.add(planx);
                    }
                }

            } else {
                this.skipBuffer.clear();
            }
        }, 0.0F, this.refreshDelay);
        Events.on(CHEventType.PlayerPlansChangeEvent.class, (e) -> {
            if (Vars.control.input instanceof DesktopInput && this.IsEnabled() && Vars.state.isGame()
                    && Vars.player != null && Vars.player.unit() != null) {
                Iterator var2 = e.added.iterator();

                while (var2.hasNext()) {
                    BuildPlan plan = (BuildPlan) var2.next();
                    BuildPlan duplicate = GeneralUtils.GetPlanAt(new Vec2Int(plan.x, plan.y), plan.block.size,
                            this.skipBuffer);
                    if (duplicate != null) {
                        this.skipBuffer.remove(duplicate);
                    }
                }

            } else {
                this.skipBuffer.clear();
            }
        });
        Events.run(Trigger.draw, () -> {
            if (this.IsEnabled() && Vars.state.isGame() && Vars.player != null && Vars.player.unit() != null
                    && Vars.control.input instanceof DesktopInput) {
                this.DrawBottom();
            } else {
                this.skipBuffer.clear();
            }
        });
    }

    public void DrawBottom() {
        DesktopInput input = (DesktopInput) Vars.control.input;
        if (this.pos1 != null && this.pos2 != null) {
            Placement.NormalizeResult result = Placement.normalizeArea(this.pos1.x, this.pos1.y, this.pos2.x,
                    this.pos2.y, input.rotation, false, this.maxLength);
            Iterator var3 = this.skipBuffer.iterator();

            while (true) {
                BuildPlan plan;
                do {
                    do {
                        do {
                            if (!var3.hasNext()) {
                                input.drawBottom();
                                return;
                            }

                            plan = (BuildPlan) var3.next();
                        } while (plan == null);
                    } while (plan.block == null);
                } while (plan.tile().build != null && plan.build() != null && plan.tile().build == plan.build());

                boolean breaking = false;
                if (input.isBreaking()) {
                    int tilesize = 8;
                    Tmp.r1.set((float) (result.x * tilesize), (float) (result.y * tilesize),
                            (float) ((result.x2 - result.x) * tilesize), (float) ((result.y2 - result.y) * tilesize));
                    if (plan.bounds(Tmp.r2).overlaps(Tmp.r1)) {
                        breaking = true;
                    }
                }

                plan.block.drawPlan(plan, this.skipBuffer, true);
                Draw.reset();
                Draw.mixcol(Color.white, 0.24F + Mathf.absin(Time.globalTime, 6.0F, 0.28F));
                Draw.alpha(1.0F);
                plan.block.drawPlanConfigTop(plan, this.skipBuffer);
                Draw.reset();
                if (breaking) {
                    Drawf.selected(plan.x, plan.y, plan.block, Pal.remove);
                }
            }
        }
    }

    public void RemoveSelection() {
        Placement.NormalizeResult result = Placement.normalizeArea(this.pos1.x, this.pos1.y, this.pos2.x, this.pos2.y,
                Vars.control.input.rotation, false, this.maxLength);
        int tilesize = 8;
        Tmp.r1.set((float) (result.x * tilesize), (float) (result.y * tilesize),
                (float) ((result.x2 - result.x) * tilesize), (float) ((result.y2 - result.y) * tilesize));
        Iterator<BuildPlan> iterator = this.skipBuffer.iterator();

        while (iterator.hasNext()) {
            BuildPlan plan = (BuildPlan) iterator.next();
            if (plan.bounds(Tmp.r2).overlaps(Tmp.r1)) {
                iterator.remove();
            }
        }

    }

    public boolean ShouldSkip(BuildPlan plan) {
        if (plan != null && plan.block != null) {
            if (plan.breaking) {
                return false;
            } else if (Vars.player.unit().core() != null && !Vars.state.rules.infiniteResources
                    && !Vars.player.team().rules().infiniteResources) {
                ItemStack[] var2 = plan.block.requirements;
                int var3 = var2.length;

                for (int var4 = 0; var4 < var3; ++var4) {
                    ItemStack requirement = var2[var4];
                    if (Vars.player.core().items.get(requirement.item) <= 0) {
                        return true;
                    }
                }

                return false;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean IsEnabled() {
        return Core.settings.getBool("plansSkipper", true);
    }
}
