package controlhelper.core.inputs;

import static arc.Core.bundle;

import arc.input.KeyCode;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import arc.struct.Seq;
import arc.util.Align;
import controlhelper.ControlHelper;
import mindustry.graphics.Pal;
import mindustry.ui.dialogs.BaseDialog;

public class RebindOverlay extends BaseDialog {
    public RebindOverlay() {
        super("");
    }

    protected CHKeybind keybind;
    public Seq<KeyCode> ignoreKeys = new Seq<>(new KeyCode[] {
            KeyCode.mouseLeft,
            KeyCode.mouseRight
    });
    public Seq<KeyCode> backKeys = new Seq<>(new KeyCode[] {
            KeyCode.back,
            KeyCode.escape
    });

    public void Init() {
        bottom().left().clearChildren();
        closeOnBack();

        add(bundle.get("settings.keybinds.rebind.label")).color(Pal.accent).size(256f, 48f).get().setPosition(0, 0,
                Align.center);
        hide();

        addListener(new InputListener() {
            private void Rebind(KeyCode key) {
                if (keybind == null)
                    return;
                keybind.Rebind(key);
                keybind.Save();
            }

            private void Back() {
                hide();
                ControlHelper.controlsDialog.Refresh();
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode key) {
                if (keybind == null)
                    return false;
                if (ignoreKeys.contains(key))
                    return false;
                if (backKeys.contains(key)) {
                    Back();
                    return false;
                }

                if (keybind.FindDuplicate(key) != null)
                    return false;
                Rebind(key);
                Back();
                return false;
            }

            @Override
            public boolean keyDown(InputEvent event, KeyCode key) {
                if (keybind == null)
                    return false;
                if (ignoreKeys.contains(key))
                    return false;
                if (backKeys.contains(key)) {
                    Back();
                    return false;
                }

                if (keybind.FindDuplicate(key) != null)
                    return false;
                Rebind(key);
                Back();
                return false;
            }
        });
    }

    public void Show(CHKeybind keybind) {
        this.keybind = keybind;
        show();
    }
}
