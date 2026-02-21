package com.bank.cliente.karate;

import com.intuit.karate.junit5.Karate;
import org.junit.jupiter.api.Tag;

/**
 * Karate API test runner for cliente-persona-service.
 * <p>
 * Runs all .feature files under src/test/resources/karate/
 * To run only karate tests:  ./gradlew karateTest
 * To run all tests:          ./gradlew test
 * <p>
 * Note: the service must be running on port 8080 for these tests to pass.
 * Use the 'mock' profile or docker-compose before running.
 */
@Tag("karate")
class KarateRunnerTest {

    @Karate.Test
    Karate testAll() {
        return Karate.run("classpath:karate").relativeTo(getClass());
    }
}
