package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class BancaPrivadaPage extends BasePage {

    public static final By BANCA_PRIVADA = By.cssSelector("a[href*='/bp/home']");

    public BancaPrivadaPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToLink(By locator) {
        click(locator);
    }
}