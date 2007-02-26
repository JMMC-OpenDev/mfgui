<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : resultToHtml.xsl
    Created on : 16 février 2007, 16:51
    Author     : mella
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>
    
    <!-- root document correspond to the result branch of main document
    -->
    <xsl:template match="/">
        <html>
            <body>
                <h1>Covariance matrix</h1>
                <table border="1">
                    <!-- append table headers first is empty-->
                    <tr>
                        <th></th>
                        <xsl:for-each select="//param/*">
                            <th><xsl:value-of select="name()"/></th>
                        </xsl:for-each>
                    </tr>
                    <!-- append param name on first column then the table content-->
                    <xsl:for-each select="//param/*">
                        <xsl:variable name="lineNb" select="position()"/>
                        <tr><td><xsl:value-of select="name()"/></td>
                            <xsl:copy-of select="//covar/table/tr[position()=$lineNb]/td"/>
                        </tr>
                    </xsl:for-each>                
                </table>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>
