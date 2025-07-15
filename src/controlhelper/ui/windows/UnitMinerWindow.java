package controlhelper.ui.windows;
/*package controlhelper.ui;

import controlhelper.ui.elements.CheckBoxMod;
import controlhelper.ui.elements.WindowMod;
import mindustry.Vars;
import mindustry.content.UnitTypes;
import mindustry.gen.Iconc;

import static controlhelper.ControlHelper.*;

import arc.struct.Seq;

public class UnitMinerWindow extends WindowMod
{
    public UnitMinerWindow() 
    {
        super("unitMiner");
    }

    @Override
    public void Init()
    {
        super.Init();

        visible(() -> Vars.state.isGame() && Vars.ui.hudfrag.shown);
    }

    
    protected Seq<CheckBoxMod> umCheckBoxes = new Seq<>();


    @Override
    public void InitCont() 
    {
         
        super.InitCont();
        umCheckBoxes.add(cont.add(new CheckBoxMod("polys-mine", "" + Iconc.unitPoly, false, checked ->
        {
            if (checked) unitMiner.AddUnitType(UnitTypes.poly);
            else unitMiner.RemoveUnitType(UnitTypes.poly);
            unitMiner.RefreshMining();
        })).size(buttonSize).get());
        umCheckBoxes.add(cont.add(new CheckBoxMod("pulsars-mine", "" + Iconc.unitPulsar, false, checked ->
        {
            if (checked) unitMiner.AddUnitType(UnitTypes.pulsar);
            else unitMiner.RemoveUnitType(UnitTypes.pulsar);
            unitMiner.RefreshMining();
        })).size(buttonSize).get());
        umCheckBoxes.add(cont.add(new CheckBoxMod("megas-mine", "" + Iconc.unitMega, false, checked ->
        {
            if (checked) unitMiner.AddUnitType(UnitTypes.mega);
            else unitMiner.RemoveUnitType(UnitTypes.mega);
            unitMiner.RefreshMining();
        })).size(buttonSize).get());
        umCheckBoxes.add(cont.add(new CheckBoxMod("quasars-mine", "" + Iconc.unitQuasar, false, checked ->
        {
            if (checked) unitMiner.AddUnitType(UnitTypes.quasar);
            else unitMiner.RemoveUnitType(UnitTypes.quasar);
            unitMiner.RefreshMining();
        })).size(buttonSize).get());
        
    }

    public void UncheckUMCheckBoxes()
    {
        for (CheckBoxMod checkBox : umCheckBoxes) 
        {
            checkBox.SetChecked(false);
        }
    }
}
*/