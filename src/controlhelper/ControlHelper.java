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
    //public static UnitMiner unitMiner;
    public static DrillsValidator drillsValidator;
    public static HandMiner handMiner;
    public static PlansSaver plansSaver;
    public static SupportsIgnorer supportsIgnorer;
    public static FactoriesDepowerer factoriesDepowerer;
    public static ProducersDepowerer producersDepowerer;
    public static PlansPrioritizer plansPrioritizer;
    public static ExtinguishedRebuilder extinguishedRebuilder;

    //public static UnitMinerWindow unitMinerWindow;
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
        //unitMiner = new UnitMiner();
        drillsValidator = new DrillsValidator();
        plansSaver = new PlansSaver();
        handMiner = new HandMiner();
        supportsIgnorer = new SupportsIgnorer();
        factoriesDepowerer = new FactoriesDepowerer();
        producersDepowerer = new ProducersDepowerer();
        plansPrioritizer = new PlansPrioritizer();
        extinguishedRebuilder = new ExtinguishedRebuilder();

        //unitMinerWindow = new UnitMinerWindow();
        controlHelperWindow = new ControlHelperWindow();


        Keybind.Init();

        rebindOverlay.Init();
        controlsDialog.Init();
        advancedSettingsDialog.Init();
        settingsDialog.Init();
        
        requestExecutor.Init();

        unitSplitter.Init();
        //unitMiner.Init();
        drillsValidator.Init();
        plansSaver.Init();
        handMiner.Init();
        supportsIgnorer.Init();
        plansPrioritizer.Init();
        extinguishedRebuilder.Init();

        //unitMinerWindow.Init();
        //unitMinerWindow.Build();
        controlHelperWindow.Init();
        controlHelperWindow.Build();
    }


    //* Улучшить автовин
    //* Выделение юнитов без саппортов
    //* Отключение заводов и сильно потребляющих производст
    //* Несколько хоткеев для разного кол-ва выделенных юнитов
    //* Сделать, чтобы можно было сбросить хоткей
    //* Приоритетная установка турелей и тушилок
    //* Пофиксить фигню, что конвееры строятся первыми при включенном removeExcessDrills
    //* Обновить RebindOverlay
    //* Соединение разьеденённой энергосети после отключение заводов юнитов
    
    //* Проверка на то идёт ли вода в блок тушилки
    //* todo Удаление блоков в огне и потом их перестройка
    //todo Отключение всех заводов, чьих производимых ресурсов больше 1500
}