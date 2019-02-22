package cucumber.cukeulator.test;

import android.os.Bundle;

import cucumber.api.CucumberOptions;
import cucumber.api.android.CucumberAndroidJUnitRunner;

import java.io.File;

/**
 * Android JUnit compatible replacement for {@link cucumber.api.android.CucumberInstrumentation}.
 * The CucumberOptions annotation is mandatory for exactly one of the classes in the test project.
 * Only the first annotated class that is found will be used, others are ignored. If no class is
 * annotated, an exception is thrown.
*/
@CucumberOptions(
        features = "features"
)
public class CukeulatorAndroidJUnitRunner extends CucumberAndroidJUnitRunner {


    @Override
    public void onCreate(final Bundle bundle) {
        bundle.putString("plugin", getPluginConfigurationString()); // we programmatically create the plugin configuration
        super.onCreate(bundle);
    }

    /**
     * Since we want to checkout the external storage directory programmatically, we create the plugin configuration
     * here, instead of the {@link CucumberOptions} annotation.
     *
     * @return the plugin string for the configuration, which contains XML, HTML and JSON paths
     */
    private String getPluginConfigurationString() {
        String cucumber = "cucumber";
        String separator = "--";
        return "junit:" + getAbsoluteFilesPath() + "/" + cucumber + ".xml" + separator +
                        "html:" + getAbsoluteFilesPath() + "/" + cucumber + ".html" + separator +
                        "json:" + getAbsoluteFilesPath() + "/" + cucumber + ".json";
    }

    /**
     * The path which is used for the report files.
     *
     * @return the absolute path for the report files
     */
    private String getAbsoluteFilesPath() {

        //sdcard/Android/data/cucumber.cukeulator
        File directory = getTargetContext().getExternalFilesDir(null);
        return new File(directory,"reports").getAbsolutePath();
    }
}
