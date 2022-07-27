package io.github.pmckeown.dependencytrack.finding.report;

import io.github.pmckeown.dependencytrack.DependencyTrackException;
import io.github.pmckeown.dependencytrack.finding.Finding;
import io.github.pmckeown.dependencytrack.finding.FindingThresholds;
import io.github.pmckeown.dependencytrack.policy.PolicyViolation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static io.github.pmckeown.dependencytrack.finding.FindingListBuilder.aListOfFindings;
import static io.github.pmckeown.dependencytrack.policy.PolicyViolationListBuilder.aListOfPolicyViolations;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@RunWith(MockitoJUnitRunner.class)
public class FindingsReportGeneratorTest {

    @InjectMocks
    private FindingsReportGenerator findingsReportGenerator;

    @Mock
    private XmlReportWriter xmlReportWriter;

    @Mock
    private HtmlReportWriter htmlReportWriter;

    @Test
    public void thatBothReportsAreGenerated() throws Exception {
        FindingThresholds findingThresholds = new FindingThresholds(1, null, null, null, null);
        List<Finding> findings = aListOfFindings().build();
        List<PolicyViolation> policyViolations = aListOfPolicyViolations().build();
        findingsReportGenerator.generate(null, findings, findingThresholds, false, policyViolations);

        verify(xmlReportWriter).write(isNull(), any(FindingsReport.class));
        verify(htmlReportWriter).write(isNull());
    }

    @Test
    public void thatExceptionWhenWritingXmlReportIsHandledAndHtmlIsNotAttempted() throws Exception {
        FindingThresholds findingThresholds = new FindingThresholds(1, null, null, null, null);
        List<Finding> findings = aListOfFindings().build();

        doThrow(DependencyTrackException.class).when(xmlReportWriter).write(isNull(), any(FindingsReport.class));

        try {
            findingsReportGenerator.generate(null, findings, findingThresholds, false, new ArrayList<>());
            fail("Exception expected");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(DependencyTrackException.class)));
        }

        verifyNoInteractions(htmlReportWriter);
    }

    @Test
    public void thatExceptionWhenWritingHtmlReportIsHandled() throws Exception {
        FindingThresholds findingThresholds = new FindingThresholds(1, null, null, null, null);
        List<Finding> findings = aListOfFindings().build();

        doThrow(DependencyTrackException.class).when(htmlReportWriter).write(isNull());

        try {
            findingsReportGenerator.generate(null, findings, findingThresholds, false, new ArrayList<>());
            fail("Exception expected");
        } catch (Exception e) {
            assertThat(e, is(instanceOf(DependencyTrackException.class)));
        }

        verify(xmlReportWriter).write(isNull(), any(FindingsReport.class));
    }
}