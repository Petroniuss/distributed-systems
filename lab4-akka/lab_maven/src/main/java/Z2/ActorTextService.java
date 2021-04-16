package Z2;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.receptionist.Receptionist;

import java.util.LinkedList;
import java.util.List;

public class ActorTextService extends AbstractBehavior<ActorTextService.Command>  {

    // --- messages
    interface Command {}


    public static class Request implements Command {
        final String text;

        public Request(String text) {
            this.text = text;
        }
    }

    // TODO: new message type implementing Command, with Receptionist.Listing field
    public static class ListingRequest implements Command {
        final Receptionist.Listing listing;

        public ListingRequest(Receptionist.Listing listing) {
            this.listing = listing;
        }
    }


    // --- constructor and create
    // TODO: field for message adapter
    private final List<ActorRef<String>> workers = new LinkedList<>();
    private final ActorRef<Receptionist.Listing> listingResponseAdapter;

    public ActorTextService(ActorContext<ActorTextService.Command> context) {
        super(context);

        // this one becomes a child on actor text service
        this.listingResponseAdapter =
                context.messageAdapter(Receptionist.Listing.class, ListingRequest::new);

        // send receptionist subscription command
        context.getSystem().receptionist()
                .tell(Receptionist.subscribe(ActorUpperCase.upperCaseServiceKey, listingResponseAdapter));
    }

    public static Behavior<Command> create() {
        return Behaviors.setup(ActorTextService::new);
    }

    // --- define message handlers
    @Override
    public Receive<Command> createReceive() {
        System.out.println("creating receive for text service");

        // TODO: handle the new type of message
        return newReceiveBuilder()
                .onMessage(Request.class, this::onRequest)
                .onMessage(ListingRequest.class, this::onListingRequest)
                .build();
    }

    private Behavior<Command> onRequest(Request msg) {
        System.out.println("request: " + msg.text);
        for (ActorRef<String> worker : workers) {
            System.out.println("sending to worker: " + worker);
            worker.tell(msg.text);
        }
        return this;
    }

    // TODO: handle the new type of message
    private Behavior<Command> onListingRequest(ListingRequest msg) {
        System.out.println("On ListingRequest");
        Receptionist.Listing listing = msg.listing;

        this.workers.clear();
        this.workers.addAll(listing.getAllServiceInstances(ActorUpperCase.upperCaseServiceKey));

        return this;
    }

}
