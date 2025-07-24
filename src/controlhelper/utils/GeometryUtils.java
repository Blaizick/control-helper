package controlhelper.utils;

import arc.Core;
import arc.math.geom.Vec2;
import mindustry.Vars;
import mindustry.core.World;

public class GeometryUtils 
{
    public static int TileX(float cursorX)
    {
        Vec2 vec = Core.input.mouseWorld(cursorX, 0);
        if(Vars.control.input.selectedBlock())
        {
            vec.sub(Vars.control.input.block.offset, Vars.control.input.block.offset);
        }
        return World.toTile(vec.x);
    }

    public static int TileY(float cursorY)
    {
        Vec2 vec = Core.input.mouseWorld(0, cursorY);
        if(Vars.control.input.selectedBlock())
        {
            vec.sub(Vars.control.input.block.offset, Vars.control.input.block.offset);
        }
        return World.toTile(vec.y);
    }    
}
