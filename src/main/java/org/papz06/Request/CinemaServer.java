package org.papz06.Request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.papz06.Controllers.CinemaController;
import org.json.JSONObject;
import org.papz06.KeyValue;
import org.papz06.Models.Cinema;
import org.papz06.Utils;

import java.util.*;

public class CinemaServer {

    public KeyValue<Integer, String> CinemaList() {
        /** GET
         Returns list of cinemas managed by the user.
         **/
        CinemaController cinControl = new CinemaController();
        JSONArray result = new JSONArray();
        JSONArray data = cinControl.getCinemaData();
        if (cinControl.isEmptyList()) {
            result.put(new JSONObject().put("error", "Permission denied.."));
            return new KeyValue<Integer, String>(403, result.toString());
        }
        result = data;
        return new KeyValue<>(200, result.toString());
    }



    public KeyValue<Integer, String> CinemaCreate(String requestBody) {
        /** POST
         Creates new cinema.
         **/
        Map<String, String> retMap = Utils.getValueFromRequest(requestBody);
        String newCinemaName = retMap.get("name");

        CinemaController cinControl = new CinemaController();
        JSONArray result = new JSONArray();
        JSONArray data = cinControl.insertNewCinema(newCinemaName);
        if (cinControl.isEmptyList()) {
            result.put(new JSONObject().put("error", "Permission denied.."));
            return new KeyValue<Integer, String>(403, result.toString());
        }
        else if (cinControl.checkExist(newCinemaName)){
            result.put(new JSONObject().put("error", "BAD_REQUEST, already exists"));
            return new KeyValue<Integer, String>(400, result.toString());
        }
        else if (!cinControl.sizeNewNameCinema(newCinemaName)){
            result.put(new JSONObject().put("error", "BAD_REQUEST, name is empty"));
            return new KeyValue<Integer, String>(400, result.toString());
        }
        result = data;
        return new KeyValue<Integer, String>(200, result.toString());
    }

    public KeyValue<Integer, String> CinemaDetails(Integer id) {
        /** GET
         Returns cinema details.
         **/
        CinemaController cinControl = new CinemaController();
        JSONArray result = new JSONArray();
        JSONArray data = cinControl.getCinemaById(id);
        if (cinControl.isEmptyList()) {
            result.put(new JSONObject().put("error", "Permission denied"));
            return new KeyValue<Integer, String>(403, result.toString());
        }
        else if (!cinControl.checkExist(id)){
            result.put(new JSONObject().put("error", "NOT_FOUND"));
            return new KeyValue<Integer, String>(404, result.toString());
        }
        result = data;
        return new KeyValue<Integer, String>(200, result.toString());
    }

    public KeyValue<Integer, String> CinemaUpdate(Integer id, String requestBody) {
        /**
         * PATCH
         * Update name of cinema
         */
        Map<String, String> retMap = Utils.getValueFromRequest(requestBody);
        String newCinemaName = retMap.get("name");
        CinemaController cinControl = new CinemaController();
        JSONArray result = new JSONArray();
        JSONArray data = cinControl.updateCinemaName(id, newCinemaName);
        if (cinControl.checkExist(newCinemaName)){
            result.put(new JSONObject().put("error", "Bad Request"));
            return new KeyValue<Integer, String>(400, result.toString());
        }
        else if (cinControl.isEmptyList()) {
            result.put(new JSONObject().put("error", "Permission denied"));
            return new KeyValue<Integer, String>(403, result.toString());
        }
        else if (!cinControl.checkExist(id)){
            result.put(new JSONObject().put("error", "NOT_FOUND"));
            return new KeyValue<Integer, String>(404, result.toString());
        }
        result = data;
        return new KeyValue<Integer, String>(200, result.toString());
    }

    public KeyValue<Integer, String> CinemaDelete(Integer id) {
        return null;
    }

}

