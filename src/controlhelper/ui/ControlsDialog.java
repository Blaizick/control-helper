package controlhelper.ui;

import static arc.Core.bundle;

import controlhelper.ControlHelper;
import controlhelper.inputs.Keybind;
import mindustry.ui.dialogs.BaseDialog;

public class ControlsDialog extends BaseDialog
{
    public ControlsDialog ()
    {
        super("Controls");
    }

    public void Init()
    {
        addCloseButton();
        Refresh();
    }

    public void Refresh()
    {
        cont.clear();
        cont.row();

        for (Keybind keybind : Keybind.all)
        {
            if (!keybind.shown) continue;

            cont.add(bundle.get("keybind." + keybind.name()));

            cont.add(keybind.key.name()).padLeft(80f);

            cont.button(bundle.get("settings.keybinds.rebind"), () ->
            {
                ControlHelper.rebindOverlay.Show(keybind);
            }).size(120f, 50f).padLeft(30f);

            cont.button(bundle.get("settings.keybinds.reset"), () ->
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
            }).size(120f, 50f).padLeft(30f);

            cont.row();
        }
    }
}
