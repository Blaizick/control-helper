package controlhelper;

import static arc.Core.input;
import static arc.Core.settings;

import arc.input.KeyCode;

public enum Keybind
{
    //region bindings

    split("Split units", KeyCode.shiftLeft, KeyCode.j),;

    //endregion


    public static final Keybind[] all = values();

    public final String displayName;
    public final KeyCode defaultKey, defaultAdditionalKey;
    public KeyCode key, additionalKey;


    private Keybind(String displayName, KeyCode defaultAdditionalKey, KeyCode defaultKey)
    {
        this.displayName = displayName;
        this.defaultAdditionalKey = defaultAdditionalKey;
        this.defaultKey = defaultKey;
    }

    private Keybind(String displayName, KeyCode defaultKey)
    {
        this.displayName = displayName;
        this.defaultAdditionalKey = KeyCode.unset;
        this.defaultKey = defaultKey;
    }


    public boolean IsSingle()
    {
        return defaultAdditionalKey == null || defaultAdditionalKey == KeyCode.unset;
    }


    public boolean KeyDown()
    {
        if (IsSingle())
        {
            return input.keyDown(key);
        }
        else
        {
            return input.keyDown(additionalKey) && input.keyDown(key);
        }
    }

    public boolean KeyTap()
    {
        if (IsSingle())
        {
            return input.keyTap(key);
        }
        else
        {
            return input.keyDown(additionalKey) && input.keyTap(key);
        }
    }

    public boolean KeyUp()
    {
        if (IsSingle())
        {
            return input.keyRelease(key);
        }
        else
        {
            return !input.keyDown(additionalKey) && input.keyRelease(key);
        }
    }


    public void Reset()
    {
        key = defaultKey;
        additionalKey = defaultAdditionalKey;
    }


    public void Rebind(KeyCode key)
    {
        this.key = key;
    }


    public void Save()
    {
        if (IsSingle())
        {
            settings.put("control-helper-keybind-" + this + "-key", key.ordinal());
        }
        else
        {
            settings.put("control-helper-keybind-" + this + "-additional-key", additionalKey.ordinal());
            settings.put("control-helper-keybind-" + this + "-key", key.ordinal());
        }
    }

    public void Load()
    {
        if (IsSingle())
        {
            key = KeyCode.all[settings.getInt("control-helper-keybind-" + this + "-key", defaultKey.ordinal())];
        }
        else
        {
            additionalKey = KeyCode.all[settings.getInt("control-helper-keybind-" + this + "-additional-key", defaultAdditionalKey.ordinal())];
            key = KeyCode.all[settings.getInt("control-helper-keybind-" + this + "-key", defaultKey.ordinal())];
        }
    }


    public static void Init()
    {
        for (Keybind keybind : all)
        {
            keybind.Load();
        }
    }
}