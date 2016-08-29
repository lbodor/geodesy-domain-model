package au.gov.ga.geodesy.port.adapter.rest;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.config.XmlConfig.xmlConfig;

import static org.hamcrest.Matchers.equalTo;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import au.gov.ga.geodesy.support.TestResources;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

public class SiteLogEndpointTest extends RestDocTest {

    @BeforeClass
    public void setup() {
        RestAssuredMockMvc.mockMvc(RestTest.mvc);
    }

    @Test
    @Rollback(false)
    public void upload() throws Exception {
        String geodesyML = IOUtils.toString(TestResources.customGeodesyMLSiteLogReader("ALIC"));
        given()
            .body(geodesyML).
        when()
            .post("/siteLogs/upload").
        then()
            .statusCode(HttpStatus.CREATED.value());
    }

    @Test(dependsOnMethods = "upload")
    @Rollback(false)
    public void testFindGeodesyMLSiteLog() throws Exception {
        given()
            .config(RestAssuredMockMvc.config().xmlConfig(xmlConfig().declareNamespace("geo", "urn:xml-gov-au:icsm:egeodesy:0.3")))
            .when()
            .get("/siteLogs/search/findByFourCharacterId?id=ALIC&format=geodesyml")
            .then()
                .statusCode(HttpStatus.OK.value())
                .contentType("application/xml")
                .body("geo:siteLog.geo:siteIdentification.geo:siteName.text()", equalTo("Alice Springs AU012"))
                .log().body();
    }

    @Test(dependsOnMethods = "upload")
    @Rollback(false)
    public void testFindJsonSiteLog() throws Exception {
        given()
            .when()
            .get("/siteLogs/search/findByFourCharacterId?id=ALIC&format=json")
            .then()
                .statusCode(HttpStatus.OK.value())
                .contentType("application/json")
                .body("siteIdentification.siteName", equalTo("Alice Springs AU012"))
                .log().body();
    }

    @Test
    public void testNotFound() throws Exception {
        given()
            .when()
            .get("/siteLogs/search/findByFourCharacterId?id=FOO&format=geodesyml")
            .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
}
