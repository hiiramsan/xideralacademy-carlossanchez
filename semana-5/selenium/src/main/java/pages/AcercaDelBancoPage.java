package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AcercaDelBancoPage extends BasePage {

    public static final By FUNDACION_SANTANDER = By.cssSelector("a[href*='fundacion-santander']");

    public static final By SOSTENIBILIDAD = By.cssSelector("a[href*='responsabilidad-social']");

    public static final By EDUCACION_FINANCIERA = By.cssSelector("a[href*='/educacion-financiera']");

    public static final By INVERSIONISTAS = By.cssSelector("a[href*='/ir/home']");

    public static final By SALA_COMUNICACION = By.cssSelector("a[href*='sala_prensa']");

    public static final By BOLSA_TRABAJO = By.cssSelector("a[href*='bolsa-de-trabajo']");

    public static final By BLOG = By.cssSelector("a[href*='/personas/blog']");

    public AcercaDelBancoPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToLink(By locator) {
        click(locator);
    }
}