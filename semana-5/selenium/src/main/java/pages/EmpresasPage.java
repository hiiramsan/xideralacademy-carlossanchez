package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class EmpresasPage extends BasePage {

    public static final By EMPRESAS_Y_GOBIERNO = By.cssSelector("a[href*='/bei/home']");

    public static final By MULTINACIONALES = By.cssSelector("a[href*='multinacionales']");

    public EmpresasPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToLink(By locator) {
        click(locator);
    }
}