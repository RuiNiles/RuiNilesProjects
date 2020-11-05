import java.util.ArrayList;

public class SingletonSet extends IntSet{

    private final int value;
    private static ArrayList<SingletonSet> common = new ArrayList<>();

    private SingletonSet(int n){
        value = n;
    }

    public static IntSet singleton(int n){
        if(n>=0 && n<=7){
            int x = 0;
            boolean notFound = true;
            if(common.size() > 0){
                while (x<common.size()-1 && notFound){
                    if(common.get(x).value == n) notFound = false;
                    x++;
                }
            }
            if(!notFound) return common.get(x-1);
            SingletonSet addNew = new SingletonSet(n);
            common.add(addNew);
            return addNew;
        }
        return new SingletonSet(n);
    }

    public IntSet add(int n){
        if(contains(n))  return this;
        if(value%2 == 0)  return new TreeSet(halfIntSet(value),EmptySet.emptySet(), n);
        return new TreeSet(EmptySet.emptySet(),halfIntSet(value), n);
    }

    public boolean contains(int n) {
        return value == n;
    }

    public IntSet union(IntSet other) {
        return other.add(value);
    }

    public IntSet getLeft() {
        if(value % 2 == 0)return halfIntSet(value);
        return EmptySet.emptySet();
    }

    public IntSet getRight() {
        if(value % 2 != 0)return halfIntSet(value);
        return EmptySet.emptySet();
    }

    public ArrayList<Integer> getNums() {
        ArrayList<Integer> nums = new ArrayList<>();
        nums.add(value);
        return nums;
    }

    public String toString() {
        return "{" + value + "}";
    }

    public IntSet halfIntSet(int n){
        float x = n;
        n = (int)Math.floor(x/2);
        return EmptySet.emptySet().add(n);
    }
}
