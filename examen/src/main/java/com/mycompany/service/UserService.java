/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.service;

import com.mycompany.model.Address;
import com.mycompany.model.Company;
import com.mycompany.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Gregorio
 */
public class UserService {
     public static List<User> fetchUsersByName(String searchTerm) {
        List<User> users = new ArrayList<>();
       
        try {
            URL url = new URL("https://jsonplaceholder.typicode.com/users");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(6000);
            conn.setReadTimeout(6000);

            int status = conn.getResponseCode();
            if (status != 200) {
                System.err.println("Error: código HTTP " + status);
                return users; // Lista vacía
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                response.append(line);
            reader.close();

            JSONArray array = new JSONArray(response.toString());

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String name = obj.getString("name");

                if (name.toLowerCase().contains(searchTerm.toLowerCase())) {
                    String email = obj.getString("email");
                    String phone = obj.getString("phone");

                    JSONObject addressObj = obj.getJSONObject("address");
                    String street = addressObj.getString("street");
                    String city = addressObj.getString("city");
                    Address address = new Address(street, city);

                    JSONObject companyObj = obj.getJSONObject("company");
                    String companyName = companyObj.getString("name");
                    String companyCatchPhrase = companyObj.getString("catchPhrase");
                    String companyBs = companyObj.getString("bs");
                    Company company = new Company(companyName, companyCatchPhrase, companyBs);

                    User user = new User(name, email, phone, address, company);
                    users.add(user);
                }
            }
            
            saveToLocalFile(users);
        } catch (java.net.SocketTimeoutException e) {
            System.err.println("Error: Tiempo de espera agotado (timeout).");
        } catch (java.io.IOException e) {
            System.err.println("Error de red o conexión: " + e.getMessage());
        } catch (org.json.JSONException e) {
            System.err.println("Error al procesar JSON: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
        }
        return users;
    }
     
     public static void saveToLocalFile(List<User> users) {
        JSONArray jsonArray = new JSONArray();

        for (User user : users) {
            JSONObject obj = new JSONObject();
            obj.put("name", user.getName());
            obj.put("email", user.getEmail());
            obj.put("phone", user.getPhone());

            JSONObject address = new JSONObject();
            address.put("street", user.getAddress().getStreet());
            address.put("city", user.getAddress().getCity());
            obj.put("address", address);

            JSONObject company = new JSONObject();
            company.put("name", user.getCompany().getName());
            company.put("catchPhrase", user.getCompany().getName());
            company.put("bs", user.getCompany().getName());
            
            obj.put("company", company);

            jsonArray.put(obj);
        }

        try (FileWriter file = new FileWriter("cache.json")) {
            file.write(jsonArray.toString(4));
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
     public static List<User> loadFromLocalFile() {
        List<User> users = new ArrayList<>();
        File file = new File("cache.json");

        if (!file.exists()) return users;

        try (FileReader reader = new FileReader(file)) {
            StringBuilder sb = new StringBuilder();
            int ch;
            while ((ch = reader.read()) != -1)
                sb.append((char) ch);

            JSONArray array = new JSONArray(sb.toString());

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String name = obj.getString("name");
                String email = obj.getString("email");
                String phone = obj.getString("phone");

                JSONObject addr = obj.getJSONObject("address");
                Address address = new Address(addr.getString("street"), addr.getString("city"));

                JSONObject comp = obj.getJSONObject("company");
                Company company = new Company(comp.getString("name"),comp.getString("catchPhrase"),comp.getString("bs"));

                users.add(new User(name, email, phone, address, company));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }
}
