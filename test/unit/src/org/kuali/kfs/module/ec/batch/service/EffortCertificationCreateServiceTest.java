/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.effort.service;

import static org.kuali.test.fixtures.UserNameFixture.KULUSER;

import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.util.ObjectUtil;
import org.kuali.module.effort.EffortConstants;
import org.kuali.module.effort.bo.EffortCertificationDetailBuild;
import org.kuali.module.effort.bo.EffortCertificationDocumentBuild;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.effort.document.EffortCertificationDocument;
import org.kuali.module.gl.web.TestDataGenerator;
import org.kuali.test.ConfigureContext;
import org.kuali.test.util.TestDataPreparator;

@ConfigureContext(session = KULUSER)
public class EffortCertificationCreateServiceTest extends KualiTestBase {

    private final Properties properties, message;
    private final String detailFieldNames, documentFieldNames, reportDefinitionFieldNames, documentHeaderFieldNames;
    private final String deliminator;

    private BusinessObjectService businessObjectService;
    private EffortCertificationCreateService effortCertificationCreateService;

    /**
     * Constructs a EffortCertificationCreateServiceTest.java.
     */
    public EffortCertificationCreateServiceTest() {
        super();
        String messageFileName = "test/src/org/kuali/module/effort/testdata/message.properties";
        String propertiesFileName = "test/src/org/kuali/module/effort/testdata/effortCertificationCreateService.properties";

        TestDataGenerator generator = new TestDataGenerator(propertiesFileName, messageFileName);
        properties = generator.getProperties();
        message = generator.getMessage();

        deliminator = properties.getProperty("deliminator");

        detailFieldNames = properties.getProperty("detailFieldNames");
        documentFieldNames = properties.getProperty("documentFieldNames");
        reportDefinitionFieldNames = properties.getProperty("reportDefinitionFieldNames");
        documentHeaderFieldNames = properties.getProperty("documentHeaderFieldNames");
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        effortCertificationCreateService = SpringContext.getBean(EffortCertificationCreateService.class);
    }

    /**
     * check if the service can approperiately handle the input parameters
     * 
     * @see EffortCertificationCreateService.create(Integer, String)
     */
    public void testInputParameters_ValidParameters() throws Exception {
        String testTarget = "inputParameters.validParameters.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + "fiscalYear")));
        String reportNumber = properties.getProperty(testTarget + "reportNumber");

        TestDataPreparator.doCleanUpWithReference(EffortCertificationDocument.class, properties, "documentCleanup", documentFieldNames, deliminator);
        TestDataPreparator.doCleanUpWithReference(EffortCertificationDocumentBuild.class, properties, "documentCleanup", documentFieldNames, deliminator);

        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition("");
        reportDefinition = TestDataPreparator.persistDataObject(reportDefinition);

        EffortCertificationDocumentBuild documentBuild = this.buildDocumentBuild(testTarget);
        documentBuild = TestDataPreparator.persistDataObject(documentBuild);

        try {
            effortCertificationCreateService.create(fiscalYear, reportNumber);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail(message.getProperty("error.validParameters"));
        }
    }

    /**
     * check if the service can approperiately handle the input parameters
     * 
     * @see EffortCertificationCreateService.create(Integer, String)
     */
    public void testInputParameters_EmptyFiscalYear() throws Exception {
        String testTarget = "inputParameters.emptyFiscalYear.";
        String reportNumber = properties.getProperty(testTarget + "reportNumber");

        try {
            effortCertificationCreateService.create(null, reportNumber);
            fail(message.getProperty("error.emptyFiscalYear"));
        }
        catch (Exception e) {
        }
    }

    /**
     * check if the service can approperiately handle the input parameters
     * 
     * @see EffortCertificationCreateService.create(Integer, String)
     */
    public void testInputParameters_EmptyReportNumber() throws Exception {
        String testTarget = "inputParameters.emptyReportNumber.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + "fiscalYear")));
        String reportNumber = properties.getProperty(testTarget + "reportNumber");

        try {
            effortCertificationCreateService.create(fiscalYear, reportNumber);
            fail(message.getProperty("error.emptyReportNumber"));
        }
        catch (Exception e) {
        }
    }

    /**
     * check if the service can approperiately handle the input parameters
     * 
     * @see EffortCertificationCreateService.create(Integer, String)
     */
    public void testInputParameters_UndefinedReportDefinition() throws Exception {
        String testTarget = "inputParameters.undefinedReportDefinition.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + "fiscalYear")));
        String reportNumber = properties.getProperty(testTarget + "reportNumber");

        try {
            effortCertificationCreateService.create(fiscalYear, reportNumber);
            fail(message.getProperty("error.undefinedReportDefinition"));
        }
        catch (Exception e) {
        }
    }

    /**
     * check if the service can approperiately handle the input parameters
     * 
     * @see EffortCertificationCreateService.create(Integer, String)
     */
    public void testInputParameters_InactiveReportDefinition() throws Exception {
        String testTarget = "inputParameters.inactiveReportDefinition.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + "fiscalYear")));
        String reportNumber = properties.getProperty(testTarget + "reportNumber");

        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition(testTarget);
        reportDefinition = TestDataPreparator.persistDataObject(reportDefinition);

        try {
            effortCertificationCreateService.create(fiscalYear, reportNumber);
            fail(message.getProperty("error.inactiveReportDefinition"));
        }
        catch (Exception e) {
        }
    }
    
    /**
     * check if the service can approperiately handle the input parameters
     * 
     * @see EffortCertificationCreateService.create(Integer, String)
     */
    public void testInputParameters_NotOpenedReportDefinition() throws Exception {
        String testTarget = "inputParameters.notOpenedReportDefinition.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + "fiscalYear")));
        String reportNumber = properties.getProperty(testTarget + "reportNumber");

        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition(testTarget);
        reportDefinition = TestDataPreparator.persistDataObject(reportDefinition);

        try {
            effortCertificationCreateService.create(fiscalYear, reportNumber);
            fail(message.getProperty("error.notOpenedReportDefinition"));
        }
        catch (Exception e) {
        }
    }
    
    /**
     * check if the service can approperiately handle the input parameters
     * 
     * @see EffortCertificationCreateService.create(Integer, String)
     */
    public void testInputParameters_ClosedReportDefinition() throws Exception {
        String testTarget = "inputParameters.closedReportDefinition.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + "fiscalYear")));
        String reportNumber = properties.getProperty(testTarget + "reportNumber");

        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition(testTarget);
        reportDefinition = TestDataPreparator.persistDataObject(reportDefinition);

        try {
            effortCertificationCreateService.create(fiscalYear, reportNumber);
            fail(message.getProperty("error.closedReportDefinition"));
        }
        catch (Exception e) {
        }
    }

    /**
     * check if the service can approperiately handle the input parameters
     * 
     * @see EffortCertificationCreateService.create(Integer, String)
     */
    public void testInputParameters_DocumentBuildNotExist() throws Exception {
        String testTarget = "inputParameters.documentBuildNotExist.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + "fiscalYear")));
        String reportNumber = properties.getProperty(testTarget + "reportNumber");

        TestDataPreparator.doCleanUpWithReference(EffortCertificationDocument.class, properties, "documentCleanup", documentFieldNames, deliminator);
        TestDataPreparator.doCleanUpWithReference(EffortCertificationDocumentBuild.class, properties, "documentCleanup", documentFieldNames, deliminator);

        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition("");
        reportDefinition = TestDataPreparator.persistDataObject(reportDefinition);

        try {
            effortCertificationCreateService.create(fiscalYear, reportNumber);
            fail(message.getProperty("error.documentBuildNotExist"));
        }
        catch (Exception e) {
        }
    }

    /**
     * check if the service can approperiately handle the input parameters
     * 
     * @see EffortCertificationCreateService.create(Integer, String)
     */
    public void testInputParameters_DocumentExist() throws Exception {
        String testTarget = "inputParameters.documentExist.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + "fiscalYear")));
        String reportNumber = properties.getProperty(testTarget + "reportNumber");

        TestDataPreparator.doCleanUpWithReference(EffortCertificationDocument.class, properties, "documentCleanup", documentFieldNames, deliminator);
        TestDataPreparator.doCleanUpWithReference(EffortCertificationDocumentBuild.class, properties, "documentCleanup", documentFieldNames, deliminator);

        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition("");
        reportDefinition = TestDataPreparator.persistDataObject(reportDefinition);

        DocumentHeader documentHeader = TestDataPreparator.buildTestDataObject(DocumentHeader.class, properties, testTarget + "documentHeader", documentHeaderFieldNames, deliminator);
        documentHeader = TestDataPreparator.persistDataObject(documentHeader);

        EffortCertificationDocument document = this.buildDocument(testTarget);
        document.setDocumentHeader(documentHeader);
        document = TestDataPreparator.persistDataObject(document);

        try {
            effortCertificationCreateService.create(fiscalYear, reportNumber);
            fail(message.getProperty("error.documentExist"));
        }
        catch (Exception e) {
        }
    }

    /**
     * If everything is set up correctly, a set of documents can be created.
     * 
     * @see EffortCertificationCreateService.create(Integer, String)
     */
    public void testCreate() throws Exception {
        String testTarget = "create.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + "fiscalYear")));
        String reportNumber = properties.getProperty(testTarget + "reportNumber");

        TestDataPreparator.doCleanUpWithReference(EffortCertificationDocument.class, properties, "documentCleanup", documentFieldNames, deliminator);
        TestDataPreparator.doCleanUpWithReference(EffortCertificationDocumentBuild.class, properties, "documentCleanup", documentFieldNames, deliminator);

        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition("");
        reportDefinition = TestDataPreparator.persistDataObject(reportDefinition);

        EffortCertificationDocumentBuild documentBuild = this.buildDocumentBuild(testTarget);
        documentBuild = TestDataPreparator.persistDataObject(documentBuild);

        effortCertificationCreateService.create(fiscalYear, reportNumber);

        List<EffortCertificationDocument> documentList = TestDataPreparator.findMatching(EffortCertificationDocument.class, properties, "documentCleanup", documentFieldNames, deliminator);

        int numberOfExpectedDocuments = Integer.valueOf(properties.getProperty(testTarget + "numOfExpectedDocuments"));
        List<EffortCertificationDocument> expectedDocuments = TestDataPreparator.buildExpectedValueList(EffortCertificationDocument.class, properties, testTarget + "expectedDocuments", documentFieldNames, deliminator, numberOfExpectedDocuments);

        assertEquals(numberOfExpectedDocuments, documentList.size());

        List<String> documentKeyFields = ObjectUtil.split(documentFieldNames, deliminator);
        documentKeyFields.remove(KFSPropertyConstants.DOCUMENT_NUMBER);
        assertTrue(TestDataPreparator.hasSameElements(expectedDocuments, documentList, documentKeyFields));
    }

    /**
     * after each document is created, it should be routed for approval.
     * 
     * @see EffortCertificationCreateService.create(Integer, String)
     */
    public void testRoute() throws Exception {
        String testTarget = "route.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + "fiscalYear")));
        String reportNumber = properties.getProperty(testTarget + "reportNumber");

        TestDataPreparator.doCleanUpWithReference(EffortCertificationDocument.class, properties, "documentCleanup", documentFieldNames, deliminator);
        TestDataPreparator.doCleanUpWithReference(EffortCertificationDocumentBuild.class, properties, "documentCleanup", documentFieldNames, deliminator);

        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition("");
        reportDefinition = TestDataPreparator.persistDataObject(reportDefinition);

        EffortCertificationDocumentBuild documentBuild = this.buildDocumentBuild(testTarget);
        documentBuild = TestDataPreparator.persistDataObject(documentBuild);

        effortCertificationCreateService.create(fiscalYear, reportNumber);

        List<EffortCertificationDocument> documentList = TestDataPreparator.findMatching(EffortCertificationDocument.class, properties, "documentCleanup", documentFieldNames, deliminator);

        for (EffortCertificationDocument document : documentList) {
            assertEquals(document.getDocumentHeader().getFinancialDocumentStatusCode(), KFSConstants.DocumentStatusCodes.ENROUTE);
        }
    }

    /**
     * build a report defintion object from the given test target
     * 
     * @param testTarget the given test target that specifies the test data being used
     * @return a report defintion object
     */
    private EffortCertificationReportDefinition buildReportDefinition(String testTarget) {
        return TestDataPreparator.buildTestDataObject(EffortCertificationReportDefinition.class, properties, testTarget + "reportDefinitionFieldValues", reportDefinitionFieldNames, deliminator);
    }

    /**
     * build an Effort Certification Document object from the given test target
     * 
     * @param testTarget the given test target that specifies the test data being used
     * @return an Effort Certification Document object
     */
    private EffortCertificationDocument buildDocument(String testTarget) {
        return TestDataPreparator.buildTestDataObject(EffortCertificationDocument.class, properties, testTarget + "document", documentFieldNames, deliminator);
    }

    private EffortCertificationDocumentBuild buildDocumentBuild(String testTarget) {
        EffortCertificationDocumentBuild documentBuild = TestDataPreparator.buildTestDataObject(EffortCertificationDocumentBuild.class, properties, testTarget + "documentBuild", documentFieldNames, deliminator);
        List<EffortCertificationDetailBuild> detailBuild = this.buildDetailLineBuild(testTarget);
        documentBuild.setEffortCertificationDetailLinesBuild(detailBuild);
        return documentBuild;
    }
    
    private List<EffortCertificationDetailBuild> buildDetailLineBuild(String testTarget) {
        int numberOfDetailBuild = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + "numOfDetailBuild")));
        return TestDataPreparator.buildTestDataList(EffortCertificationDetailBuild.class, properties, testTarget + "detailBuild", detailFieldNames, deliminator, numberOfDetailBuild);
    }
}
