Feature: I want to test base skeleton behavior
  
  Background:
    Given Request with Authorization Header:
      | headerName    | headerValue                    |
      | Authorization | Basic c2tlbGV0b246YnJhc2lsSHVl |
    
  Scenario: I want to make a get request on base path
    When I make a GET call to "skeleton" endpoint
    Then response status code should be 200
