package cloud.wraith.javascratchpad;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

@Immutable
@JsonIgnoreProperties(ignoreUnknown=true)
public final class Pojo implements Cloneable {
    private final String id;
    private final String name;
    private final int intValue;
    private final String typeOfThing;
    private final String ignored;

    public enum TypeOfThing {
        OTHER_THING,
        SOME_THING,
        THAT_THING,
        THIS_THING;
    }

    private void validateConstruction(String id, String name, int intValue, String typeOfThing, String ignored) {
        if (StringUtils.isBlank(id))
            throw new IllegalArgumentException("Id should not be blank");

        if (!EnumUtils.isValidEnum(Pojo.TypeOfThing.class, typeOfThing))
            throw new IllegalArgumentException(String.format("Type of thing is [%s] but should be one of %s", typeOfThing, TypeOfThing.values().toString()));
    }

    private void validateConstruction(String id, String name, int intValue, String typeOfThing) {
        validateConstruction(id, name, intValue, typeOfThing, StringUtils.EMPTY);
    }

    /**
     * Hide the noargs constructor.
     * Prefer factory function to encapsulate construction... avoid abundance of new operator in code.
     */
    private Pojo() {
        throw new IllegalArgumentException("Avoid the noargs constructor. Use the factory function instead.");
    }

    private Pojo(String id, String name, int intValue, String typeOfThing, String ignored) {
        validateConstruction(id, name, intValue, typeOfThing, ignored);

        this.id = Objects.requireNonNullElse(id, StringUtils.EMPTY);
        this.name = Objects.requireNonNullElse(name, StringUtils.EMPTY);
        this.intValue = intValue;
        this.typeOfThing = Objects.requireNonNullElse(typeOfThing, StringUtils.EMPTY);
        this.ignored = Objects.requireNonNullElse(ignored, StringUtils.EMPTY);
    }

    private Pojo(String id, String name, int intValue, String typeOfThing) {
        validateConstruction(id, name, intValue, typeOfThing);

        this.id = Objects.requireNonNullElse(id, StringUtils.EMPTY);
        this.name = Objects.requireNonNullElse(name, StringUtils.EMPTY);
        this.intValue = intValue;
        this.typeOfThing = Objects.requireNonNullElse(typeOfThing, StringUtils.EMPTY);
        this.ignored = StringUtils.EMPTY;
    }

    /**
     * Declare a factory function.
     *
     * @param id Value of id
     * @param name Value of name
     * @param intValue Value of intValue
     * @param typeOfThing Value of typeOfThing
     * @return A new instance of Pojo
     */
    public static Optional<Pojo> of(
        String id,
        String name,
        int intValue,
        String typeOfThing
    ) {
        try {

            return Optional.<Pojo>of(new Pojo(id, name, intValue, typeOfThing));

        } catch (IllegalArgumentException e) {

            return Optional.<Pojo>empty();

        }
    }

    /**
     * Returns a new instance of a Pojo as a duplicate (ie. a clone) of a supplied source instance.
     * Tolerates a source value of null, returning an empty Optional<Pojo>
     *
     * @param source The source Pojo instance
     * @return A new instance of Pojo based on a clone of source
     */
    public static Optional<Pojo> of(Pojo source) {
        return Objects.isNull(source)
            ? Optional.<Pojo>empty()
            : Optional.<Pojo>of(source.clone());
    }

    /**
     * Returns a new instance of a Pojo constructed by deserialising json supplied as a string.
     * Tolerates a source value of null, returning an empty Optional<Pojo>
     *
     * @param json The source json as a string
     * @return A new instance of Pojo based on a clone of source
     */
    public static Optional<Pojo> of(String json) {
        try {

            return Objects.isNull(json)
                ? Optional.<Pojo>empty()
                : Optional.<Pojo>of((new ObjectMapper()).readValue(json, Pojo.class));

        } catch (JsonParseException | JsonMappingException e) {

            return Optional.<Pojo>empty();

        } catch (IOException e) {

            return Optional.<Pojo>empty();

        }
    }

    /**
     * Returns a new instance of a Pojo constructed by deserialising json supplied as a byte array.
     * Tolerates a source value of null, returning an empty Optional<Pojo>
     *
     * @param bytes The source json as a byte array
     * @return A new instance of Pojo based on a clone of source
     */
    public static Optional<Pojo> of(byte[] bytes) {
        try {

            return Objects.isNull(bytes)
                ? Optional.<Pojo>empty()
                : Optional.<Pojo>of((new ObjectMapper()).readValue(bytes, Pojo.class));

        } catch (JsonParseException | JsonMappingException e) {

            return Optional.<Pojo>empty();

        } catch (IOException e) {

            return Optional.<Pojo>empty();

        }
    }

    /**
     * Declare a factory function, for deserialising JSON with Jackson databind.
     *
     * @param id Value of id
     * @param name Value of name
     * @param intValue Value of intValue
     * @param typeOfThing Value of typeOfThing
     * @return A new instance of Pojo
     */
    @JsonCreator
    public static Pojo jsonCreator(
        @JsonProperty(value="id") String id,
        @JsonProperty(value="name") String name,
        @JsonProperty(value="intValue") int intValue,
        @JsonProperty(value="typeOfThing") String typeOfThing
    ) {
        return new Pojo(id, name, intValue, typeOfThing);
    }

    @JsonProperty(value="id")
    public String getId() {
        return this.id;
    }

    @JsonProperty(value="name")
    public String getName() {
        return this.name;
    }

    @JsonProperty(value="intValue")
    public int getIntValue() {
        return this.intValue;
    }

    @JsonProperty(value="typeOfThing")
    public String getTypeOfThing() {
        return this.typeOfThing;
    }

    @JsonIgnore
    public String getIgnored() {
        return this.ignored;
    }

    @Override
    public Pojo clone() {
        return new Pojo(this.id, this.name, this.intValue, this.typeOfThing, this.ignored);
    }

    @Override
    public boolean equals(Object o) {
        if (Objects.isNull(o))
            return false;
        if (o == this)
            return true;
        if (!(o instanceof Pojo))
            return false;

        Pojo pojo = (Pojo) o;

        return Objects.equals(id, pojo.id)
            && Objects.equals(name, pojo.name)
            && intValue == pojo.intValue
            && Objects.equals(typeOfThing, pojo.typeOfThing)
            && Objects.equals(ignored, pojo.ignored);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, intValue, typeOfThing, ignored);
    }

    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", name='" + getName() + "'" +
            ", intValue='" + getIntValue() + "'" +
            ", typeOfThing='" + getTypeOfThing() + "'" +
            ", ignored='" + getIgnored() + "'" +
            "}";
    }

    public Optional<String> toJson() {
        try {

            return Optional.<String>of((new ObjectMapper()).writeValueAsString(this));

        } catch (JsonProcessingException e) {

            return Optional.<String>empty();

        }
    }

    public Optional<byte[]> toBytes() {
        try {

            return Optional.<byte[]>of((new ObjectMapper()).writeValueAsBytes(this));

        } catch (JsonProcessingException e) {

            return Optional.<byte[]>empty();

        }
    }
}
