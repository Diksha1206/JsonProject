package com.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import com.example.Invitation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.xml.crypto.Data;

import org.json.simple.ItemList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Hello world!
 *
 */
public class App 
{

    private static List<Partner> createPartnerList(JSONArray partnerJsonArray) {

        JSONObject individualPartner = new JSONObject();
            
            List<Partner> partnerList = new ArrayList<>();
            int length = partnerJsonArray.size();
            for(int i=0; i<length; i++) {
                individualPartner = (JSONObject) partnerJsonArray.get(i);    
                Partner partner = new Partner();
                partner.setFirstName((String) individualPartner.get("firstName"));
                partner.setLasttName((String) individualPartner.get("lastName"));
                partner.setCountry((String) individualPartner.get("country"));
                partner.setEmail((String) individualPartner.get("email"));
                
                List<Date> availableDates = new ArrayList<>();
                JSONArray datesArray = new JSONArray();
                datesArray = (JSONArray) individualPartner.get("availableDates");
                int datesLength = datesArray.size();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                for(int j=0; j< datesLength; j++) {
                    Date date;
                    try {
                        date = formatter.parse((String) datesArray.get(j));
                        availableDates.add(date);
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    
                }
                partner.setAvailableDates(availableDates);                                 
                partnerList.add(partner);                        
            }
            return partnerList;
    }



    public static List<String> getDateList(Map<String, List<Partner>> countryPartnerList) {
        List<String> dateList = new ArrayList<>();

        int length = countryPartnerList.size();

        for(String country: countryPartnerList.keySet()){
            List<Partner> partnersList = countryPartnerList.get(country);

            int size = partnersList.size();

            for(int j=0; j<partnersList.size();j++) {

                Partner partner = partnersList.get(j);

                int partnerSize = partner.getAvailableDates().size();

                for(int k=0; k<partner.getAvailableDates().size()-1;k++) {
                    // getting only those dates for which the partners are available hence 619 and not 1500 records in datelist
                    Date d1 = partner.getAvailableDates().get(k);
                    Date d2 = partner.getAvailableDates().get(k+1);
                    long diffInMillies = Math.abs(d2.getTime() - d1.getTime());
                    long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

                    if(diff == 1) {
                        
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
                        String strDate = formatter.format(d1);  
                        //System.out.println("Date Format with MM/dd/yyyy : "+strDate);                                                                       
                        dateList.add(strDate);
                    }
                }
                
            }
        }
        for(int h=0; h<dateList.size();h++) {
           // System.out.println(dateList.get(h));
        }
        return dateList;
}

  

    private static  Map<String, List<Partner>> createCountrywisePartners(List<Partner> listOfPartners) {
        Map<String, List<Partner>> countryPartners = new HashMap<>();
        List<Partner> part = new ArrayList<>();
        for(int i=0; i<listOfPartners.size(); i++) {
            
            Partner partner = listOfPartners.get(i);
            String country = partner.getCountry();        

            if(countryPartners.containsKey(partner.getCountry())) {
                part.add(partner);
                countryPartners.put(country, part);
            } else {
                part = new ArrayList<>();
                part.add(partner);
                countryPartners.put(country, part);
            }
        }
        return countryPartners;
    }




        
        
        public static List<String>  getListOfDatesWithMaximumCount(Map<String, Integer> availableDateFrequqnecy, 
        Map<String, List<Partner>> countryPartners) {

               // getting the maximum date count from the MAP
        int maxDateCount = Collections.max(availableDateFrequqnecy.values());

       // System.out.println("The maximum count is " + maxDateCount);

        // get List of Dates that has maximum count
        List<String> maxCountDateList = new ArrayList<>();
        Map<String, String> countryDateMap = new HashMap<>();

        for(String mapDate: availableDateFrequqnecy.keySet()) {
            if(availableDateFrequqnecy.get(mapDate) == maxDateCount) {
                maxCountDateList.add(mapDate);
            }
        } 
        return maxCountDateList;
    }


    public static Map<String, String> getFinalCountryAndDateMapping(List<String> maxCountDateList, Map<String,
     List<Partner>> countryPartners) {

        Collections.sort(maxCountDateList);
        Map<String, String> countryDateMapping = new HashMap<>();

        for(String country: countryPartners.keySet()) {            
           // System.out.println(country);
            if(maxCountDateList !=null && maxCountDateList.size() >0) {
                countryDateMapping.put(country, maxCountDateList.get(0));
            } else {
                countryDateMapping.put(country, null);
            }
        } 

        for (Map.Entry<String,String> entry : countryDateMapping.entrySet()){
           // System.out.println("Key = " + entry.getKey() +
                        //     ", Value = " + entry.getValue());
        }

        return countryDateMapping;
    }



    private static Map<String, Map<String, Integer>> getCountryWiseMaximumDates(Map<String, List<Partner>> countryPartnerMap) {

        Map<String, Integer> dateFrequqnecy;

        Map<String, Map<String, Integer>> countryWiseDateFrequency = new HashMap<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); 

        for(String country: countryPartnerMap.keySet()) {

            List<Partner> partnersList = new ArrayList<>();
            partnersList = countryPartnerMap.get(country);
            dateFrequqnecy = new LinkedHashMap<>();

            for(int partnerCount= 0; partnerCount < partnersList.size(); partnerCount++) {

                Partner partner = new Partner();
                 partner = partnersList.get(partnerCount);
                List<Date> availableDates = new ArrayList<>();
                availableDates = partner.getAvailableDates();

                for(int dateCount=0; dateCount <availableDates.size(); dateCount++) {

                    Date date = availableDates.get(dateCount);                                        
                    String strDate = formatter.format(date); 

                    if(dateFrequqnecy.containsKey(strDate)) {
                        dateFrequqnecy.put(strDate, dateFrequqnecy.get(strDate)+1);    
                    } else {
                        dateFrequqnecy.put(strDate, 1);
                    }                     
                }
            }
            countryWiseDateFrequency.put(country, dateFrequqnecy);
            //System.out.println(countryWiseDateFrequency);
        }
        return countryWiseDateFrequency;
    }



    private static Map<String, Integer> getDuplicateDateCountMap(List<String> allDates) {
        Map<String, Integer> dateFrequency = new HashMap<>();
        int allDatesSize = allDates.size();
        for(int count =0; count < allDates.size(); count++) {
            if(dateFrequency.containsKey(allDates.get(count))) {
                dateFrequency.put(allDates.get(count), (dateFrequency.get(allDates.get(count))) +1);
            } else {
                dateFrequency.put(allDates.get(count), 1);
            }
        }

        Iterator hmIterator = dateFrequency.entrySet().iterator();
  
        while (hmIterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry)hmIterator.next();
            int marks = ((int)mapElement.getValue());
          //  System.out.println(mapElement.getKey());
         //   System.out.println(marks);
        }
        return dateFrequency;
    } 

    

    public static void main( String[] args )
    {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader("/home/diksha/Desktop/Jsonproject/jsonData.json"));
            JSONObject jsonObject = (JSONObject) obj;    
            JSONObject jsonObjectResult = new JSONObject();  
            
            

            // Result convert POJO to JSON string
            Invitation invitation = new Invitation();
            invitation.setName("Diksha");
            invitation.setStartDate("today");
            invitation.setAttendeeCount(20);


            Invitation invitation1 = new Invitation();
            invitation1.setName("Diksha");
            invitation1.setStartDate("today");
            invitation1.setAttendeeCount(20);


            List<Invitation> listInvitation = new ArrayList<>();
            listInvitation.add(invitation);
            listInvitation.add(invitation1);

         
          

            ObjectMapper mapper = new ObjectMapper();
            JSONParser jsonParser = new JSONParser();
            String json= new String();
            JSONArray jsonArrayResult = new JSONArray();
            for(int i=0; i<listInvitation.size(); i++) {
                json = mapper.writeValueAsString(listInvitation.get(i));
                //System.out.println(json);
                jsonArrayResult.add(jsonParser.parse(json));
            }

            
            //System.out.println("The result of json Array is  "+jsonArrayResult);
            String jsonInputString = jsonArrayResult.toJSONString();
            //System.out.println(jsonInputString);



            // Send data to server using POST

            URL url = new URL ("https://reqres.in/api/users");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);			
            }



           







                   
           
            JSONArray jsonArray = (JSONArray) jsonObject.get("partners");            
            //System.out.println("The length is "+ length);
            List<Partner> listOfPartners = createPartnerList(jsonArray);            
            // Just date operation to check subtraction works fine
            dateOperations(listOfPartners);

            // Sorting the json data as: Country -> List of partner belonging to that country
             Map<String, List<Partner>> countryWisePartners = createCountrywisePartners(listOfPartners);

             // Creating a list of all dates of all partners from all countries
             List<String> allDates = getDateList(countryWisePartners);

             // getting the date and its frequency in a Map. 
             Map<String, Integer> availableDateFrequqnecy = getDuplicateDateCountMap(allDates);  

             // Finding the date that has maximum frequency and then find all dates that have this maximum frequency,
             List<String>  datesWithMaxDateCount = getListOfDatesWithMaximumCount(availableDateFrequqnecy, countryWisePartners);


             // After finding the dates with maximum frequency , sort the date and set minimum date to all the countries.
             Map<String, String> finalCountryAndDatesMapping = getFinalCountryAndDateMapping(datesWithMaxDateCount, countryWisePartners);
             
             

            // COUNTRY WISE VARIATION



             // Variation to find out the date frequqncy for each country
             Map<String, Map<String, Integer>> countryWiseDateFrequency = getCountryWiseMaximumDates(countryWisePartners); 
           //  System.out.println(countryWiseDateFrequency);

             getCountryWiseMaximumFrequencyDate(countryWiseDateFrequency);

        } catch (Exception e) {
            System.out.println(e);
        }
        
        
    }

    private static Map<String, Map<String, Integer>> getCountryWiseMaximumFrequencyDate(Map<String, Map<String, Integer>> countryWiseDateFrequency) {
        int maxCount=0;
        String date= new String();
        Map<String, Map<String, Integer>> countryFrequqency = new HashMap<>();
        Map<String, Integer> maxDateFrequency;

        for(String country: countryWiseDateFrequency.keySet()) {
            Map<String, Integer> dateCount = countryWiseDateFrequency.get(country);
            maxDateFrequency = new HashMap<>();
            for(Map.Entry<String, Integer> entry: dateCount.entrySet()) {
                maxCount = Math.max(maxCount, entry.getValue());
                date = entry.getKey();
            }
            maxDateFrequency.put(date, maxCount);
            countryFrequqency.put(country, maxDateFrequency);
            maxCount = 0;
        }
       // System.out.println(countryFrequqency);
        return countryFrequqency;
    }



    public static void dateOperations(List<Partner> partnersList) {
        for(int i=0; i<partnersList.size(); i++) {
            Partner partner = partnersList.get(i);
            int size = partner.getAvailableDates().size();
           // System.out.println();
            for(int j=0; j< size-1; j++) {
                Date d1 = partner.getAvailableDates().get(j);
                Date d2 = partner.getAvailableDates().get(j+1);
                long diffInMillies = Math.abs(d2.getTime() - d1.getTime());
                long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                //System.out.print(diff);
            }
        }                
    }
}
