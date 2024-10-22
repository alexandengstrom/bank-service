package se.liu.ida.tdp024.account.logic.impl.facade;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import se.liu.ida.tdp024.account.data.api.exceptions.ServiceConfigurationException;
import se.liu.ida.tdp024.account.logic.api.facade.ExternalResourceFacade;

public class ExternalResourceFacadeImpl<T> implements ExternalResourceFacade<T> {
    private String baseUrl;
    private ObjectMapper objectMapper;
    private Class<T> clazz;
    private TypeReference<List<T>> typeRef; 

    public ExternalResourceFacadeImpl(String baseUrl, Class<T> c, TypeReference<List<T>> typeRef) {
        this.baseUrl = baseUrl;
        this.objectMapper = new ObjectMapper();  
        this.typeRef = typeRef;
        this.clazz = c;
    }

    @Override
    public List<T> list() throws Exception {
        String response = sendGet(baseUrl);
        if (typeRef != null) {
            return objectMapper.readValue(response, typeRef);
        } else {
            throw new IllegalStateException("Something went wrong...");
        }
    }

    @Override
    public List<T> findByName(String name) throws Exception {
        String urlWithParam = baseUrl + "?name=" + name;
        String response = sendGet(urlWithParam);
        if (typeRef != null) {
            return objectMapper.readValue(response, typeRef);
        } else {
            throw new IllegalStateException("Something went wrong...");
        }
    }

    public T findById(int id) throws Exception {
        String urlWithId = baseUrl + "/" + Integer.toString(id);
        String response = sendGet(urlWithId);
        return objectMapper.readValue(response, clazz);
    }

    private String sendGet(String urlString) throws EntityNotFoundException, ServiceConfigurationException, Exception {
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) { 
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
            return content.toString();
        } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
            con.disconnect();
            throw new Exception("Resource was not found");
        } else {
            con.disconnect();
            throw new ServiceConfigurationException("Service is unavailable, try again later.");
        }
    }
}
