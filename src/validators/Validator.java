package validators;

/**
 * Validation strategy for entities.
 *
 * @param <T> the type of the validated entities
 */
public interface Validator<T> {
    /**
     * Checks the entity.
     *
     * @param t the entity to validate
     * @throws ValidationException if the entity is not valid
     */
    void validate(T t) throws ValidationException;
}
