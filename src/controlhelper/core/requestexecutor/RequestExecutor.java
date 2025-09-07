package controlhelper.core.requestexecutor;

import arc.struct.Queue;
import arc.util.Log;
import arc.util.Timer;
import mindustry.Vars;

public class RequestExecutor {
    protected Queue<IRequest> requests = new Queue<>();
    protected Queue<IUnmergableRequest> unmergableRequests = new Queue<>();

    public final float executeDelay = 0.02f;

    public void Init() {
        Timer.schedule(() -> {
            if (!Vars.state.isGame()) {
                requests.clear();
                unmergableRequests.clear();
                return;
            }
            Execute();
        }, 0, executeDelay);
    }

    public void AddRequest(IRequest request) {
        for (IRequest requestB : requests) {
            if (requestB.AreSimiliar(request)) {
                requestB.MergeRequest(request);
                return;
            }
        }
        requests.add(request);
    }

    public void AddPriorityRequest(IRequest request) {
        for (IRequest requestB : requests) {
            if (requestB.AreSimiliar(request)) {
                request.MergeRequest(requestB);
                return;
            }
        }
        requests.addFirst(request);
    }

    public void AddRequest(IUnmergableRequest request) {
        unmergableRequests.add(request);
    }

    public void AddPriorityRequest(IUnmergableRequest request) {
        unmergableRequests.addFirst(request);
    }

    public void Execute() {
        IUnmergableRequest request = null;
        if (!requests.isEmpty()) {
            request = requests.last();
            requests.removeLast();
        } else if (!unmergableRequests.isEmpty()) {
            request = unmergableRequests.last();
            unmergableRequests.removeLast();
        } else {
            return;
        }

        if (request != null) {
            request.Execute();
            request.SetExecuted(true);
        }
    }

    public void RemoveRequest(IUnmergableRequest request) {
        unmergableRequests.remove(request);
    }
}