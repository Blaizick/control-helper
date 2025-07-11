package controlhelper.core;

import static arc.Core.bundle;
import static arc.Core.input;
import static arc.Core.settings;

import java.util.LinkedList;

import arc.Events;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Log;
import controlhelper.inputs.Keybind;
import mindustry.Vars;
import mindustry.game.EventType.*;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;

public class AdvancedAttacker 
{
    private Thread requestsExecutor;

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
        });

        Events.on(DisposeEvent.class, e ->
        {
            requestsExecutor.interrupt();
            requestsExecutor.stop();
            requestsExecutor = null;
        });

        Events.on(UnitControlEvent.class, e -> 
        {
            for (AttackRequest attackRequest : attackRequests) 
            {
                if (attackRequest.GetUnits().contains(e.unit))
                {
                    attackRequests.remove(attackRequest);
                }    
            }

            Log.info(e);
        });

        requestsExecutor = new Thread(() ->
        {
            while (true) 
            {
                try
                {
                    ExecuteNextRequest();
                    Thread.sleep(UnitsAttackDelay());
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
        requestsExecutor.start();
    }

    private int UnitsAttackDelay()
    {
        return settings.getInt(bundle.get("settings.drillsValidator.name"), 50);
    }


    public void OnBasicAttackTap()
    {
        if (!Vars.control.input.commandMode || Vars.control.input.selectedUnits.isEmpty()) return;

        for (Unit unit : Vars.control.input.selectedUnits) 
        {
            attackRequests.removeIf(i -> i.GetUnits().contains(unit));
        }
    }

    public void Attack()
    {
        if (!Vars.control.input.commandMode || Vars.control.input.selectedUnits.isEmpty()) return;

        attackRequests.clear();

        Vec2 target = input.mouseWorld().cpy();
        Teamc attack = Vars.world.buildWorld(target.x, target.y);

        if (attack == null || attack.team() == Vars.player.team()) 
        {
            attack = Vars.control.input.selectedEnemyUnit(0, 0);
        }

        for (int i = 0; i < Vars.control.input.selectedUnits.size; i++) 
        {
            int id = Vars.control.input.selectedUnits.get(i).id;
            int[] ids = new int[] { id };
            attackRequests.add(new AttackRequest(ids, target, attack));
        }
    }


    public LinkedList<AttackRequest> attackRequests = new LinkedList<>();

    public void ExecuteNextRequest()
    {
        if (attackRequests.isEmpty()) return;
        AttackRequest request = attackRequests.pop();
        if (request == null) return;
        request.Execute();
    }


    public class AttackRequest
    {
        public int[] ids;
        public Vec2 target;
        public Teamc attack;

        public AttackRequest(int[] ids, Vec2 target, Teamc attack) 
        {
            this.ids = ids;
            this.target = target;
            this.attack = attack;
        }

        public void Execute() 
        {
            if (attack != null)
            {
                Events.fire(Trigger.unitCommandAttack);
            }

            Building attackedBuilding = null;
            Unit attackedUnit = null;
            if (attack instanceof Building) attackedBuilding = (Building) attack;
            else if (attack instanceof Unit) attackedUnit = (Unit) attack;

            Call.commandUnits(Vars.player, ids, attackedBuilding, attackedUnit, target);
        }

        public Seq<Unit> GetUnits()
        {
            Seq<Unit> units = new Seq<>();

            for (int id : ids) 
            {
                units.add(Groups.unit.getByID(id));
            }

            return units;
        }
    }
}
