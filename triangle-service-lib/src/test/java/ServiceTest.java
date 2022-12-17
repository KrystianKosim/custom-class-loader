import com.company.service.Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


public class ServiceTest {

    @ParameterizedTest
    @MethodSource("generateDataForFieldTest")
    @DisplayName("Should calculate correct field of triangle ")
    void shouldCalculateFieldOfTriangle(double base, double high, double expectedValue) {
            double actualValue = Service.getFieldOfTriangle(base, high);

            assertEquals(expectedValue, actualValue);
    }

    static Stream<Arguments> generateDataForFieldTest() {
        return Stream.of(
                Arguments.of(1, 2, 1),
                Arguments.of(2, 3, 3),
                Arguments.of(5, 10, 25),
                Arguments.of(100, 200, 10000),
                Arguments.of(0.1, 10, 0.5),
                Arguments.of(0.5, 0.5, 0.125)
        );
    }

    @ParameterizedTest
    @MethodSource("generateDataForFieldTestException")
    @DisplayName("Should throw IllegalArgumentException while calculate field because value is not greater than 0")
    void shouldThrowExceptionBecauseValuesAreIncorrectField(int base, int high){

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Service.getFieldOfTriangle(base, high);
        });

        String expectedMessage = "Wartosci musza byc wieksze od 0!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    static Stream<Arguments> generateDataForFieldTestException(){
        return Stream.of(
                Arguments.of(0,1),
                Arguments.of(-1,2),
                Arguments.of(-1,-1),
                Arguments.of(0,0)
        );
    }

    @ParameterizedTest
    @MethodSource("generateDataForAreaTest")
    @DisplayName("Should calculate correct area of triangle ")
    void shouldCalculateAreaOfTriangle(double base, double expectedValue) {
            double actualValue = Service.getAreaOfTriangle(base);

            assertEquals(expectedValue, actualValue);
    }

    static Stream<Arguments> generateDataForAreaTest() {
        return Stream.of(
                Arguments.of(1, 3),
                Arguments.of(3, 9),
                Arguments.of(5, 15),
                Arguments.of(9, 27),
                Arguments.of(20, 60),
                Arguments.of(220, 660),
                Arguments.of(0.20, 0.6)
        );
    }

    @ParameterizedTest
    @MethodSource("generateDataForAreaException")
    @DisplayName("Should throw IllegalArgumentException while calculate area because value is not greater than 0")
        void shouldThrowExceptionBecauseValueIsIncorrectArea(int base){

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Service.getAreaOfTriangle(base);
        });

        String expectedMessage = "Wartosci musza byc wieksze od 0!";
        String actuallMessage = exception.getMessage();

        assertTrue(actuallMessage.contains(expectedMessage));
    }

    static Stream<Arguments> generateDataForAreaException(){
        return Stream.of(
                Arguments.of(0),
                Arguments.of(-1)
        );
    }
}
