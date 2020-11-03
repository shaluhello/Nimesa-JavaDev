package com.zaff;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.ArrayList;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Main {

    public static void main(String[] args) {
        try {

            URL url = new URL("https://samples.openweathermap.org/data/2.5/forecast/hourly?q=London,us&appid=b6907d289e10d714a6e88b30761fae22");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            //Getting the response code
            int responsecode = conn.getResponseCode();

            if (responsecode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responsecode);
            } else {

                String inline = "";
                Scanner scanner = new Scanner(url.openStream());

                //Write all the JSON data into a string using a scanner
                //int count=0;
                while (scanner.hasNext()) {
                    inline += scanner.nextLine();
                    //count++;
                }
                //System.out.println(count);
                //Close the scanner
                scanner.close();

                //Using the JSON simple library parse the string into a json object
                JSONParser parse = new JSONParser();
                JSONObject data_obj = (JSONObject) parse.parse(inline);
                String description1 = "";
                String description2 = "";
                String temp_check = "";
                JSONArray arr = (JSONArray) data_obj.get("list");

                Set<Integer> dateSet =  new HashSet<Integer>();
                ArrayList<Integer> hours = new ArrayList<Integer>();
                for (int i = 0; i < arr.size(); i++) {
                    JSONObject dateCheck = (JSONObject) arr.get(i);
                    JSONObject weatherCheck = (JSONObject) arr.get(i);
                    JSONObject mainCheck = (JSONObject) arr.get(i);

                    if (dateCheck.containsKey("dt_txt")) {
                        String dateTime = dateCheck.get("dt_txt").toString();
                        // Is the response contains 4 days of data
                        String  date = (String.valueOf(dateTime.charAt(8)) + String.valueOf(dateTime.charAt(9)));
                        dateSet.add(Integer.parseInt(date));

                        //Is all the forecast in the hourly interval ( no hour should be missed )
                        String  hour = (String.valueOf(dateTime.charAt(11)) + String.valueOf(dateTime.charAt(12)));
                        String  minutes = (String.valueOf(dateTime.charAt(14)) + String.valueOf(dateTime.charAt(15)));
                        String  seconds = (String.valueOf(dateTime.charAt(17)) + String.valueOf(dateTime.charAt(18)));
                        int intHour = Integer.parseInt(hour);
                        int intMinutes = Integer.parseInt(minutes);
                        int intSecond = Integer.parseInt(seconds);
                        hours.add(intHour);
                    }

                    //For all 4 days, the temp should not be less than temp_min and not more than temp_max
                    if (mainCheck.containsKey("main")) {
                        JSONObject mainElements = (JSONObject) mainCheck.get("main");

                        for (int z=0; z<mainElements.size(); z++) {
                            float temp_min = Float.parseFloat(mainElements.get("temp_min").toString());
                            float temp = Float.parseFloat(mainElements.get("temp").toString());
                            float temp_max = Float.parseFloat(mainElements.get("temp_max").toString());
                            if ( temp_min <= temp && temp <= temp_max) {
                                temp_check = "The temp is less than temp_min and not more than temp_max";
                            } else {
                                temp_check = "The temp is not between the temp_min and temp_max";
                            }
                            break;
                        }
                    }

                    // If the weather id is 500, the description should be light rain
                    // If the weather id is 800, the description should be a clear sky
                    if (dateCheck.containsKey("weather")) {
                        JSONArray weatherElements = (JSONArray) weatherCheck.get("weather");
                        Iterator<String> it1 = weatherElements.iterator();
                        while(it1.hasNext()) {
                            JSONObject jo = (JSONObject) weatherElements.get(0);
                            String id = jo.get("id").toString();
                            int id1 = Integer.parseInt(id);
                            if (id1 == 800) {
                                description1 = "For weather id " +jo.get("id") + " The Description is : " + jo.get("description");
                                break;
                            }else if (id1 == 500) {
                                description2 = "For weather id " +jo.get("id") + " The Description is : " + jo.get("description");
                                break;
                            }
                            break;
                        }
                    }

                }
                System.out.println("Is the response contains 4 days of data?");
                if((Collections.max(dateSet) - Collections.min(dateSet)) == 4) {
                    System.out.println("The response contains 4 days of data");
                }else {
                    System.out.println("The response does not contains 4 days of data");
                }

                System.out.println();
                System.out.println("Is all the forecast in the hourly interval ( no hour should be missed )?");
                int difference = 0;
                for (int k=0; k<hours.size(); k++){
                    int firstHour = hours.get(k);
                    int secondHour = hours.get(k+1);
                    if (firstHour == 23) {
                        secondHour = 24 - secondHour;
                    }
                    difference = secondHour - firstHour;
                    break;
                }
                if (difference == 1) {
                    System.out.println("All the forecast are in the hourly interval ");
                }else {
                    System.out.println("All the forecast are not in hourly interval ");
                }

                System.out.println();
                System.out.println("For all 4 days, is the temp less than temp_min and not more than temp_max?");
                System.out.println(temp_check);

                System.out.println();
                System.out.println("If the weather id is 800, the description should be light rain");
                System.out.println(description1);

                System.out.println();
                System.out.println("If the weather id is 500, the description should be light rain");
                System.out.println(description2);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

/*

//Get the required object from the above created object
                JSONObject obj = (JSONObject) data_obj.get("Global");

                //Get the required data using its key
                System.out.println(obj.get("TotalRecovered"));

                JSONArray arr = (JSONArray) data_obj.get("Countries");

                for (int i = 0; i < arr.size(); i++) {

                    JSONObject new_obj = (JSONObject) arr.get(i);

                    if (new_obj.get("Slug").equals("albania")) {
                        System.out.println("Total Recovered: " + new_obj.get("TotalRecovered"));
                        break;
                    }
                }
 */