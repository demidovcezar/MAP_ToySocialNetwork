package repository;
import domain.Pereche;
import domain.Prietenie;
import domain.Utilizator;
import repository.InMemoryRepository;
import repository.PrietenieDbRepository;
import repository.PrietenieFileRepository;
import repository.Repository;
import repository.RepositoryException;
import repository.RepositoryType;
import repository.UtilizatorDbRepository;
import repository.UtilizatorFileRepository;
import validators.Validator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * Simple Factory: the only class that knows the concrete repositories.
 * The persistence type is read from config.properties.
 */
public class RepositoryFactory {
    private static final String CONFIG_FILE = "src/main/resources/config.properties";
    private final Properties props = new Properties();
    private final RepositoryType tip;

    /**
     * Loads the configuration.
     *
     * @throws RepositoryException if the file is missing or repository.type is invalid
     */
    public RepositoryFactory() {
        try (InputStream in = new FileInputStream(CONFIG_FILE)) {
            props.load(in);
        } catch (IOException e) {
            throw new RepositoryException("Nu pot citi fisierul de configurare " + CONFIG_FILE, e);
        }

        String text = citeste("repository.type");
        try {
            tip = RepositoryType.valueOf(text);
        } catch (IllegalArgumentException e) {
            throw new RepositoryException(
                    "repository.type necunoscut: '" + text + "' (valori: MEMORY, FILE, DATABASE)", e);
        }
    }

    /**
     * Reads a required property.
     *
     * @param cheie the property name
     * @return the value
     * @throws RepositoryException if the property is missing
     */
    private String citeste(String cheie) {
        String valoare = props.getProperty(cheie);
        if (valoare == null || valoare.isBlank())
            throw new RepositoryException("Lipseste proprietatea '" + cheie + "' din " + CONFIG_FILE);
        return valoare;
    }

    /**
     * Creates the user repository.
     *
     * @param validator the validation strategy
     * @return the user repository of the configured type
     * @throws RepositoryException if a required property is missing
     */
    public Repository<Integer, Utilizator> createUtilizatorRepository(Validator<Utilizator> validator) {
        switch (tip) {
            case MEMORY:
                return new InMemoryRepository<>(validator);
            case FILE:
                return new UtilizatorFileRepository(citeste("utilizatori.file"), validator);
            case DATABASE:
                return new UtilizatorDbRepository(validator,
                        citeste("db.url"), citeste("db.user"), citeste("db.password"));
            default:
                throw new RepositoryException("Tip de persistenta necunoscut: " + tip);
        }
    }
    /**
     * Creates the friendship repository.
     *
     * @param validator the validation strategy
     * @return the friendship repository of the configured type
     * @throws RepositoryException if a required property is missing
     */
    public Repository<Pereche<Integer, Integer>, Prietenie> createPrietenieRepository(Validator<Prietenie> validator) {
        switch (tip) {
            case MEMORY:
                return new InMemoryRepository<>(validator);
            case FILE:
                return new PrietenieFileRepository(citeste("prietenii.file"), validator);
            case DATABASE:
                return new PrietenieDbRepository(validator,
                        citeste("db.url"), citeste("db.user"), citeste("db.password"));
            default:
                throw new RepositoryException("Tip de persistenta necunoscut: " + tip);
        }
    }
}