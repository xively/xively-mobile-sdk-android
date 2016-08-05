package com.xively.internal.rest;

import com.xively.XiSdkConfig;
import com.xively.internal.logger.LMILog;

import junit.framework.TestCase;

import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class XiCookieManagerTest extends TestCase {

    private Map<String, List<String>> mockResponseHeadersWithCookie;
    private Map<String, List<String>> mockResponseHeaders;
    private URI mockUri = URI.create("http://mockAddress.net");
    private final String xiAccessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpZCI6IjI0NWU2ZDJhLWY4MzItNDZkMi1iMzNmLWRlMmE5ZTNjN2MxOCIsInVzZXJJZCI6IjAzOTA1YTBmLTAwNTAtNGU4NS05NjVlLWJmOWJjYWY0ODA4MyIsImV4cGlyZXMiOjE0MzcwMzc1NTk3OTcsInJlbmV3YWxLZXkiOiJEQjkwVDNsbzF3SXA4QWVjR1RuNjB3PT0iLCJhY2NvdW50SWQiOiI1ODM5YmQ1ZS1kZDU2LTQ0ODMtYmUxMC03ZTAxMmUwOTZlYTciLCJjZXJ0IjoiMDllMDllMzYtOThiNC00NDc2LTg4OTgtZjViYjVhOTI2YTcxIn0.dXflKKIjXhsyYevrLHV4IniVLtFA83JEs9UctpSE5kqgKCjgtV_4Fu-Sr_808ZwJ1EZgOI5R2r84kGg_rHEhbsAVYCQj8SKk9Lw3qbemRDUF4Hi0yQ5dieQO8ZqXQNaBNoGzSooExSQWOQMVnblvt9Gx9cQfoUY8U77ca5ebNWEDKB8bltcsFUheNJxZZlszmuTJgGscr3Bztg4dT09ZSRmuC5T4M4ac_KQ5VY9l-hmzzgKU7ThVhdFb6v2JEQcOVHOX9JWE0VN9HGaTWePQX4Z";
    private final String xiAccessToken2= "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpZCI6ImMzYThjNDI1LTc3NDMtNGExNi04NmQwLTBiMDhkZGQ4MGFkNiIsInVzZXJJZCI6ImVlMzg2ODE1LTg1YmYtNDNjMi04MDhjLTE3NDVhMzI4Njc4ZiIsImV4cGlyZXMiOjE0NDcxNTk3OTM5NTgsInJlbmV3YWxLZXkiOiJYVm5qS2VXM3hQL29VT0dEaS8raVBBPT0iLCJyZW5ld2FsRXhwIjoxNDQ4MzU5MTA2MjU0LCJhY2NvdW50SWQiOiJiYmVhZWU4ZC0yNTkwLTRhZjAtOTY3My1hYzJmZDYxOTMwMDciLCJjb29raWVQcm9wZXJ0aWVzIjp7Im1heEFnZSI6MzE1NTY5MjU5NzQ3LCJkb21haW4iOiIuc3RhZ2UueGl2ZWx5LnVzIiwic2VjdXJlIjp0cnVlLCJodHRwT25seSI6dHJ1ZX0sImNlcnQiOiI3YTcxNzhmYS0zMmJmLTQxN2YtOTg2Ni0xZjNmYTYzZjIxNzQifQ.RG53T-PUJj3qymWyLLngcnsz5M3EYtkvup5FzORJ6EsU9caqC40S2DmnHfeSudzaU1mOP3AzFV6Glj_eIAn8YeCDe2d65ISc05iL6uLPN3iYZGYHU8YaYI_kzsedC1pk5oappXJ7nfcfX-CTOctAf5DhUqrARqzf77jRunG24YTlt6KAx-gFzxrXe48EdrqHPWSKJb8YUOWGDw_rjHWNkXA8Y_1wqegVdAp-8C1eNnUnzlr-zn0Z7ckTxZiVOleloxUTQjAdgAb-6wIMoxyJ9GKq6-HVBQYUD8nqt5flScWq45QhUo1hiSfONnoAaEV1Gi-yHv9WAOxPY1E4CHf9Gw";

    @Override
    protected void setUp() throws Exception {
        LMILog.initLog(LMILog.LogType.NONE, XiSdkConfig.LogLevel.ERROR, null);
        MockitoAnnotations.initMocks(this);

        initMockResponseHeaders();
    }

    public void testCookieManagerParsesAccessTokenFromCookieOnPut() {
        XiCookieManager testObject = new XiCookieManager();

        try {
            testObject.put(mockUri, mockResponseHeadersWithCookie);
        } catch (IOException e) {
            fail(e.toString());
        }

        assertEquals(xiAccessToken, testObject.getCurrentAccessToken());

    }

    public void testCookieManagerParsesAccessTokenOnPut() {
        XiCookieManager testObject = new XiCookieManager();

        try {
            testObject.put(mockUri, mockResponseHeaders);
        } catch (IOException e) {
            fail(e.toString());
        }

        assertEquals(xiAccessToken2, testObject.getCurrentAccessToken());

    }

    public void testAccessTokenListenerOnTokenUpdatedFromCookie() {
        XiCookieManager.AccessTokenUpdateListener mockUpdateListener =
                Mockito.mock(XiCookieManager.AccessTokenUpdateListener.class);

        XiCookieManager testObject = new XiCookieManager();
        testObject.setAccessTokenListener(mockUpdateListener);
        try {
            testObject.put(mockUri, mockResponseHeadersWithCookie);
        } catch (IOException e) {
            fail(e.toString());
        }

        verify(mockUpdateListener).onAccessTokenUpdated(eq(xiAccessToken));
    }

    public void testAccessTokenListenerOnTokenUpdated() {
        XiCookieManager.AccessTokenUpdateListener mockUpdateListener =
                Mockito.mock(XiCookieManager.AccessTokenUpdateListener.class);

        XiCookieManager testObject = new XiCookieManager();
        testObject.setAccessTokenListener(mockUpdateListener);
        try {
            testObject.put(mockUri, mockResponseHeaders);
        } catch (IOException e) {
            fail(e.toString());
        }

        verify(mockUpdateListener).onAccessTokenUpdated(eq(xiAccessToken2));
    }

    public void testAccessTokenListenerHandlesInvalidTokenValuesInCookie() {
        XiCookieManager.AccessTokenUpdateListener mockUpdateListener =
                Mockito.mock(XiCookieManager.AccessTokenUpdateListener.class);

        Map<String, List<String>> mockEmptyResponseHeaders = new HashMap<>();
        mockEmptyResponseHeaders.put("Access-Control-Allow-Credentials", new ArrayList<String>());
        mockEmptyResponseHeaders.put("Set-Cookie", new ArrayList<String>());
        mockEmptyResponseHeaders.put("Access-Control-Allow-Origin", new ArrayList<String>());

        Map<String, List<String>> mockResponseHeadersIncomplete = new HashMap<>();
        mockResponseHeadersIncomplete.put("Access-Control-Allow-Credentials", new ArrayList<String>());
        mockResponseHeadersIncomplete.put("Set-Cookie",
                new ArrayList<String>() {{
                    add("Max-Age=315569259.747");
                    add("Domain=.dev.xively.us");
                    add("Path=/");
                    add("Expires=Tue 15 Jul 2025 18:53:39 GMT");
                }});
        mockResponseHeadersIncomplete.put("Access-Control-Allow-Origin", new ArrayList<String>());

        XiCookieManager testObject = new XiCookieManager();
        testObject.setAccessTokenListener(mockUpdateListener);
        try {
            testObject.put(mockUri, mockResponseHeadersWithCookie);
            testObject.put(mockUri, mockEmptyResponseHeaders);
            testObject.put(mockUri, mockResponseHeadersIncomplete);
        } catch (IOException e) {
            fail(e.toString());
        }

        assertEquals(xiAccessToken, testObject.getCurrentAccessToken());
        verify(mockUpdateListener, times(1)).onAccessTokenUpdated(anyString());
    }

    public void testAccessTokenListenerHandlesInvalidTokenValues() {
        XiCookieManager.AccessTokenUpdateListener mockUpdateListener =
                Mockito.mock(XiCookieManager.AccessTokenUpdateListener.class);

        Map<String, List<String>> mockEmptyResponseHeaders = new HashMap<>();
        mockEmptyResponseHeaders.put("Access-Control-Allow-Credentials", new ArrayList<String>());
        mockEmptyResponseHeaders.put("Set-Cookie", new ArrayList<String>());
        mockEmptyResponseHeaders.put("Access-Control-Allow-Origin", new ArrayList<String>());

        Map<String, List<String>> mockResponseHeadersIncomplete = new HashMap<>();
        mockResponseHeadersIncomplete.put("Access-Control-Allow-Credentials", new ArrayList<String>());
        mockResponseHeadersIncomplete.put(xiAccessToken, null);
        mockResponseHeadersIncomplete.put("Access-Control-Allow-Origin", new ArrayList<String>());

        XiCookieManager testObject = new XiCookieManager();
        testObject.setAccessTokenListener(mockUpdateListener);
        try {
            testObject.put(mockUri, mockResponseHeadersWithCookie);
            testObject.put(mockUri, mockEmptyResponseHeaders);
            testObject.put(mockUri, mockResponseHeadersIncomplete);
        } catch (IOException e) {
            fail(e.toString());
        }

        assertEquals(xiAccessToken, testObject.getCurrentAccessToken());
        verify(mockUpdateListener, times(1)).onAccessTokenUpdated(anyString());
    }

    public void testAccessTokenListenerHandlesNullHeader() {
        XiCookieManager.AccessTokenUpdateListener mockUpdateListener =
                Mockito.mock(XiCookieManager.AccessTokenUpdateListener.class);

        XiCookieManager testObject = new XiCookieManager();
        testObject.setAccessTokenListener(mockUpdateListener);

        try {
            testObject.put(mockUri, null);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }

        verify(mockUpdateListener, never()).onAccessTokenUpdated(anyString());
    }

    public void testAccessTokenListenerHandlesEmptyHeader() {
        XiCookieManager.AccessTokenUpdateListener mockUpdateListener =
                Mockito.mock(XiCookieManager.AccessTokenUpdateListener.class);

        XiCookieManager testObject = new XiCookieManager();
        testObject.setAccessTokenListener(mockUpdateListener);

        Map<String, List<String>> mockResponseHeadersEmpty = new HashMap<>();

        try {
            testObject.put(mockUri, mockResponseHeadersEmpty);
        } catch (IOException e) {
            fail(e.toString());
        }

        verify(mockUpdateListener, never()).onAccessTokenUpdated(anyString());
    }

    private void initMockResponseHeaders() {
        mockResponseHeadersWithCookie = new HashMap<>();

        mockResponseHeadersWithCookie.put("Access-Control-Allow-Credentials",
                new ArrayList<String>() {{
                    add("true");
                }});

        mockResponseHeadersWithCookie.put("Access-Control-Allow-Methods",
                new ArrayList<String>() {{
                    add("GET");
                    add("POST");
                    add("DELETE");
                }});

        mockResponseHeadersWithCookie.put("Access-Control-Allow-Origin",
                new ArrayList<String>() {{
                    add("undefined");
                }});

        mockResponseHeadersWithCookie.put("Cache-Control",
                new ArrayList<String>() {{
                    add("no-cache");
                    add("no-store");
                    add("must-revalidate");
                }});

        mockResponseHeadersWithCookie.put("Content-Type",
                new ArrayList<String>() {{
                    add("application/json");
                    add("charset=utf-8");
                }});

        mockResponseHeadersWithCookie.put("Set-Cookie",
                new ArrayList<String>() {{
                    add("xively-access-token=" + xiAccessToken);
                    add("Max-Age=315569259.747");
                    add("Domain=.dev.xively.us");
                    add("Path=/");
                    add("Expires=Tue 15 Jul 2025 18:53:39 GMT");
                }});

        mockResponseHeadersWithCookie.put("X-Powered-By",
                new ArrayList<String>() {{
                    add("Express");
                }});

        mockResponseHeaders = new HashMap<>();

        mockResponseHeaders.put("Access-Control-Allow-Credentials",
                new ArrayList<String>() {{
                    add("true");
                }});

        mockResponseHeaders.put("Access-Control-Allow-Methods",
                new ArrayList<String>() {{
                    add("GET");
                    add("POST");
                    add("DELETE");
                }});

        mockResponseHeaders.put("Access-Control-Allow-Origin",
                new ArrayList<String>() {{
                    add("undefined");
                }});

        mockResponseHeaders.put("Cache-Control",
                new ArrayList<String>() {{
                    add("no-cache");
                    add("no-store");
                    add("must-revalidate");
                }});

        mockResponseHeaders.put("Content-Type",
                new ArrayList<String>() {{
                    add("application/json");
                    add("charset=utf-8");
                }});

        mockResponseHeaders.put("xively-access-token",
                new ArrayList<String>() {{
                    add(xiAccessToken2);
                }});

        mockResponseHeaders.put("X-Powered-By",
                new ArrayList<String>() {{
                    add("Express");
                }});
    }

}