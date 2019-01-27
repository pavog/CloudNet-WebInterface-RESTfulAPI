package cloud.waldiekiste.java.projekte.cloudnet.webinterface.adapter;

import com.google.gson.*;
import de.dytanic.cloudnet.lib.serverselectors.sign.SignGroupLayouts;
import de.dytanic.cloudnet.lib.serverselectors.sign.SignLayout;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SignGroupLayoutsAdapter  implements JsonSerializer<SignGroupLayouts>, JsonDeserializer<SignGroupLayouts> {
    @Override
    public SignGroupLayouts deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        String name = object.get("name").getAsString();
        ArrayList<SignLayout> layouts = new ArrayList<>();
        object.get("layouts").getAsJsonArray().forEach(t->layouts.add(jsonDeserializationContext.deserialize(t,SignLayout.class)));
        return new SignGroupLayouts(name,layouts);
    }

    @Override
    public JsonElement serialize(SignGroupLayouts signGroupLayouts, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject object = new JsonObject();
        object.addProperty("name",signGroupLayouts.getName());
        JsonArray array = new JsonArray();
        signGroupLayouts.getLayouts().forEach(t->array.add(jsonSerializationContext.serialize(t)));
        object.add("layouts", array);
        return object;
    }
}
