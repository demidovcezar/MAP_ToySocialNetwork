package domain;

public class UtilizatorValidator  implements Validator<Utilizator>{
    @Override
   public void validate(Utilizator t) {
        String erori = "";
        if (t.getNume() == null || t.getNume().isBlank()) {
            erori += "Numele nu poate fi vid\n";
        }
        if (t.getPrenume() == null || t.getPrenume().isBlank()) {
            erori += "Prenumele nu poate fi vid\n";
        }
        if (!erori.isEmpty()) {
            throw new ValidationException(erori.trim());
        }
    }
}

