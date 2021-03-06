package org.papz06.Controllers;

import org.json.JSONArray;
import org.json.JSONObject;
import org.papz06.Function;
import org.papz06.Models.TicketType;

import java.sql.ResultSet;
import java.util.ArrayList;

public class TicketTypeController {

    public static ArrayList<TicketType> getAllTicketTypeList() {
        /*
        Get list of all ticket types
         */
        ArrayList<TicketType> ticketTypeList = new ArrayList<>();
        Function fc = new Function();
        ResultSet rs;
        try {
            rs = fc.executeQuery("select * from ticketTypes");
            while (rs.next()) {
                ticketTypeList.add(
                        new TicketType(rs.getInt(1),
                                rs.getString(2),
                                rs.getInt(3),
                                rs.getInt(4)));
            }
            fc.closeQuery();
        } catch (Exception e) {
            System.out.println(e);
        }
        return ticketTypeList;
    }

    public static JSONArray getTicketTypes(int cinemaId) {
        /*
        Get ticket types in certain cinema
         */
        JSONArray resultData = new JSONArray();
        Function fc = new Function();
        ResultSet rs;
        try {
            String query = String.format(
                    "select ticketType_id, name, price from ticketTypes where cinema_id = %d and available = 1",
                    cinemaId);
            rs = fc.executeQuery(query);
            while (rs.next()) {
                JSONObject ticketTypeData = new JSONObject();
                ticketTypeData.put("id", rs.getInt(1));
                ticketTypeData.put("name", rs.getString(2));
                ticketTypeData.put("price", rs.getInt(3));
                resultData.put(ticketTypeData);
            }
            fc.closeQuery();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return resultData;
    }

    public static JSONObject getTicketTypeById(int id) {
        /*
        Get ticket type data by id
         */
        JSONObject tckTypeData = new JSONObject();
        Function fc = new Function();
        ResultSet rs;
        try {
            String sqlSelect = String.format("select ticketType_id, name, price from ticketTypes " +
                    "where ticketType_id = %d and available = 1", id);
            rs = fc.executeQuery(sqlSelect);
            while (rs.next()) {
                tckTypeData.put("id", rs.getInt(1));
                tckTypeData.put("name", rs.getString(2));
                tckTypeData.put("price", rs.getInt(3));
            }
            fc.closeQuery();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return tckTypeData;
    }

    public static JSONObject insertNewTicketType(String name, int price, int cinemaId) {
        /*
        Create new ticket type
         */
        Function fc = new Function();
        ResultSet rs;
        int tckTypeID = 0;
        try {
            // insert new ticket type
            String sqlInsert = String.format("insert into TicketTypes values (default, '%s', %d, %d, default)",
                    name, price, cinemaId);
            fc.executeQuery(sqlInsert);
            // get more data of just created ticket type
            rs = fc.executeQuery(
                    "select * from TicketTypes where available = 1 order by ticketType_id desc fetch next 1 rows only");
            rs.next();
            tckTypeID = rs.getInt(1);
            fc.closeQuery();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return getTicketTypeById(tckTypeID);
    }

    public static JSONObject updateTicketType(int id, String name, int price) {
        /*
        Update existing ticket type
         */
        Function fc = new Function();
        try {
            String sqlUpdate = String.format(
                    "update TicketTypes set name = '%s', price = %d where ticketType_id = %d and available = 1",
                    name, price, id);
            fc.executeQuery(sqlUpdate);
            fc.closeQuery();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return getTicketTypeById(id);
    }

    public static JSONObject deleteTicketType(int id) {
        /*
        Delete ticket type by setting available to 0
         */
        Function fc = new Function();
        try {
            String sqlDelete = String.format("update TicketTypes set available = 0 where ticketType_id = %d", id);
            fc.executeQuery(sqlDelete);
            fc.closeQuery();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return new JSONObject();
    }

    public static boolean checkExist(String name, int cinemaId) {
        // check whether ticket type exists in certain cinema
        for (TicketType tckType : getAllTicketTypeList()) {
            if (tckType.getCinemaId().equals(cinemaId))
                if (tckType.getName().equals(name))
                    return true;
        }
        return false;
    }

    public static boolean notExist(int id) {
        // check whether ticket type exists by id
        for (TicketType tckType : getAllTicketTypeList()) {
            if (tckType.getId().equals(id)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNameEmpty(String newName) {
        return newName.length() == 0;
    }

    public static boolean isPriceNegative(int price) {
        return price < 0;
    }

    public static int getPriceById(int id) {
        /*
        Get price of ticket type by id
         */
        Function fc = new Function();
        ResultSet rs;
        int resPrice = 0;
        try {
            String query = String.format(
                    "select price from ticketTypes where ticketType_id = %d and available = 1", id);
            rs = fc.executeQuery(query);
            rs.next();
            resPrice = rs.getInt(1);
            fc.closeQuery();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return resPrice;
    }

}