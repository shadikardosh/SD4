package libClasses;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ChangeOrderModifier /*implements OrderModifier*/ extends Modifier<Order> {
    Integer newSeatNum;

    @Override
    public void apply() {
        if (target == null) {
            throw new NotImplementedException();
        }

        target.updateSeat(newSeatNum);

//        if (target.isCancelled()) {
//            target.uncancel();
//        }

    }

    @Override
    public void undo() {
        if (target == null) {
            throw new NotImplementedException();
        }
        // TODO: is it needed to undo order change?
        throw new NotImplementedException();
    }
}
