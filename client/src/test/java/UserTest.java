import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTest {

    private static final String BASE_URL = "http://localhost:8080";

    @Test
    public void testRegisterAndInfo() throws IOException {
        OkHttpClient client = new OkHttpClient();

        HttpUrl registerUrl = Objects.requireNonNull(HttpUrl.parse(BASE_URL + "/register"))
                .newBuilder()
                .addQueryParameter("user-id", "1")
                .addQueryParameter("name", "user1")
                .build();

        Request registerRequest = new Request.Builder()
                .url(registerUrl)
                .build();

        Response registerResponse = client.newCall(registerRequest).execute();
        assertEquals(200, registerResponse.code());

        HttpUrl profileUrl = Objects.requireNonNull(HttpUrl.parse(BASE_URL + ""))
                .newBuilder()
                .addQueryParameter("user-id", "1")
                .build();

        Request profileRequest = new Request.Builder()
                .url(profileUrl)
                .build();

        Response profileResponse = client.newCall(profileRequest).execute();
        assertEquals(200, profileResponse.code());
        assertTrue(Objects.requireNonNull(profileResponse.body()).string().contains("user1"));
    }
}
