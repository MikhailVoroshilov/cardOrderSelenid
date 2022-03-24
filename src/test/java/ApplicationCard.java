import com.codeborne.selenide.SelenideElement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class ApplicationCard {
    SelenideElement form = $(".form");

    @BeforeEach
    public void setUp() {
        open("http://localhost:9999/");
    }

    @Test
    public void shouldTestCorrect() {
        form.$("[data-test-id='name'] input").setValue("Иван Петров-Иванов");
        form.$("[data-test-id='phone'] input").setValue("+79305698778");
        form.$("[data-test-id=agreement]").click();
        form.$("button").click();

        $("[data-test-id=order-success]").shouldHave(exactText("Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время."));
    }

    @Test
    public void shouldNotEnterRows() {
        $(".form").$(".button").click();
        $("[data-test-id=order-success]").shouldNot(exist);
    }

    @ParameterizedTest
    @CsvSource({
            "English, Maik",
            "spec Simbol, Петр Машк&вич",
            "underscore, Анна_Сергеевна",
            "emptiness, ''",
    })
    public void shouldTestIncorrectEnterName(String testName, String name) {
        form.$("[data-test-id='name'] input").setValue(name);
        form.$("[data-test-id='phone'] input").setValue("+79305698778");
        form.$("[data-test-id=agreement]").click();
        form.$(".button").click();

        form.$("[data-test-id=name].input_invalid .input__sub").shouldBe(visible);
    }

    @ParameterizedTest
    @CsvSource({
            "not plus, 79025647891",
            "over limit, +798541236547",
            "less than limit, +7984562356",
            "spec simbol, +7&963256485",
            "null, ''",
            "text phone number, +7e965463269",
    })
    public void shouldTestIncorrectEnterPhone(String testName, String phone) {
        form.$("[data-test-id='name'] input").setValue("Аннф-Петровна");
        form.$("[data-test-id='phone'] input").setValue(phone);
        form.$("[data-test-id=agreement]").click();
        form.$(".button").click();

        form.$("[data-test-id=phone].input_invalid .input__sub").shouldBe(visible);
    }

    @Test
    public void shouldNotClickCheckbox() {
        form.$("[data-test-id='name'] input").setValue("Анна");
        form.$("[data-test-id='phone'] input").setValue("+79865923654");
        form.$(".button").click();

        form.$("[data-test-id=agreement].input_invalid .checkbox__text").shouldBe(visible);
    }
}
