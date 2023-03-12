import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClientTest {
    private final TradingContainer container = new TradingContainer("trading:latest")
            .withFixedExposedPort(49152, 8080)
            .withExposedPorts(8080);

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .build();

    private String baseURL;

    @BeforeEach
    void setUp() throws IOException {
        container.start();
        baseURL = "http://" + container.getHost() + ":" + container.getMappedPort(8080);

        Request request1 = new Request.Builder()
                .url(Objects.requireNonNull(HttpUrl.parse(baseURL + "/admin/register?company=x&count=200&price=100")))
                .build();
        Request request2 = new Request.Builder()
                .url(Objects.requireNonNull(HttpUrl.parse(baseURL + "/admin/register?company=y&count=1000&price=1")))
                .build();

        try (Response response1 = client.newCall(request1).execute();
             Response response2 = client.newCall(request2).execute()) {
            assertEquals(200, response1.code());
            assertEquals(200, response2.code());
        }
    }

    @AfterEach
    void terminate() {
        container.stop();
    }

    private void register() throws IOException {
        Request request = new Request.Builder()
                .url(Objects.requireNonNull(HttpUrl.parse(baseURL + "/register?user-id=1&name=user1")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            assertEquals(200, response.code());
        }
    }

    private void buy(double value, int code) throws IOException {
        Request request1 = new Request.Builder()
                .url(Objects.requireNonNull(HttpUrl.parse(baseURL + "/buy?user-id=1&company=x&count=" + (int) (200 * value))))
                .build();
        Request request2 = new Request.Builder()
                .url(Objects.requireNonNull(HttpUrl.parse(baseURL + "/buy?user-id=1&company=y&count=" + (int) (1000 * value))))
                .build();

        try (Response response1 = client.newCall(request1).execute();
             Response response2 = client.newCall(request2).execute()) {
            assertEquals(code, response1.code());
            assertEquals(code, response2.code());
        }
    }

    @Test
    void testBuy() throws IOException {
        register();

        Request request = new Request.Builder()
                .url(Objects.requireNonNull(HttpUrl.parse(baseURL + "/change-balance?user-id=1&value=10000")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            assertEquals(200, response.code());
        }

        buy(1, 200);
    }

    @Test
    void testCanSell() throws Exception {
        register();
        get("/change-balance?user-id=1&value=100000000");
        assertEquals(200, getLastResponse().code());
        buy(55.5, 200);
        get("/sell?user-id=1&company=x&count=1");
        assertEquals(200, getLastResponse().code());
        get("/buy?user-id=1&company=y&count=1");
        assertEquals(200, getLastResponse().code());
    }

    @Test
    void testCantBuyExceedingLimit() throws Exception {
        register();
        get("/change-balance?user-id=1&value=10000000");
        assertEquals(200, getLastResponse().code());
        buy(15, 400);
    }

    @Test
    void testCantBuyExceedingBalance() throws Exception {
        register();
        get("/change-balance?user-id=1&value=1050");
        assertEquals(200, getLastResponse().code());
        buy(0.1, 400);
        buy(0.02, 200);
    }

    @Test
    void testCantSellExceeding() throws Exception {
        register();
        get("/change-count?user-id=1&count=100000000");
        assertEquals(200, getLastResponse().code());
        buy(0.5, 200);
        get("/sell?user-id=1&company=x&count=101");
        assertEquals(400, getLastResponse().code());
    }

    @Test
    void testStatIsCorrect() throws Exception {
        register();
        get("/change-balance?user-id=1&value=10000");
        assertEquals(200, getLastResponse().code());
        buy(0.5, 200);
        client.newCall(new Request.Builder()
                .url(baseURL + "/admin/change-price?company=y&price=7")
                .build()).execute();
        get("/?user-id=1");
        assertEquals(200, getLastResponse().code());
        assertTrue(getLastResponse().body().string().contains("13500"));
    }

    private Response getLastResponse() {
        return ((OkHttpClientInternal) client).getLastResponse();
    }

    private void get(String path) throws Exception {
        Request request = new Request.Builder()
                .url(baseURL + path)
                .build();
        Response response = client.newCall(request).execute();
        ((OkHttpClientInternal) client).setLastResponse(response);
    }

    private static class OkHttpClientInternal extends OkHttpClient {
        private Response lastResponse;

        Response getLastResponse() {
            return lastResponse;
        }

        void setLastResponse(Response lastResponse) {
            this.lastResponse = lastResponse;
        }
    }
}
