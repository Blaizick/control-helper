package controlhelper.ui.elements;

import arc.func.Cons;
import arc.scene.ui.Button;
import arc.util.Align;
import mindustry.ui.Styles;

public class CHIconCheckBox extends Button
{
    protected Cons<Boolean> onCheck;
    public boolean checked;
    public final boolean defaultChecked;
    
    public ButtonStyle style;
    public String icon;


    public CHIconCheckBox(String icon, Cons<Boolean> onCheck)
    {
        super();
        this.name = null;
        this.onCheck = onCheck;
        this.defaultChecked = false;
        this.icon = icon;
        this.style = Styles.flatTogglet;
    }

    public CHIconCheckBox(String icon, boolean defaultChecked, Cons<Boolean> onCheck)
    {
        super();
        this.name = null;
        this.onCheck = onCheck;
        this.defaultChecked = defaultChecked;
        this.icon = icon;
        this.style = Styles.flatTogglet;
    }

    public CHIconCheckBox Init()
    {
        SetChecked(defaultChecked);
        setStyle(Styles.flatTogglet);
        add(icon).align(Align.center);
        clicked(() -> {SetChecked(!checked);});
        return this;
    }

    public void SetChecked(boolean value)
    {
        checked = value;
        setChecked(checked);
        onCheck.get(checked);
    }
}
