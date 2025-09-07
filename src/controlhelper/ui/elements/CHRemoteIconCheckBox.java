package controlhelper.ui.elements;

import static controlhelper.ControlHelper.distributionAlternator;

import java.util.function.Predicate;

import arc.func.Boolc;
import arc.func.Boolf;
import arc.func.Boolp;
import arc.func.Cons;
import arc.util.Align;
import arc.util.Log;
import mindustry.ui.Styles;

public class CHRemoteIconCheckBox extends CHIconCheckBox {
    public Boolc setCheckedRemote = null;
    public Boolp getCheckedRemote = null;

    public CHRemoteIconCheckBox(String icon, Boolc setCheckedRemote, Boolp getCheckedRemote) {
        super(icon);
        this.setCheckedRemote = setCheckedRemote;
        this.getCheckedRemote = getCheckedRemote;
    }

    @Override
    public CHIconCheckBox Init() {
        checked = defaultChecked;
        if (callbackOnInit)
            onCheck.each(i -> i.get(checked));
        setStyle(Styles.flatTogglet);
        add(icon).align(Align.center);
        clicked(() -> {
            onCheck.each(i -> i.get(checked));
            setCheckedRemote.get(!getCheckedRemote.get());
        });
        update(() -> {
            setChecked(getCheckedRemote.get());
        });
        return this;
    }
}
