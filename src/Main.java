import Service.Service;
import UI.Consola;
import domain.*;
import repository.InMemoryRepository;
import repository.Repository;

public class Main {
    public static void main(String[] args) {
        Validator validatorUtilizatori = new UtilizatorValidator();
        Repository<Integer, Utilizator> repoUtilizatori= new InMemoryRepository<Integer, Utilizator>(validatorUtilizatori);

        Validator validatorPrieteni = new PrietenieValidator();
        Repository<Pereche<Integer,Integer>, Prietenie> repoPrieteni = new InMemoryRepository<Pereche<Integer, Integer>, Prietenie>(validatorPrieteni);

        Service service = new Service(repoUtilizatori,repoPrieteni);

        Consola ui=new Consola(service);
        ui.run();
    }
}