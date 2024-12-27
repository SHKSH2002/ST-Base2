Feature: Add Reservation

  Scenario Outline: Add one reservation
    Given A manager user
    And A sample restaurant
    And A sample user
    And A sample table
    When Add Reservation with time 2024 year 11 month 20 day 17 hour 10 minute
    Then The count of reservation should be 1

  Scenario: Adding three reservation
    Given A manager user
    And A sample restaurant
    And A sample user
    And A sample table
    When Add Reservation with time 2021 year 11 month 17 day 12 hour 11 minute
    When Add Reservation with time 2024 year 9 month 21 day 16 hour 13 minute
    When Add Reservation with time 2022 year 12 month 11 day 15 hour 20 minute
    Then The count of reservation should be 3
