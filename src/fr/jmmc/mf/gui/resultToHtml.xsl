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

    <!-- TODO customize transformation rules 
         syntax recommendation http://www.w3.org/TR/xslt 
    -->
    <xsl:template match="/">
        <html>
            <body>
                <h1>Covariance matrix</h1>
                <table border="1">
                <xsl:copy-of select="//covar/table/*"/>    
                </table>
                            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>
