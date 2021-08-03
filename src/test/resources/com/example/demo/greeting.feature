@greet
Feature: the version can be retrieved
  Scenario: client makes call to GET /version
    Given the client provide version "1.0"
    When the client calls "/version"
    Then the client receives status code of 200
    And the client receives server version 1.0