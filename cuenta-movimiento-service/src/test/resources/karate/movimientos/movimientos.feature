Feature: Movimientos API - Registro y validaciones F2/F3

  Background:
    * url baseUrl
    # Each scenario creates its own fresh account with a unique number,
    # so tests are fully idempotent across multiple runs.
    * def uid = function(){ return java.util.UUID.randomUUID().toString().substring(0, 6).toUpperCase() }

  Scenario: F2 - Registrar deposito y verificar saldo actualizado
    * def numeroCuenta = 'MOV-DEP-' + uid()
    Given path '/cuentas'
    And request
      """
      {
        "numeroCuenta": #(numeroCuenta),
        "tipoCuenta": "Ahorros",
        "saldoInicial": 1000.00,
        "estado": true,
        "clienteId": "CLI-1712345678"
      }
      """
    When method POST
    Then status 201
    * def cuentaId = response.data.id

    Given path '/movimientos'
    And request
      """
      {
        "cuentaId": #(cuentaId),
        "tipoMovimiento": "Deposito",
        "valor": 500.00
      }
      """
    When method POST
    Then status 201
    And match response.success == true
    And match response.data.valor == 500.00
    And match response.data.saldo == 1500.00
    And match response.data.tipoMovimiento == 'Deposito'
    And match response.data.fecha == '#string'

    Given path '/cuentas', cuentaId
    When method GET
    Then status 200
    And match response.data.saldoDisponible == 1500.00

  Scenario: F2 - Registrar retiro y verificar saldo
    * def numeroCuenta = 'MOV-RET-' + uid()
    Given path '/cuentas'
    And request
      """
      {
        "numeroCuenta": #(numeroCuenta),
        "tipoCuenta": "Ahorros",
        "saldoInicial": 800.00,
        "estado": true,
        "clienteId": "CLI-1712345678"
      }
      """
    When method POST
    Then status 201
    * def cuentaId = response.data.id

    Given path '/movimientos'
    And request
      """
      {
        "cuentaId": #(cuentaId),
        "tipoMovimiento": "Retiro",
        "valor": -300.00
      }
      """
    When method POST
    Then status 201
    And match response.data.saldo == 500.00
    And match response.data.valor == -300.00

  Scenario: F2 - Registrar multiples movimientos consecutivos
    * def numeroCuenta = 'MOV-MUL-' + uid()
    Given path '/cuentas'
    And request
      """
      {
        "numeroCuenta": #(numeroCuenta),
        "tipoCuenta": "Corriente",
        "saldoInicial": 200.00,
        "estado": true,
        "clienteId": "CLI-1723456789"
      }
      """
    When method POST
    Then status 201
    * def cuentaId = response.data.id

    Given path '/movimientos'
    And request { "cuentaId": #(cuentaId), "valor": 300.00 }
    When method POST
    Then status 201
    And match response.data.saldo == 500.00

    Given path '/movimientos'
    And request { "cuentaId": #(cuentaId), "valor": -100.00 }
    When method POST
    Then status 201
    And match response.data.saldo == 400.00

    Given path '/movimientos'
    And request { "cuentaId": #(cuentaId), "valor": 50.00 }
    When method POST
    Then status 201
    And match response.data.saldo == 450.00

    Given path '/cuentas', cuentaId
    When method GET
    Then status 200
    And match response.data.saldoDisponible == 450.00

  Scenario: F3 - Retiro sin saldo debe retornar "Saldo no disponible" (HTTP 422)
    * def numeroCuenta = 'MOV-SF1-' + uid()
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
    * def cuentaId = response.data.id

    Given path '/movimientos'
    And request
      """
      {
        "cuentaId": #(cuentaId),
        "tipoMovimiento": "Retiro",
        "valor": -100.00
      }
      """
    When method POST
    Then status 422
    And match response.success == false
    And match response.message == 'Saldo no disponible'

    Given path '/cuentas', cuentaId
    When method GET
    Then status 200
    And match response.data.saldoDisponible == 0.00

  Scenario: F3 - Retiro que excede saldo debe fallar
    * def numeroCuenta = 'MOV-SF2-' + uid()
    Given path '/cuentas'
    And request
      """
      {
        "numeroCuenta": #(numeroCuenta),
        "tipoCuenta": "Ahorros",
        "saldoInicial": 100.00,
        "estado": true,
        "clienteId": "CLI-1712345678"
      }
      """
    When method POST
    Then status 201
    * def cuentaId = response.data.id

    Given path '/movimientos'
    And request { "cuentaId": #(cuentaId), "valor": -500.00 }
    When method POST
    Then status 422
    And match response.message == 'Saldo no disponible'

  Scenario: GET - Listar movimientos
    Given path '/movimientos'
    When method GET
    Then status 200
    And match response.success == true
    And match response.data == '#array'

  Scenario: GET - Listar movimientos por cuenta
    * def numeroCuenta = 'MOV-LST-' + uid()
    Given path '/cuentas'
    And request
      """
      {
        "numeroCuenta": #(numeroCuenta),
        "tipoCuenta": "Corriente",
        "saldoInicial": 500.00,
        "estado": true,
        "clienteId": "CLI-1723456789"
      }
      """
    When method POST
    Then status 201
    * def cuentaId = response.data.id

    Given path '/movimientos'
    And request { "cuentaId": #(cuentaId), "valor": 100.00 }
    When method POST
    Then status 201

    Given path '/movimientos/cuenta', cuentaId
    When method GET
    Then status 200
    And match response.data == '#[_ > 0]'
    And match each response.data contains { cuentaId: #(cuentaId) }

  Scenario: Validacion - Crear movimiento sin valor debe fallar
    Given path '/movimientos'
    And request { "cuentaId": 1 }
    When method POST
    Then status 400
    And match response.success == false
