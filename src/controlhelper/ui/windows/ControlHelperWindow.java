package controlhelper.ui.windows;

import static arc.Core.settings;
import static controlhelper.ControlHelper.distributionAlternator;
import static controlhelper.ControlHelper.factoriesDepowerer;
//import static controlhelper.ControlHelper.mapSchemeSelector;
import static controlhelper.ControlHelper.producersDepowerer;

import controlhelper.ui.elements.CHIconCheckBox;
import controlhelper.ui.elements.CHIconCheckPref;
import controlhelper.ui.elements.CHWindow;
import mindustry.Vars;
import mindustry.gen.Iconc;

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

        cont.add(new CHIconCheckPref("alternateDistribution", Iconc.blockInvertedSorter + "", c -> {
            distributionAlternator.enabled = c;
        }).Init()).size(buttonSize);

        super.InitCont();
    }

    public boolean IsEnabled() {
        return settings.getBool("showControlHelperWindow");
    }
}