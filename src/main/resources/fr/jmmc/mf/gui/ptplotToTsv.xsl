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

<!-- LBO: 2021.03.04: disabled data + model crossmatch as it is a performance bottleneck (O N2) -->
<!-- waiting for most robust solution (directly traverse vectors X and Model -->
<!--
        <xsl:choose>
             try to mix data and model on plots that get data with and without error bars
            <xsl:when test="(//p[@lowErrorBar] and //p[not(@lowErrorBar)]) or (//m[@lowErrorBar] and //m[not(@lowErrorBar)])">
                <xsl:value-of select="'#'"/>
                <xsl:value-of select="'x'"/>
                <xsl:value-of select="'&#09;'"/>
                <xsl:value-of select="'dataValue'"/>
                <xsl:value-of select="'&#09;'"/>
                <xsl:value-of select="'dataError'"/>
                <xsl:value-of select="'&#09;'"/>
                <xsl:value-of select="'data_name'"/>
                <xsl:value-of select="'&#09;'"/>
                <xsl:value-of select="'modelValue'"/>
                <xsl:value-of select="'&#09;'"/>
                <xsl:value-of select="'model_name'"/>
                <xsl:value-of select="'&#10;'"/>

                <xsl:for-each select="//p[@lowErrorBar] | //m[@lowErrorBar]">
                    <xsl:variable name="x" select="@x"/>
                    <xsl:variable name="model" select="//p[not(@lowErrorBar) and @x=$x] | //m[not(@lowErrorBar) and @x=$x]"/>

                    <xsl:value-of select="@x"/>
                    <xsl:value-of select="'&#09;'"/>
                    <xsl:value-of select="@y"/>
                    <xsl:value-of select="'&#09;'"/>

                    <xsl:value-of select="@y - @lowErrorBar"/>
                    <xsl:value-of select="'&#09;'"/>
                    <xsl:value-of select="./ancestor::dataset/@name"/>
                    <xsl:value-of select="'&#09;'"/>
                    <xsl:value-of select="$model/@y"/>
                    <xsl:value-of select="'&#09;'"/>
                    <xsl:value-of select="$model/ancestor::dataset/@name"/>
                    <xsl:value-of select="'&#10;'"/>
                </xsl:for-each>
            </xsl:when>
-->
            <!-- else follow each data record one by one -->
<!--
            <xsl:otherwise>
-->
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
                    <xsl:value-of select="@lowErrorBar"/>
                    <xsl:value-of select="'&#09;'"/>
                    <xsl:value-of select="./ancestor::dataset/@name"/>
                    <xsl:value-of select="'&#10;'"/>
                </xsl:for-each>
<!--
            </xsl:otherwise>
        </xsl:choose>
-->
    </xsl:template>
</xsl:stylesheet>
