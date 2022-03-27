import com.codeborne.selenide.SelenideElement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApplicationCard {
    SelenideElement form = $(".form");

    @BeforeEach
    public void setUp() {
        open("http://localhost:9999/");
    }

    @ParameterizedTest
    @CsvSource({
            "one name, Анна",
            "twho, Сергей Васильевич",
            "thre,Иван Петров-Иванов",
    })
    public void shouldTestCorrect(String title, String name) {
        form.$("[data-test-id='name'] input").setValue(name);
        form.$("[data-test-id='phone'] input").setValue("+79305698778");
        form.$("[data-test-id=agreement]").click();
        form.$("button").click();

        String message = "Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.";
        $("[data-test-id=order-success]").shouldHave(exactText(message));
    }

    @Test
    public void shouldNotEnterRows() {
        $(".button").click();
        String error = "Поле обязательно для заполнения";
        $("[data-test-id=name] .input__sub").shouldHave(exactText(error));
    }

    @ParameterizedTest
    @CsvSource({
            "English, Maik, 'Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.'",
            "spec Simbol, Петр Машк&вич, 'Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.'",
            "underscore, Анна_Сергеевна, 'Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.'",
            "emptiness, '', Поле обязательно для заполнения",
    })
    public void shouldTestIncorrectEnterName(String testName, String name, String errors) {
        form.$("[data-test-id='name'] input").setValue(name);
        form.$("[data-test-id='phone'] input").setValue("+79305698778");
        form.$("[data-test-id=agreement]").click();
        form.$(".button").click();

        String actual = form.$("[data-test-id=name].input_invalid .input__sub").getText().trim();
        assertEquals(errors, actual);
    }

    @ParameterizedTest
    @CsvSource({
            "not plus, 79025647891, 'Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.'",
            "over limit, +798541236547, 'Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.'",
            "less than limit, +7984562356, 'Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.'",
            "spec simbol, +7&963256485, 'Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.'",
            "text phone number, +7e965463269, 'Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.'",
            "null, '', Поле обязательно для заполнения",
    })
    public void shouldTestIncorrectEnterPhone(String testName, String phone, String errors) {
        form.$("[data-test-id='name'] input").setValue("Аннф-Петровна");
        form.$("[data-test-id='phone'] input").setValue(phone);
        form.$("[data-test-id=agreement]").click();
        form.$(".button").click();

        String actual = form.$("[data-test-id=phone].input_invalid .input__sub").getText().trim();
        assertEquals(errors, actual);
    }

    @Test
    public void shouldNotClickCheckbox() {
        form.$("[data-test-id='name'] input").setValue("Анна");
        form.$("[data-test-id='phone'] input").setValue("+79865923654");
        form.$(".button").click();

        String actual = form.$("[data-test-id=agreement].input_invalid .checkbox__text").getText().trim();
        String expected = "Я соглашаюсь с условиями обработки и использования моих персональных данных и разрешаю сделать запрос в бюро кредитных историй";
        assertEquals(expected, actual);
    }
}
