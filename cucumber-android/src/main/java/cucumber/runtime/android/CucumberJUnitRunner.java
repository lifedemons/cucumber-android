package cucumber.runtime.android;

import androidx.test.platform.app.InstrumentationRegistry;
import cucumber.api.android.CucumberAndroidJUnitRunner;
import cucumber.runtime.formatter.UniqueTestNameProvider;
import gherkin.events.PickleEvent;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;

import android.os.Bundle;

import java.util.Iterator;
import java.util.List;

public class CucumberJUnitRunner extends Runner implements Filterable {

    private final CucumberExecutor cucumberExecutor;

    private final List<PickleEvent> pickleEvents;

    private final UniqueTestNameProvider<PickleEvent> uniqueTestNameProvider = new UniqueTestNameProvider<>();

    public CucumberJUnitRunner(@SuppressWarnings("unused") Class testClass)
            throws NoTestsRemainException {
        CucumberAndroidJUnitRunner instrumentationRunner = (CucumberAndroidJUnitRunner) InstrumentationRegistry
                .getInstrumentation();
        Bundle arguments = instrumentationRunner.getArguments();
        cucumberExecutor = new CucumberExecutor(new Arguments(arguments),
                                                instrumentationRunner);
        pickleEvents = cucumberExecutor.getPickleEvents();
        filterScenarios(arguments);
    }

    private void filterScenarios(final Bundle arguments) throws NoTestsRemainException {
        String scenarioQuery = arguments.getString(CucumberAndroidJUnitRunner.ARGUMENT_CUCUMBER_SCENARIO);
        if (scenarioQuery != null && !scenarioQuery.isEmpty()) {
            Description targetDescription = getDescriptionFromScenarioQuery(scenarioQuery);

            filter(Filter.matchMethodDescription(targetDescription));
        }
    }

    private Description getDescriptionFromScenarioQuery(final String scenarioQuery) {
        int indexOfFeatureAndScenarioSeparator = scenarioQuery.indexOf("#");
        String featureName = scenarioQuery.substring(0, indexOfFeatureAndScenarioSeparator);
        String scenarioName = scenarioQuery.substring(indexOfFeatureAndScenarioSeparator + 1);

        return Description.createTestDescription(featureName, scenarioName, scenarioName);
    }

    @Override
    public Description getDescription() {
        Description rootDescription = Description.createSuiteDescription("All tests", 1);

        for (PickleEvent pickleEvent : pickleEvents) {
            rootDescription.addChild(makeDescriptionFromPickle(pickleEvent));
        }
        return rootDescription;
    }

    private Description makeDescriptionFromPickle(PickleEvent pickleEvent) {
        String testName = uniqueTestNameProvider.calculateUniqueTestName(pickleEvent, pickleEvent.pickle.getName(), pickleEvent.uri);
        return Description.createTestDescription(getClassNameFromUri(pickleEvent.uri), testName, testName);
    }

    private String getClassNameFromUri(final String pickleUri) {
        return pickleUri.replaceAll("/", "_");
    }

    @Override
    public void run(final RunNotifier notifier) {
        cucumberExecutor.execute();
    }

    @Override
    public int testCount() {
        return pickleEvents.size();
    }

    @Override
    public void filter(final Filter filter) throws NoTestsRemainException {
        for (Iterator<PickleEvent> iterator = pickleEvents.iterator(); iterator.hasNext(); ) {
            PickleEvent method = iterator.next();
            if (!filter.shouldRun(makeDescriptionFromPickle(method))) {
                iterator.remove();
            }
        }

        if (pickleEvents.isEmpty()) {
            throw new NoTestsRemainException();
        }
    }

}
