package controlhelper.ui.elements;

import static arc.Core.settings;

import arc.func.Boolc;
import arc.scene.ui.CheckBox;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable.CheckSetting;

public class TooltipCheckSetting extends CheckSetting {
    private Boolc changed;
    private String tooltip;
    private boolean def;

    public TooltipCheckSetting(String name, String tooltip, boolean def, Boolc changed) {
        super(name, def, changed);
        this.changed = changed;
        this.tooltip = tooltip;
        this.def = def;
    }

    @Override
    public void add(SettingsTable table) {
        CheckBox box = new CheckBox(title);

        box.changed(() -> {
            settings.put(name, box.isChecked());
            if (changed != null) {
                changed.get(box.isChecked());
            }
        });

        box.left();
        addDesc(table.add(box).tooltip(tooltip).left().padTop(3f).get());
        table.row();
    }

}