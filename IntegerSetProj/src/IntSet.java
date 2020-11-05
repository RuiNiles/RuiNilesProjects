import java.util.ArrayList;

public abstract class IntSet {
    public abstract IntSet add(int n);
    public abstract boolean contains(int n);
    public abstract IntSet union(IntSet other);
    public abstract IntSet getLeft();
    public abstract IntSet getRight();
    public abstract ArrayList<Integer> getNums();
    public abstract String toString();
}
