package dev.javatools.maputils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.javatools.maputils.helpers.Format;
import dev.javatools.maputils.helpers.MapUtilsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


class MapUpdateTest {

    private final ClassLoader classLoader = getClass().getClassLoader();
    ObjectMapper objectMapper = new ObjectMapper();
    private Map sampleInput;

    @BeforeEach
    void setUp() throws IOException {
        Path jsonSampleInputFilePath = Path.of(classLoader.getResource("mapUpdate/sample-input.json").getPath());
        String sampleJsonInput = Files.readString(jsonSampleInputFilePath);
        sampleInput = MapCreator.create(sampleJsonInput, Format.JSON);

    }

    @Test
    void get() {

    }

    @Test
    void setTest01() {
        Map test = new HashMap<>();
        MapUpdate.set("name", test, "James Butt");
        MapUpdate.set("age", test, 26);
        assertEquals("James Butt", test.get("name"));
        assertEquals(26, test.get("age"));
    }

    @Test
    void setTest02() {
        Map test = new HashMap<>();
        MapUpdate.set("associatedAddresses[]", test, "San Ramon");
        assertEquals("San Ramon", ((List<String>) test.get("associatedAddresses")).get(0));

    }

    @Test
    void setTest03() {
        Map test = new HashMap<>();
        MapUpdate.set("associatedAddresses[].city", test, "San Ramon");
        List<Map> addresses = (List) test.get("associatedAddresses");
        Map address = addresses.get(0);
        assertEquals("San Ramon", address.get("city"));
    }

    @Test
    void setTest04() {
        Map test = new HashMap<>();
        MapUpdate.set("associatedAddresses[5].city", test, "San Ramon");
        List<Map> addresses = (List) test.get("associatedAddresses");
        Map address = addresses.get(5);
        //System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(test));
        assertEquals("San Ramon", address.get("city"));
    }

    @Test
    void setTest041() {
        Map test = new HashMap<>();
        MapUpdate.set(" associatedAddresses[].city", test, "San Ramon");
        List<Map> addresses = (List) test.get("associatedAddresses");
        Map address = addresses.get(0);
        //System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(test));
        assertEquals("San Ramon", address.get("city"));
    }

    @Test
    void setTest042() throws JsonProcessingException {
        Map test = new HashMap<>();
        MapUpdate.set("friends[].address.state.zip", test, "22873");
        List<Map> friends = (List) test.get("friends");
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(test));
        Map friend = friends.get(0);
        Map address = (Map)friend.get("address");
        Map state = (Map)address.get("state");
        String zip = (String)state.get("zip");
        assertEquals("22873", zip);
    }

    @Test
    void setTest05() {
        Map test = new HashMap<>();
        MapUpdate.set("friends[0].associatedAddresses[5].city", test, "San Ramon");
        List<Map> friends = (List) test.get("friends");
        List<Map> addresses = (List) friends.get(0).get("associatedAddresses");
        Map address = addresses.get(5);
        //System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(test));
        assertEquals("San Ramon", address.get("city"));
    }

    @Test
    void setTest06() {
        Map test = new HashMap<>();
        MapUpdate.set("friends[2].associatedAddresses[5].city", test, "San Ramon");
        List<Map> friends = (List) test.get("friends");
        List<Map> addresses = (List) friends.get(2).get("associatedAddresses");
        Map address = addresses.get(5);
        //System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(test));
        assertEquals("San Ramon", address.get("city"));
    }

    @Test
    void setTest07() {
        boolean tested = false;
        List friends = (List) sampleInput.get("friends");
        for (Object currentFriend : friends) {
            Map currentFriendMap = (Map) currentFriend;
            if (currentFriendMap.get("name").equals("Josephine Darakjy")) {
                for (Object currentAssociatedAddress : (List) currentFriendMap.get("associatedAddresses")) {
                    Map currentAssociatedAddressMap = (Map) currentAssociatedAddress;
                    if (currentAssociatedAddressMap.get("state").equals("CA")) {
                        assertEquals(currentAssociatedAddressMap.get("city"), "Los Angeles");
                        MapUpdate.set("friends[{name=Josephine Darakjy}].associatedAddresses[{state=CA}].city", sampleInput, "New California City");
                        assertEquals(currentAssociatedAddressMap.get("city"), "New California City");
                        tested = true;
                    }
                }
            }
        }
        assertTrue(tested);
    }

    @Test
    void setTest08() {
        boolean tested = false;
        List friends = (List) sampleInput.get("friends");
        for (Object currentFriend : friends) {
            Map currentFriendMap = (Map) currentFriend;
            if (currentFriendMap.get("name").equals("Lenna Paprocki")) {
                for (Object currentAssociatedAddress : (List) currentFriendMap.get("associatedAddresses")) {
                    Map currentAssociatedAddressMap = (Map) currentAssociatedAddress;
                    if (currentAssociatedAddressMap.get("state").equals("TX")) {
                        assertEquals(currentAssociatedAddressMap.get("city"), "Irving");
                        MapUpdate.set("friends[{name=Lenna Paprocki}].associatedAddresses[{state=TX}].city", sampleInput, "New Texas City");
                        assertEquals(currentAssociatedAddressMap.get("city"), "New Texas City");
                    }
                }
            }
        }
        //System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(sampleInput));
    }

    @Test
    void setTest09() {
        boolean tested = false;
        List friends = (List) sampleInput.get("friends");
        for (Object currentFriend : friends) {
            Map currentFriendMap = (Map) currentFriend;
            if (currentFriendMap.get("name").equals("Lenna Paprocki")) {
                for (Object currentAssociatedAddress : (List) currentFriendMap.get("associatedAddresses")) {
                    Map currentAssociatedAddressMap = (Map) currentAssociatedAddress;
                    if (currentAssociatedAddressMap.get("state").equals("TX") && currentAssociatedAddressMap.get("city").equals("Irving")) {
                        assertEquals(currentAssociatedAddressMap.get("street"), "618 W Yakima Ave");
                        MapUpdate.set("friends[{name=Lenna Paprocki}].associatedAddresses[{state=TX}, {city=Irving}].street", sampleInput, "street in Irving, Texas");
                        assertEquals(currentAssociatedAddressMap.get("street"), "street in Irving, Texas");
                    }
                }
            }
        }
        //System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(sampleInput));
    }

    @Test
    void setTest10() {
        MapUtilsException mapUtilsException = assertThrows(MapUtilsException.class, ()->MapUpdate.set("friends[{name=Lenna Paprocki}].associatedAddresses[{state=TX}, {city=Irving}]", sampleInput, "street in Irving, Texas"));
        assertEquals("friends[{name=Lenna Paprocki}].associatedAddresses[{state=TX}, {city=Irving}]: Found the element in this path, but to assign the value, we also need a key.", mapUtilsException.getMessage());
    }
}