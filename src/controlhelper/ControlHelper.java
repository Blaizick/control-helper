package controlhelper;

import controlhelper.core.AdvancedAttacker;
import controlhelper.core.DrillsValidator;
import controlhelper.core.HandMiner;
import controlhelper.core.PlansSaver;
import controlhelper.core.UnitMiner;
import controlhelper.core.UnitSplitter;
import controlhelper.inputs.Keybind;
import controlhelper.inputs.RebindOverlay;
import controlhelper.ui.ControlsDialog;
import controlhelper.ui.SettingsDialog;
import controlhelper.ui.UnitMinerWindow;
import mindustry.mod.*;

public class ControlHelper extends Mod
{
    public static ControlHelper instance;
    
    public static RebindOverlay rebindOverlay;
    public static ControlsDialog controlsDialog;
    public static SettingsDialog settingsTable;
    
    public static UnitSplitter unitSplitter;
    public static AdvancedAttacker advancedAttacker;
    public static UnitMiner unitMiner;
    public static DrillsValidator drillsValidator;
    public static HandMiner handMiner;
    public static PlansSaver plansSaver;

    public static UnitMinerWindow unitMinerWindow;

    @Override
    public void init()
    {
        if (instance != this)
        {
            instance = this;
        }

        
        rebindOverlay = new RebindOverlay();
        controlsDialog = new ControlsDialog();
        settingsTable = new SettingsDialog();
        
        unitSplitter = new UnitSplitter();
        advancedAttacker = new AdvancedAttacker();
//        unitMiner = new UnitMiner();
        drillsValidator = new DrillsValidator();
        plansSaver = new PlansSaver();
        handMiner = new HandMiner();

//        unitMinerWindow = new UnitMinerWindow();


        Keybind.Init();

        rebindOverlay.Init();
        controlsDialog.Init();
        settingsTable.Init();
        
        unitSplitter.Init();
        advancedAttacker.Init();
//        unitMiner.Init();
        drillsValidator.Init();
        plansSaver.Init();
        handMiner.Init();

//        unitMinerWindow.Init();
//        unitMinerWindow.Build();
    }
}
