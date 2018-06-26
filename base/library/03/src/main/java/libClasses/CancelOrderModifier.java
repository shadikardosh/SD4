package libClasses;


import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class CancelOrderModifier /*implements OrderModifier*/ extends Modifier<CancellableOrder> {

    private boolean wasApplied = false;

    @Override
    public void apply() {
        if (target == null /*|| target.isCancelled()*/) {
            throw new NotImplementedException();
        } // TODO: 11-Jun-18 cancelling twice is allowed according to Piazza 92
        // TODO: 12-Jun-18 but this class isn't used? why fix here?
        wasApplied = target.isApplied;
        target.cancel();
    }

    @Override
    public void undo() {
        if (target == null || !target.isCancelled()) {
            throw new NotImplementedException();
        }
        if (wasApplied) {
            target.apply(); // this takes care of uncancelling
        }
//        target.uncancel();
    }
}
