import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.testcontainers.containers.BrowserWebDriverContainer

environments {


    dockerChrome {
        driver = {
            def container = new BrowserWebDriverContainer()
                    .withCapabilities(new ChromeOptions())
            container.start()
            container.getWebDriver()
        }
    }
    dockerFirefox {
        driver = {
            def container = new BrowserWebDriverContainer()
                    .withCapabilities(new FirefoxOptions())
            container.start()
            container.getWebDriver()
        }
    }


    // run via “./gradlew -Dgeb.env=chrome iT”
    chrome {
        driver = { new ChromeDriver() }
    }

    // run via “./gradlew -Dgeb.env=chromeHeadless iT”
    chromeHeadless {
        driver = {
            ChromeOptions o = new ChromeOptions()
            o.addArguments('headless')
            new ChromeDriver(o)
        }
    }

    // run via “./gradlew -Dgeb.env=firefox iT”
    firefox {
        driver = { new FirefoxDriver() }
    }
}
