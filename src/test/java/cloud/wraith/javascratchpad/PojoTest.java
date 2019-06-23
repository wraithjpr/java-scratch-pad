package cloud.wraith.javascratchpad;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import org.junit.Test;

/**
 * Unit test for Pojo class.
 */
public class PojoTest {
    private static final int KEY = 0;
    private static final int VALUE = 1;

    private static final String JSON_TEMPLATE = "{\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":%s,\"%s\":\"%s\"}";
    private static final String[] ID_PAIR = {"id", "578da685-f6ea-4ffe-869a-49acbbc998b2"};
    private static final String[] NAME_PAIR = {"name", "my-test-name"};
    private static final String[] INT_VALUE_PAIR = {"intValue", "12345"};
    private static final String[] TYPE_OF_THING_PAIR = {"typeOfThing", Pojo.TypeOfThing.SOME_THING.toString()};
    private static final String EMPTY_JSON_OBJECT = "{}";

    private static final String JSON = String.format(JSON_TEMPLATE,
        ID_PAIR[KEY], ID_PAIR[VALUE],
        NAME_PAIR[KEY], NAME_PAIR[VALUE],
        INT_VALUE_PAIR[KEY], INT_VALUE_PAIR[VALUE],
        TYPE_OF_THING_PAIR[KEY], TYPE_OF_THING_PAIR[VALUE]
    );

    private static final Pojo POJO;

    static {
        POJO = Pojo.of(
            ID_PAIR[VALUE],
            NAME_PAIR[VALUE],
            Integer.parseInt(INT_VALUE_PAIR[VALUE]),
            TYPE_OF_THING_PAIR[VALUE]
        ).orElseThrow();
    }

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Test construction with factory function.
     *   of :: String -> String -> int -> String -> Pojo
     */
    @Test
    public void shouldConstructAPojoViaTheFactory() {

        final Pojo actual = Pojo.of(
            ID_PAIR[VALUE],
            NAME_PAIR[VALUE],
            Integer.parseInt(INT_VALUE_PAIR[VALUE]),
            TYPE_OF_THING_PAIR[VALUE]
        ).orElseThrow();

        assertEquals("Id should match", ID_PAIR[VALUE], actual.getId());
        assertEquals("Name should match", NAME_PAIR[VALUE], actual.getName());
        assertEquals("Int value should match", Integer.parseInt(INT_VALUE_PAIR[VALUE]), actual.getIntValue());
        assertEquals("Type of thing value should match", TYPE_OF_THING_PAIR[VALUE], actual.getTypeOfThing());
        assertEquals("Ignored property value should be empty", StringUtils.EMPTY, actual.getIgnored());
    }

    /**
     * Test construction for empty id.
     */
    @Test
    public void shouldFailConstructionForEmptyId() {

        final Optional<Pojo> actual = Pojo.of(
            StringUtils.EMPTY,
            NAME_PAIR[VALUE],
            Integer.parseInt(INT_VALUE_PAIR[VALUE]),
            TYPE_OF_THING_PAIR[VALUE]
        );

        assertTrue("Should be an empty optional", actual.isEmpty());
        assertFalse("Should not be present", actual.isPresent());

    }

    /**
     * Test construction for empty type of thing.
     */
    @Test
    public void shouldFailConstructionForEmptyTypeOfThing() {

        final Optional<Pojo> actual = Pojo.of(
            ID_PAIR[VALUE],
            NAME_PAIR[VALUE],
            Integer.parseInt(INT_VALUE_PAIR[VALUE]),
            StringUtils.EMPTY
        );

        assertTrue("Should be an empty optional", actual.isEmpty());
        assertFalse("Should not be present", actual.isPresent());

    }

    /**
     * Test cloning.
     */
    @Test
    public void shouldClone() {

        final Pojo actual = POJO.clone();

        assertTrue("Should be different objects", POJO != actual);
        assertTrue("Values should match", actual.equals(POJO));
        assertTrue("Values should match via Objects.equals", Objects.equals(actual, POJO));
        assertTrue("Values should match via Objects.deepEquals", Objects.deepEquals(actual, POJO));

        assertTrue("Id should match via Objects.equals", Objects.equals(actual.getId(), POJO.getId()));
        assertTrue("Name should match via Objects.equals", Objects.equals(actual.getName(), POJO.getName()));
        assertTrue("Int value should match via Objects.equals", Objects.equals(actual.getIntValue(), POJO.getIntValue()));
        assertTrue("Type of thing should match via Objects.equals", Objects.equals(actual.getTypeOfThing(), POJO.getTypeOfThing()));
        assertTrue("Ignored property should match via Objects.equals", Objects.equals(actual.getIgnored(), POJO.getIgnored()));

        assertEquals("Id should match", POJO.getId(), actual.getId());
        assertEquals("Name should match", POJO.getName(), actual.getName());
        assertEquals("Int value should match", POJO.getIntValue(), actual.getIntValue());
        assertEquals("Type of thing value should match", POJO.getTypeOfThing(), actual.getTypeOfThing());
        assertEquals("Ignored property value should match", POJO.getIgnored(), actual.getIgnored());

    }

    /**
     * Test Pojo factory.
     *   of :: Pojo -> Pojo
     */
    @Test
    public void shouldConstructAPojoFromAPojo() {

        final Pojo actual = Pojo.of(POJO).orElseThrow();

        assertTrue("Should be different objects", POJO != actual);
        assertTrue("Values should match", actual.equals(POJO));
        assertTrue("Values should match via Objects.equals", Objects.equals(actual, POJO));
        assertTrue("Values should match via Objects.deepEquals", Objects.deepEquals(actual, POJO));

        assertEquals("Id should match", POJO.getId(), actual.getId());
        assertEquals("Name should match", POJO.getName(), actual.getName());
        assertEquals("Int value should match", POJO.getIntValue(), actual.getIntValue());
        assertEquals("Type of thing value should match", POJO.getTypeOfThing(), actual.getTypeOfThing());
        assertEquals("Ignored property value should match", POJO.getIgnored(), actual.getIgnored());

    }

    /**
     * Test json serialisation.
     * @throws JsonProcessingException
     */
    @Test
    public void shouldSerializePojoToJson() throws JsonProcessingException {

        final String actual = mapper.writeValueAsString(POJO);

        assertEquals("Json string value should match", JSON, actual);
    }

    /**
     * Test json deserialisation.
     *
     */
    @Test
    public void shouldDeserializeJsonToPojo() throws JsonParseException, JsonMappingException, IOException {

        final Pojo actual = mapper.readValue(JSON, Pojo.class);

        assertEquals("Id should match", ID_PAIR[VALUE], actual.getId());
        assertEquals("Name should match", NAME_PAIR[VALUE], actual.getName());
        assertEquals("Int value should match", Integer.parseInt(INT_VALUE_PAIR[VALUE]), actual.getIntValue());
        assertEquals("Type of thing value should match", TYPE_OF_THING_PAIR[VALUE], actual.getTypeOfThing());
    }

    /**
     * Test type of thing enum.
     */
    @Test
    public void shouldValidateATypeOfThing() {
        assertTrue("SOME_THING should be a valid enum name", EnumUtils.isValidEnum(Pojo.TypeOfThing.class, "SOME_THING"));
    }

    /**
     * Test validation with enum.
     */
    @Test
    public void shouldValidatePojo() throws JsonParseException, JsonMappingException, IOException {

        final Pojo actual = mapper.readValue(JSON, Pojo.class);

        assertTrue("Type of thing should not be blank", StringUtils.isNotBlank(actual.getTypeOfThing()));
        assertTrue("Type of thing should be a valid enum name", EnumUtils.isValidEnum(Pojo.TypeOfThing.class, actual.getTypeOfThing()));

    }

    /**
     * Test deserialisation for empty json object.
     */
    @Test(expected = InvalidDefinitionException.class)
    public void shouldFailDeserialisationForEmptyJsonObject() throws InvalidDefinitionException, JsonParseException, JsonMappingException, IOException {

        mapper.readValue(EMPTY_JSON_OBJECT, Pojo.class);

    }

    /**
     * Test deserialisation for missing id.
     */
    @Test(expected = InvalidDefinitionException.class)
    public void shouldFailDeserialisationForMissingId() throws InvalidDefinitionException, JsonParseException, JsonMappingException, IOException {

        final String JSON = String.format("{\"%s\":\"%s\",\"%s\":%s,\"%s\":\"%s\"}",
            NAME_PAIR[KEY], NAME_PAIR[VALUE],
            INT_VALUE_PAIR[KEY], INT_VALUE_PAIR[VALUE],
            TYPE_OF_THING_PAIR[KEY], TYPE_OF_THING_PAIR[VALUE]
        );

        mapper.readValue(JSON, Pojo.class);

    }

    /**
     * Test deserialisation for empty id.
     */
    @Test(expected = InvalidDefinitionException.class)
    public void shouldFailDeserialisationForEmptyId() throws InvalidDefinitionException, JsonParseException, JsonMappingException, IOException {

        final String JSON = String.format(JSON_TEMPLATE,
            ID_PAIR[KEY], StringUtils.EMPTY,
            NAME_PAIR[KEY], NAME_PAIR[VALUE],
            INT_VALUE_PAIR[KEY], INT_VALUE_PAIR[VALUE],
            TYPE_OF_THING_PAIR[KEY], TYPE_OF_THING_PAIR[VALUE]
        );

        mapper.readValue(JSON, Pojo.class);

    }

    /**
     * Test deserialisation for missing type of thing.
     */
    @Test(expected = InvalidDefinitionException.class)
    public void shouldFailDeserialisationForMissingTypeOfThing() throws InvalidDefinitionException, JsonParseException, JsonMappingException, IOException {

        final String JSON = String.format("{\"%s\":\"%s\",\"%s\":\"%s\",\"%s\":%s}",
            ID_PAIR[KEY], ID_PAIR[VALUE],
            NAME_PAIR[KEY], NAME_PAIR[VALUE],
            INT_VALUE_PAIR[KEY], INT_VALUE_PAIR[VALUE]
        );

        mapper.readValue(JSON, Pojo.class);

    }

    /**
     * Test deserialisation for empty type of thing.
     */
    @Test(expected = InvalidDefinitionException.class)
    public void shouldFailDeserialisationForEmptyTypeOfThing() throws InvalidDefinitionException, JsonParseException, JsonMappingException, IOException {

        final String JSON = String.format(JSON_TEMPLATE,
            ID_PAIR[KEY], ID_PAIR[VALUE],
            NAME_PAIR[KEY], NAME_PAIR[VALUE],
            INT_VALUE_PAIR[KEY], INT_VALUE_PAIR[VALUE],
            TYPE_OF_THING_PAIR[KEY], StringUtils.EMPTY
        );

        mapper.readValue(JSON, Pojo.class);

    }

    /**
     * Test deserialisation for invalid type of thing enum value.
     */
    @Test(expected = InvalidDefinitionException.class)
    public void shouldFailDeserialisationForInvalidTypeOfThing() throws InvalidDefinitionException, JsonParseException, JsonMappingException, IOException {

        final String JSON = String.format(JSON_TEMPLATE,
            ID_PAIR[KEY], ID_PAIR[VALUE],
            NAME_PAIR[KEY], NAME_PAIR[VALUE],
            INT_VALUE_PAIR[KEY], INT_VALUE_PAIR[VALUE],
            TYPE_OF_THING_PAIR[KEY], "this-thing-is-not-a-valid-enum-value"
        );

        mapper.readValue(JSON, Pojo.class);

    }

    /**
     * Test defaults for json deserialisation.
     */
    @Test
    public void shouldSetDefaultValuesOnDeserialisation() throws InvalidDefinitionException, JsonParseException, JsonMappingException, IOException {

        final String JSON = String.format("{\"%s\":\"%s\",\"%s\":\"%s\"}",
            ID_PAIR[KEY], ID_PAIR[VALUE],
            TYPE_OF_THING_PAIR[KEY], TYPE_OF_THING_PAIR[VALUE]
        );

        final Pojo actual = mapper.readValue(JSON, Pojo.class);

        assertNotNull("name should not be null", actual.getName());
        assertEquals("name should be empty", StringUtils.EMPTY, actual.getName());
        assertEquals("intValue should be zero", 0, actual.getIntValue());

    }

    /**
     * Test the hash code.
     */
    @Test
    public void shouldHash() {
        final int expected = Objects.hash(ID_PAIR[VALUE], NAME_PAIR[VALUE], Integer.parseInt(INT_VALUE_PAIR[VALUE]), TYPE_OF_THING_PAIR[VALUE], StringUtils.EMPTY);

        assertEquals("Hash codes should match", expected, POJO.hashCode());
    }

    /**
     * Test serialisation to json.
     */
    @Test
    public void shouldSerializeToJson() {
        final String actual = POJO.toJson().orElseThrow();

        assertEquals("Json should match", JSON, actual);
    }

    /**
     * Test deserialisation via the json factory.
     *   of :: String -> Pojo
     */
    @Test
    public void shouldConstructAPojoFromJsonAsAString() {

        final Pojo actual = Pojo.of(JSON).orElseThrow();

        assertTrue("Values should match via Objects.equals", Objects.equals(actual, POJO));

        assertEquals("Id should match", POJO.getId(), actual.getId());
        assertEquals("Name should match", POJO.getName(), actual.getName());
        assertEquals("Int value should match", POJO.getIntValue(), actual.getIntValue());
        assertEquals("Type of thing value should match", POJO.getTypeOfThing(), actual.getTypeOfThing());
        assertEquals("Ignored property value should match", POJO.getIgnored(), actual.getIgnored());

    }

    /**
     * Test serialisation to byte array.
     */
    @Test
    public void shouldSerializeToBytes() {
        final byte[] actual = POJO.toBytes().orElseThrow();

        assertEquals("Byte arrays should match", JSON, (new String(actual)));
    }

    /**
     * Test deserialisation via the byte array factory.
     *   of :: byte[] -> Pojo
     */
    @Test
    public void shouldConstructAPojoFromJsonAsAByteArray() {

        final Pojo actual = Pojo.of(JSON.getBytes()).orElseThrow();

        assertTrue("Values should match via Objects.equals", Objects.equals(actual, POJO));

        assertEquals("Id should match", POJO.getId(), actual.getId());
        assertEquals("Name should match", POJO.getName(), actual.getName());
        assertEquals("Int value should match", POJO.getIntValue(), actual.getIntValue());
        assertEquals("Type of thing value should match", POJO.getTypeOfThing(), actual.getTypeOfThing());
        assertEquals("Ignored property value should match", POJO.getIgnored(), actual.getIgnored());

    }

    /**
     * Test serialise an array of Pojos to json.
     */
    @Test
    public void shouldSerializeAnArrayOfPojos() throws JsonProcessingException {

        final String expected = "[" +
            "{\"id\":\"84f6ff48-80bc-442c-8b67-00aa9e526beb\",\"name\":\"Pojo one\",\"intValue\":1,\"typeOfThing\":\"SOME_THING\"}," +
            "{\"id\":\"26f974dd-8762-4e7b-b5f7-7fd6c14e4de8\",\"name\":\"Pojo two\",\"intValue\":2,\"typeOfThing\":\"THIS_THING\"}," +
            "{\"id\":\"a986347e-6925-4a0e-8625-29eba7ff6a91\",\"name\":\"Pojo three\",\"intValue\":3,\"typeOfThing\":\"THAT_THING\"}," +
            "{\"id\":\"b61d92ba-6b9e-4dbb-afb8-5f84ffad34d6\",\"name\":\"Pojo four\",\"intValue\":4,\"typeOfThing\":\"OTHER_THING\"}" +
        "]";

        final Pojo[] arrayOfPojos = new Pojo[] {
            Pojo.of("84f6ff48-80bc-442c-8b67-00aa9e526beb", "Pojo one", 1, Pojo.TypeOfThing.SOME_THING.toString()).orElseThrow(),
            Pojo.of("26f974dd-8762-4e7b-b5f7-7fd6c14e4de8", "Pojo two", 2, Pojo.TypeOfThing.THIS_THING.toString()).orElseThrow(),
            Pojo.of("a986347e-6925-4a0e-8625-29eba7ff6a91", "Pojo three", 3, Pojo.TypeOfThing.THAT_THING.toString()).orElseThrow(),
            Pojo.of("b61d92ba-6b9e-4dbb-afb8-5f84ffad34d6", "Pojo four", 4, Pojo.TypeOfThing.OTHER_THING.toString()).orElseThrow()
        };

        final List<Pojo> listOfPojos = Arrays.asList(arrayOfPojos);

        final String actual = mapper.writeValueAsString(listOfPojos);

        assertEquals("Json values should match", expected, actual);

    }

    /**
     * Test deserialise json to an array of Pojos.
     */
    @Test
    public void shouldDeserializeAnArrayOfPojos() throws JsonParseException, JsonMappingException, IOException {

        final Pojo[] expected = new Pojo[] {
            Pojo.of("84f6ff48-80bc-442c-8b67-00aa9e526beb", "Pojo one", 1, Pojo.TypeOfThing.SOME_THING.toString()).orElseThrow(),
            Pojo.of("26f974dd-8762-4e7b-b5f7-7fd6c14e4de8", "Pojo two", 2, Pojo.TypeOfThing.THIS_THING.toString()).orElseThrow(),
            Pojo.of("a986347e-6925-4a0e-8625-29eba7ff6a91", "Pojo three", 3, Pojo.TypeOfThing.THAT_THING.toString()).orElseThrow(),
            Pojo.of("b61d92ba-6b9e-4dbb-afb8-5f84ffad34d6", "Pojo four", 4, Pojo.TypeOfThing.OTHER_THING.toString()).orElseThrow()
        };

        final String json = "[" +
            "{\"id\":\"84f6ff48-80bc-442c-8b67-00aa9e526beb\",\"name\":\"Pojo one\",\"intValue\":1,\"typeOfThing\":\"SOME_THING\"}," +
            "{\"id\":\"26f974dd-8762-4e7b-b5f7-7fd6c14e4de8\",\"name\":\"Pojo two\",\"intValue\":2,\"typeOfThing\":\"THIS_THING\"}," +
            "{\"id\":\"a986347e-6925-4a0e-8625-29eba7ff6a91\",\"name\":\"Pojo three\",\"intValue\":3,\"typeOfThing\":\"THAT_THING\"}," +
            "{\"id\":\"b61d92ba-6b9e-4dbb-afb8-5f84ffad34d6\",\"name\":\"Pojo four\",\"intValue\":4,\"typeOfThing\":\"OTHER_THING\"}" +
        "]";

        final Pojo[] actual = mapper.readValue(json, Pojo[].class);

        assertArrayEquals("Array values should match", expected, actual);

    }

    /**
     * Test deserialise json to a list of Pojos.
     */
    @Test
    public void shouldDeserializeAListOfPojos() throws JsonParseException, JsonMappingException, IOException {

        final Pojo[] arrayOfPojos = new Pojo[] {
            Pojo.of("84f6ff48-80bc-442c-8b67-00aa9e526beb", "Pojo one", 1, Pojo.TypeOfThing.SOME_THING.toString()).orElseThrow(),
            Pojo.of("26f974dd-8762-4e7b-b5f7-7fd6c14e4de8", "Pojo two", 2, Pojo.TypeOfThing.THIS_THING.toString()).orElseThrow(),
            Pojo.of("a986347e-6925-4a0e-8625-29eba7ff6a91", "Pojo three", 3, Pojo.TypeOfThing.THAT_THING.toString()).orElseThrow(),
            Pojo.of("b61d92ba-6b9e-4dbb-afb8-5f84ffad34d6", "Pojo four", 4, Pojo.TypeOfThing.OTHER_THING.toString()).orElseThrow()
        };

        final List<Pojo> expected = Arrays.asList(arrayOfPojos);

        final String json = "[" +
            "{\"id\":\"84f6ff48-80bc-442c-8b67-00aa9e526beb\",\"name\":\"Pojo one\",\"intValue\":1,\"typeOfThing\":\"SOME_THING\"}," +
            "{\"id\":\"26f974dd-8762-4e7b-b5f7-7fd6c14e4de8\",\"name\":\"Pojo two\",\"intValue\":2,\"typeOfThing\":\"THIS_THING\"}," +
            "{\"id\":\"a986347e-6925-4a0e-8625-29eba7ff6a91\",\"name\":\"Pojo three\",\"intValue\":3,\"typeOfThing\":\"THAT_THING\"}," +
            "{\"id\":\"b61d92ba-6b9e-4dbb-afb8-5f84ffad34d6\",\"name\":\"Pojo four\",\"intValue\":4,\"typeOfThing\":\"OTHER_THING\"}" +
        "]";

        final List<Pojo> actual = mapper.readValue(json, new TypeReference<List<Pojo>>() {});

        assertEquals("List values should match", expected, actual);
        assertTrue("List values should be equal", actual.equals(expected));
        assertArrayEquals("Array values should match", expected.toArray(), actual.toArray());

    }

    /**
     * Test serialise an array of Pojos to json as a byte array.
     */
    @Test
    public void shouldSerializeAnArrayOfPojosToByteArray() throws JsonProcessingException {

        final String expected = "[" +
            "{\"id\":\"84f6ff48-80bc-442c-8b67-00aa9e526beb\",\"name\":\"Pojo one\",\"intValue\":1,\"typeOfThing\":\"SOME_THING\"}," +
            "{\"id\":\"26f974dd-8762-4e7b-b5f7-7fd6c14e4de8\",\"name\":\"Pojo two\",\"intValue\":2,\"typeOfThing\":\"THIS_THING\"}," +
            "{\"id\":\"a986347e-6925-4a0e-8625-29eba7ff6a91\",\"name\":\"Pojo three\",\"intValue\":3,\"typeOfThing\":\"THAT_THING\"}," +
            "{\"id\":\"b61d92ba-6b9e-4dbb-afb8-5f84ffad34d6\",\"name\":\"Pojo four\",\"intValue\":4,\"typeOfThing\":\"OTHER_THING\"}" +
        "]";

        final Pojo[] arrayOfPojos = new Pojo[] {
            Pojo.of("84f6ff48-80bc-442c-8b67-00aa9e526beb", "Pojo one", 1, Pojo.TypeOfThing.SOME_THING.toString()).orElseThrow(),
            Pojo.of("26f974dd-8762-4e7b-b5f7-7fd6c14e4de8", "Pojo two", 2, Pojo.TypeOfThing.THIS_THING.toString()).orElseThrow(),
            Pojo.of("a986347e-6925-4a0e-8625-29eba7ff6a91", "Pojo three", 3, Pojo.TypeOfThing.THAT_THING.toString()).orElseThrow(),
            Pojo.of("b61d92ba-6b9e-4dbb-afb8-5f84ffad34d6", "Pojo four", 4, Pojo.TypeOfThing.OTHER_THING.toString()).orElseThrow()
        };

        final List<Pojo> listOfPojos = Arrays.asList(arrayOfPojos);

        final byte[] actual = mapper.writeValueAsBytes(listOfPojos);

        assertArrayEquals("Byte array values should match", expected.getBytes(), actual);

    }

    /**
     * Test deserialise json as a byte array to a list of Pojos.
     */
    @Test
    public void shouldDeserializeAByteArrayToListOfPojos() throws JsonParseException, JsonMappingException, IOException {

        final Pojo[] arrayOfPojos = new Pojo[] {
            Pojo.of("84f6ff48-80bc-442c-8b67-00aa9e526beb", "Pojo one", 1, Pojo.TypeOfThing.SOME_THING.toString()).orElseThrow(),
            Pojo.of("26f974dd-8762-4e7b-b5f7-7fd6c14e4de8", "Pojo two", 2, Pojo.TypeOfThing.THIS_THING.toString()).orElseThrow(),
            Pojo.of("a986347e-6925-4a0e-8625-29eba7ff6a91", "Pojo three", 3, Pojo.TypeOfThing.THAT_THING.toString()).orElseThrow(),
            Pojo.of("b61d92ba-6b9e-4dbb-afb8-5f84ffad34d6", "Pojo four", 4, Pojo.TypeOfThing.OTHER_THING.toString()).orElseThrow()
        };

        final List<Pojo> expected = Arrays.asList(arrayOfPojos);

        final String json = "[" +
            "{\"id\":\"84f6ff48-80bc-442c-8b67-00aa9e526beb\",\"name\":\"Pojo one\",\"intValue\":1,\"typeOfThing\":\"SOME_THING\"}," +
            "{\"id\":\"26f974dd-8762-4e7b-b5f7-7fd6c14e4de8\",\"name\":\"Pojo two\",\"intValue\":2,\"typeOfThing\":\"THIS_THING\"}," +
            "{\"id\":\"a986347e-6925-4a0e-8625-29eba7ff6a91\",\"name\":\"Pojo three\",\"intValue\":3,\"typeOfThing\":\"THAT_THING\"}," +
            "{\"id\":\"b61d92ba-6b9e-4dbb-afb8-5f84ffad34d6\",\"name\":\"Pojo four\",\"intValue\":4,\"typeOfThing\":\"OTHER_THING\"}" +
        "]";

        final List<Pojo> actual = mapper.readValue(json.getBytes(), new TypeReference<List<Pojo>>() {});

        assertEquals("List values should match", expected, actual);
        assertTrue("List values should be equal", actual.equals(expected));
        assertArrayEquals("Array values should match", expected.toArray(), actual.toArray());

    }
}
