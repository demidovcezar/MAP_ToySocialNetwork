package domain;

import java.util.Objects;

public class Prietenie extends Entity<Pereche<Integer, Integer>> {

    public Prietenie(int a, int b) {
        super(new Pereche<>(Math.min(a, b), Math.max(a, b)));   // mai micul primul
    }

    public int getIdUtilizator1() {
        return getId().getE1();
    }

    public int getIdUtilizator2() {
        return getId().getE2();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        return Objects.equals(getId(), ((Prietenie) o).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Prietenie: " + getIdUtilizator1() + " - " + getIdUtilizator2();
    }
}