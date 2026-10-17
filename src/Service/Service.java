package Service;

import domain.Pereche;
import domain.Utilizator;
import repository.Repository;
import domain.*;

import java.util.*;

public class Service {
    private final Repository<Integer, Utilizator> repoUtilizatori;
    private final Repository<Pereche<Integer, Integer>, Prietenie> repoPrietenii;
    public Service(Repository<Integer, Utilizator> repoUtilizatori,Repository<Pereche<Integer, Integer>, Prietenie> repoPrietenii) {
        this.repoUtilizatori = repoUtilizatori;
        this.repoPrietenii = repoPrietenii;
    }
    private int genereazaId() {
        int max = 0;
        for (Utilizator u : repoUtilizatori.findAll()) {
            if (u.getId() > max) {
                max = u.getId();
            }
        }
        return max + 1;
    }
    public void adaugaUtilizator(String Nume,String Prenume)
    {
        Utilizator nou= new Utilizator(genereazaId(),Nume,Prenume);
        if(repoUtilizatori.save(nou)!=null)
        {
            throw new ServiceException("Eroare in Service la salvare utilizator");
        }


    }
    public void adaugaPrietenie(int a,int b)
    {
        Prietenie noua =new Prietenie(a,b);
        if (repoUtilizatori.findOne(a) == null) {
            throw new ServiceException("Utilizatorul " + a + " nu exista");
        }

        if (repoUtilizatori.findOne(b) == null) {
            throw new ServiceException("Utilizatorul " + b + " nu exista");
        }
        if(repoPrietenii.save(noua)!=null)
        {
            throw  new ServiceException("Prietenia exista deja");
        }
    }
    public void stergePrietenie(int a,int b)
    {
        Prietenie noua= new Prietenie(a,b);
        if(repoPrietenii.delete(noua.getId())==null) {
            throw new ServiceException("Prietenia nu exista");
        }

    }
    public void stergeUtilizator(int id) {
        if (repoUtilizatori.findOne(id) == null) {
            throw new ServiceException("Utilizatorul " + id + " nu exista");
        }
        List<Prietenie> deSters = new ArrayList<>();
        for (Prietenie p : repoPrietenii.findAll()) {
            if (p.getIdUtilizator1() == id || p.getIdUtilizator2() == id) {
                deSters.add(p);
            }
        }
        for (Prietenie p : deSters) {
            repoPrietenii.delete(p.getId());
        }
        repoUtilizatori.delete(id);
    }
    public Iterable<Utilizator> getUtilizatori() {
        return repoUtilizatori.findAll();
    }

    public Iterable<Prietenie> getPrietenii() {
        return repoPrietenii.findAll();
    }
    private Map<Integer, List<Integer>> construiesteAdiacenta() {
    Map<Integer, List<Integer>> adiacenta = new HashMap<>();
    for (Utilizator u : repoUtilizatori.findAll()) {
        adiacenta.put(u.getId(), new ArrayList<>());
    }
    for (Prietenie p : repoPrietenii.findAll()) {
        int a = p.getIdUtilizator1();
        int b = p.getIdUtilizator2();
        adiacenta.get(a).add(b);
        adiacenta.get(b).add(a);
    }
    return adiacenta;
}
    private void dfs(int id, Map<Integer, List<Integer>> adiacenta, Set<Integer> vizitati) {
        vizitati.add(id);
        for (int vecin : adiacenta.get(id)) {
            if (!vizitati.contains(vecin)) {
                dfs(vecin, adiacenta, vizitati);
            }
        }
    }
    public int numarComunitati() {
        Map<Integer, List<Integer>> adiacenta = construiesteAdiacenta();
        Set<Integer> vizitati = new HashSet<>();
        int contor = 0;
        for (int id : adiacenta.keySet()) {
            if (!vizitati.contains(id)) {
                contor++;
                dfs(id, adiacenta, vizitati);
            }
        }

        return contor;
    }
    private void colecteazaComunitate(int id, Map<Integer, List<Integer>> adiacenta,
                                      Set<Integer> vizitati, List<Integer> comunitate) {
        vizitati.add(id);
        comunitate.add(id);
        for (int vecin : adiacenta.get(id)) {
            if (!vizitati.contains(vecin)) {
                colecteazaComunitate(vecin, adiacenta, vizitati, comunitate);
            }
        }
    }

    private int drumMaxim(int id, Map<Integer, List<Integer>> adiacenta, Set<Integer> peDrum) {
        peDrum.add(id);
        int max = 0;
        for (int vecin : adiacenta.get(id)) {
            if (!peDrum.contains(vecin)) {
                int lungime = 1 + drumMaxim(vecin, adiacenta, peDrum);
                if (lungime > max) {
                    max = lungime;
                }
            }
        }
        peDrum.remove(id);
        return max;
    }

    public List<Utilizator> comunitateSociabila() {
        Map<Integer, List<Integer>> adiacenta = construiesteAdiacenta();
        Set<Integer> vizitati = new HashSet<>();
        List<Integer> cea = new ArrayList<>();
        int ceaMaiMare = -1;

        for (int id : adiacenta.keySet()) {
            if (!vizitati.contains(id)) {
                List<Integer> comunitate = new ArrayList<>();
                colecteazaComunitate(id, adiacenta, vizitati, comunitate);

                int lungime = 0;
                for (int nod : comunitate) {
                    int drum = drumMaxim(nod, adiacenta, new HashSet<>());
                    if (drum > lungime) {
                        lungime = drum;
                    }
                }

                if (lungime > ceaMaiMare) {
                    ceaMaiMare = lungime;
                    cea = comunitate;
                }
            }
        }

        List<Utilizator> rezultat = new ArrayList<>();
        for (int id : cea) {
            rezultat.add(repoUtilizatori.findOne(id));
        }
        return rezultat;
    }

}



