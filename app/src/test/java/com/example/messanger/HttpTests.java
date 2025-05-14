package com.example.messanger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import java.util.AbstractMap;
import java.util.Map;

public class HttpTests {

    @Test
    public void HTTP_checkRegisterApiCall() {
        HttpClientManager httpClient = new HttpClientManager();
        assertEquals(true, httpClient.isConnected());
        String testingUsername = "test_" + System.currentTimeMillis();
        System.out.println("username: " + testingUsername);
        AbstractMap.SimpleEntry<Boolean, String> loggedUser = httpClient.register(testingUsername, testingUsername);
        assertEquals(false, loggedUser.getKey());
        assertEquals(testingUsername, loggedUser.getValue());
    }
    @Test
    public void HTTP_nullPasswordWhileRegisterApiCall() {
        HttpClientManager httpClient = new HttpClientManager();
        String testingCredential = "test_" + System.currentTimeMillis();
        AbstractMap.SimpleEntry<Boolean, String> loggedUser = httpClient.register(testingCredential, "");
        assertEquals(true, loggedUser.getKey());
    }

    @Test
    public void HTTP_nullUsernameWhileRegisterApiCall() {
        HttpClientManager httpClient = new HttpClientManager();
        String testingCredential = "test_" + System.currentTimeMillis();
        AbstractMap.SimpleEntry<Boolean, String> loggedUser = httpClient.register("", testingCredential);
        assertEquals(true, loggedUser.getKey());
    }

    @Test
    public void HTTP_checkLoginApiCall(){
        HttpClientManager httpClient = new HttpClientManager();
        String testingCredential = "test2";
        AbstractMap.SimpleEntry<String, User> res = httpClient.login(testingCredential, testingCredential);
        assertEquals("", res.getKey());
    }

    @Test
    public void HTTP_checkLoginUserThatDoesNotExistApiCall(){
        HttpClientManager httpClient = new HttpClientManager();
        String testingCredential = "test_" + System.currentTimeMillis();
        AbstractMap.SimpleEntry<String, User> res = httpClient.login(testingCredential, testingCredential);
        assertFalse(res.getKey().isEmpty());
        assertNull(res.getValue());
    }

    @Test
    public void HTTP_checkOkToken() {
        HttpClientManager httpClient = new HttpClientManager();
        String testingCredential = "test_" + System.currentTimeMillis();
        AuthManager auth = new AuthManager(httpClient);
        auth.register(testingCredential, testingCredential);
        auth.login(testingCredential, testingCredential);
        String err = httpClient.checkAuthorizationToken(auth.getUser().token);
        assertEquals("", err);
    }

    @Test
    public void HTTP_checkNotOkToken() {
        HttpClientManager httpClient = new HttpClientManager();
        String err = httpClient.checkAuthorizationToken("Bad Token");
        assertNotEquals("", err);
    }

    @Test
    public void HTTP_checkGetUsernameByIdANDGetUserIdByUsername() {
        HttpClientManager httpClient = new HttpClientManager();
        String testingCredential = "test_" + System.currentTimeMillis();
        AuthManager auth = new AuthManager(httpClient);
        auth.register(testingCredential, testingCredential);
        assertEquals(
                testingCredential,
                httpClient.getUsernameById(httpClient.getUserIdByUsername(testingCredential))
        );
    }

    @Test
    public void HTTP_checkGetAllUsers() {
        HttpClientManager httpClient = new HttpClientManager();
        Map<String, String> usrs = httpClient.getAllUsers();
        System.out.println(usrs.toString());
        assertNotNull(usrs);
    }
}
