<?xml version="1.0" encoding="UTF-8"?>
<?altova_samplexml FC122_003444_MCXPSS.xml?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs">

	<xsl:template match="/TestSuiteCollection[@name]">
		<TestSuiteCollection>
			<Name>
				<xsl:value-of select="/TestSuiteCollection[@name]/@name"/>
			</Name>
			<xsl:for-each select="/TestSuiteCollection/TestSuite[starts-with(@name,'TESTCASES')]/TestCase[not(attribute::active='false')]/TestResult[last()][not(attribute::passed='true')]">
				<TestCase>
					<TCName>
						<xsl:value-of select="parent::TestCase[@name]/@name"/>
					</TCName>
					<TestCaseDescription>
						<xsl:value-of select="parent::TestCase/Description"/>
					</TestCaseDescription>					
					<ResultText>
						<xsl:value-of select="Description"/>
					</ResultText>					
				</TestCase>
			 </xsl:for-each>
				</TestSuiteCollection>
	</xsl:template>
</xsl:stylesheet>
