package td2.seckilltd;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.*;

/**
 * It is a simple script for tiny--deal online shop flash sales (seckills). Not
 * for those with captcha entering, but for deals which need coupon code. So, it
 * simply logins (enter your email and pass in proper places), refreshes countdown page (as it glitches and doesn't refresh
 * automatically when counter comes to 00:00), gets coupon code and enters on checkout window. 
 * Before the script you have to add seckill item to shopping cart.
 * After the script you need to see whether the price changed to 0.01 and confirm payment manually. 
 * The script works, but I wasn't lucky as it is a popular shop and thousands of ppl try to win.
 * 
 * You need to find out what quickjava is and how to download it.
 * Written in a testing manner, as I've just started learning automation ;)
 */

public class AppTest {

	private WebDriver driver;

	@BeforeTest
	public void setUp() {
		// DON'T FORGET TO ADD SECKILL ITEM TO CART MANUALLY
		
		// DISABLING IMAGES AND STARTING FirefoxDriver
		// profile.setPreference("permissions.default.image", 2); -- not working anymore
		final String quickjavaPath = "D:\\quickjava-2.0.6-fx.xpi"; //change this location if needed
		
		FirefoxProfile profile = new FirefoxProfile();
		try {
			profile.addExtension(new File(quickjavaPath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		profile.setPreference("thatoneguydotnet.QuickJava.curVersion", "2.0.6.1");
		profile.setPreference("thatoneguydotnet.QuickJava.startupStatus.Images", 2);
		profile.setPreference("thatoneguydotnet.QuickJava.startupStatus.AnimatedImage", 2);

		driver = new FirefoxDriver(profile);
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(500, TimeUnit.MILLISECONDS);

		// LOGIN
		driver.get("https://www.tinydeal.com/index.php?main_page=login");
		WebElement email = driver.findElement(By.id("login-email-address"));
		email.sendKeys("YOUREMAIL"); // change to your email
		WebElement pass = driver.findElement(By.id("login-password"));
		pass.sendKeys("YOURPASS"); // change to your password
		pass.submit();
	}

	@Test
	public void testBuy() {
		// GETTING SALE PAGE
		driver.get("http://www.tinydeal.com/summer-clearance-sale-si-4359.html"); //change this to new seckill page

		String seckillWindow = driver.getWindowHandle();

		// OPENING NEW WINDOW, COS WITH TABS NO SWITCH POSSIBLE
		JavascriptExecutor js = null;
		if (driver instanceof JavascriptExecutor) {
			js = (JavascriptExecutor) driver;
		}
		js.executeScript("window.open('http://www.tinydeal.com/index.php?main_page=shopping_cart','_blank');");
		// stops here for some reason, let's handle windows another way using Set

		Set<String> windows = driver.getWindowHandles();
		String checkoutWindow = ((String) windows.toArray()[1]);

		// PREPARING CHECKOUT PAGE
		driver.switchTo().window(checkoutWindow);
		driver.findElement(By.xpath("//*[@id='paypal_express_checkout']/div[1]/div[2]/a/img")).click();
		driver.findElement(By.xpath("//*[@id='order_information']/fieldset[1]/legend/a/span")).click();

		// GOING BACK TO SECKILL PAGE AND REFRESHING IT LIKE CRAZY
		driver.switchTo().window(seckillWindow);
		while (!(driver.findElements(By.cssSelector(".coupon-area>span")).size() > 0)) {
			driver.navigate().refresh();
		}
		
		// GETTING COUPON CODE
		WebElement span = driver.findElement(By.cssSelector(".coupon-area>span"));
		String spanText = span.getText();

		// BACK TO CHECKOUT PAGE AND INSERTING COUPON CODE
		driver.switchTo().window(checkoutWindow);
		driver.findElement(By.id("disc-ot_coupon")).sendKeys(spanText);
		driver.findElement(By.xpath(".//*[@id='coupon_fields0']/input[2]")).click();

		// YOU NEED TO SEE WHETHER THE PRICE CHANGED TO 0.01 AND CONFIRM PAYMENT MANUALLY
	}

}
