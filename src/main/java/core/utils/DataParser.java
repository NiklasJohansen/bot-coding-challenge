package core.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Utility class for the Google gson parser.
 *
 * @author Niklas Johansen
 */
public class DataParser
{
    private static Gson gson;

    private DataParser() {}

    private static Gson getGson()
    {
        return (gson == null) ? gson = new GsonBuilder().create() : gson;
    }

    public static <T> String parseToJSON(T dataObject)
    {
        return getGson().toJson(dataObject);
    }

    public static <T> T parseFromJSON(String jsonString, Class<T> objClass)
    {
        return getGson().fromJson(jsonString, objClass);
    }
}
