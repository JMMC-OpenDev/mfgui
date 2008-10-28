<?xml version="1.0" encoding="UTF-8" ?>

<!--
Document   : extractError.xsl
Author     : mella
Description:
Generate text view of error extracted from xml settings files.
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
    xmlns:xt="http://www.jclark.com/xt"
    xmlns:libxslt="http://xmlsoft.org/XSLT/namespace"
    xmlns:test="http://xmlsoft.org/XSLT/"
    extension-element-prefixes="exslt math date func set str dyn saxon xt libxslt test"
    exclude-result-prefixes="math str" >
 
    <xsl:output method="text"/>

    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="settings">
        <xsl:apply-templates select=".//result"/>
    </xsl:template>

    <xsl:template match="result">
        <xsl:for-each select=".//error">
            <xsl:copy-of
                        select="."/>
        </xsl:for-each>
    </xsl:template>
    
</xsl:stylesheet>
