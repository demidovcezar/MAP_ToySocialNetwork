package domain;

import java.util.Objects;

public class Pereche<E1,E2> {
    private final E1 e1;
    private final E2 e2;
    public Pereche(E1 e1,E2 e2)
    {
        this.e1=e1;
        this.e2=e2;
    }

    public E1 getE1() {
        return e1;
    }
    public E2 getE2()
    {
        return e2;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        Pereche<?, ?> altul = (Pereche<?, ?>) o;          // un singur cast, pe obiect
        return Objects.equals(this.e1, altul.e1)
                && Objects.equals(this.e2, altul.e2);
    }
    @Override
    public int hashCode() {
        return Objects.hash(this.getE1(),this.getE2());
    }

    @Override
    public String toString() {
        return "(" + e1 + ", " + e2 + ")";
    }
}
