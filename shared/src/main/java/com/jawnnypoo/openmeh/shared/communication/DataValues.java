package com.jawnnypoo.openmeh.shared.communication;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * The different types of data that can be sent
 */
public class DataValues {

    public static final String DATA_PATH_MEH_RESPONSE = "/meh_response";
    public static final String DATA_KEY_MEH_RESPONSE = "response_json";
    public static final String DATA_KEY_MEH_IMAGE = "image";

    private static final SimpleDateFormat URL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public static String getDataMehPathForToday() {
        Date date = new Date();
        return URL_DATE_FORMAT.format(date);
    }
}
