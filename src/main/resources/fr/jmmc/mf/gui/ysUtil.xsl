<?xml version="1.0" encoding="UTF-8" ?>
<!--
Document   : ysUtil.xsl
Created on : 16 fevrier 2007, 16:51
Author     : mella
Description:
Common xsl stylesheet.
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:exslt="http://exslt.org/common"
    xmlns:math="http://exslt.org/math"
    xmlns:date="http://exslt.org/dates-and-times"
    xmlns:func="http://exslt.org/functions"
    xmlns:set="http://exslt.org/sets"
    xmlns:str="http://exslt.org/strings"
    xmlns:dyn="http://exslt.org/dynamic"
    xmlns:saxon="http://icl.com/saxon"
    xmlns:xalanredirect="org.apache.xalan.xslt.extensions.Redirect"
    xmlns:xt="http://www.jclark.com/xt"
    xmlns:libxslt="http://xmlsoft.org/XSLT/namespace"
    xmlns:test="http://xmlsoft.org/XSLT/"
    extension-element-prefixes="exslt math date func set str dyn saxon xalanredirect xt libxslt test"
    exclude-result-prefixes="math str"
    >

    <xsl:variable name="cssHeader">
    <style type="text/css">
    <xsl:comment>
     .content {
        background-color:#F0F0F0;
border:1px solid #BBBBBB;
color: #010170;
padding: 1px;
margin: 1px;
font-family:Arial,Helvetica,sans-serif;
font-size:11px;
}
.vbox {
border:1px solid #CCCCCC;
padding: 3px;
margin: 3px;
float: left;
}
.box {
border:1px solid #CCCCCC;
padding: 3px;
margin: 3px;
}
table {
border: 2px solid #000099;
        border-collapse:collapse;
}
td {
border: 1px solid #000099;
}
th {
border: 1px solid #000099;
        background-color:#FFFFDD;
}
</xsl:comment>
</style>

</xsl:variable>


    <xsl:template name="createXhtmlFile">
        <xsl:param name="content">
            <xsl:comment><xsl:copy-of select="."/></xsl:comment>
        </xsl:param>
        <xsl:param name="title" select="concat(name(), ' element')"/>
        <xsl:param name="filename" select="concat(name(),'.html')"/>
        <xsl:param name="userHead"><xsl:comment> use userHead param into
                createXhtmlFile to
                xsl template to replace this comment </xsl:comment></xsl:param>

        <xsl:message>Creating <xsl:value-of select="$filename"/> </xsl:message>
        <xsl:document href="{$filename}" method="xml" indent="yes"
            encoding="ISO-8859-1" doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
            doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
            <!--XHTML document outline-->
            <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
                <head>
                    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
                    <xsl:copy-of select="$cssHeader"/>
                    <title><xsl:value-of select="$title"/></title>
                    <xsl:copy-of select="$userHead"/>
                </head>
                <body>
                    <a name="top"/>
                    <div class="content">
                        <xsl:copy-of select="$content"/>
                    </div>
                    <pre>Generated on <xsl:value-of select="date:date-time()"/></pre>
                </body>
            </html>
            </xsl:document>
        </xsl:template>


    </xsl:stylesheet>
