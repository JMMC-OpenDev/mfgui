<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
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
 exclude-result-prefixes="math str">
    <xsl:output method="text" omit-xml-declaration="yes" indent="no"/>

    <xsl:param name="title"></xsl:param>

    <xsl:template match="/">
        <xsl:value-of select="'# title : '"/>
        <xsl:value-of select="//title"/>
        <xsl:value-of select="'&#10;'"/>

        <xsl:value-of select="'# xLabel : '"/>
        <xsl:value-of select="//xLabel"/>
        <xsl:value-of select="'&#10;'"/>

        <xsl:value-of select="'# yLabel : '"/>
        <xsl:value-of select="//yLabel"/>
        <xsl:value-of select="'&#10;'"/>

        <xsl:value-of select="'#'"/>
        <xsl:value-of select="'x'"/>
        <xsl:value-of select="'&#09;'"/>
        <xsl:value-of select="'y'"/>
        <xsl:value-of select="'&#09;'"/>
        <xsl:value-of select="'error'"/>
        <xsl:value-of select="'&#09;'"/>
        <xsl:value-of select="'datasetName'"/>
        <xsl:value-of select="'&#10;'"/>

        <xsl:for-each select="//p | //m">
            <xsl:value-of select="@x"/>
            <xsl:value-of select="'&#09;'"/>
            <xsl:value-of select="@y"/>
            <xsl:value-of select="'&#09;'"/>
            <xsl:value-of select="@lowErrorBar+@highErrorBar"/>
            <xsl:value-of select="'&#09;'"/>
            <xsl:value-of select="./ancestor::dataset/@name"/>
            <xsl:value-of select="'&#10;'"/>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
