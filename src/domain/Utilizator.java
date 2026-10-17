package domain;

import java.util.Objects;

public class Utilizator extends Entity<Integer> implements Comparable<Utilizator>{
    private String nume;
    private String prenume;
    public Utilizator(int id,String nume,String prenume)
    {
        super(id);
        this.nume=nume;
        this.prenume=prenume;

    }
    public void setNume(String Nume)
    {
        this.nume=Nume;
    }
    public void setPrenume(String Prenume)
    {
        this.prenume=Prenume;
    }
    public String getNume()
    {
        return nume;

    }
    public String getPrenume()
    {
        return prenume;
    }
    @Override
    public String toString()
    {
        return nume+" "+prenume;
    }
    @Override
    public int compareTo(Utilizator s)
    {
        int r= nume.compareTo(s.nume);
        if(r!=0)
        {
            return r;
        }
        return prenume.compareTo(s.prenume);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        return Objects.equals(super.getId(),((Utilizator) o).getId());

    }

    @Override
    public int hashCode() {
        return Objects.hash(super.getId());
    }


}
