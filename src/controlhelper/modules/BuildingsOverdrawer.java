package controlhelper.modules;

import static arc.Core.input;

import java.util.HashSet;

import arc.Core;
import arc.Events;
import arc.func.Boolf;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.content.UnitTypes;
import mindustry.entities.units.BuildPlan;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.input.DesktopInput;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock.CoreBuild;

public class BuildingsOverdrawer {
    protected HashSet<Building> redrawSet = new HashSet<>();
    protected BuildPlan splan = null;

    public void Init(){
        Timer.schedule(() -> {
            redrawSet.clear();
            splan = null;
            if (Vars.player == null || !Vars.state.isGame() || !IsEnabled()){
                return;
            }

            if (Vars.control.input instanceof DesktopInput){
                DesktopInput input = (DesktopInput)Vars.control.input;
                Log.info(input.splan);
                if (input.splan != null){
                    splan = input.splan;
                }
            }

            Groups.unit.each(unit -> {
                if (unit.isPlayer() && !unit.isEnemy()) return;

                Building build = Vars.world.build(unit.tileX(), unit.tileY());
                if (build != null && build instanceof CoreBuild && !redrawSet.contains(build)){
                    redrawSet.add(build);
                }
                if (splan != null && !Contains(splan, new Vec2(unit.x, unit.y))){
                    splan = null;
                }
            });
        }, 0, 0.05f);

        Events.run(Trigger.drawOver, () -> {
            if (Vars.player == null || !Vars.state.isGame() || !IsEnabled()){
                return;
            }

            Draw.reset();
            Draw.color(new Color(1,1,1,0.75f));
            Draw.z(Layer.flyingUnit + 1);

            for (Building building : redrawSet) 
            {
                Draw.rect(building.block.region, building.x, building.y);
                building.block.drawOverlay(building.x, building.y, building.rotation);

            }

            DesktopInput input = (DesktopInput)Vars.control.input;
            if (splan != null){

                boolean valid = input.validPlace(splan.x, splan.y, splan.block, splan.rotation, splan);
                if(splan.block.rotate && splan.block.drawArrow){
                    input.drawArrow(splan.block, splan.x, splan.y, splan.rotation, valid);
                }

                splan.block.drawPlan(splan, new Seq<BuildPlan>(new BuildPlan[] {splan}), valid);
                
                input.drawSelected(splan.x, splan.y, splan.block, GetPlan(splan) != null ? Pal.remove : Pal.accent);
            }

            Draw.reset();
        });
    }



    public BuildPlan GetPlan(BuildPlan plan){
        int x = plan.x;
        int y = plan.y;
        int tilesize = Vars.tilesize;
        int size = plan.block.size;
        Rect r1 = new Rect();
        Rect r2 = new Rect();
        Player player = Vars.player;
        Seq<BuildPlan> selectPlans = Vars.control.input.selectPlans;

        float offset = ((size + 1) % 2) * tilesize / 2f;
        r2.setSize(tilesize * size);
        r2.setCenter(x * tilesize + offset, y * tilesize + offset);

        Boolf<BuildPlan> test = p -> {
            if(p == plan) return false;
            Tile other = p.tile();

            if(other == null) return false;

            if(!p.breaking){
                r1.setSize(p.block.size * tilesize);
                r1.setCenter(other.worldx() + p.block.offset, other.worldy() + p.block.offset);
            }else{
                r1.setSize(other.block().size * tilesize);
                r1.setCenter(other.worldx() + other.block().offset, other.worldy() + other.block().offset);
            }

            return r2.overlaps(r1);
        };

        for(BuildPlan p : player.unit().plans()){
            if(test.get(p)) return p;
        }

        return selectPlans.find(test);
    }


    public boolean Contains(BuildPlan plan, Vec2 pos){
        Rect rect = new Rect();
        plan.block.bounds(plan.x, plan.y, rect);
        return rect.contains(pos);
    }


    public boolean IsEnabled(){
        return Core.settings.getBool("@setting.buildingsOverdrawer.name", true);
    }
}
