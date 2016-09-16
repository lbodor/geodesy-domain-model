package au.gov.ga.geodesy.test.system;

import static io.restassured.RestAssured.given;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.testng.annotations.Test;

import io.restassured.builder.ResponseBuilder;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

/**
 * System tests for SOPAC site logs.
 */
public class DownloadSiteLogs extends BaseSystemTest {

    private String[] ids = new String[]{"A351", "A368", "ADE1", "ADE2", "AFHT", "AFKB", "AIRA", "ANKR", "BF38", "BHIL", "BHR2", "BJFS", "BRFT", "BRMU", "BRST", "BUCU", "BUE2", "CCJ2", "CCJM", "CNMR", "CONZ", "DRAG", "DUBO", "DUBR", "EPRT", "FLIN", "GLSV", "GOUG", "GRIM", "GUAT", "GUUG", "HILR", "HRM1", "HYDE", "ISBA", "ISBS", "ISER", "ISKU", "ISNA", "ISSD", "ISTA", "KELY", "KIT3", "KOK5", "KOUC", "KOUR", "KSTU", "KUNM", "LAE1", "LROC", "MALI", "MANA", "MAUI", "MCIL", "MLO1", "MORP", "MTKA", "NMB2", "NOUM", "NOVM", "NRMD", "NYA1", "NYAL", "OSN1", "PAH6", "PERT", "POLV", "POTS", "PRE1", "QUI2", "RBAY", "RSBY", "SCUB", "SOFI", "SSIA", "SUVA", "SUWN", "SYOG", "TAH1", "TORK", "TRO1", "TROM", "ULAB", "UPO5", "UZHL", "VANU", "VESL", "WEL1", "WHIY", "XIAN", "ZAMB", "ZHN1"};

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

        /* List<String> ids = response.jsonPath().get("_embedded.siteLogs.siteIdentification.fourCharacterId"); */

        for (String id : ids) {
            ExtractableResponse<?> extract = given().contentType("application/xml").
                filter((requestSpec, responseSpec, ctx) -> new ResponseBuilder().clone(ctx.next(requestSpec, responseSpec)).setContentType("application/xml").build()).

                when().
                    get(getConfig().getWebServicesUrl() + "/siteLogs/search/findByFourCharacterId?format=geodesyml&id=" + id).
                then().
                    contentType("").
                    extract();

            /* System.out.println(extract.contentType()); */
            System.out.println(extract.statusCode());
            if (extract.statusCode() == 200) {
                System.out.println(id);
            }
        }
    }
}
