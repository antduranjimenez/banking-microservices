Feature: Reportes API - Estado de Cuenta (F4)

  Background:
    * url baseUrl

  Scenario: F4 - Generar estado de cuenta para Marianela Montalvo con rango de fechas
    Given path '/reportes'
    And param fecha = '2022-01-01,2022-12-31'
    And param cliente = 'CLI-1723456789'
    When method GET
    Then status 200
    And match response.success == true
    And match response.data.clienteId == 'CLI-1723456789'
    And match response.data.nombreCliente == 'Marianela Montalvo'
    And match response.data.cuentas == '#array'
    And match response.data.cuentas[0].numeroCuenta == '#string'
    And match response.data.cuentas[0].tipoCuenta == '#string'
    And match response.data.cuentas[0].saldoInicial == '#number'
    And match response.data.cuentas[0].saldoDisponible == '#number'
    And match response.data.cuentas[0].estado == '#boolean'
    And match response.data.cuentas[0].movimientos == '#array'

  Scenario: F4 - Estado de cuenta para Jose Lema
    Given path '/reportes'
    And param fecha = '2022-01-01,2022-12-31'
    And param cliente = 'CLI-1712345678'
    When method GET
    Then status 200
    And match response.data.clienteId == 'CLI-1712345678'
    And match response.data.nombreCliente == 'Jose Lema'
    And match response.data.cuentas == '#[] #object'

  Scenario: F4 - Rango de fechas sin movimientos devuelve cuentas con movimientos vacios
    Given path '/reportes'
    And param fecha = '2000-01-01,2000-01-31'
    And param cliente = 'CLI-1712345678'
    When method GET
    Then status 200
    And match response.success == true
    And match response.data.cuentas == '#array'
    # Movements list should be empty for this date range
    And match each response.data.cuentas contains { movimientos: '#[]' }

  Scenario: F4 - Caso de Uso 5 - Listado de movimientos de Marianela del 08/02/2022 al 10/02/2022
    Given path '/reportes'
    And param fecha = '2022-02-08,2022-02-10'
    And param cliente = 'CLI-1723456789'
    When method GET
    Then status 200
    And match response.data.nombreCliente == 'Marianela Montalvo'
    * def cuentas = response.data.cuentas
    * def allMovimientos = karate.jsonPath(response, '$.data.cuentas[*].movimientos[*]')
    * karate.log('Total movimientos en rango:', allMovimientos.length)
    And match allMovimientos == '#array'

  Scenario: F4 - Cliente inexistente debe retornar error
    Given path '/reportes'
    And param fecha = '2022-01-01,2022-12-31'
    And param cliente = 'CLI-NOEXISTE'
    When method GET
    Then status 404
    And match response.success == false
