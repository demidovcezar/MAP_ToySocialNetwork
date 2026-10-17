package domain;

public class PrietenieValidator implements   Validator<Prietenie>{
    @Override
    public void validate(Prietenie t) {
        String erori = "";
        if(t.getIdUtilizator1() == t.getIdUtilizator2())
            erori+="Eroare id-uri egale\n";
        if (!erori.isEmpty()) {
            throw new ValidationException(erori.trim());
        }
    }
}
