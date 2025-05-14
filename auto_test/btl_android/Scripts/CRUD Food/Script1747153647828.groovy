import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import com.kms.katalon.core.util.KeywordUtil as KeywordUtil

Mobile.startApplication('D:\\PTIT\\IV.2\\IV.2_Mobile\\app-debug.apk', false)


Mobile.waitForElementPresent(findTestObject('Object Repository/log weight/android.widget.ImageButton'), 60)

Mobile.verifyElementExist(findTestObject('Object Repository/log weight/android.widget.ImageButton'), 5)

Mobile.tap(findTestObject('Object Repository/crud food/android.widget.ImageButton'), 0)

Mobile.tap(findTestObject('Object Repository/crud food/android.widget.TextView - Log Food'), 0)

Mobile.tap(findTestObject('Object Repository/crud food/android.widget.TextView - My Foods'), 0)

Mobile.delay(1)

Mobile.tap(findTestObject('Object Repository/crud food/android.widget.TextView - Create New Food'), 0)

Mobile.delay(1)

Mobile.setText(findTestObject('Object Repository/crud food/android.widget.EditText - ex. Campbells'), '50Nam', 0)

Mobile.setText(findTestObject('Object Repository/crud food/android.widget.EditText - ex. Chicken Soup'), 'GiaiPhongMienNam', 
    0)

Mobile.setText(findTestObject('Object Repository/crud food/android.widget.EditText - unit(s)'), 'VN', 0)

Mobile.tap(findTestObject('Object Repository/crud food/android.widget.TextView - Next'), 0)

Mobile.delay(1)

Mobile.setText(findTestObject('Object Repository/crud food/android.widget.EditText - 0.0'), '1975', 0)

Mobile.setText(findTestObject('Object Repository/crud food/android.widget.EditText - 0.0 (1)'), '2025', 0)

Mobile.tap(findTestObject('Object Repository/crud food/android.widget.ImageView'), 0)

Mobile.delay(1)

Mobile.tap(findTestObject('Object Repository/crud food/android.widget.TextView - My Foods'), 0)

Mobile.delay(1)
Mobile.tap(findTestObject('Object Repository/crud food/android.widget.TextView - 50Nam'), 0)

Mobile.setText(findTestObject('Object Repository/crud food/android.widget.EditText - 50Nam'), '50 Nam', 0)

Mobile.tap(findTestObject('Object Repository/crud food/android.widget.TextView - Next'), 0)

Mobile.delay(1)

Mobile.tap(findTestObject('Object Repository/crud food/android.widget.ImageView'), 0)

Mobile.delay(1)
//
//Mobile.tap(findTestObject('Object Repository/crud food/android.widget.TextView - My Foods'), 0)
//
//int x = Mobile.getElementLeftPosition(findTestObject('Object Repository/crud food/android.widget.TextView - 1975.0'), 10)
//
//int y = Mobile.getElementTopPosition(findTestObject('Object Repository/crud food/android.widget.TextView - 1975.0'), 10)
//
//Mobile.delay(1)
//
//Mobile.swipe(x+100, y+10, -200, 0)

Mobile.closeApplication()