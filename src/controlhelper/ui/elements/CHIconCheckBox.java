package controlhelper.ui.elements;

import arc.func.Boolc;
import arc.scene.ui.Button;
import arc.struct.Seq;
import arc.util.Align;
import mindustry.ui.Styles;

public class CHIconCheckBox extends Button
{
    public boolean checked;

    protected Seq<Boolc> onCheck = new Seq<>();
    public final boolean defaultChecked;
    
    public ButtonStyle style;
    public String icon;

    public boolean callbackOnInit = true;

    public CHIconCheckBox(String icon)
    {
        super();
        this.defaultChecked = false;
        this.icon = icon;
        this.style = Styles.flatTogglet;
    }

    public CHIconCheckBox(String icon, Boolc onCheck)
    {
        super();
        if (onCheck != null) this.onCheck.add(onCheck);
        this.defaultChecked = false;
        this.icon = icon;
        this.style = Styles.flatTogglet;
    }

    public CHIconCheckBox(String icon, boolean defaultChecked, Boolc onCheck)
    {
        super();
        if (onCheck != null) this.onCheck.add(onCheck);
        this.defaultChecked = defaultChecked;
        this.icon = icon;
        this.style = Styles.flatTogglet;
    }

    public CHIconCheckBox Init()
    {
        checked = defaultChecked;
        if (callbackOnInit) onCheck.each(i -> i.get(checked));
        setStyle(Styles.flatTogglet);
        add(icon).align(Align.center);
        clicked(() -> 
        {
            checked = !checked;
            onCheck.each(i -> i.get(checked));
            setChecked(checked);
        });
        return this;
    }

    public CHIconCheckBox DisableCallbackOnInit()
    {
        callbackOnInit = false;
        return this;
    }

    public void OnCheckAdd(Boolc onCheck)
    {
        this.onCheck.add(onCheck);
    }
}