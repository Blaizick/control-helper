package controlhelper.core;

import static arc.Core.bundle;
import static arc.Core.graphics;
import static arc.Core.input;
import static arc.Core.settings;
import static controlhelper.ControlHelper.requestExecutor;

import java.util.LinkedList;

import arc.Events;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import controlhelper.Utils.GeneralUtils;
import controlhelper.core.requestexecutor.IUnitsRequest;
import controlhelper.inputs.Keybind;
import mindustry.Vars;
import mindustry.game.EventType.*;
import mindustry.gen.Unit;

public class AdvancedAttacker 
{
    protected float curExecuteDelay = 0; 

    public Vec2 target = null;
    public Seq<Unit> unitsArrived = new Seq<>();

    protected boolean spamming = false;
    public float targetUnitsAtPoint = 0.8f;

    public void Init()
    {
        Events.run(Trigger.update, () ->
        {
            if (Keybind.advancedAttack.KeyTap())
            {
                Attack();
            }
            if (Keybind.attack.KeyTap())
            {
                OnBasicAttackTap();
            }

            if (curExecuteDelay > 0)
            {
                curExecuteDelay -= graphics.getDeltaTime();
            }
            else
            {
                ExecuteNextRequest();
                curExecuteDelay = GetUnitsAttackDelay();
            }

            if (target != null && attackRequests.size() == 0 && unitsArrived.size > 0 && !spamming)
            {
                spamming = true;
            }
        });

        Events.on(UnitControlEvent.class, e -> 
        {
            attackRequests.removeIf(i -> GeneralUtils.GetUnitByIds(i.unitIds).contains(e.unit));
            unitsArrived.remove(e.unit);

            if (attackRequests.size() == 0 && unitsArrived.size == 0)
            {
                target = null;
            }
        });
    }

    
    public void SpamRequest()
    {
        if (unitsArrived.size == 0) return;

        requestExecutor.AddPriorityRequest(new IUnitsRequest.MoveRequest(GeneralUtils.GetUnitIds(unitsArrived), null, null, target, () -> 
        {
            SpamRequest();
        }));
    }



    private float GetUnitsAttackDelay()
    {
        return (float)settings.getInt(bundle.get("settings.unitsAttackDelay.name"), 50) / 1000f;
    }


    public void OnBasicAttackTap()
    {
        if (!Vars.control.input.commandMode || Vars.control.input.selectedUnits.isEmpty()) return;

        for (Unit unit : Vars.control.input.selectedUnits) 
        {
            attackRequests.removeIf(i -> GeneralUtils.GetUnitByIds(i.unitIds).contains(unit));
            unitsArrived.remove(unit);
        }

        if (attackRequests.size() == 0 && unitsArrived.size == 0)
        {
            target = null;
        }
    }

    public void Attack()
    {
        if (!Vars.control.input.commandMode || Vars.control.input.selectedUnits.isEmpty()) return;

        attackRequests.clear();
        target = input.mouseWorld().cpy();

        for (int i = 0; i < Vars.control.input.selectedUnits.size; i++) 
        {
            int id = Vars.control.input.selectedUnits.get(i).id;
            int[] ids = new int[] { id };
            attackRequests.add(new IUnitsRequest.MoveRequest(ids, null, null, target));
        }
    }


    public LinkedList<IUnitsRequest.MoveRequest> attackRequests = new LinkedList<>();

    public void ExecuteNextRequest()
    {
        if (attackRequests.isEmpty()) return;
        IUnitsRequest request = attackRequests.pop();
        if (request == null) return;
        requestExecutor.AddPriorityRequest(request);
    }
}
