function fn() {
    var env = karate.env; // 'dev', 'test', 'docker'
    karate.log('karate.env =', env);

    var config = {
        baseUrl: 'http://localhost:8080/api'
    };

    if (env === 'docker') {
        config.baseUrl = 'http://cliente-persona-service:8080/api';
    } else if (env === 'test') {
        config.baseUrl = 'http://localhost:8080/api';
    }

    return config;
}
