package com.example.demo;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class GetStep {

    @Value("${local.server.port}")
    private int port;

    private final Map<String, Object> parameters = new HashMap<>();

    private Response response;

    @Given("the client provide version {string}")
    public void theClientProvideVersion(String version) {
        parameters.put("version", version);
    }

    @When("the client calls {string}")
    public void theClientCalls(String url) {
        response = given().
                when().
                params(parameters).
                get(url);

    }

    @Then("^the client receives status code of (\\d+)$")
    public void the_client_receives_status_code_of(int statusCode) throws Throwable {
        response.then().statusCode(statusCode);
    }

    @And("^the client receives server version (.+)$")
    public void the_client_receives_server_version_body(String version) throws Throwable {
        response.then()
                .body(matchesJsonSchemaInClasspath("greeting-schema.json"))
                .body("version", Matchers.equalTo(version));
    }

    @Before
    public void before() {
        RestAssured.port = port;
        RestAssured.basePath = "/api";
    }
}
