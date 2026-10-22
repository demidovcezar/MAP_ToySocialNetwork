package UI;

import domain.*;
import Service.*;
import repository.RepositoryException;
import validators.ValidationException;

import java.util.Scanner;

/**
 * Console menu of the application.
 */
public class Consola {
    private final Service service;
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Creates the console over the service.
     *
     * @param service the application service
     */
    public Consola(Service service) {
        this.service = service;
    }

    private void afiseazaMeniu() {
        System.out.println();
        System.out.println("1. Adauga utilizator");
        System.out.println("2. Sterge utilizator");
        System.out.println("3. Adauga prietenie");
        System.out.println("4. Sterge prietenie");
        System.out.println("5. Afiseaza utilizatori");
        System.out.println("6. Afiseaza prietenii");
        System.out.println("7. Afiseaza numar comunitati");
        System.out.println("8. Afiseaza comunitate sociabila");
        System.out.println("0. Iesire");
    }

    /**
     * Shows the menu until the user chooses to exit.
     * Errors are shown as messages.
     */
    public void run() {
        boolean activ = true;
        while (activ) {
            afiseazaMeniu();
            System.out.print("Optiune: ");
            String optiune = scanner.nextLine();
            try {
                switch (optiune) {
                    case "1": adaugaUtilizator();
                                break;
                    case "2": stergeUtilizator();
                                break;
                    case "3": adaugaPrietenie();
                                break;
                    case "4": stergePrietenie();
                                break;
                    case "5": afiseazaUtilizatori();
                                break;
                    case "6": afiseazaPrietenii();
                                break;
                    case "7": afiseazaNumarComunitati();
                                break;
                    case "8": afiseazaComunitateSociabila();
                                break;
                    case "0": activ = false;
                                break;
                    default : System.out.println("Optiune invalida");
                }
            } catch (ValidationException | ServiceException | RepositoryException | NumberFormatException | UnsupportedOperationException e) {
                System.out.println("Eroare: " + e.getMessage());
            }
        }
    }
    private void adaugaUtilizator() {
        System.out.print("Nume: ");
        String nume = scanner.nextLine();
        System.out.print("Prenume: ");
        String prenume = scanner.nextLine();
        service.adaugaUtilizator(nume, prenume);
        System.out.println("Utilizator adaugat");
    }

    private void stergeUtilizator() {
        System.out.print("Id utilizator: ");
        int id = Integer.parseInt(scanner.nextLine());
        service.stergeUtilizator(id);
        System.out.println("Utilizator sters");
    }

    private void adaugaPrietenie() {
        System.out.print("Id primul utilizator: ");
        int a = Integer.parseInt(scanner.nextLine());
        System.out.print("Id al doilea utilizator: ");
        int b = Integer.parseInt(scanner.nextLine());
        service.adaugaPrietenie(a, b);
        System.out.println("Prietenie adaugata");
    }

    private void stergePrietenie() {
        System.out.print("Id primul utilizator: ");
        int a = Integer.parseInt(scanner.nextLine());
        System.out.print("Id al doilea utilizator: ");
        int b = Integer.parseInt(scanner.nextLine());
        service.stergePrietenie(a, b);
        System.out.println("Prietenie stearsa");
    }

    private void afiseazaUtilizatori() {
        for (Utilizator u : service.getUtilizatori()) {
            System.out.println(u.getId() + ": " + u);
        }
    }

    private void afiseazaPrietenii() {
        for (Prietenie p : service.getPrietenii()) {
            System.out.println(p);
        }
    }
    private void afiseazaNumarComunitati() {
        System.out.println("Numar comunitati: " + service.numarComunitati());
    }
    private void afiseazaComunitateSociabila() {
        for (Utilizator u : service.comunitateSociabila()) {
            System.out.println(u.getId() + ": " + u);
        }
    }
}