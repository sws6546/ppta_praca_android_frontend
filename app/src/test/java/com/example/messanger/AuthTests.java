package com.example.messanger;

import org.junit.Test;

import static org.junit.Assert.*;

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