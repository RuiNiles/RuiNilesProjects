import java.util.ArrayList;

public class EmptySet extends IntSet{

    private static EmptySet instance = null;

    private EmptySet(){}

    public static EmptySet emptySet(){
        if(instance == null) instance = new EmptySet();
        return instance;
    }

    public IntSet add(int n){
        return SingletonSet.singleton(n);
    }

    public boolean contains(int n) {
        return false;
    }

    public IntSet union(IntSet other) {
        return other;
    }

    public IntSet getLeft() {
        return this;
    }

    public IntSet getRight() {
        return this;
    }

    public ArrayList<Integer> getNums() {
        return null;
    }

    public String toString() {
        return "{}";
    }

}
