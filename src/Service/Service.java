package Service;

import domain.Pereche;
import domain.Utilizator;
import repository.Repository;
import domain.*;

import java.util.*;

/**
 * Business logic: users, friendships and communities.
 */
public class Service {
    private final Repository<Integer, Utilizator> repoUtilizatori;
    private final Repository<Pereche<Integer, Integer>, Prietenie> repoPrietenii;
    /**
     * Creates the service over the two repositories.
     *
     * @param repoUtilizatori the repository of users
     * @param repoPrietenii the repository of friendships
     */
    public Service(Repository<Integer, Utilizator> repoUtilizatori,Repository<Pereche<Integer, Integer>, Prietenie> repoPrietenii) {
        this.repoUtilizatori = repoUtilizatori;
        this.repoPrietenii = repoPrietenii;
    }
    /**
     * Generates the id of a new user.
     *
     * @return the next free user id (maximum id + 1)
     */
    private int genereazaId() {
        int max = 0;
        for (Utilizator u : repoUtilizatori.findAll()) {
            if (u.getId() > max) {
                max = u.getId();
            }
        }
        return max + 1;
    }
    /**
     * Adds a user with a generated id.
     *
     * @param Nume the last name
     * @param Prenume the first name
     * @throws validators.ValidationException if a name is blank
     * @throws ServiceException if the user could not be saved
     * @throws repository.RepositoryException if the data cannot be persisted
     */
    public void adaugaUtilizator(String Nume,String Prenume)
    {
        Utilizator nou= new Utilizator(genereazaId(),Nume,Prenume);
        if(repoUtilizatori.save(nou)!=null)
        {
            throw new ServiceException("Eroare in Service la salvare utilizator");
        }


    }
    /**
     * Adds a friendship between two existing users.
     *
     * @param a the id of one user
     * @param b the id of the other user
     * @throws ServiceException if a user does not exist or the friendship already exists
     * @throws validators.ValidationException if a equals b
     * @throws repository.RepositoryException if the data cannot be persisted
     */
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
    /**
     * Removes a friendship.
     *
     * @param a the id of one user
     * @param b the id of the other user
     * @throws ServiceException if the friendship does not exist
     * @throws repository.RepositoryException if the data cannot be persisted
     */
    public void stergePrietenie(int a,int b)
    {
        Prietenie noua= new Prietenie(a,b);
        if(repoPrietenii.delete(noua.getId())==null) {
            throw new ServiceException("Prietenia nu exista");
        }

    }
    /**
     * Removes a user together with all of his friendships.
     *
     * @param id the user id
     * @throws ServiceException if the user does not exist
     * @throws repository.RepositoryException if the data cannot be persisted
     */
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
    /**
     * Returns all users.
     *
     * @return all users
     */
    public Iterable<Utilizator> getUtilizatori() {
        return repoUtilizatori.findAll();
    }

    /**
     * Returns all friendships.
     *
     * @return all friendships
     */
    public Iterable<Prietenie> getPrietenii() {
        return repoPrietenii.findAll();
    }

    /**
     * Builds the friendship graph as an adjacency list.
     *
     * @return for each user id, the list of his friends' ids
     */
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

    /**
     * Marks as visited all users reachable from the given user.
     */
    private void dfs(int id, Map<Integer, List<Integer>> adiacenta, Set<Integer> vizitati) {
        vizitati.add(id);
        for (int vecin : adiacenta.get(id)) {
            if (!vizitati.contains(vecin)) {
                dfs(vecin, adiacenta, vizitati);
            }
        }
    }
    /**
     * Counts the communities.
     *
     * @return the number of communities (connected components of the friendship graph)
     */
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
    /**
     * Collects in 'comunitate' all users reachable from the given user.
     */
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

    /**
     * Finds the longest simple path starting at a user (DFS with backtracking).
     *
     * @return the length of the longest simple path starting at the given user
     */
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

    /**
     * Finds the most sociable community.
     *
     * @return the users of the community that contains the longest simple path
     */
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



