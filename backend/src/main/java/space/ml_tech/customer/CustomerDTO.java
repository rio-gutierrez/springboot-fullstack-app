package space.ml_tech.customer;

/**
 * CustomerDTO class. We omit `id` because this field is autogenerated
 * by the `@SequenceGenerator` and `@GenerateValue` annotations on Customer `id`
 */
public record CustomerDTO (
        String name,
        String email,
        Integer age,
        Gender gender) {
}