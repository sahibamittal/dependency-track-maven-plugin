package io.github.pmckeown.dependencytrack.finding.report;

import io.github.pmckeown.dependencytrack.finding.Finding;
import io.github.pmckeown.dependencytrack.finding.FindingThresholds;
import io.github.pmckeown.dependencytrack.finding.Severity;
import io.github.pmckeown.dependencytrack.policy.Policy;
import io.github.pmckeown.dependencytrack.policy.PolicyViolation;
import org.junit.Test;

import java.util.List;

import static io.github.pmckeown.dependencytrack.finding.AnalysisBuilder.anAnalysis;
import static io.github.pmckeown.dependencytrack.finding.ComponentBuilder.aComponent;
import static io.github.pmckeown.dependencytrack.finding.FindingBuilder.aFinding;
import static io.github.pmckeown.dependencytrack.finding.FindingListBuilder.aListOfFindings;
import static io.github.pmckeown.dependencytrack.finding.VulnerabilityBuilder.aVulnerability;
import static io.github.pmckeown.dependencytrack.policy.PolicyConditionBuilder.aPolicyCondition;
import static io.github.pmckeown.dependencytrack.policy.PolicyViolationBuilder.aPolicyViolation;
import static io.github.pmckeown.dependencytrack.policy.PolicyViolationListBuilder.aListOfPolicyViolations;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FindingsReportTest {

    @Test
    public void thatAFindingsReportCanBeGenerated() {
        FindingThresholds findingThresholds = new FindingThresholds(1, null, null, null, null);
        List<Finding> findings = aListOfFindings()
                .withFinding(aFinding()
                        .withAnalysis(anAnalysis())
                        .withVulnerability(aVulnerability())
                        .withComponent(aComponent()
                                .withName("shonky-lib")))
                .build();
        List<PolicyViolation> policyViolations = aListOfPolicyViolations()
                .withPolicyViolation(aPolicyViolation()
                        .withType("SEVERITY")
                        .withPolicyCondition(aPolicyCondition()
                                .withPolicy(new Policy("testPolicy1", "INFO")))
                        .withComponent(aComponent())).build();
        FindingsReport findingsReport = new FindingsReport(findingThresholds, findings, true, policyViolations);

        assertThat(findingsReport.getCritical().getCount(), is(equalTo(1)));
        assertThat(findingsReport.getCritical().getFindings().get(0).getComponent().getName(),
                is(equalTo("shonky-lib")));
        assertThat(findingsReport.getPolicyViolationWrapper().getPolicyViolations().get(0).getPolicyCondition().getPolicy().getName(),
                is(equalTo("testPolicy1")));
    }

    @Test
    public void thatAFindingsAreSortedIntoSeparateBuckets() {
        FindingThresholds findingThresholds = new FindingThresholds(1, null, null, null, null);
        List<Finding> findings = aListOfFindings()
                .withFinding(aFinding()
                        .withAnalysis(anAnalysis())
                        .withVulnerability(aVulnerability().withSeverity(Severity.CRITICAL))
                        .withComponent(aComponent()))
                .withFinding(aFinding()
                        .withAnalysis(anAnalysis())
                        .withVulnerability(aVulnerability().withSeverity(Severity.HIGH))
                        .withComponent(aComponent()))
                .withFinding(aFinding()
                        .withAnalysis(anAnalysis())
                        .withVulnerability(aVulnerability().withSeverity(Severity.MEDIUM))
                        .withComponent(aComponent()))
                .withFinding(aFinding()
                        .withAnalysis(anAnalysis())
                        .withVulnerability(aVulnerability().withSeverity(Severity.LOW))
                        .withComponent(aComponent()))
                .withFinding(aFinding()
                        .withAnalysis(anAnalysis())
                        .withVulnerability(aVulnerability().withSeverity(Severity.UNASSIGNED))
                        .withComponent(aComponent()))
                .build();
        List<PolicyViolation> policyViolations = aListOfPolicyViolations()
                .withPolicyViolation(aPolicyViolation()
                        .withType("SEVERITY")
                        .withPolicyCondition(aPolicyCondition()
                                .withPolicy(new Policy("testPolicy1", "INFO")))
                        .withComponent(aComponent()))
                .withPolicyViolation(aPolicyViolation()
                        .withType("SEVERITY")
                        .withPolicyCondition(aPolicyCondition()
                                .withPolicy(new Policy("testPolicy2", "WARN")))
                        .withComponent(aComponent()))
                .build();
        FindingsReport findingsReport = new FindingsReport(findingThresholds, findings, true, policyViolations);

        assertThat(findingsReport.getCritical().getCount(), is(equalTo(1)));
        assertThat(findingsReport.getHigh().getCount(), is(equalTo(1)));
        assertThat(findingsReport.getMedium().getCount(), is(equalTo(1)));
        assertThat(findingsReport.getLow().getCount(), is(equalTo(1)));
        assertThat(findingsReport.getUnassigned().getCount(), is(equalTo(1)));
        assertThat(findingsReport.getPolicyViolationWrapper().getCount(), is(equalTo(2)));
    }

}