package controlhelper.ui.elements;

import static arc.Core.bundle;
import static arc.Core.graphics;
import static arc.Core.scene;
import static arc.Core.settings;

import arc.input.KeyCode;
import arc.math.geom.Vec2;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import arc.scene.event.Touchable;
import arc.scene.style.Drawable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.TextButton.TextButtonStyle;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Align;
import mindustry.gen.Tex;
import mindustry.ui.Styles;


public class CHWindow extends Table  
{
    protected Table titleBarMin = new Table();
    protected Table titleBarMax = new Table(), titlePane = new Table(), cont = new Table();

    public boolean minimized = true;
    public boolean dragging = false;

    public float buttonSize = 32f;
    public TextButtonStyle buttonStyle = Styles.nonet;

    public Drawable titleBarBg, titlePaneBg, contBg;

    public static Seq<CHWindow> windows = new Seq<>();
    private CHWindow thisW = this;

    public CHWindow(String name)
    {
        this.name = name;

        var whiteui = (TextureRegionDrawable)Tex.whiteui;
        titleBarBg = whiteui.tint(0.1f, 0.1f, 1f, 0.3f);
        titlePaneBg = Styles.black6;
        contBg = Styles.black3;
    }
    
    public void Init()
    {
        InitTitleBarMin();
        InitTitleBarMax();
        InitTitlePane();
        InitCont();

        Register();

        RefreshBackgrounds();
        MinimizationRefresh();

        Load();
        FitIntoScreenBorders();
        Snap();
    }

    public void InitTitleBarMin()
    {
        titleBarMin.add(bundle.get("windows." + name + ".name"));
        titleBarMin.button("+", buttonStyle, () -> 
        {
            if (dragging) return;
            minimized = false;
            MinimizationRefresh();
            FitIntoScreenBorders();
            Snap();
            Save();
        }).size(buttonSize).align(Align.right);

        titleBarMin.addListener(new TitleBarInputListener());
        titleBarMin.touchable = Touchable.enabled;
    }

    public void InitTitleBarMax()
    {
        titleBarMax.add(titlePane);

        titleBarMax.add("");
        titleBarMax.button("-", buttonStyle, () -> 
        {
            if (dragging) return;
            minimized = true;
            MinimizationRefresh();
            FitIntoScreenBorders();
            Snap();
            Save();
        }).size(buttonSize).get().setPosition(0, 0);;

        titleBarMax.addListener(new TitleBarInputListener());
        titleBarMax.touchable = Touchable.enabled;
    }

    public void InitTitlePane()
    {
        titlePane.touchable = Touchable.enabled;
        titlePane.align(Align.left);
        titlePane.visible(() -> titlePane.hasChildren());
    }

    public void InitCont()
    {
        cont.touchable = Touchable.enabled;
        cont.visible(() -> cont.hasChildren());
    }

    public void MinimizationRefresh()
    {
        clear();

        if (minimized)
        {
            add(titleBarMin).growX();
        }
        else
        {
            add(titleBarMax).growX();
            row();
            add(cont).growX();
        }

        width = getPrefWidth();
        height = getPrefHeight();
    }

    public void RefreshBackgrounds()
    {
        titleBarMin.setBackground(titleBarBg);
        titleBarMax.setBackground(titleBarBg);
        titlePane.setBackground(titlePaneBg);
        cont.setBackground(contBg);
    }

    public void FitIntoScreenBorders()
    {
        /*float prefWidth = getPrefWidth();
        float prefHeight = getPrefHeight();

        if (x - (prefWidth * scaleX / 2) < 0)
        {
            x = 0 + (prefWidth * scaleX / 2);
        }
        else if (x + (prefWidth * scaleX / 2)> graphics.getWidth())
        {
            x = graphics.getWidth() - (prefWidth * scaleX / 2);
        }*/

        if (x < 0)
        {
            x = 0;
        }
        else if (x + (width * scaleX) > graphics.getWidth())
        {
            x = graphics.getWidth() - (width * scaleX);
        }

        if (y < 0)
        {
            y = 0;
        }
        else if (y + (height * scaleY) > graphics.getHeight())
        {
            y = graphics.getHeight() - (height * scaleY);
        }
    }


    public float snapDst = 30f;

    public void Snap()
    {
        SnapToEdges();
        SnapToWindows();
    }

    public void SnapToEdges()
    {
        if (x < snapDst)
        {
            x = 0;
        }
        else if (x + (width * scaleX) > graphics.getWidth() - snapDst)
        {
            x = graphics.getWidth()- (width * scaleX);
        }

        if (y < snapDst)
        {
            y = 0;
        }
        else if (y + (height * scaleY) > graphics.getHeight() - snapDst)
        {
            y = graphics.getHeight() - (height * scaleY);
        }
    }

    public void SnapToWindows()
    {
        /*
        for (CHWindow window : windows) 
        {
            float wx = window.getX(window.getAlign());
            float wy = window.getY(window.getAlign());
            float wWidth = window.getWidth();
            float wHeight = window.getHeight();
            float wScaleX = window.scaleX;
            float wScaleY = window.scaleY;

            if (Math.abs((x + (width * scaleX)) - (wx)) < snapDst)
            {
                x = wx - (width * scaleX);
            }
        }
        */
    }


    public void Save()
    {
        if (name == null || name.isEmpty()) return;

        String prefix = GetPrefix();
        settings.put(prefix + "minimized", minimized);
        settings.put(prefix + "x", x);
        settings.put(prefix + "y", y);
        settings.put(prefix + "scale", (float)scaleX * 100);
    }

    public void Load()
    {
        if (name == null || name.isEmpty()) return;

        String prefix = GetPrefix();
        minimized = settings.getBool(prefix + "minimized", true);
        x = settings.getFloat(prefix + "x", 0);
        y = settings.getFloat(prefix + "y", 0);
        setScale(settings.getFloat(prefix + "scale", 100) / 100);
    }

    public String GetPrefix()
    {
        return "control-helper-window-" + name + "-";
    }


    protected void Register()
    {
        if (name == null || name.isEmpty() || windows.contains(w -> w.name == name)) return;
        windows.add(this);
    }

    public void Build()
    {
        scene.add(this);
    }


    private class TitleBarInputListener extends InputListener
    {
        private Vec2 from = new Vec2();

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode button) 
        {
            from.set(x, y);

            return true;
        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, KeyCode button) 
        {
            if (dragging)
            {
                toFront();
                dragging = false;
            }

        }

        @Override
        public void touchDragged(InputEvent event, float x, float y, int pointer) 
        {
            Vec2 touch = new Vec2(x, y);
            Vec2 pos = localToStageCoordinates(touch.sub(from));

            thisW.setPosition(pos.x, pos.y);
            FitIntoScreenBorders();
            Snap();
            Save();

            dragging = true;
        }
    }
}