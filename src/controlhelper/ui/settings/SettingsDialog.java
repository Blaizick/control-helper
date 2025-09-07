package controlhelper.ui.settings;

import static arc.Core.bundle;
import static arc.Core.settings;
import static controlhelper.ControlHelper.advancedSettingsDialog;
import static controlhelper.ControlHelper.controlsDialog;

import arc.func.Boolc;
import arc.struct.Seq;
import arc.util.Reflect;
import controlhelper.ui.elements.TooltipCheckSetting;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable.CheckSetting;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable.Setting;

public class SettingsDialog extends BaseDialog {
    public SettingsTable table;

    public SettingsDialog() {
        super(bundle.get("settings.title"));
    }

    public void Init() {
        Vars.ui.settings.addCategory("@settings.category", Icon.commandAttack, table -> {
            this.table = table;
        });

        // table.checkPref("drillsValidator", true);
        // table.checkPref("plansSaver", true);
        // table.checkPref("handMiner", true);
        // table.checkPref("ignoreSupportUnits", true);
        // table.checkPref("showControlHelperWindow", false);
        // table.checkPref("prioritizePlans", true);
        // table.checkPref("nodesBreaker", true);
        // table.checkPref("plansSkipper", false);
        // table.checkPref("buildingsOverdrawer", true);

        TooltipCheckPref("drillsValidator", true);
        TooltipCheckPref("plansSaver", true);
        TooltipCheckPref("handMiner", true);
        TooltipCheckPref("ignoreSupportUnits", true);
        TooltipCheckPref("showControlHelperWindow", false);
        TooltipCheckPref("nodesBreaker", true);
        TooltipCheckPref("plansSkipper", false);
        TooltipCheckPref("buildingsOverdrawer", true);

        // TooltipCheckPref("drillsValidator", "sin shalavi", false, null);

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

    public void TooltipCheckPref(String name, boolean def) {
        TooltipCheckPref(name, def, null);
    }

    public void TooltipCheckPref(String name, boolean def, Boolc changed) {
        var list = (Seq<Setting>) Reflect.get(table, "list");
        list.add(new TooltipCheckSetting(name, bundle.get("setting." + name + ".tooltip"), def, changed));
        settings.defaults(name, def);
        table.rebuild();
    }
}