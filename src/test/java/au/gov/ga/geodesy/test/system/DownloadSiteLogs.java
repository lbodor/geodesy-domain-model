package au.gov.ga.geodesy.test.system;

import static io.restassured.RestAssured.given;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

/**
 * System tests for SOPAC site logs.
 */
public class DownloadSiteLogs extends BaseSystemTest {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(DownloadSiteLogs.class);

    @Test
    public void downloadSiteLogs() throws Exception {
        Response response = given().
            when().
                get(getConfig().getWebServicesUrl() + "/siteLogs?size=10000").
            then().
                statusCode(HttpStatus.OK.value()).
            extract().
                response();

        List<String> ids = response.jsonPath().get("_embedded.siteLogs.siteIdentification.fourCharacterId");

        ids.forEach(id -> {
            ExtractableResponse<?> extract = given().
                when().
                    get(getConfig().getWebServicesUrl() + "/siteLogs/search/findByFourCharacterId?format=geodesyml&id=" + id).
                then().
                    extract();

            if (extract.statusCode() == 200) {
                System.out.println(id);
            }
        });
    }
}
