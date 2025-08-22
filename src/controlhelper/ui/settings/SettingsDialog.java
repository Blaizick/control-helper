package controlhelper.ui.settings;

import static arc.Core.bundle;
import static controlhelper.ControlHelper.advancedSettingsDialog;
import static controlhelper.ControlHelper.controlsDialog;

import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable;

public class SettingsDialog extends BaseDialog {
    public SettingsTable table;

    public SettingsDialog() {
        super(bundle.get("settings.title"));
    }

    public void Init() {
        Vars.ui.settings.addCategory(bundle.get("settings.category"), Icon.commandAttack, table -> {
            this.table = table;
        });

        table.checkPref("drillsValidator", true);
        table.checkPref("plansSaver", true);
        table.checkPref("handMiner", true);
        table.checkPref("ignoreSupportUnits", true);
        table.checkPref("showControlHelperWindow", false);
        table.checkPref("prioritizePlans", true);
        table.checkPref("nodesBreaker", true);
        table.checkPref("plansSkipper", false);
        table.checkPref("buildingsOverdrawer", true);

        table.row();
        table.button(bundle.get("settings.controlsButton.label"), () -> {
            controlsDialog.Refresh();
            controlsDialog.show();
        }).size(250f, 60f);
        table.row();
        table.button(bundle.get("settings.advancedSettingsButton.label"), () -> {
            advancedSettingsDialog.show();
        }).size(250f, 60f).padTop(5f);
    }
}