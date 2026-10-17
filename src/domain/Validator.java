package domain;

public interface Validator<T> {
    void validate(T t) throws  ValidationException;
}
