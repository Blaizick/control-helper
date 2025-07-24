/*package controlhelper.modules.mapscheme;

import static controlhelper.ControlHelper.mapSchemeManager;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.input.KeyCode;
import arc.math.geom.Vec2;
import controlhelper.core.CHDraw;
import controlhelper.core.Vec2Int;
import controlhelper.utils.GeometryUtils;
import mindustry.Vars;
import mindustry.game.EventType.Trigger;

public class MapSchemeAppendor 
{
    public boolean selection;
    protected boolean touched;
    public Vec2Int pos1, pos2;

    public Color col1, col2;
    public int maxLength = Integer.MAX_VALUE;

    public MapSchemeAppendor()
    {
        col1 = Color.valueOf("#FFEE8C");
        col2 = Color.valueOf("#FFCE1B");
    }

    public void Init()
    {
        Events.run(Trigger.drawOver, () -> 
        {
            if (selection && touched) CHDraw.Selection(pos1, pos2, maxLength, col1, col2);
        });
        Events.run(Trigger.update, () -> 
        {
            if (!selection || !Vars.state.isGame()) 
            {
                selection = false;
                touched = false;
                return;
            }
            if (Core.input.keyDown(KeyCode.mouseLeft))
            {
                Vec2Int pos = new Vec2Int(GeometryUtils.TileX(Core.input.mouseX()), GeometryUtils.TileY(Core.input.mouseY()));
                if (!touched) pos1 = pos.cpy();
                pos2 = pos.cpy();
                touched = true;
            }
            if (Core.input.keyRelease(KeyCode.mouseLeft) && touched)
            {
                EndSelection();
            }
        });
    }

    public void EndSelection()
    {
        ShowSchemeSave();
        touched = false;
        selection = false;
    }

    public void ShowSchemeSave()
    {
        Vars.ui.showTextInput("@mapScheme.addSchemeTextInput.title", "@mapScheme.addSchemeTextInput.nameLabel", 1000, "", text ->
        {
            Vec2 anchor = new Vec2((pos1.x + pos2.x) / 2, (pos1.y + pos2.y) / 2);
            MSScheme scheme = new MSScheme(anchor, Vars.schematics.create(pos1.x, pos1.y, pos2.x, pos2.y));
            scheme.tags.put("name", text);
            scheme.tags.put("description", "");
            mapSchemeManager.AddScheme(scheme);
            Vars.ui.showInfoFade("@mapScheme.schemeAddedFade.text");
        });
    }
}
*/