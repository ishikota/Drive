package jp.ikota.drive.di;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

import javax.inject.Singleton;

import dagger.Provides;
import jp.ikota.drive.AndroidApplication;
import jp.ikota.drive.data.model.Shots;
import jp.ikota.drive.network.DribbleService;
import jp.ikota.drive.network.DribbleURL;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

@dagger.Module(
    injects = AndroidApplication.class,
    library = true
)
public class DribbleApiModule {

    class ShotsDeserializer implements JsonDeserializer<Shots> {
        @Override
        public Shots deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonArray array = json.getAsJsonArray();
            JSONObject jo = new JSONObject();
            try {
                jo.put("items", array);
            } catch (JSONException e) {
                throw new JsonParseException(e.getMessage());
            }
            String wrapped_json = "{\"items\":"+array+"}";
            return new Gson().fromJson(wrapped_json, Shots.class);
        }
    }

    @Provides @Singleton
    public DribbleService provideDribbleService() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Shots.class, new ShotsDeserializer())
                .create();
        return new RestAdapter.Builder()
                .setEndpoint(DribbleURL.END_POINT)
                .setConverter(new GsonConverter(gson))
                .build()
                .create(DribbleService.class);
    }

}
