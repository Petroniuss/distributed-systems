package Z1;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class MathActorDivide extends AbstractBehavior<MathActor.MathCommandDivide> {

    private int operationCount = 0;

    // --- use messages from MathActor -> no need to define new ones

    // --- constructor and create
    public MathActorDivide(ActorContext<MathActor.MathCommandDivide> context) {
        super(context);
    }

    public static Behavior<MathActor.MathCommandDivide> create() {
        return Behaviors.setup(MathActorDivide::new);
    }

    // --- define message handlers
    @Override
    public Receive<MathActor.MathCommandDivide> createReceive() {
        return newReceiveBuilder()
                .onMessage(MathActor.MathCommandDivide.class, this::onMathCommandDivide)
                .build();
    }

    private Behavior<MathActor.MathCommandDivide> onMathCommandDivide(MathActor.MathCommandDivide mathCommandDivide) {
        this.operationCount++;

        System.out.println("ActorDivide: received command: divide");
        int result = mathCommandDivide.firstNumber / mathCommandDivide.secondNumber;
        System.out.println("actorDivide: divide result = " + result);
        System.out.println("actorDivide: sending response");
        System.out.println("actorDivide: " + operationCount + " operations performed");
        mathCommandDivide.replyTo.tell(new MathActor.MathCommandResult(result));

        return this;
    }
}
