package akka;

import akka.actor.UntypedActor;

import java.util.ArrayList;

public class PrimeWorker extends UntypedActor {
    private static boolean isPrime(int n) {
        if (n == 1 || n == 0) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        for (int i = 3; i * i <= n; i += 2)
            if (n % i == 0)
                return false;
        return true;
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof int[]) {
            int[] msg = (int[]) message;
            int start = msg[0];
            int end = msg[1];
            ArrayList<Integer> result = new ArrayList<>();
            for (int i = start; i <= end; i++)
                if (isPrime(i))
                    result.add(i);
            getSender().tell(result, getSelf());
        } else
            unhandled(message);
    }
}
