package se.dennisvonbargen.openlogger.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;

/**
 *
 * Created by dennis on 2016-07-25.
 */
public final class Export {

    private Export() {}

    public static String toJSONString(RawFlightLog log) {
        try {
            return new ObjectMapper().writeValueAsString(log);
        }
        catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
