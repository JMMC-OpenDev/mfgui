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
    <xsl:template match="/">                
        <!-- a lot of information has been reduced after rev 14871 and replaced by gui_info computation on the CLI side-->
        
        <!-- Search gui_info at 2 location. global is for current public server and last is a subpart of the alpha server response -->
        <xsl:copy-of select=".//gui_info/text()|.//gui_info/table/tr[1]/td[1]/text()"/>  
        <!-- we still should explain why we may have so many tds if table format -->
    </xsl:template>

</xsl:stylesheet>
