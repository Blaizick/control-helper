package controlhelper.ui.settings;

import static arc.Core.bundle;
import static controlhelper.ControlHelper.rebindOverlay;

import controlhelper.inputs.Keybind;
import mindustry.gen.Iconc;
import mindustry.ui.dialogs.BaseDialog;

public class ControlsDialog extends BaseDialog
{
    public float buttonWidth = 120f, buttonHeight = 50f;
    public float buttonPad = 5f;

    public ControlsDialog ()
    {
        super(bundle.get("settings.controls.title"));
    }

    public void Init()
    {
        addCloseButton();
        closeOnBack();
        
        Refresh();
    }

    public void Refresh()
    {
        cont.clear();
        cont.row();

        for (Keybind keybind : Keybind.all)
        {
            if (!keybind.shown) continue;

            cont.add(bundle.get("keybind." + keybind.name() + ".name"));

            cont.add(keybind.key.name()).left().padLeft(80f);

            cont.button(Iconc.trash + "", () ->
            {
                keybind.Unset();
                keybind.Save();
                Refresh();
            }).size(50f).padLeft(30f);

            cont.button(bundle.get("settings.keybinds.rebindButton.label"), () ->
            {
                rebindOverlay.Show(keybind);
            }).size(buttonWidth, buttonHeight).padLeft(buttonPad);

            cont.button(bundle.get("settings.keybinds.resetButton.label"), () ->
            {
                Keybind duplicate = keybind.FindDuplicate(keybind.defaultKey);
                if (duplicate != null)
                {
                    duplicate.Reset();
                    duplicate.Save();
                }
                keybind.Reset();
                keybind.Save();
                Refresh();
            }).size(buttonWidth, buttonHeight).padLeft(buttonPad);

            cont.row();
        }
    }
}
