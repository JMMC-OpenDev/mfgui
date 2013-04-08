<?xml version="1.0" encoding="UTF-8" ?>

<!--
Document   : resultToHtml.xsl
Created on : 16 fevrier 2007, 16:51
Author     : mella
Description:
Generate Html view of given xml settings files .
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
 
    <xsl:output method="html"/>
    <!-- The input document may not contain all settings related part  -->
    <xsl:variable name="root" select="/" />

    <xsl:template match="/">        
        <pre>
            <xsl:value-of select="//gui_info/text()"/>            
        </pre>        
        <!-- a lot of information has been reduced after rev 14871 -->
    </xsl:template>

</xsl:stylesheet>
