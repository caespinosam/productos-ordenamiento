package com.indi.product.adapter.`in`.rest

import com.indi.product.adapter.`in`.rest.webmodel.ProductWebModel
import io.restassured.RestAssured
import jakarta.annotation.PostConstruct
import org.assertj.core.api.Assertions
import org.hamcrest.Matchers
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SearchProductsControllerIntegrationTest {

    @LocalServerPort
    val port = 0

    @PostConstruct
    fun setup() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
    }

    /*
        Current data in the in-memory DB is (see src/test/resources):

        id name                      sales_units stock
        1 V-NECH BASIC SHIRT         100         S: 4 / M:9 / L:0
        2 CONTRASTING FABRIC T-SHIRT 50          S: 35 / M:9 / L:9
        3 RAISED PRINT T-SHIRT       80          S: 20 / M:2 / L:20
        4 PLEATED T-SHIRT            3           S: 25  / M:30 / L:10
        5 CONTRASTING LACE T-SHIRT   650         S: 0 / M:1 / L:0
        6 SLOGAN T-SHIRT             20          S: 9  / M:2 / L:5
     */

    @Nested
    inner class InputValidation {
        @Test
        fun should_not_accept_negative_weights_for_sales_unit() {
            RestAssured
                .given()
                .queryParam("sales_unit", "-0.5")
                .get("products")
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", Matchers.containsString("must be greater than or equal to 0"))
        }

        @Test
        fun should_not_accept_weights_greater_than_1_for_sales_unit() {
            RestAssured
                .given()
                .queryParam("sales_unit", "1.1")
                .get("products")
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", Matchers.containsString("must be less than or equal to 1.0"))
        }

        @Test
        fun should_not_accept_negative_weights_for_stock_ratio() {
            RestAssured
                .given()
                .queryParam("stock_ratio", "-0.5")
                .get("products")
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", Matchers.containsString("must be greater than or equal to 0"))
        }

        @Test
        fun should_not_accept_weights_greater_than_1_for_stock_ratio() {
            RestAssured
                .given()
                .queryParam("stock_ratio", "1.1")
                .get("products")
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", Matchers.containsString("must be less than or equal to 1.0"))
        }

        @Test
        fun should_not_accept_negative_values_for_page_number() {
            RestAssured
                .given()
                .queryParam("page_number", "-1")
                .get("products")
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", Matchers.containsString("must be greater than or equal to 0"))
        }

        @Test
        fun should_not_accept_negative_values_for_page_size() {
            RestAssured
                .given()
                .queryParam("page_size", "-1")
                .get("products")
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", Matchers.containsString("must be greater than or equal to 0"))
        }

        @Test
        fun should_not_accept_invalid_values_for_direction() {
            RestAssured
                .given()
                .queryParam("direction", "random")
                .get("products")
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
        }
    }

    /*
        Current data in the in-memory DB is (see src/test/resources):

        id name                      sales_units stock
        1 V-NECH BASIC SHIRT         100         S: 4 / M:9 / L:0
        2 CONTRASTING FABRIC T-SHIRT 50          S: 35 / M:9 / L:9
        3 RAISED PRINT T-SHIRT       80          S: 20 / M:2 / L:20
        4 PLEATED T-SHIRT            3           S: 25  / M:30 / L:10
        5 CONTRASTING LACE T-SHIRT   650         S: 0 / M:1 / L:0
        6 SLOGAN T-SHIRT             20          S: 9  / M:2 / L:5
     */
    @Nested
    inner class Smoke {
        @Test
        fun should_return_the_requested_elements_in_the_page() {
            val jsonPath = RestAssured
                .given()
                .queryParam("sales_unit", "0.2")
                .queryParam("stock_ratio", "0.8")
                .queryParam("page_number", "0")
                .queryParam("page_size", "6")
                .queryParam("direction", "DESC")
                .accept("application/json")
                .get("products")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().response().jsonPath()

            Assertions.assertThat(jsonPath).isNotNull()
            val products = jsonPath.getList<ProductWebModel>("content")
            Assertions.assertThat(products).extracting("id").containsAll(
                listOf(
                    5,
                    1,
                    3,
                    2,
                    6,
                    4
                )
            )
            Assertions.assertThat(jsonPath.getInt("currentPage")).isEqualTo(0)
            Assertions.assertThat(jsonPath.getInt("currentElements")).isEqualTo(6)
            Assertions.assertThat(jsonPath.getInt("maxElementsPerPage")).isEqualTo(6)
            Assertions.assertThat(jsonPath.getInt("totalElements")).isEqualTo(6)
        }


        @Test
        fun should_return_items_ordered_by_id_desc_given_no_metric_is_sent() {
            val jsonPath = RestAssured
                .given()
                .accept("application/json")
                .get("products")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().response().jsonPath()

            Assertions.assertThat(jsonPath).isNotNull()
            val products = jsonPath.getList<ProductWebModel>("content")
            Assertions.assertThat(products).extracting("id").containsAll(
                listOf(
                    6,
                    5,
                    4,
                    3,
                    2,
                    1
                )
            )
            Assertions.assertThat(jsonPath.getInt("currentPage")).isEqualTo(0)
            Assertions.assertThat(jsonPath.getInt("currentElements")).isEqualTo(6)
            Assertions.assertThat(jsonPath.getInt("maxElementsPerPage")).isEqualTo(20)
            Assertions.assertThat(jsonPath.getInt("totalElements")).isEqualTo(6)
        }
    }
}