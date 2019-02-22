package cucumber.api.android;

import android.os.Bundle;
import androidx.test.runner.AndroidJUnitRunner;
import cucumber.runtime.android.CucumberJUnitRunnerBuilder;

/**
 * Android Orchestrator compatible replacement for {@link cucumber.api.android.CucumberInstrumentation}.
 */
public class CucumberAndroidJUnitRunner extends AndroidJUnitRunner {

    private static final String ARGUMENT_ORCHESTRATOR_RUNNER_BUILDER = "runnerBuilder";
    private static final String ARGUMENT_ORCHESTRATOR_TEST_CLASS = "class";
    public static final String ARGUMENT_CUCUMBER_SCENARIO = "scenario";

    private Bundle arguments;

    @Override
    public void onCreate(final Bundle bundle) {
        bundle.putString(ARGUMENT_ORCHESTRATOR_RUNNER_BUILDER, CucumberJUnitRunnerBuilder.class.getName());
        interceptClassArgument(bundle);
        arguments = bundle;

        super.onCreate(bundle);
    }

    private void interceptClassArgument(final Bundle bundle) {
        String testClass = bundle.getString(ARGUMENT_ORCHESTRATOR_TEST_CLASS);
        if(testClass != null && !testClass.isEmpty()) {
            bundle.remove(ARGUMENT_ORCHESTRATOR_TEST_CLASS);
            bundle.putString(ARGUMENT_CUCUMBER_SCENARIO, testClass);
        }
    }

    public Bundle getArguments() {
        return arguments;
    }
}
