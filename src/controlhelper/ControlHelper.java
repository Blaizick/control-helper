package controlhelper;

import controlhelper.core.DrillsValidator;
import controlhelper.core.ExtinguishedRebuilder;
import controlhelper.core.HandMiner;
import controlhelper.core.PlansPrioritizer;
import controlhelper.core.PlansSaver;
import controlhelper.core.SupportsIgnorer;
//import controlhelper.core.UnitMiner;
import controlhelper.core.UnitSplitter;
import controlhelper.core.buildingsdepowerer.FactoriesDepowerer;
import controlhelper.core.buildingsdepowerer.ProducersDepowerer;
import controlhelper.core.requestexecutor.RequestExecutor;
import controlhelper.inputs.Keybind;
import controlhelper.inputs.RebindOverlay;
import controlhelper.ui.settings.AdvancedSettingsDialog;
import controlhelper.ui.settings.ControlsDialog;
import controlhelper.ui.settings.SettingsDialog;
import controlhelper.ui.windows.ControlHelperWindow;
//import controlhelper.ui.UnitMinerWindow;
import mindustry.mod.*;

public class ControlHelper extends Mod
{
    public static RebindOverlay rebindOverlay;
    public static ControlsDialog controlsDialog;
    public static AdvancedSettingsDialog advancedSettingsDialog;
    public static SettingsDialog settingsDialog;

    public static RequestExecutor requestExecutor;

    public static UnitSplitter unitSplitter;
    public static DrillsValidator drillsValidator;
    public static HandMiner handMiner;
    public static PlansSaver plansSaver;
    public static SupportsIgnorer supportsIgnorer;
    public static FactoriesDepowerer factoriesDepowerer;
    public static ProducersDepowerer producersDepowerer;
    public static PlansPrioritizer plansPrioritizer;
    public static ExtinguishedRebuilder extinguishedRebuilder;

    public static ControlHelperWindow controlHelperWindow;

    @Override
    public void init()
    {
        rebindOverlay = new RebindOverlay();
        controlsDialog = new ControlsDialog();
        advancedSettingsDialog = new AdvancedSettingsDialog();
        settingsDialog = new SettingsDialog();
        
        requestExecutor = new RequestExecutor();

        unitSplitter = new UnitSplitter();
        drillsValidator = new DrillsValidator();
        plansSaver = new PlansSaver();
        handMiner = new HandMiner();
        supportsIgnorer = new SupportsIgnorer();
        factoriesDepowerer = new FactoriesDepowerer();
        producersDepowerer = new ProducersDepowerer();
        plansPrioritizer = new PlansPrioritizer();
        extinguishedRebuilder = new ExtinguishedRebuilder();

        controlHelperWindow = new ControlHelperWindow();


        Keybind.Init();

        rebindOverlay.Init();
        controlsDialog.Init();
        advancedSettingsDialog.Init();
        settingsDialog.Init();
        
        requestExecutor.Init();

        unitSplitter.Init();
        drillsValidator.Init();
        plansSaver.Init();
        handMiner.Init();
        supportsIgnorer.Init();
        plansPrioritizer.Init();
        extinguishedRebuilder.Init();

        controlHelperWindow.Init();
        controlHelperWindow.Build();
    }
}