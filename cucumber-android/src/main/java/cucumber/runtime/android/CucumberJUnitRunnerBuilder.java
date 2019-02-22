package cucumber.runtime.android;

import org.junit.runner.Runner;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runners.model.RunnerBuilder;

public class CucumberJUnitRunnerBuilder extends RunnerBuilder {
    @Override
    public Runner runnerForClass(Class<?> testClass) throws NoTestsRemainException {
        if (testClass.equals(getClass())) {
            return new CucumberJUnitRunner(testClass);
        }

        return null;
    }
}
