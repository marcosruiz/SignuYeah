package markens.signu;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import markens.signu.objects.User;

public class MyResultObjectAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (type.getRawType()!= User.class) return null;

        TypeAdapter<User> defaultAdapter = (TypeAdapter<User>) gson.getDelegateAdapter(this, type);
        return (TypeAdapter<T>) new MyResultObjectAdapter(defaultAdapter);
    }

    public class MyResultObjectAdapter extends TypeAdapter<User> {

        protected TypeAdapter<User> defaultAdapter;


        public MyResultObjectAdapter(TypeAdapter<User> defaultAdapter) {
            this.defaultAdapter = defaultAdapter;
        }

        @Override
        public void write(JsonWriter out, User value) throws IOException {
            defaultAdapter.write(out, value);
        }

        @Override
        public User read(JsonReader in) throws IOException {
            /*
            This is the critical part. So if the value is a string,
            Skip it (no exception) and return null.
            */
            if (in.peek() == JsonToken.STRING) {
                in.skipValue();
                return null;
            }
            return defaultAdapter.read(in);
        }
    }
}
