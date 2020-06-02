package eod.event.relay;

import eod.GameObject;
import eod.effect.Effect;
import eod.event.Event;
import eod.warObject.Status;
import eod.warObject.WarObject;

import java.util.ArrayList;
import java.util.Arrays;

import static eod.effect.EffectFunctions.GiveStatus;

public abstract class StatusHolder {

    private WarObject carrier;
    private ArrayList<Status> holdingStatus;
    private EventReceiver endingEventReceiver = new EndingReceiver();

    public StatusHolder(WarObject carrier) {
        this.carrier = carrier;
        holdingStatus = getHoldingStatus();
        holdingStatus.forEach(status -> carrier.getPlayer().tryToExecute(
                GiveStatus(status, Effect.HandlerType.Owner).to(carrier)
        ));
        carrier.registerReceiver(endingEventReceiver);
    }

    // the receiver that holds status for a limited time
    // It will only remove the status if no other holder is holding its statuses.
    protected abstract ArrayList<Status> getHoldingStatus();
    protected abstract ArrayList<Class<? extends Event>> getEndingEvent();

    private void teardown() {
        carrier.unregisterReceiver(endingEventReceiver);
        carrier.removeStatusHolder(this);

        StatusHolder[] temporaryReceivers = carrier.getStatusHolders();
        holdingStatus.forEach(status -> {
            if(Arrays.stream(temporaryReceivers)
                    .filter(receiver -> receiver.getHoldingStatus().contains(status))
                    .toArray().length == 0) {
                carrier.removeStatus(status);
            }
        });

        carrier = null;
        holdingStatus.clear();
        holdingStatus = null;
    }

    private class EndingReceiver implements EventReceiver {

        @Override
        public ArrayList<Class<? extends Event>> supportedEventTypes() {
            return getEndingEvent();
        }

        @Override
        public void onEventOccurred(GameObject sender, Event event) {
            StatusHolder.this.teardown();
        }

        @Override
        public void teardown() {

        }
    }
}
