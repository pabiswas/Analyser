<?xml version="1.0" encoding="UTF-8"?>
<?altova_samplexml ..\FinalXML.xml?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:java="java">
	<xsl:template match="/">
		<html>
			<body>
				<h2>TestSuite</h2>
				<h2>
					<xsl:value-of select="/TestSuiteCollection/Name"/>
				</h2>
				<table border="1">
					<tr bgcolor="#9acd32">
						<th>TestCase Id</th>
						<th>Title</th>
						<th>Trace File</th>
						<th>Message Flow</th>
					</tr>
					<xsl:for-each select="/TestSuiteCollection/TestCase">
						<tr>
							<td style="width:15%">
								<xsl:value-of select="TCName"/>
							</td>
							<td style="width:25%">
								<xsl:value-of select="Title"/>
								<br/>
							</td>
							<td style="width:30%">
								<xsl:for-each select="TraceFile">
									<!--xsl:value-of select="."/><br /><br /-->
									<a href="{.}">
										<xsl:value-of select="."/>
									</a>
								</xsl:for-each>
							</td>
							<td style="width:30%">
								<xsl:for-each select="MessageFlow">
									<xsl:value-of select="."/>
									<br/>
								</xsl:for-each>
							</td>
						</tr>
					</xsl:for-each>
				</table>
				<img alt="Pie Chart" src="Statistics.png"/>
				<img alt="Bar Chart" src="barChart.png"/>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
