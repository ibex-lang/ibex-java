package nl.thijsmolendijk.swift_reference.util;

/**
 * A simple pair of two (possibly) different types.
 * Created by molenzwiebel on 17-12-15.
 */
public class Pair<LeftType, RightType> {
    private LeftType left;
    private RightType right;

    public Pair(LeftType left, RightType right) {
        this.left = left;
        this.right = right;
    }

    public LeftType getLeft() {
        return left;
    }

    public RightType getRight() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair<?, ?> pair = (Pair<?, ?>) o;
        return !(left != null ? !left.equals(pair.left) : pair.left != null) && !(right != null ? !right.equals(pair.right) : pair.right != null);
    }

    @Override
    public int hashCode() {
        int result = left != null ? left.hashCode() : 0;
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Pair{");
        sb.append("left=").append(left);
        sb.append(", right=").append(right);
        sb.append('}');
        return sb.toString();
    }
}
