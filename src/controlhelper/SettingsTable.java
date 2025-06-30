package controlhelper;

import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.gen.Icon;

public class SettingsTable
{
    public void Init()
    {
        Vars.ui.settings.addCategory("Control helper", Icon.commandAttack, table ->
        {
            this.table = table;
        });
        Refresh();
    }

    private Table table;

    public void Refresh()
    {
        table.clear();
        table.row();

        for (Keybind key : Keybind.all)
        {
            table.add(key.displayName);

            table.add(key.key.name()).padLeft(80f);

            table.button("Rebind", () ->
            {
                ControlHelper.rebindOverlay.Show(key);
            }).size(120f, 50f).padLeft(30f);

            table.button("Reset", () ->
            {
                key.Reset();
                key.Save();
                ControlHelper.settingsTable.Refresh();
            }).size(120f, 50f).padLeft(30f);
        }
    }
}