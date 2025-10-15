package controlhelper.ui.windows;

import static arc.Core.settings;
import static controlhelper.ControlHelper.distributionAlternator;
import static controlhelper.ControlHelper.factoriesDepowerer;
import static controlhelper.ControlHelper.handMiner;
import static controlhelper.ControlHelper.plansSkipper;
import static controlhelper.ControlHelper.producersDepowerer;

import controlhelper.modules.DistributionAlternator;
import controlhelper.modules.HandMiner;
import controlhelper.ui.elements.CHIconCheckBox;
import controlhelper.ui.elements.CHIconIndicator;
import controlhelper.ui.elements.CHRemoteIconCheckBox;
import controlhelper.ui.elements.CHWindow;
import mindustry.Vars;
import mindustry.gen.Iconc;
import mindustry.ui.Styles;

public class ControlHelperWindow extends CHWindow {
    public ControlHelperWindow() {
        super("controlHelper");
    }

    @Override
    public void Init() {
        visible(() -> Vars.state.isGame() && Vars.ui.hudfrag.shown && IsEnabled());
        if (settings.getBool("showControlHelperWindow"))
            ;
        super.Init();
    }

    @Override
    public void InitCont() {
        cont.add(new CHIconCheckBox(Iconc.unitCorvus + "", c -> {
            if (c)
                factoriesDepowerer.DepowerBuilds();
            else
                factoriesDepowerer.PowerBuilds();
        }).Init()).size(buttonSize);

        cont.add(new CHIconCheckBox(Iconc.blockSurgeSmelter + "", c -> {
            if (c)
                producersDepowerer.DepowerBuilds();
            else
                producersDepowerer.PowerBuilds();
        }).Init()).size(buttonSize);

        cont.add(new CHRemoteIconCheckBox(Iconc.blockInvertedSorter + "", c -> distributionAlternator.SetEnabled(c),
                distributionAlternator::IsEnabled).Init()).size(buttonSize);

        cont.add(new CHRemoteIconCheckBox(Iconc.copy + "", c -> {
            plansSkipper.SetEnabled(c);
        }, plansSkipper::IsEnabled).Init()).size(buttonSize);

        cont.add(new CHIconIndicator(Iconc.blockMechanicalDrill + "", () -> handMiner.active).Init()).size(buttonSize);

        // cont.add(new CHIconCheckPref("alternateDistribution",
        // Iconc.blockInvertedSorter + "", c -> {
        // distributionAlternator.enabled = c;
        // }).Init()).size(buttonSize);

        super.InitCont();

    }

    public boolean IsEnabled() {
        return settings.getBool("showControlHelperWindow");
    }
}