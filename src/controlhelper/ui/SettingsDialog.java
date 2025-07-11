package controlhelper.ui;

import static arc.Core.bundle;

import controlhelper.ControlHelper;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable;

public class SettingsDialog extends BaseDialog
{
    public SettingsDialog()
    {
        super(bundle.get("settings.title"));
    }

    public void Init()
    {
        Vars.ui.settings.addCategory(bundle.get("settings.category"), Icon.commandAttack, table ->
        {
            this.table = table;
        });

        Refresh();
    }

    private SettingsTable table;

    public void Refresh()
    {
        table.clear();

        table.checkPref(bundle.get("settings.drillsValidator.name"), true);
        table.checkPref(bundle.get("settings.plansSaver.name"), true);
        table.checkPref(bundle.get("settings.handMiner.name"), true);
        table.sliderPref(bundle.get("settings.unitsAttackDelay.name"), 50, 10, 300, 10, i -> String.valueOf(i));
        table.sliderPref(bundle.get("settings.plansResetMilis.name"), 500, 25, 1000, 25, i -> String.valueOf(i));

        table.row();
        table.row();

        table.button(bundle.get("settings.controls"), () ->
        {
            ControlHelper.controlsDialog.Refresh();
            ControlHelper.controlsDialog.show();
        }).size(120f, 50f);
    }
}