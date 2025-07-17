package controlhelper.core;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Log;
import controlhelper.inputs.Keybind;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.core.World;
import mindustry.entities.Fires;
import mindustry.entities.units.BuildPlan;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Building;
import mindustry.gen.Fire;
import mindustry.graphics.Layer;
import mindustry.input.Placement;
import mindustry.input.Placement.NormalizeDrawResult;
import mindustry.input.Placement.NormalizeResult;
import mindustry.ui.Fonts;
import mindustry.world.Tile;

public class ExtinguishedRebuilder
{
    public boolean selection = false;
    public int selectionX, selectionY;
    public int cursorX, cursorY;
    public Color col1, col2;

    public boolean updateSelection = false;
    public Seq<BuildPlan> brokenBlocks = new Seq<>();

    public ExtinguishedRebuilder()
    {
        col1 = Color.valueOf("#ed8870");
        col2 = Color.valueOf("#e76243");
    }

    public void Init()
    {
        Events.run(Trigger.drawOver, () -> 
        {
            if (selection) DrawSelection();
        });
        Events.run(Trigger.update, () ->
        {
            if (!Vars.state.isGame() || Vars.control.input.commandMode) 
            {
                selection = false;
                updateSelection = false;
                return;
            }

            if (Keybind.rebuildExtinguished.KeyDown())
            {
                if (updateSelection) RebuildBrokenBlocks();

                cursorX = TileX(Core.input.mouseX());
                cursorY = TileY(Core.input.mouseY());
                if (!selection) 
                {
                    selectionX = cursorX;
                    selectionY = cursorY;
                    brokenBlocks.clear();
                }

                selection = true;
                updateSelection = false;
            }

            if (Keybind.rebuildExtinguished.KeyUp())
            {
                cursorX = TileX(Core.input.mouseX());
                cursorY = TileY(Core.input.mouseY());

                updateSelection = true;
                selection = false;
            }

            if (updateSelection) UpdateSelection();
        });
    }


    public void UpdateSelection()
    {
        Seq<Tile> fires = GetFiresInSelection();
        if (fires == null || fires.size == 0) 
        {
            RebuildBrokenBlocks();
            updateSelection = false;
            return;
        }

        Seq<Building> builds = GetBuildings(fires);
        for (Building build : builds) 
        {
            if (ContainsBreakPlan(build)) continue;
            brokenBlocks.add(new BuildPlan(build.tileX(), build.tileY(), build.rotation, build.block, build.config()));
            Vars.control.input.tryBreakBlock(build.tileX(), build.tileY());
        }
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

    public boolean ContainsBreakPlan(Building build)
    {
        for (BuildPlan plan : Vars.player.unit().plans) 
        {
            if (!plan.breaking) continue;
            if (plan.build() != null && plan.build().pos() == build.pos()) return true;
        }
        return false;
    }

    public Seq<Building> GetBuildings(Seq<Tile> tiles)
    {
        Seq<Building> buildings = new Seq<>();
        for (Tile tile : tiles) 
        {
            if (tile.build == null) continue;
            buildings.add(tile.build);
        }
        return buildings;
    }

    public Seq<Tile> GetFiresInSelection()
    {
        int x1 = Mathf.round(selectionX);
        int x2 = Mathf.round(cursorX);
        int y1 = Mathf.round(selectionY);
        int y2 = Mathf.round(cursorY);
        int maxLength = Integer.MAX_VALUE;

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


    public int TileX(float cursorX)
    {
        Vec2 vec = Core.input.mouseWorld(cursorX, 0);
        if(Vars.control.input.selectedBlock())
        {
            vec.sub(Vars.control.input.block.offset, Vars.control.input.block.offset);
        }
        return World.toTile(vec.x);
    }

    public int TileY(float cursorY)
    {
        Vec2 vec = Core.input.mouseWorld(0, cursorY);
        if(Vars.control.input.selectedBlock())
        {
            vec.sub(Vars.control.input.block.offset, Vars.control.input.block.offset);
        }
        return World.toTile(vec.y);
    }

    public void DrawSelection()
    {
        int x1 = Mathf.round(selectionX);
        int x2 = Mathf.round(cursorX);
        int y1 = Mathf.round(selectionY);
        int y2 = Mathf.round(cursorY);
        int maxLength = Integer.MAX_VALUE;
        NormalizeDrawResult result = Placement.normalizeDrawArea(Blocks.air, x1, y1, x2, y2, false, maxLength, 1f);

        var col = Draw.getColor();
        Lines.stroke(2f);
        Draw.color(col2);
        Lines.rect(result.x, result.y - 1, result.x2 - result.x, result.y2 - result.y);
        Draw.color(col1);
        Lines.rect(result.x, result.y, result.x2 - result.x, result.y2 - result.y);
        Lines.stroke(1f);
        Draw.color(col);

        Font font = Fonts.outline;
        font.setColor(col2);
        var ints = font.usesIntegerPositions();
        font.setUseIntegerPositions(false);
        var z = Draw.z();
        Draw.z(Layer.endPixeled);
        font.getData().setScale(1 / Vars.renderer.getDisplayScale());
        font.draw((int)((result.x2 - result.x) / 8) + "x" + (int)((result.y2 - result.y) / 8), result.x2, result.y);
        font.setColor(Color.white);
        font.getData().setScale(1);
        font.setUseIntegerPositions(ints);
        Draw.z(z);
    }
}