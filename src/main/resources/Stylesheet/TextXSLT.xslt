<?xml version="1.0" encoding="UTF-8"?>
<?altova_samplexml ..\FinalXML.xml?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions">
<xsl:output method="text"/>

<xsl:template match="/TestSuiteCollection">
	<xsl:text>SUITE : </xsl:text>
	<xsl:value-of select="Name"/><xsl:text>&#10;</xsl:text>
-----------------------------------------------------------
	<xsl:text>&#10;</xsl:text>
	<xsl:text>&#10;</xsl:text>
	<xsl:for-each select="TestCase">
TEST CASE NAME : <xsl:value-of select="TCName"/>
				<xsl:text>&#10;</xsl:text>
		<xsl:value-of select="Title"/>
		
MESSAGE FlOW : 

<xsl:for-each select="MessageFlow">
<xsl:value-of select="."/><xsl:text>&#10;</xsl:text>
</xsl:for-each>
-----------------------------------------------------------
	</xsl:for-each>
</xsl:template>
</xsl:stylesheet>
