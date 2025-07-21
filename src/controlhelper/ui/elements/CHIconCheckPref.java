package controlhelper.ui.elements;

import arc.Core;
import arc.func.Cons;
import arc.util.Align;
import mindustry.ui.Styles;

public class CHIconCheckPref extends CHIconCheckBox
{

    public CHIconCheckPref(String name, String icon, boolean defaultChecked, Cons<Boolean> onCheck) 
    {
        super(icon, defaultChecked, onCheck);
        this.name = name;
    }

    public CHIconCheckPref(String name, String icon, Cons<Boolean> onCheck) 
    {
        super(icon, onCheck);
        this.name = name;
    }

    @Override
    public CHIconCheckBox Init()
    {
        Load();
        setStyle(Styles.flatTogglet);
        add(icon).align(Align.center);
        clicked(() -> 
        {
            SetChecked(!checked);
            Save();
        });
        return this;
    }

    public void Save()
    {
        if (name == null || name.isEmpty()) return;
        Core.settings.put("control-helper-check-pref-" + name, checked);
    }

    public void Load()
    {
        if (name == null || name.isEmpty()) return;
        SetChecked(Core.settings.getBool("control-helper-check-pref-" + name, defaultChecked));
    }
}
