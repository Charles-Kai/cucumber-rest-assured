package com.example.demo;


import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Work around. Surefire does not use JUnits Test Engine discovery
 * functionality. Alternatively execute the the
 * org.junit.platform.console.ConsoleLauncher with the maven-antrun-plugin.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"html:target/results.html", "message:target/results.ndjson"})
@CucumberContextConfiguration
@AutoConfigureMockMvc
@SpringBootTest(classes = DemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class RunCucumberTest {





}