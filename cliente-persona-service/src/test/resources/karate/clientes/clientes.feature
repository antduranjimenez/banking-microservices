Feature: Clientes API - CRUD completo

  Background:
    * url baseUrl
    # Generate a unique suffix per test run so tests are idempotent
    * def uid = function(){ return java.util.UUID.randomUUID().toString().substring(0, 8) }
    * def id1 = uid()
    * def id2 = uid()
    * def id3 = uid()
    * def id4 = uid()
    * def id5 = uid()
    * def id6 = uid()

  Scenario: POST - Crear un cliente nuevo
    * def identificacion = '1' + id1
    Given path '/clientes'
    And request
      """
      {
        "nombre": "Test Karate",
        "genero": "Masculino",
        "edad": 25,
        "identificacion": #(identificacion),
        "direccion": "Calle Karate 123",
        "telefono": "099000001",
        "contrasena": "karate123",
        "estado": true
      }
      """
    When method POST
    Then status 201
    And match response.success == true
    And match response.data.nombre == 'Test Karate'
    And match response.data.identificacion == identificacion
    And match response.data.estado == true
    And match response.data.id == '#number'
    And match response.data.clienteId == '#string'

  Scenario: POST - No debe crear cliente con identificacion duplicada
    * def identificacion = '2' + id1
    * def payload =
      """
      {
        "nombre": "Duplicado Test",
        "genero": "Masculino",
        "edad": 25,
        "identificacion": #(identificacion),
        "direccion": "Dir Dup",
        "telefono": "099000010",
        "contrasena": "dup123",
        "estado": true
      }
      """
    # First insert - must succeed
    Given path '/clientes'
    And request payload
    When method POST
    Then status 201
    # Second insert with same identificacion - must fail
    Given path '/clientes'
    And request payload
    When method POST
    Then status 409
    And match response.success == false
    And match response.message contains identificacion

  Scenario: GET - Listar todos los clientes
    Given path '/clientes'
    When method GET
    Then status 200
    And match response.success == true
    And match response.data == '#array'
    And match response.data[0] == '#object'

  Scenario: GET - Obtener cliente por ID
    * def identificacion = '3' + id2
    Given path '/clientes'
    And request
      """
      {
        "nombre": "Cliente Get Test",
        "genero": "Femenino",
        "edad": 30,
        "identificacion": #(identificacion),
        "direccion": "Avenida Test",
        "telefono": "099000020",
        "contrasena": "pass123",
        "estado": true
      }
      """
    When method POST
    Then status 201
    * def clienteId = response.data.id

    Given path '/clientes', clienteId
    When method GET
    Then status 200
    And match response.success == true
    And match response.data.id == clienteId
    And match response.data.nombre == 'Cliente Get Test'

  Scenario: GET - Debe retornar 404 si cliente no existe
    Given path '/clientes/999999'
    When method GET
    Then status 404
    And match response.success == false

  Scenario: PUT - Actualizar cliente completo
    * def identificacion = '4' + id3
    Given path '/clientes'
    And request
      """
      {
        "nombre": "Para Actualizar",
        "genero": "Masculino",
        "edad": 22,
        "identificacion": #(identificacion),
        "direccion": "Dir Original",
        "telefono": "099000030",
        "contrasena": "pass000",
        "estado": true
      }
      """
    When method POST
    Then status 201
    * def idCreado = response.data.id

    Given path '/clientes', idCreado
    And request
      """
      {
        "nombre": "Para Actualizar Modificado",
        "genero": "Masculino",
        "edad": 23,
        "identificacion": #(identificacion),
        "direccion": "Dir Nueva",
        "telefono": "099000099",
        "contrasena": "newpass",
        "estado": true
      }
      """
    When method PUT
    Then status 200
    And match response.success == true
    And match response.data.nombre == 'Para Actualizar Modificado'
    And match response.data.edad == 23

  Scenario: PATCH - Actualizar parcialmente (solo estado)
    * def identificacion = '5' + id4
    Given path '/clientes'
    And request
      """
      {
        "nombre": "Para Patch",
        "genero": "Femenino",
        "edad": 28,
        "identificacion": #(identificacion),
        "direccion": "Dir Patch",
        "telefono": "099000040",
        "contrasena": "patch123",
        "estado": true
      }
      """
    When method POST
    Then status 201
    * def idPatch = response.data.id

    Given path '/clientes', idPatch
    And request { "estado": false }
    When method PATCH
    Then status 200
    And match response.data.estado == false
    And match response.data.nombre == 'Para Patch'

  Scenario: DELETE - Eliminar cliente
    * def identificacion = '6' + id5
    Given path '/clientes'
    And request
      """
      {
        "nombre": "Para Eliminar",
        "genero": "Masculino",
        "edad": 40,
        "identificacion": #(identificacion),
        "direccion": "Dir Delete",
        "telefono": "099000050",
        "contrasena": "del123",
        "estado": true
      }
      """
    When method POST
    Then status 201
    * def idDelete = response.data.id

    Given path '/clientes', idDelete
    When method DELETE
    Then status 200
    And match response.success == true

    Given path '/clientes', idDelete
    When method GET
    Then status 404

  Scenario: Validacion - Crear cliente sin nombre debe fallar
    * def identificacion = '7' + id6
    Given path '/clientes'
    And request
      """
      {
        "genero": "Masculino",
        "identificacion": #(identificacion),
        "contrasena": "123",
        "estado": true
      }
      """
    When method POST
    Then status 400
    And match response.success == false
    And match response.data.nombre == '#string'
