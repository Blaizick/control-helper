package controlhelper.ui.elements;

import arc.Core;
import arc.func.Boolc;
import arc.util.Align;
import mindustry.ui.Styles;

public class CHIconCheckPref extends CHIconCheckBox {
    public CHIconCheckPref(String name, String icon, boolean defaultChecked, Boolc onCheck) {
        super(icon, defaultChecked, onCheck);
        this.name = name;
    }

    public CHIconCheckPref(String name, String icon, Boolc onCheck) {
        super(icon, onCheck);
        this.name = name;
    }

    @Override
    public CHIconCheckBox Init() {
        Load();
        setChecked(checked);
        if (callbackOnInit)
            onCheck.each(i -> i.get(checked));
        setStyle(Styles.flatTogglet);
        add(icon).align(Align.center);
        clicked(() -> {
            checked = !checked;
            onCheck.each(i -> i.get(checked));
            Save();
            setChecked(checked);
        });
        return this;
    }

    @Override
    public void setChecked(boolean isChecked) {
        super.setChecked(isChecked);
    }

    public void Save() {
        if (name == null || name.isEmpty())
            return;
        Core.settings.put("control-helper-check-pref-" + name, checked);
    }

    public void Load() {
        if (name == null || name.isEmpty())
            return;
        checked = Core.settings.getBool("control-helper-check-pref-" + name, defaultChecked);
    }
}
