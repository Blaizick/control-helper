package controlhelper;

import mindustry.mod.*;

public class ControlHelper extends Mod
{
    public static ControlHelper instance;
    
    public static RebindOverlay rebindOverlay;
    public static SettingsTable settingsTable;
    
    public static UnitSplitter unitSplitter;


    @Override
    public void init()
    {
        if (instance != this)
        {
            instance = this;
        }

        rebindOverlay = new RebindOverlay();
        settingsTable = new SettingsTable();
        unitSplitter = new UnitSplitter();

        Keybind.Init();

        rebindOverlay.Init();
        settingsTable.Init();
        
        unitSplitter.Init();
    }
}
