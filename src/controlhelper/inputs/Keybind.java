package controlhelper.inputs;

import static arc.Core.input;
import static arc.Core.settings;

import arc.input.KeyCode;

public enum Keybind
{
    //region bindings

    split(KeyCode.j),
    attack(KeyCode.mouseRight, false),
    rebuildExtinguished(KeyCode.o),
    splitAdd1(KeyCode.unset),
    splitAdd2(KeyCode.unset),
    splitAdd3(KeyCode.unset);

    //endregion


    public static final Keybind[] all = values();

    public final KeyCode defaultKey;
    public KeyCode key, additionalKey;

    public final boolean shown;

    private Keybind(KeyCode defaultKey, boolean shown)
    {
        this.defaultKey = defaultKey;
        this.shown = shown;
    }

    private Keybind(KeyCode defaultKey)
    {
        this.defaultKey = defaultKey;
        this.shown = true;
    }


    public boolean KeyDown()
    {
        return input.keyDown(key);
    }

    public boolean KeyTap()
    {
        return input.keyTap(key);
    }

    public boolean KeyUp()
    {
        return input.keyRelease(key);
    }


    public void Unset()
    {
        key = KeyCode.unset;
    }

    public void Reset()
    {
        key = defaultKey;
    }

    public void Rebind(KeyCode key)
    {
        this.key = key;
    }


    public void Save()
    {
        settings.put("control-helper-keybind-" + this + "-key", key.ordinal());
    }

    public void Load()
    {
        key = KeyCode.all[settings.getInt("control-helper-keybind-" + this + "-key", defaultKey.ordinal())];
    }


    public Keybind FindDuplicate(KeyCode keyCode)
    {
        for (Keybind keybind : all) 
        {
            if (keybind == this) continue;
            if (keybind.key == keyCode) return keybind;
        }

        return null;
    }


    public static void Init()
    {
        for (Keybind keybind : all)
        {
            keybind.Load();
        }
    }
}