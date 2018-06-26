package libClasses;

abstract class Modifier<T> {

    protected T target = null;


    public void setTarget(T target) {
        this.target = target;
    }

    public T target() {
        return target;
    }

    abstract public void apply();

    abstract public void undo();
}
