package com.bank.cuenta.karate;

import com.intuit.karate.junit5.Karate;
import org.junit.jupiter.api.Tag;

/**
 * Karate API test runner for cuenta-movimiento-service.
 * <p>
 * Runs all .feature files under src/test/resources/karate/
 * To run only karate tests:  ./gradlew karateTest
 * To run all tests:          ./gradlew test
 * <p>
 * Note: the service must be running on port 8081 (and its deps) for these to pass.
 * Use docker-compose before running: docker-compose up -d
 */
@Tag("karate")
class KarateRunnerTest {

    @Karate.Test
    Karate testAll() {
        return Karate.run("classpath:karate").relativeTo(getClass());
    }
}
