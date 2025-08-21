package controlhelper.core;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.Lines;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.graphics.Layer;
import mindustry.input.Placement;
import mindustry.input.Placement.NormalizeDrawResult;
import mindustry.ui.Fonts;

public class CHDraw 
{
    public static void Selection(Vec2Int pos1, Vec2Int pos2, int maxLength, Color col1, Color col2)
    {
        NormalizeDrawResult result = Placement.normalizeDrawArea(Blocks.air, pos1.x, pos1.y, pos2.x, pos2.y, false, maxLength, 1f);

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