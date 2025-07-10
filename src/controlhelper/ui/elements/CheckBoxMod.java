package controlhelper.ui.elements;

import static arc.Core.settings;

import arc.func.Cons;
import arc.scene.ui.Button;
import arc.util.Align;
import mindustry.ui.Styles;

public class CheckBoxMod extends Button
{
    protected Cons<Boolean> onCheck;
    public boolean checked;
    public final boolean defaultChecked;
    
    public ButtonStyle style;
    public String icon;

    public CheckBoxMod(String name, String icon, boolean defaultChecked, Cons<Boolean> onCheck)
    {
        super();

        this.name = name;
        this.onCheck = onCheck;
        this.defaultChecked = defaultChecked;
        this.icon = icon;
        this.style = Styles.flatTogglet;

        Init();
    }

    public CheckBoxMod(String icon, boolean defaultChecked, Cons<Boolean> onCheck)
    {
        super();
        this.name = null;
        this.onCheck = onCheck;
        this.defaultChecked = defaultChecked;
        this.icon = icon;
        this.style = Styles.flatTogglet;
    }

    public void Init()
    {
        setStyle(Styles.flatTogglet);
        add(icon).align(Align.center);
        if (name != null && !name.isEmpty()) Load();
        clicked(() -> {SetChecked(!checked);} );
    }

    public void SetChecked(boolean value)
    {
        checked = value;
        setChecked(checked);
        if (name != null && !name.isEmpty()) Save();
        onCheck.get(checked);
    }

    public void Load()
    {
        checked = settings.getBool("control-helper-checkbox-" + name, defaultChecked);
    }

    public void Save()
    {
        settings.put("control-helper-checkbox-" + name, checked);
    }
}
