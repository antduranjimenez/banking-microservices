Feature: Cuentas API - CRUD completo

  Background:
    * url baseUrl
    # Generate unique account numbers per run so tests are idempotent
    * def uid = function(){ return java.util.UUID.randomUUID().toString().substring(0, 6).toUpperCase() }

  Scenario: POST - Crear cuenta de ahorros
    * def numeroCuenta = 'KAR-AH-' + uid()
    Given path '/cuentas'
    And request
      """
      {
        "numeroCuenta": #(numeroCuenta),
        "tipoCuenta": "Ahorros",
        "saldoInicial": 500.00,
        "estado": true,
        "clienteId": "CLI-1712345678"
      }
      """
    When method POST
    Then status 201
    And match response.success == true
    And match response.data.numeroCuenta == numeroCuenta
    And match response.data.tipoCuenta == 'Ahorros'
    And match response.data.saldoInicial == 500.00
    And match response.data.saldoDisponible == 500.00
    And match response.data.estado == true
    And match response.data.clienteId == 'CLI-1712345678'

  Scenario: POST - Crear cuenta corriente
    * def numeroCuenta = 'KAR-CO-' + uid()
    Given path '/cuentas'
    And request
      """
      {
        "numeroCuenta": #(numeroCuenta),
        "tipoCuenta": "Corriente",
        "saldoInicial": 1000.00,
        "estado": true,
        "clienteId": "CLI-1723456789"
      }
      """
    When method POST
    Then status 201
    And match response.data.tipoCuenta == 'Corriente'
    And match response.data.saldoDisponible == 1000.00

  Scenario: GET - Listar todas las cuentas
    Given path '/cuentas'
    When method GET
    Then status 200
    And match response.success == true
    And match response.data == '#array'

  Scenario: GET - Obtener cuenta por ID
    * def numeroCuenta = 'KAR-ID-' + uid()
    Given path '/cuentas'
    And request
      """
      {
        "numeroCuenta": #(numeroCuenta),
        "tipoCuenta": "Ahorros",
        "saldoInicial": 200.00,
        "estado": true,
        "clienteId": "CLI-1734567890"
      }
      """
    When method POST
    Then status 201
    * def cuentaId = response.data.id

    Given path '/cuentas', cuentaId
    When method GET
    Then status 200
    And match response.data.id == cuentaId
    And match response.data.numeroCuenta == numeroCuenta

  Scenario: GET - Obtener cuentas por cliente
    Given path '/cuentas/cliente/CLI-1712345678'
    When method GET
    Then status 200
    And match response.success == true
    And match response.data == '#array'
    And match each response.data == { id: '#number', numeroCuenta: '#string', tipoCuenta: '#string', saldoInicial: '#number', saldoDisponible: '#number', estado: '#boolean', clienteId: 'CLI-1712345678' }

  Scenario: GET - Obtener cuenta por numero (cuenta seed)
    Given path '/cuentas/numero/478758'
    When method GET
    Then status 200
    And match response.data.numeroCuenta == '478758'
    And match response.data.tipoCuenta == 'Ahorros'

  Scenario: GET - Retornar 404 si cuenta no existe
    Given path '/cuentas/999999'
    When method GET
    Then status 404
    And match response.success == false

  Scenario: PATCH - Desactivar cuenta
    * def numeroCuenta = 'KAR-PA-' + uid()
    Given path '/cuentas'
    And request
      """
      {
        "numeroCuenta": #(numeroCuenta),
        "tipoCuenta": "Ahorros",
        "saldoInicial": 0.00,
        "estado": true,
        "clienteId": "CLI-1734567890"
      }
      """
    When method POST
    Then status 201
    * def idPatch = response.data.id

    Given path '/cuentas', idPatch
    And request { "estado": false }
    When method PATCH
    Then status 200
    And match response.data.estado == false

  Scenario: DELETE - Eliminar cuenta
    * def numeroCuenta = 'KAR-DE-' + uid()
    Given path '/cuentas'
    And request
      """
      {
        "numeroCuenta": #(numeroCuenta),
        "tipoCuenta": "Corriente",
        "saldoInicial": 100.00,
        "estado": true,
        "clienteId": "CLI-1712345678"
      }
      """
    When method POST
    Then status 201
    * def idDel = response.data.id

    Given path '/cuentas', idDel
    When method DELETE
    Then status 200

    Given path '/cuentas', idDel
    When method GET
    Then status 404
