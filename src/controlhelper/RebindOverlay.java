package controlhelper;

import arc.input.KeyCode;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import arc.util.Align;
import mindustry.graphics.Pal;
import mindustry.ui.dialogs.BaseDialog;

public class RebindOverlay extends BaseDialog
{
    public RebindOverlay()
    {
        super("");
    }

    public void Init()
    {
        bottom().left().clearChildren();
        closeOnBack();

        add("Press any key").color(Pal.accent).size(256f, 48f).get().setPosition(0, 0, Align.center);
        hide();
    }

    public void Show(Keybind keybind)
    {
        addListener(new InputListener()
        {
            private void Rebind(KeyCode key)
            {
                keybind.Rebind(key);
                keybind.Save();
            }

            private void Back()
            {
                hide();
                ControlHelper.settingsTable.Refresh();
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode key)
            {
                if (key == KeyCode.escape || key == KeyCode.back)
                {
                    Back();
                    return false;
                }

                Rebind(key);
                Back();
                return false;
            }

            @Override
            public boolean keyDown(InputEvent event, KeyCode key)
            {
                if (key == KeyCode.escape || key == KeyCode.back)
                {
                    Back();
                    return false;
                }

                Rebind(key);
                Back();
                return false;
            }
        });

        show();
    }
}
