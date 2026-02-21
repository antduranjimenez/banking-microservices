function fn() {
    var env = karate.env;
    karate.log('karate.env =', env);

    var config = {
        baseUrl: 'http://localhost:8081/api',
        // Pre-seeded cliente_ids from Liquibase seed data
        clienteJoseLema: 'CLI-1712345678',
        clienteMarianela: 'CLI-1723456789',
        clienteJuanOsorio: 'CLI-1734567890'
    };

    if (env === 'docker') {
        config.baseUrl = 'http://cuenta-movimiento-service:8081/api';
    }

    return config;
}
