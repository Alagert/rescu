/**
 * Copyright (C) 2012 - 2013 Xeiam LLC http://xeiam.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.xeiam.xchange.rest;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import com.xeiam.xchange.dto.DummyAccountInfo;
import com.xeiam.xchange.dto.DummyTicker;
import org.testng.annotations.Test;

/**
 * Test class for testing HttpTemplate methods
 */
public class HttpTemplateTest {

    @Test
    public void testGetForJsonObject() throws Exception {

        // Configure to use the example JSON objects
        final HttpURLConnection mockHttpURLConnection = configureMockHttpURLConnectionForGet("/example-ticker.json");

        // Provide a mocked out HttpURLConnection
        HttpTemplate testObject = new MockHttpTemplate(mockHttpURLConnection);

        // Perform the test

        DummyTicker ticker = testObject.executeRequest("http://example.com/ticker", DummyTicker.class, null, new HashMap<String, String>(), HttpMethod.GET, null);

        // Verify the results
        assertEquals(34567L, ticker.getVolume());
    }

    @Test
    public void testReadInputStreamAsEncodedString() throws Exception {

        HttpTemplate testObject = new HttpTemplate();

        InputStream inputStream = HttpTemplateTest.class.getResourceAsStream("/example-httpdata.txt");
        assertEquals("Test data", testObject.readInputStreamAsEncodedString(inputStream, "UTF-8"));
    }

    @Test
    public void testPostForJsonObject() throws Exception {

        // Configure to use the example JSON objects
        final HttpURLConnection mockHttpURLConnection = configureMockHttpURLConnectionForPost("/example-accountinfo-data.json");

        // Configure the test object (overridden methods are tested elsewhere)
        HttpTemplate testObject = new MockHttpTemplate(mockHttpURLConnection);

        DummyAccountInfo accountInfo = testObject.executeRequest("http://example.org/accountinfo", DummyAccountInfo.class, "Example", new HashMap<String, String>(), HttpMethod.POST, null);

        assertEquals("test", accountInfo.getUsername());

    }

    /**
     * Mocking HttpURLConnection through JMockit leads to problems with URL constructors that introduce very complex workarounds. In the interests of simplicity an implementation approach is used.
     *
     * @param resourcePath A classpath resource for the input stream to use in the response
     * @return A mock HttpURLConnection
     * @throws MalformedURLException If something goes wrong
     */
    private HttpURLConnection configureMockHttpURLConnectionForPost(final String resourcePath) throws MalformedURLException {

        return new HttpURLConnection(new URL("http://example.org")) {

            @Override
            public void disconnect() {

            }

            @Override
            public boolean usingProxy() {

                return false;
            }

            @Override
            public void connect() throws IOException {

            }

            @Override
            public InputStream getInputStream() throws IOException {

                return HttpTemplateTest.class.getResourceAsStream(resourcePath);
            }

            @Override
            public OutputStream getOutputStream() throws IOException {

                return new ByteArrayOutputStream();
            }

            @Override
            public String getHeaderField(String s) {

                if ("Content-Type".equalsIgnoreCase(s)) {
                    // Provide a Windows charset
                    return "application/json; charset=cp1252";
                }
                return null;
            }

        };

    }

    /**
     * Mocking HttpURLConnection through JMockit leads to problems with URL constructors that introduce very complex workarounds. In the interests of simplicity an implementation approach is used.
     *
     * @param resourcePath A classpath resource for the input stream to use in the response
     * @return A mock HttpURLConnection
     * @throws java.net.MalformedURLException If something goes wrong
     */
    public static HttpURLConnection configureMockHttpURLConnectionForGet(final String resourcePath) throws MalformedURLException {

        return new HttpURLConnection(new URL("http://example.org")) {

            @Override
            public void disconnect() {

            }

            @Override
            public boolean usingProxy() {

                return false;
            }

            @Override
            public void connect() throws IOException {

            }

            @Override
            public InputStream getInputStream() throws IOException {

                return HttpTemplateTest.class.getResourceAsStream(resourcePath);
            }

            @Override
            public String getHeaderField(String s) {

                return null;
            }

        };

    }

    private static class MockHttpTemplate extends HttpTemplate {

        private final HttpURLConnection mockHttpURLConnection;

        public MockHttpTemplate(HttpURLConnection mockHttpURLConnection) {

            this.mockHttpURLConnection = mockHttpURLConnection;
        }

        @Override
        public HttpURLConnection getHttpURLConnection(String urlString) throws IOException {

            return mockHttpURLConnection;
        }

        @Override
        protected int checkHttpStatusCode(HttpURLConnection connection) throws IOException {

            return 200;
        }
    }
}
