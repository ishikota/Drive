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

import dagger.Module;
import dagger.Provides;
import jp.ikota.drive.data.model.Likes;
import jp.ikota.drive.data.model.Shots;
import jp.ikota.drive.network.DribbbleRxService;
import jp.ikota.drive.network.DribbleService;
import jp.ikota.drive.network.DribbleURL;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

@Module
public class DribbleApiModule{

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

    class LikesDeserializer implements JsonDeserializer<Likes> {
        @Override
        public Likes deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonArray array = json.getAsJsonArray();
            JSONObject jo = new JSONObject();
            try {
                jo.put("items", array);
            } catch (JSONException e) {
                throw new JsonParseException(e.getMessage());
            }
            String wrapped_json = "{\"items\":"+array+"}";
            return new Gson().fromJson(wrapped_json, Likes.class);
        }
    }

    @Provides @Singleton
    public DribbleService provideDribbleService() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Shots.class, new ShotsDeserializer())
                .registerTypeAdapter(Likes.class, new LikesDeserializer())
                .create();
        return new RestAdapter.Builder()
                .setEndpoint(DribbleURL.API_END_POINT)
                .setConverter(new GsonConverter(gson))
                .build()
                .create(DribbleService.class);
    }

    @Provides @Singleton
    public DribbbleRxService provideDribbbleRxService() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Shots.class, new ShotsDeserializer())
                .registerTypeAdapter(Likes.class, new LikesDeserializer())
                .create();
        return new RestAdapter.Builder()
                .setEndpoint(DribbleURL.API_END_POINT)
                .setConverter(new GsonConverter(gson))
                .build()
                .create(DribbbleRxService.class);
    }

}
