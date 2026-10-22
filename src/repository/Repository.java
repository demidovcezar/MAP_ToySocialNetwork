package repository;

import domain.Entity;
import validators.ValidationException;

/**
 * Generic CRUD repository for entities identified by an id of type ID.
 * @param <ID> - type E must have an attribute of type ID
 * @param <E> - type of entities saved in repository
 */
public interface Repository<ID, E extends Entity<ID>> {
    /**
     * Finds the entity with the given id.
     *
     * @param id - the id of the entity to be returned
     * id must not be null
     * @return the entity with the specified id
     * or null - if there is no entity with the given id
     * @throws IllegalArgumentException
     * if id is null.
     */
    E findOne(ID id);

    /**
     * Returns all the entities.
     *
     * @return all entities
     */
    Iterable<E> findAll();

    /**
     * Saves a new entity.
     *
     * @param entity
     * entity must be not null
     * @return null - if the given entity is saved
     * otherwise returns the entity (id already exists)
     * @throws ValidationException
     * if the entity is not valid
     * @throws IllegalArgumentException
     * if the given entity is null.
     */
    E save(E entity);

    /**
     * Removes the entity with the specified id.
     *
     * @param id
     * id must be not null
     * @return the removed entity or null if there is no entity with the given id
     * @throws IllegalArgumentException
     * if the given id is null.
     */
    E delete(ID id);

    /**
     * Updates an existing entity.
     *
     * @param entity
     * entity must not be null
     * @return null - if the entity is updated,
     * otherwise returns the entity - (e.g. id does not exist).
     * @throws IllegalArgumentException
     * if the given entity is null.
     * @throws ValidationException
     * if the entity is not valid.
     */
    E update(E entity);
}