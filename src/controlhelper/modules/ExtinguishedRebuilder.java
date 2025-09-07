package controlhelper.modules;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import controlhelper.core.CHDraw;
import controlhelper.core.Vec2Int;
import controlhelper.core.inputs.Keybind;
import controlhelper.utils.GeometryUtils;
import mindustry.Vars;
import mindustry.entities.Fires;
import mindustry.entities.units.BuildPlan;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Building;
import mindustry.input.Placement;
import mindustry.input.Placement.NormalizeResult;
import mindustry.world.Tile;

public class ExtinguishedRebuilder
{
    public int firstX, firstY;
    public int secondX, secondY;

    public boolean selection = false;
    
    public Color col1, col2;
    public int maxLength = Integer.MAX_VALUE;

    public Seq<Selection> selections = new Seq<>();


    public ExtinguishedRebuilder()
    {
        col1 = Color.valueOf("#ed8870");
        col2 = Color.valueOf("#e76243");
    }

    public void Init()
    {
        Events.run(Trigger.drawOver, () -> 
        {
            if (selection) CHDraw.Selection(new Vec2Int(firstX, firstY), new Vec2Int(secondX, secondY), maxLength, col1, col2);
        });
        Events.run(Trigger.update, () ->
        {
            if (!Vars.state.isGame() || Vars.control.input.commandMode) 
            {
                selection = false;
                selections.clear();
                return;
            }

            if (Keybind.rebuildExtinguished.KeyDown())
            {
                secondX = GeometryUtils.TileX(Core.input.mouseX());
                secondY = GeometryUtils.TileY(Core.input.mouseY());
                if (!selection) 
                {
                    firstX = secondX;
                    firstY = secondY;
                }

                selection = true;
            }

            if (Keybind.rebuildExtinguished.KeyUp())
            {
                secondX = GeometryUtils.TileX(Core.input.mouseX());
                secondY = GeometryUtils.TileY(Core.input.mouseY());

                selections.add(new Selection(firstX, firstY, secondX, secondY));
                selection = false;
            }

            UpdateSelections();
        });
    }


    public void UpdateSelections()
    {
        for (Selection sel : selections) 
        {
            sel.Update();
        }

        selections.remove(sel -> sel.finished);
    }


    public class Selection
    {
        public int x1, y1;
        public int x2, y2;

        public Seq<BuildPlan> brokenBlocks = new Seq<>();
        public boolean finished = false;

        public Selection(int x1, int y1, int x2, int y2)
        {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        public void Update()
        {
            Seq<Tile> fires = GetFires();
            if (fires == null || fires.size == 0) 
            {
                RebuildBrokenBlocks();
                finished = true;
                return;
            }

            Seq<Building> builds = GetBuildingsOnTiles(fires);
            for (Building build : builds) 
            {
                if (IsBreakPlannedOnPos(new Vec2(build.x, build.y))) continue;
                brokenBlocks.add(new BuildPlan(build.tileX(), build.tileY(), build.rotation, build.block, build.config()));
                Vars.control.input.tryBreakBlock(build.tileX(), build.tileY());
            }
        }

        public Seq<Tile> GetFires()
        {
            NormalizeResult result = Placement.normalizeArea(x1, y1, x2, y2, 0, false, maxLength);
            Seq<Tile> fires = new Seq<>();
            for (int x = 0; x <= Math.abs(result.x2 - result.x); x++)
            {
                for (int y = 0; y <= Math.abs(result.y2 - result.y); y++)
                {
                    int wx = x1 + x * Mathf.sign(x2 - x1);
                    int wy = y1 + y * Mathf.sign(y2 - y1);

                    if (!Fires.has(wx, wy)) continue;

                    Tile tile = Vars.world.tile(wx, wy);
                    if (tile != null) fires.add(tile);
                }
            }

            return fires;
        }

        public void RebuildBrokenBlocks()
        {
            for (BuildPlan plan : brokenBlocks) 
            {
                boolean found = false;
                for (BuildPlan p : Vars.player.unit().plans) 
                {
                    if (!p.breaking) continue;
                    if (p.build() != null && p.tile().x == plan.x && p.tile().y == plan.x && p.block == plan.block)
                    {
                        found = true;
                        Vars.player.unit().plans.remove(p);
                        break;
                    }
                }            
                if (found) continue;
                Vars.player.unit().addBuild(plan);
            }
        }
    
    
        public boolean IsBreakPlannedOnPos(Vec2 pos)
        {
            for (BuildPlan plan : Vars.player.unit().plans) 
            {
                if (!plan.breaking) continue;
                if (plan.build() != null && plan.build().within(pos, 0.1f)) return true;
            }
            return false;
        }

        public Seq<Building> GetBuildingsOnTiles(Seq<Tile> tiles)
        {
            Seq<Building> buildings = new Seq<>();
            for (Tile tile : tiles) 
            {
                if (tile.build == null) continue;
                buildings.add(tile.build);
            }
            return buildings;
        }
    }
}