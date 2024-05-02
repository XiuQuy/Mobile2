//package com.example.appxemphim.data.remote;
//
//import com.google.gson.*;
//
//import java.lang.reflect.Type;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//
//public class DateTypeAdapter implements JsonDeserializer<Date>, JsonSerializer<Date> {
//    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//
//    @Override
//    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//        String dateString = json.getAsString();
//        if (dateString == null || dateString.isEmpty()) {
//            return null;
//        }
//        try {
//            return dateFormat.parse(json.getAsString());
//        } catch (ParseException e) {
//            throw new JsonParseException(e);
//        }
//    }
//
//    @Override
//    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
//        return new JsonPrimitive(dateFormat.format(src));
//    }
//}
