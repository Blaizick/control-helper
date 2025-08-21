package controlhelper.core.events;

import arc.struct.Queue;
import mindustry.entities.units.BuildPlan;

public class CHEventType
{
    public static class PlayerPlansChangeEvent 
    {
        public final Queue<BuildPlan> added;
        public final Queue<BuildPlan> removed;

        public PlayerPlansChangeEvent(Queue<BuildPlan> added, Queue<BuildPlan> removed)
        {
            this.added = added;
            this.removed = removed;
        }
    }
}
