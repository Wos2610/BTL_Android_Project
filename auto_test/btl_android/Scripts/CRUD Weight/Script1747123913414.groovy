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

Mobile.startApplication('D:\\PTIT\\IV.2\\IV.2_Mobile\\app-debug.apk', false)

Mobile.waitForElementPresent(findTestObject('Object Repository/log weight/android.widget.ImageButton'), 60)

Mobile.verifyElementExist(findTestObject('Object Repository/log weight/android.widget.ImageButton'), 0)

Mobile.tap(findTestObject('Object Repository/log weight/android.widget.ImageButton'), 0)

Mobile.tap(findTestObject('Object Repository/log weight/android.widget.TextView - Weight'), 0)

Mobile.tap(findTestObject('Object Repository/log weight/android.widget.EditText (1)'), 0)

Mobile.setText(findTestObject('Object Repository/log weight/android.widget.EditText'), '120', 0)

Mobile.tap(findTestObject('Object Repository/log weight/android.widget.TextView - May 13, 2025'), 0)

Mobile.tap(findTestObject('Object Repository/log weight/android.view.View - 10'), 0)

Mobile.tap(findTestObject('Object Repository/log weight/android.widget.Button - OK'), 0)

Mobile.tap(findTestObject('Object Repository/log weight/android.widget.Button - Save'), 0)

Mobile.tap(findTestObject('Object Repository/log weight/android.widget.TextView - 120.0 kg'), 0)

Mobile.tap(findTestObject('Object Repository/log weight/android.widget.Button - Delete'), 0)

Mobile.tap(findTestObject('Object Repository/log weight/android.widget.ImageButton'), 0)

Mobile.closeApplication()

