import java.util.ArrayList;

public class TreeSet extends IntSet{

    private final IntSet leftBranch;
    private final IntSet rightBranch;

    public TreeSet(IntSet l, IntSet r, Integer n){
        if (n != null){
            if(n % 2 == 0) l = l.add(half(n));
            else r = r.add(half(n));
        }
        leftBranch = l;
        rightBranch = r;
    }

    public IntSet add(int n){
        return new TreeSet(leftBranch, rightBranch, n);
    }

    public boolean contains(int n) {
        if(n % 2 == 0) return leftBranch.contains(half(n));
        else return rightBranch.contains(half(n));
    }

    public IntSet union(IntSet other) {
        return new TreeSet(leftBranch.union(other.getLeft()),rightBranch.union(other.getRight()), null);
    }

    public IntSet getLeft() {
        return leftBranch;
    }

    public IntSet getRight() {
        return rightBranch;
    }

    public ArrayList<Integer> getNums() {
        ArrayList<Integer> allNums = new ArrayList<>();
         for(Integer num : getLeft().getNums()){
             allNums.add(num*2);
         }
        for(Integer num :getRight().getNums()){
            allNums.add((num*2)+1);
        }
        return allNums;
    }

    public String toString() {
        StringBuilder stringOut = new StringBuilder("{");

        for (Integer num : getNums()){
            stringOut.append(num).append(",");
        }
        stringOut = new StringBuilder(stringOut.substring(0, stringOut.length() - 1));
        return stringOut + "}";
    }

    public int half(int n){
        float x = n;
        n = (int)Math.floor(x/2);
        return n;
    }

}
