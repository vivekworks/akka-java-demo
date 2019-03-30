package akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;

import java.util.ArrayList;

public class PrimeMaster extends UntypedActor {
    private Router workerRouter;
    private int numberOfWorkers;
    private int numberOfResults;
    ArrayList<Routee> routees = new ArrayList<>();

    public PrimeMaster(int numWork) {
        numberOfWorkers = numWork;
        for (int i = 0; i < numberOfWorkers; i++) {
            ActorRef actorRef = getContext().actorOf(Props.create(PrimeWorker.class));
            getContext().watch(actorRef);
            routees.add(new ActorRefRoutee(actorRef));
        }
        workerRouter = new Router(new RoundRobinRoutingLogic(), routees);
    }

    public void onReceive(Object msg) {
        if (msg instanceof int[]) {
            int message[] = (int[]) msg;
            int start = message[0];
            int end = message[1];
            int totalNumbers = end - start;
            int segmentLength = totalNumbers / numberOfWorkers;
            for (int i = 0; i < numberOfWorkers; i++) {
                int startNumber = start + (i * segmentLength);
                int endNumber = startNumber + segmentLength - 1;
                if (i == numberOfWorkers - 1)
                    endNumber = end;
                int[] send = {startNumber, endNumber};
                workerRouter.route(send, getSelf());
            }
        } else if (msg instanceof ArrayList) {
            ArrayList<Integer> result = (ArrayList<Integer>) msg;
            for (int n : result)
                System.out.print(n + " , ");
            System.out.println();
            if (++numberOfResults >= numberOfWorkers) {
                getContext().stop(getSelf());
                getContext().system().terminate();
            }
        } else
            unhandled(msg);
    }
}
