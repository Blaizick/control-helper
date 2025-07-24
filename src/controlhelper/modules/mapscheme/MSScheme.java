package controlhelper.modules.mapscheme;

import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.struct.StringMap;
import mindustry.game.Schematic;

public class MSScheme extends Schematic
{
    public Vec2 pos = new Vec2();

    public MSScheme()
    {
        super(new Seq<>(), new StringMap(), 0, 0);
        pos = new Vec2();
    }

    public MSScheme(Vec2 pos, Schematic schematic)
    {
        super(schematic.tiles, schematic.tags, schematic.width, schematic.height);
        this.pos = pos;
    }

    public MSScheme(Vec2 pos, Seq<Stile> tiles, StringMap tags, int width, int height) 
    {
        super(tiles, tags, width, height);
        this.pos = pos;
    }
}
