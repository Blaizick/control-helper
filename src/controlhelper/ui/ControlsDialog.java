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

        for (Keybind key : Keybind.all)
        {
            if (!key.shown) continue;

            cont.add(bundle.get("keybind." + key.name()));

            cont.add(key.key.name()).padLeft(80f);

            cont.button(bundle.get("settings.keybinds.rebind"), () ->
            {
                ControlHelper.rebindOverlay.Show(key);
            }).size(120f, 50f).padLeft(30f);

            cont.button(bundle.get("settings.keybinds.reset"), () ->
            {
                key.Reset();
                key.Save();
                Refresh();
            }).size(120f, 50f).padLeft(30f);

            cont.row();
        }
    }
}
