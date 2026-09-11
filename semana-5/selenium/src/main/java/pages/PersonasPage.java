package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PersonasPage extends BasePage {

    public static final By TARJETAS_DE_CREDITO = By.cssSelector("a[href*='/tarjetas-de-credito/']");

    public static final By CREDITO_PERSONAL = By.cssSelector("a[href*='/creditos-personales/']");

    public static final By CREDITO_HIPOTECARIO = By.cssSelector("a[href*='/creditos-hipotecarios/']");

    public static final By SIMULADOR_HIPOTECA = By.cssSelector("a[href*='/simulador-hipotecario/']");

    public static final By CREDITO_AUTOMOTRIZ = By.cssSelector("a[href*='/credito-automotriz/']");

    public static final By BURO_DE_CREDITO = By.cssSelector("a[href*='/buro-de-credito/']");

    public static final By SANTANDER_DIGITAL = By.cssSelector("a[href*='/santander-digital']");

    public static final By APP_SANTANDER = By.cssSelector("a[href*='/app-santander']");

    public static final By SANTANDER_WEB = By.cssSelector("a[href*='/santander-web']");

    public static final By CUENTAS = By.cssSelector("a[href*='/personas/cuentas']");

    public static final By BASICA = By.cssSelector("a[href*='cuentas/basica']");

    public static final By NOMINA = By.cssSelector("a[href*='/basica-nomina']");

    public static final By CHEQUES = By.cssSelector("a[href*='/cheque-saldo-promedio/']");

    public static final By PORTABILIDAD_NOMINA = By.cssSelector("a[href*='/portabilidad-de-nomina']");

    public static final By INVERSIONES = By.cssSelector("a[href*='/personas/inversiones']");

    public static final By SEGUROS = By.cssSelector("a[href*='/personas/seguros']");

    public PersonasPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToLink(By locator) {
        click(locator);
    }
}