package com.example.messanger;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.AbstractMap.SimpleEntry;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class AuthTests {

    @Test
    public void checkConnectionWithApi() {
        HttpClientManager httpClient = new HttpClientManager();
        assertEquals(true, httpClient.isConnected());
    }

    @Test
    public void HTTP_checkRegisterApiCall() {
        HttpClientManager httpClient = new HttpClientManager();
        assertEquals(true, httpClient.isConnected());
        String testingUsername = "test_" + System.currentTimeMillis();
        System.out.println("username: " + testingUsername);
        SimpleEntry<Boolean, String> loggedUser = httpClient.register(testingUsername, testingUsername);
        assertEquals(false, loggedUser.getKey());
        assertEquals(testingUsername, loggedUser.getValue());
    }
    @Test
    public void HTTP_nullPasswordWhileRegisterApiCall() {
        HttpClientManager httpClient = new HttpClientManager();
        String testingCredential = "test_" + System.currentTimeMillis();
        SimpleEntry<Boolean, String> loggedUser = httpClient.register(testingCredential, "");
        assertEquals(true, loggedUser.getKey());
    }

    @Test
    public void HTTP_nullUsernameWhileRegisterApiCall() {
        HttpClientManager httpClient = new HttpClientManager();
        String testingCredential = "test_" + System.currentTimeMillis();
        SimpleEntry<Boolean, String> loggedUser = httpClient.register("", testingCredential);
        assertEquals(true, loggedUser.getKey());
    }

    @Test
    public void HTTP_checkLoginApiCall(){
        HttpClientManager httpClient = new HttpClientManager();
        String testingCredential = "test2";
        SimpleEntry<String, User> res = httpClient.login(testingCredential, testingCredential);
        assertEquals("", res.getKey());
    }

    @Test
    public void HTTP_checkLoginUserThatDoesNotExistApiCall(){
        HttpClientManager httpClient = new HttpClientManager();
        String testingCredential = "test_" + System.currentTimeMillis();
        SimpleEntry<String, User> res = httpClient.login(testingCredential, testingCredential);
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
    public void AuthManager_CheckLogin() {
        String testingCredential = "test2";
        AuthManager auth = new AuthManager(new HttpClientManager());
        String err = auth.login(testingCredential, testingCredential);
        assertEquals("", err);
        assertNotNull(auth.getUser().username);
        assertNotNull(auth.getUser().token);
        assertTrue(auth.isUserLogged());
    }

    @Test
    public void AuthManager_CheckLoginWithUserThatDoesNotExist() {
        String testingCredential = "test_" + System.currentTimeMillis();
        AuthManager auth = new AuthManager(new HttpClientManager());
        String err = auth.login(testingCredential, testingCredential);
        assertNotEquals("", err);
        assertNull(auth.getUser().username);
        assertNull(auth.getUser().token);
        assertFalse(auth.isUserLogged());
    }

    @Test
    public void AuthManager_CheckLoginWithEmptyUsernameOrPassword() {
        String testingCredential = "";
        AuthManager auth = new AuthManager(new HttpClientManager());
        String err = auth.login(testingCredential, testingCredential);
        assertEquals("Username or password cannot be empty", err);
    }

    @Test
    public void AuthManager_CheckRegisterWithGoodCredentials() {
        String testingCredential = "test_" + System.currentTimeMillis();
        AuthManager auth = new AuthManager(new HttpClientManager());
        String err = auth.register(testingCredential, testingCredential);
        assertTrue(err.isEmpty());
    }

    @Test
    public void AuthManager_CheckEmptyUsernameAndPassword() {
        String testingCredential = "";
        AuthManager auth = new AuthManager(new HttpClientManager());
        String err = auth.register(testingCredential, testingCredential);
        assertFalse(err.isEmpty());
    }

    @Test
    public void AuthManager_CheckNullToken() {
        String err = new AuthManager(new HttpClientManager()).checkAuthToken();
        assertNotEquals("", err);
    }

    @Test
    public void AuthManager_CheckOkToken() {
        HttpClientManager httpClient = new HttpClientManager();
        AuthManager auth = new AuthManager(httpClient);
        String testingCredential = "test_" + System.currentTimeMillis();
        auth.register(testingCredential, testingCredential);
        auth.login(testingCredential, testingCredential);

        String err = auth.checkAuthToken();
        assertTrue(err.isEmpty());
    }
}