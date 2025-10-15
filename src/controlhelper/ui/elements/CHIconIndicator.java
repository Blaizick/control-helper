package controlhelper.ui.elements;

import arc.func.Boolp;
import arc.graphics.Color;
import arc.scene.ui.Button.ButtonStyle;
import arc.scene.ui.layout.Table;
import arc.util.Align;

public class CHIconIndicator extends Table {
    public Boolp test;

    public ButtonStyle style;
    public String icon;

    public final static Color uncheckedColor = Color.white.cpy();
    public final static Color checkedColor = Color.green.cpy();

    public CHIconIndicator(String icon, Boolp test) {
        super();
        this.icon = icon;
        this.test = test;
    }

    public CHIconIndicator Init() {
        add(icon).align(Align.center);
        update(() -> {
            setColor(test.get() ? checkedColor : uncheckedColor);
        });
        return this;
    }
}
