import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExampleScenarioTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @Before
    public void before(){
        System.setProperty("webdriver.chrome.driver", "webdriver/chromedriver.exe");
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setPageLoadStrategy(PageLoadStrategy.EAGER);
        driver = new ChromeDriver(chromeOptions);
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        wait = new WebDriverWait(driver, 10, 1000);

        String baseUrl = "https://www.rgs.ru/";
        driver.get(baseUrl);
    }

    @Test
    public void exampleScenario() throws InterruptedException {
        //выбрать пункт меню
        String insuranceButtonXPath = "//a[@data-toggle='dropdown' and @class='hidden-xs']";
        WebElement insuranceButton = driver.findElement(By.xpath(insuranceButtonXPath));
        insuranceButton.click();

        //выбрать пункт меню "ДМС"
        String rgsInsuranceButtonXPath = "//a[@href='https://www.rgs.ru/products/private_person/health/dms/generalinfo/index.wbp']";
        WebElement travellersInsuranceButton = driver.findElement(By.xpath(rgsInsuranceButtonXPath));
        travellersInsuranceButton.click();

        Thread.sleep(1000);

        //Проверка заголовка
        WebElement h1Zagolovok = driver.findElement(By.xpath("//h1"));
        Assert.assertEquals("Заголовок отсутствует/не соответствует требуемому",
                "ДМС — добровольное медицинское страхование", h1Zagolovok.getText());

        //Нажать "Оставить заявку"
        String rgsRequestButtonXPath = "//a[@data-gtm-category='gtm_category'  and @class='btn btn-default text-uppercase hidden-xs adv-analytics-navigation-desktop-floating-menu-button']";
        WebElement rgsRequestButton = driver.findElement(By.xpath(rgsRequestButtonXPath));
        rgsRequestButton.click();

        //Подождать 2 секунды
        Thread.sleep(2000);

        //Проверка заголовка окна заявки
        WebElement bZagolovok = driver.findElement(By.xpath("//b"));
        Assert.assertEquals("Заголовок отсутствует/не соответствует требуемому",
                "Заявка на добровольное медицинское страхование", bZagolovok.getText());


        String checkboxXPath = "//input[@class='checkbox']";
        WebElement insuranceCheckbox = driver.findElement(By.xpath(checkboxXPath));
        insuranceCheckbox.click();

        String fieldXPath = "//input[@name='%s']";
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "LastName"))), "Застрахованный");
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "FirstName"))), "Степан");
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "MiddleName"))), "Винеаминович");
        String regionXPath = "//select[@name='Region']";
        String regionMoscowXPath = "//select[@name='Region']/option[@value='77']";
        WebElement insurRegion = driver.findElement(By.xpath(regionXPath));
        WebElement insurMoscow = driver.findElement(By.xpath(regionMoscowXPath));
        insurRegion.click();
        Thread.sleep(500);
        insurMoscow.click();
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "Email"))), "gmail123");
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "ContactDate"))), "02.12.2020");
        fillInputPhone(driver.findElement(By.xpath("//label[text()='Телефон']/following-sibling::input")), "(999) 666-55-44");

        String sendButtonXPath = "//button[text()=' Отправить ']";
        WebElement sendButton = driver.findElement(By.xpath(sendButtonXPath));
        sendButton.click();

        String errorAlertXPath = "//label[text()='Эл. почта']/following-sibling::div/label/span[@class='validation-error-text']";
        WebElement errorAlert = driver.findElement(By.xpath(errorAlertXPath));
        scrollToElementJs(errorAlert);
        waitUtilElementToBeVisible(errorAlert);
        Assert.assertEquals("Проверка ошибки у alert на странице не была пройдено",
                "Введите адрес электронной почты", errorAlert.getText());
    }

    @After
    public void after() {
//        driver.quit();
    }

    private void scrollToElementJs(WebElement element) {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        javascriptExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    private void waitUtilElementToBeClickable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    private void fillInputField(WebElement element, String value) {
        scrollToElementJs(element);
        waitUtilElementToBeClickable(element);
        element.click();
        element.sendKeys(value);
        Assert.assertEquals("Поле было заполнено некорректно",
                value, element.getAttribute("value"));
    }

    private void fillInputPhone(WebElement element, String value) {
        scrollToElementJs(element);
        waitUtilElementToBeClickable(element);
        element.click();
        element.sendKeys(value);
        element.sendKeys(Keys.chord(Keys.ENTER));
        Assert.assertEquals("Поле было заполнено некорректно",
                "+7 "+value, element.getAttribute("value"));
    }

    private void waitUtilElementToBeVisible(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }
}
