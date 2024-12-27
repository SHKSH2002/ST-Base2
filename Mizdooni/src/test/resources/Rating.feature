Feature: Add Rating and Get average

  Scenario Outline: Add one reservation
    Given A manager user
    And A sample restaurant
    When Add Rating with overall 3 food 3 ambiance 5 service 2
    Then The count of review should be 1


  Scenario Outline: Add Reviews and check average
    Given A manager user
    And A sample restaurant
    When Add Rating with overall <overall1> food <food1> ambiance <ambiance1> service <service1>
    And Add Rating with overall <overall2> food <food2> ambiance <ambiance2> service <service2>
    Then The count of review should be 2
    And The average of review should be overall <avgOverall> food <avgFood> ambiance <avgAmbiance> service <avgService>

    Examples:
      | overall1 | food1 | ambiance1 | service1 | overall2 | food2 | ambiance2 | service2 | avgOverall | avgFood | avgAmbiance | avgService |
      |     4    |   2   |    1      |   3      |   2      |   4   |    3      |     5    |     3      |   3     |      2      |     4      |
      |     1    |   5   |    3      |   2      |   5      |   2   |    4      |     1    |     3      |   3.5   |      3.5    |     1.5    |
      |     3    |   1   |    4      |   4      |   4      |   5   |    5      |     4    |     3.5    |   3     |      4.5    |     4    |



  Scenario Outline: Add two review by one user
    Given A manager user
    And A sample restaurant
    And A sample user
    When User Rating with overall <overall1> food <food1> ambiance <ambiance1> service <service1>
    And User Rating with overall <overall2> food <food2> ambiance <ambiance2> service <service2>
    Then The count of review should be 1
    And The average of review should be overall <avgOverall> food <avgFood> ambiance <avgAmbiance> service <avgService>

    Examples:
      | overall1 | food1 | ambiance1 | service1 | overall2 | food2 | ambiance2 | service2 | avgOverall | avgFood | avgAmbiance | avgService |
      |     4    |   2   |    1      |   3      |   2      |   4   |    3      |     5    |     2      |   4     |      3      |     5      |
      |     1    |   5   |    3      |   2      |   5      |   2   |    4      |     1    |     5      |   2     |      4      |     1      |
      |     3    |   1   |    4      |   4      |   4      |   5   |    5      |     4    |     4      |   5     |      5      |     4      |
