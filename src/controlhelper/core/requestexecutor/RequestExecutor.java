package controlhelper.core.requestexecutor;

import java.util.LinkedList;
import arc.util.Timer;


public class RequestExecutor
{
    protected LinkedList<IUnitsRequest> unitRequests = new LinkedList<>();
    protected LinkedList<IRequest> unmergableRequests = new LinkedList<>();

    public float executeDelay = 0.02f;

    public void Init()
    {
        Timer.schedule(() -> 
        {
            Execute();
        }, 0, executeDelay);
    }

    public void AddRequest(IUnitsRequest request)
    {
        for (IUnitsRequest requestB : unitRequests) 
        {
            if (requestB.AreSimiliar(request))
            {
                requestB.MergeRequest(request);
                return;
            }
        }
        unitRequests.add(request);
    }

    public void AddPriorityRequest(IUnitsRequest request)
    {
        for (IUnitsRequest requestB : unitRequests)
        {
            if (requestB.AreSimiliar(request))
            {
                request.MergeRequest(requestB);
                return;
            }
        }
        unitRequests.addFirst(request);
    }

    public void AddRequest(IRequest request)
    {
        unmergableRequests.add(request);
    }

    public void Execute()
    {
        IRequest request = null;
        if (unitRequests.size() > 0)
        {
            request = unitRequests.pop();
        }
        else if (unmergableRequests.size() > 0)
        {
            request = unmergableRequests.pop();
        }

        if (request == null) return;
        request.Execute();
    }
}