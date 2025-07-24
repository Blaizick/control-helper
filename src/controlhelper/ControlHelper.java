package controlhelper;

import arc.files.Fi;
import controlhelper.core.events.*;
import controlhelper.core.requestexecutor.*;
import controlhelper.inputs.*;
import controlhelper.modules.*;
import controlhelper.modules.buildingsdepowerer.*;
//import controlhelper.modules.mapscheme.MapSchemeManager;
//import controlhelper.modules.mapscheme.MapSchemeAppendor;
//import controlhelper.ui.mapscheme.MapSchemeBox;
import controlhelper.ui.settings.*;
import controlhelper.ui.windows.*;
import mindustry.Vars;
import mindustry.mod.*;
import mindustry.mod.Mods.ModMeta;

public class ControlHelper extends Mod
{
    public static RebindOverlay rebindOverlay;
    public static ControlsDialog controlsDialog;
    public static AdvancedSettingsDialog advancedSettingsDialog;
    public static SettingsDialog settingsDialog;

    public static RequestExecutor requestExecutor;
    public static EventsRunner eventsRunner;

    public static UnitSplitter unitSplitter;
    public static DrillsValidator drillsValidator;
    public static HandMiner handMiner;
    public static PlansSaver plansSaver;
    public static SupportsIgnorer supportsIgnorer;
    public static FactoriesDepowerer factoriesDepowerer;
    public static ProducersDepowerer producersDepowerer;
    public static PlansPrioritizer plansPrioritizer;
    public static ExtinguishedRebuilder extinguishedRebuilder;
    public static DistributionAlternator distributionAlternator;
    //public static MapSchemeManager mapSchemeManager;
    //public static MapSchemeAppendor mapSchemeSelector;

    public static ControlHelperWindow controlHelperWindow;

    //public static MapSchemeBox mapSchemeBox;

    
    public static Fi coreDirectory;
    public static ModMeta meta;


    @Override
    public void init()
    {

        meta = Vars.mods.getMod(this.getClass()).meta;
        coreDirectory = Vars.modDirectory.child("control_helper/");

        rebindOverlay = new RebindOverlay();
        controlsDialog = new ControlsDialog();
        advancedSettingsDialog = new AdvancedSettingsDialog();
        settingsDialog = new SettingsDialog();
        
        requestExecutor = new RequestExecutor();
        eventsRunner = new EventsRunner();

        unitSplitter = new UnitSplitter();
        drillsValidator = new DrillsValidator();
        plansSaver = new PlansSaver();
        handMiner = new HandMiner();
        supportsIgnorer = new SupportsIgnorer();
        factoriesDepowerer = new FactoriesDepowerer();
        producersDepowerer = new ProducersDepowerer();
        plansPrioritizer = new PlansPrioritizer();
        extinguishedRebuilder = new ExtinguishedRebuilder();
        distributionAlternator = new DistributionAlternator();
        //mapSchemeManager = new MapSchemeManager();
        //mapSchemeSelector = new MapSchemeAppendor();

        controlHelperWindow = new ControlHelperWindow();

        //mapSchemeBox = new MapSchemeBox();


        Keybind.Init();

        rebindOverlay.Init();
        controlsDialog.Init();
        advancedSettingsDialog.Init();
        settingsDialog.Init();
        
        requestExecutor.Init();
        eventsRunner.Init();

        unitSplitter.Init();
        drillsValidator.Init();
        plansSaver.Init();
        handMiner.Init();
        supportsIgnorer.Init();
        plansPrioritizer.Init();
        extinguishedRebuilder.Init();
        distributionAlternator.Init();
        //mapSchemeManager.Init();
        //mapSchemeSelector.Init();

        //mapSchemeBox.Init();
        //mapSchemeBox.Build();

        controlHelperWindow.Init();
        controlHelperWindow.Build();
    }


    //* чередовка сортеров с конвами, роутерами и перекёстками
    //* перевести на русский
    //todo mapscheme utils
    //* крашрепорт
    //todo mvc паттерны для биндов
    //? сохранение схем после перезахода в мир
    //? закрепление позиции камеры и возвращение к ней при нажатии клавиши
}