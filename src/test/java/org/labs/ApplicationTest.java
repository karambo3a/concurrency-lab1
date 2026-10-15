package org.labs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ApplicationTest {

    @Test
    void completesDefaultSimulationWithoutDeadlock() {
        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            Application application = new Application(new ApplicationConfig(2, 1, 2));

            application.start();
            application.stop();
        });
    }

    @ParameterizedTest
    @MethodSource("simulationConfigs")
    void eatsAllFoodWithoutLargeDifferenceBetweenProgrammers(
            ApplicationConfig config
    ) {
        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            Application application = new Application(config);

            application.start();
            application.stop();

            List<Long> foodEaten = application.getFoodEaten();
            long totalFoodEaten = foodEaten.stream()
                    .mapToLong(Long::longValue)
                    .sum();
            long maxFoodEaten = foodEaten.stream()
                    .mapToLong(Long::longValue)
                    .max()
                    .orElseThrow();
            long minFoodEaten = foodEaten.stream()
                    .mapToLong(Long::longValue)
                    .min()
                    .orElseThrow();

            assertEquals(config.foodCount(), totalFoodEaten);
            assertTrue(maxFoodEaten - minFoodEaten <= 10);

            System.out.println(foodEaten);
        });
    }

    private static Stream<ApplicationConfig> simulationConfigs() {
        return Stream.of(
                new ApplicationConfig(2, 1, 20),
                new ApplicationConfig(3, 2, 30),
                new ApplicationConfig(7, 2, 70)
        );
    }
}
