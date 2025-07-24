/*package controlhelper.ui.mapscheme;

import static controlhelper.ControlHelper.mapSchemeManager;

import arc.Core;
import arc.func.Cons;
import arc.graphics.Color;
import arc.math.geom.Vec2;
import arc.scene.ui.Button;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Scaling;
import controlhelper.inputs.Keybind;
import controlhelper.modules.mapscheme.MSScheme;
import controlhelper.utils.GeometryUtils;
import mindustry.Vars;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.SchematicsDialog.SchematicImage;

public class MapSchemeBox extends Table 
{
    public boolean shown = false;
    public boolean activated = false;
    public Cons<Seq<MSScheme>> rebuildPane;
    
    public float schemePreviewSize = 100f;
    public int rows = 4;

    public void Init()
    {
        visible(() -> Vars.state.isGame() && Vars.ui.hudfrag.shown && Keybind.mapSchemeBox.KeyDown());
        pane(Styles.noBarPane, cont ->
        {
            
        });

        update(() -> 
        {
            if (Keybind.mapSchemeBox.KeyDown())
            {
                if (!activated)
                {
                    var schemes = mapSchemeManager.GetSchemes(new Vec2(GeometryUtils.TileX(Core.input.mouseX()), GeometryUtils.TileY(Core.input.mouseY())));
                    if (schemes != null) rebuildPane.get(schemes);
                    setPosition(Core.input.mouseX(), Core.input.mouseY());
                }
                activated = true;
            }
            if (Keybind.mapSchemeBox.KeyUp())
            {
                activated = false;
            }
            
            width = getPrefWidth();
            height = getPrefHeight();
        });

        pane(t -> 
        {
            rebuildPane = schemes ->
            {
                t.clear();
                int cur = 0;
                for (MSScheme scheme : schemes)
                {
                    if (cur % 4 == 0 ) t.row();
                    Button[] sel = {null};
                    sel[0] = t.button(b ->
                    {
                        b.top();
                        b.margin(0f);
                        b.stack(new SchematicImage(scheme).setScaling(Scaling.fit), new Table(n ->
                        {
                            n.top();
                            n.table(Styles.black3, c -> 
                            {
                                Label label = c.add(scheme.name()).style(Styles.outlineLabel).color(Color.white).top().growX().maxWidth(schemePreviewSize-8f).get();
                                label.setEllipsis(true);
                                label.setAlignment(Align.center);
                            }).growX().margin(1).pad(4).maxWidth(Scl.scl(schemePreviewSize-8f)).padBottom(0);
                        })).size(schemePreviewSize);
                    }, () -> 
                    {
                        mapSchemeManager.UseScheme(scheme);
                    }).pad(4).style(Styles.flati).get();
                    cur++;
                }
            };
        }).grow().scrollX(false);
    }

    public void Build()
    {
        Core.scene.add(this);
    }
}
*/