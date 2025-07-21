package controlhelper;

import controlhelper.core.events.*;
import controlhelper.core.requestexecutor.*;
import controlhelper.inputs.*;
import controlhelper.modules.*;
import controlhelper.modules.buildingsdepowerer.*;
import controlhelper.ui.settings.*;
import controlhelper.ui.windows.*;
import mindustry.mod.*;

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

    public static ControlHelperWindow controlHelperWindow;


    @Override
    public void init()
    {
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

        controlHelperWindow = new ControlHelperWindow();


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

        controlHelperWindow.Init();
        controlHelperWindow.Build();
    }


    //* чередовка сортеров с конвами, роутерами и перекёстками
    //todo сохранение схем после перезахода
    //todo перевести на русский
    //? закрепление позиции камеры и возвращение к ней при нажатии клавиши
}