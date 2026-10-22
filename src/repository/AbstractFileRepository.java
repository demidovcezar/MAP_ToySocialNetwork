package repository;

import java.io.*;
import java.nio.charset.*;
import java.time.DateTimeException;

import domain.Entity;
import validators.*;

/**
 * Repository that also keeps its entities in a text file, one per line.
 * The subclasses only convert a line to an entity and back (Template Method).
 */
public abstract class  AbstractFileRepository<ID,E extends Entity<ID>> extends InMemoryRepository<ID,E>{
    private  final String fileName;
    /**
     * Creates the repository and loads the file, if it exists.
     *
     * @param validator the validation strategy
     * @param fileName the path of the text file
     */
    public AbstractFileRepository(Validator<E> validator,String fileName) {
        super(validator);
        this.fileName = fileName;
        loadData();
    }
    /**
     * Loads the file; invalid lines are reported and skipped.
     *
     * @throws RepositoryException if the file cannot be read
     */
    private void loadData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName, StandardCharsets.UTF_8))) {
            String linie = reader.readLine();
            int numarLinie = 0;
            while (linie != null) {
                numarLinie++;
                if (!linie.isBlank()) {
                    try {
                        E entity = extractEntity(linie);
                        E existent = super.save(entity);
                        if (existent != null) {
                            System.err.println("Linia " + numarLinie + " ignorata: id duplicat (" + linie + ")");
                        }
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException
                             | DateTimeException | ValidationException e) {
                        System.err.println("Linia " + numarLinie + " ignorata: " + e.getMessage() + " (" + linie + ")");
                    }
                }
                linie = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Fisierul " + fileName + " nu exista; repository gol.");
        } catch (IOException e) {
            throw new RepositoryException("Eroare la citire: " + fileName, e);
        }
    }
    /**
     * Rewrites the whole file.
     *
     * @throws RepositoryException if the file cannot be written
     */
    private void writeToFile()
    {
        File file = new File(fileName).getParentFile();
        if(file!=null)
            file.mkdirs();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, StandardCharsets.UTF_8))) {
            for(var elem : findAll())
           {
               writer.write(createEntityAsString(elem));
               writer.newLine();

           }
        } catch (IOException e) {
            throw new RepositoryException("Eroare la scriere: " + fileName, e);
        }
    }

    /**
     * Converts a line of the file into an entity.
     *
     * @param line a line of the file
     * @return the entity described by the line
     */
    protected abstract E extractEntity(String line);
    /**
     * Converts an entity into a line of the file.
     *
     * @param entity the entity to convert
     * @return the line that describes the entity
     */
    protected abstract String createEntityAsString(E entity);

    /**
     * Saves the entity and, if it was saved, rewrites the file.
     *
     * @throws RepositoryException if the file cannot be written
     */
    @Override
    public E save(E entity) {
        E rezultat =super.save(entity);
        if(rezultat==null)
        {
            writeToFile();
        }
        return rezultat;
    }
    /**
     * Deletes the entity and, if it existed, rewrites the file.
     *
     * @throws RepositoryException if the file cannot be written
     */
    @Override
    public E delete(ID id) {
        E rezultat =super.delete(id);
        if(rezultat!=null)
        {
            writeToFile();
        }
        return rezultat;
    }
    /**
     * Updates the entity and, if it was updated, rewrites the file.
     *
     * @throws RepositoryException if the file cannot be written
     */
    @Override
    public E update(E entity) {
        E rezultat =super.update(entity);
        if(rezultat==null)
        {
            writeToFile();
        }
        return rezultat;
    }
}
