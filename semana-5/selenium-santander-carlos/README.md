# Semana 5: Selenium Framework + TestNG (Automatización de página Santander)


El siguiente proyecto es la implementación del framework de automatización de pruebas de Selenium y TestNG, diseñado en base al patrón Page Object Model (POM)

---

**Automation Testing:** Es el proceso de ejecutar pruebas mediante scripts. Incluye datos, acciones y validacion de datos esperados. 

### Selenium

Selenium es una librería que permite controlar un navegador mediante código: abre páginas, encuentra elementos, escribe, hace clic, valida y
simula la interacción de un usuario real con la aplicación web.

**Locators**: permiten identificar elementos dentro del DOM (estrategia)

**Selector**: valor o exresion que identifica

### TestNG

**TestNG** es un framework de pruebas basado en anotaciones (`@Test`,
`@BeforeMethod`, `@AfterMethod`) que permite organizar escenarios, ejecutarlos
en orden, integrar listeners y generar reportes HTML en `test-output/`.

---

### Funcionamiento de Selenium WebDriver

Selenium automatiza navegadores reales (Chrome, Firefox, Edge). En este
proyecto el driver de Chrome se crea a través de `DriverFactory`

```java
public static WebDriver createChromeDriver() {
    ...
    WebDriver driver = new ChromeDriver(options);
    driver.manage().window().maximize();
    return driver;
}
```

### Automatizacion

Las pruebas automatizadas siguen este ciclo:
1. Localizar el elemento
2. Interactuar
3. Esperar
4. Verificar

`BaseTest` abre el navegador (`@BeforeMethod`) y lo cierra (`@AfterMethod`):

```java
@BeforeMethod(alwaysRun = true)
public void setUp() {
    driver = DriverFactory.createChromeDriver();
    driver.get("https://www.santander.com.mx/");
}

@AfterMethod(alwaysRun = true)
public void tearDown() {
    if (driver != null) {
        driver.quit();
    }
}
```

---

### Finding Elements (Locators)

Los localizadores se usan con `By` y se guardan como constantes en la página.

### ID

```java
private static final By NAME = By.id("name");
private static final By COUNTRY = By.id("country");
```

### CSS Selectors

```java
private static final By FIRST_PRODUCT =
        By.cssSelector("#productTable tbody tr:first-child input[type='checkbox']");
private static final By ACERCA_MENU = By.cssSelector("a[href*='/fundacion-santander']");
```

### XPath (No recomendada)

```java
private static final By LAPTOPS_OPTION = By.xpath(
        "//div[contains(@class,'dropdown-content')]//a[normalize-space()='Laptops']");
private static final By COPY_BUTTON =
        By.xpath("//button[normalize-space()='Copy Text']");
```

### ¿Cual usar?

- **ID / Name**: el más confiable si son únicos y estables.
- **CSS Selector**: rápido y expresivo por atributo (`[href*='...']`) o por
  posición (`tr:first-child`).
- **XPath**: ideal para texto visible, posiciones relativas o estructuras
  complejas. Evitar su uso pues es muy relativa, siempre se busca trabajar con ID's o css selectors

---

### Manejo de elementos

### Inputs

```java
protected void write(By locator, String value) {
    WebElement element = find(locator);
    element.clear();
    element.sendKeys(value);
}
```

### Dropdowns

```java
protected void selectByText(By locator, String value) {
    new Select(find(locator)).selectByVisibleText(value);
}
```

Para selecciones múltiples se crea el `Select` y se elige cada opción:

```java
Select colorList = new Select(find(COLORS));
colorList.selectByVisibleText("Blue");
colorList.selectByVisibleText("Green");
```

### Waits

`BasePage` crea un `WebDriverWait` de 12 segundos y métodos como `find()`, que
espera a que el elemento sea visible antes de devolverlo:

```java
protected WebElement find(By locator) {
    return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
}
```

## Page Object Model (POM)

El Page Object Model es un patron de diseño que separa los elementos y acciones de una pagina delcodigo de prueba, reduciendo duplicidad y facilitando el mantenimiento.

Arquitectura POM:

Test -> PageObject -> WebDriver -> Browser 

### `BasePage`

Clase base con las operaciones reutilizables (find, click, write, text,
select, waits). Las páginas la extienden:

```java
public abstract class BasePage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final Actions actions;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        this.actions = new Actions(driver);
    }
    ...
}
```

### `pages.*` (Page Objects)

Cada página encapsula **localizadores** (constantes `By`) y **acciones**
(métodos del dominio). Ejemplo de `HomePage`:

```java
public class HomePage extends BasePage {
    private static final By PERSONAS = By.id("firstLevel-mainItem-0-menu-button");

    public PersonasPage openPersonasNavbar() {
        click(PERSONAS);
        wait.until(ExpectedConditions.visibilityOfElementLocated(PERSONAS_MENU));
        return new PersonasPage(driver);
    }
}
```

Los métodos devuelven la siguiente página del flujo (fluent POM).

### `utils.DriverFactory`

Fábrica que crea y configura el `WebDriver` (headless, maximizado, Selenium
Manager).

### `base.BaseTest`

Clase base de pruebas con **setup/teardown** de TestNG. Los tests la extienden
y heredan `driver`.

### `tests.*`

Clases de **escenarios** con anotaciones `@Test` y aserciones de TestNG. No
contienen localizadores; solo orquestan páginas:

```java
@Test
public void shouldNavigateToPersonasLinks() {
    assertEachLinkNavigates(
        List.of(PersonasPage.TARJETAS_DE_CREDITO, PersonasPage.NOMINA, ...),
        link -> new HomePage(driver).openPersonasNavbar().navigateToLink(link)
    );
}
```

### `listeners.ScreenshotListener`

Implementa `ITestListener` y, en `onTestFailure`, captura la pantalla del
driver y guarda un PNG en `test-output/screenshots/` con marca de tiempo.
Se registra en `testng.xml`.

### `testng.xml`

Suite de TestNG que define el orden de ejecución y los listeners:

```xml
<suite name="Automation Practice - POM" parallel="false">
    <listeners>
        <listener class-name="listeners.ScreenshotListener"/>
    </listeners>
    <test name="Recorrido completo">
        <classes>
            <class name="tests.AutomationLinksTest"/>
        </classes>
    </test>
</suite>
```

---

## Utilidad del proyecto

- La finalidad del proyecto fue utilizar los conocimientos aprendidos de Selenium + TestNG + POM para automatizar la navegación de una pagina, en este caso la de santander, con el objetivo de verificar que todos los links del navbar de la pagina redirigen a su pagina correspondiente

## Setup / Instalacion

1. **Java 21** y **Maven** instalados.
2. Las dependencias se descargan desde Maven Central (no se requiere
   ChromeDriver manual: Selenium Manager lo gestiona).
3. Clonar e instalar:

```bash
mvn clean test
```

- Si Docker/no se quiere ver el navegador:

```bash
mvn clean test -Dheadless=true
```

- Para generar una falla intencional y probar la captura de pantalla:

```bash
mvn clean test -DforceFailure=true
```

### Eclipse

1. Importar como `Existing Maven Project`.
2. Ejecutar `Maven > Update Project`.
3. Abrir `testng.xml` → `Run As > TestNG Suite`.

Resultados en:
- `test-output/index.html`
- `test-output/emailable-report.html`
- `test-output/screenshots/` (capturas de errores)

---