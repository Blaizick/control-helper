package controlhelper.ui.settings;

import static arc.Core.bundle;

import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable;

public class AdvancedSettingsDialog extends BaseDialog
{
    public AdvancedSettingsDialog() 
    {
        super(bundle.get("settings.advancedSettings.title"));
    }
    
    public SettingsTable table;

    public void Init()
    {
        closeOnBack();
        addCloseButton();
        
        table = cont.add(new SettingsTable()).get();

        table.sliderPref("splitAdd1.size", 0, 0, 100, i -> String.valueOf(i) + "%");
        table.sliderPref("splitAdd2.size", 0, 0, 100, i -> String.valueOf(i) + "%");
        table.sliderPref("splitAdd3.size", 0, 0, 100, i -> String.valueOf(i) + "%");
    }
}
