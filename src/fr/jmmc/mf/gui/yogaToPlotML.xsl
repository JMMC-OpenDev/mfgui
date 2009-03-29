<?xml version="1.0" standalone="yes" ?>

<!--
Document   : yogaToPlotML.xsl
Created on : 14 mars 2007, 11:09
Author     : mella
Description:        
Template code try to make same actions of following yoga functions
lp_plot_baselines, lp_plot_uvcoverage, lp_plot_radial, 
lp_plot_image, lp_plot_uvmap

Output must be follow plotml format:
http://ptolemy.berkeley.edu/java/ptplot5.6/ptolemy/plot/doc/plot.pdf

NOTES pour futur documenter les GROS PROBLEMES CONSTATE entre xsltproc et xalan
le param d'un template qui disparait a completer...
exslt:node-set($var)//eleName ne marche pas, il faut utiliser exslt:node-set($var)/eleName

Ensuite avec xalan et le proxy la reference au dtd peut poser probleme avec une trasfo xsl

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
            
    <!-- <xsl:output method="xml" standalone="yes" doctype-public= "-//UC Berkeley//DTD PlotML 1//EN"
                doctype-system="http://ptolemy.eecs.berkeley.edu/archive/plotml.dtd"/>
                -->
    <xsl:output method="xml" standalone="yes" />
    <!-- this parameter should be one or more of the following types (separated by space, comma, or whatever)
     - plotBaselines plotUVCoverage visamp visphi vis2 t3amp t3phi
     -->
    <xsl:param name="plotName">plotBaselines</xsl:param>    
    
    <xsl:template match="/">
        <xsl:choose>
            <xsl:when test="contains($plotName,'vis2') or contains($plotName,'visamp') or contains($plotName,'visphi') or contains($plotName,'t3phi') or contains($plotName,'t3amp')">
                <xsl:call-template name="plotRadial">
                    <xsl:with-param name="plotName" select="$plotName"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$plotName='plotBaselines'">
                <xsl:call-template name="plotBaselines"/>
            </xsl:when>
            <xsl:when test="$plotName='plotUVCoverage'">
                <xsl:call-template name="plotUVCoverage"/>
            </xsl:when>            
            <xsl:otherwise>
                <plot>
                    <title>
                        <xsl:value-of select="$plotName"/> plot not yet supported
                    </title>
                </plot>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template name="plotRadial">
        <xsl:param name="plotName"/>
        <plot>
            <title>
                <xsl:value-of select="concat('Plot ',$plotName,' versus radial distance')"/>
            </title>
            <xLabel>spatial frequency (1/rad)</xLabel>
            <yLabel>
                <xsl:value-of select="$plotName"/>
            </yLabel>
            <xsl:for-each select="//_modeler/dataset//*[starts-with(name(),'DB')]">
                <xsl:if test="./*[name()=$plotName or name()=concat($plotName,'data')]">
                    <!-- plot data -->
                    <xsl:call-template name="ptPlotDataSet">
                        <xsl:with-param name="xElements">
                            <xsl:copy-of select=".//ruv//td"/>
                        </xsl:with-param>
                        <xsl:with-param name="yElements">
                            <xsl:copy-of select="./*[name()=$plotName]//td"/>
                            <xsl:if test="$plotName='vis2'">
                                <xsl:copy-of select="./vis2data//td"/>
                            </xsl:if>
                        </xsl:with-param>
                        <xsl:with-param name="errorBarElements">
                            <xsl:copy-of select="./*[name()=concat($plotName,'err')]//td"/>                            
                        </xsl:with-param>
                        <xsl:with-param name="name" select="concat($plotName, ' (data)')"/>
                        <xsl:with-param name="marks" select="'points'"/>
                    </xsl:call-template>
                    <!-- plot model -->
                    <xsl:call-template name="ptPlotDataSet">
                        <xsl:with-param name="xElements">
                            <xsl:copy-of select=".//ruv//td"/>
                        </xsl:with-param>
                        <xsl:with-param name="yElements">
                            <xsl:copy-of select="./*[name()=concat($plotName,'_model')]//td"/>
                            <xsl:if test="$plotName='vis2'">
                                <xsl:copy-of select="./vis2data_model//td"/>
                            </xsl:if>
                        </xsl:with-param>
                        <xsl:with-param name="name" select="concat($plotName, ' (model)')"/>
                        <xsl:with-param name="marks" select="'dots'"/>
                    </xsl:call-template>
                </xsl:if>
            </xsl:for-each>
        </plot>
    </xsl:template>

    <xsl:template name="ptPlotDataSet">
        <xsl:param name="xElements"/>
        <xsl:param name="yElements"/>
        <xsl:param name="errorBarElements" select='no'/>
        <xsl:param name="name" select="'missing name'"/>
        <xsl:param name="marks" select="'points'"/>

        <xsl:element name="dataset">
            <xsl:attribute name="name">
                <xsl:value-of select="$name"/>
            </xsl:attribute>
            <xsl:attribute name="connected">no</xsl:attribute>
            <xsl:attribute name="marks">
                <xsl:value-of select="$marks"/>
            </xsl:attribute>

            <xsl:for-each select="exslt:node-set($xElements)/*">
                <xsl:variable name="i" select="position()"/>
                <xsl:variable name="x" select="."/>
                <xsl:variable name="y" select="exslt:node-set($yElements)/*[position()=$i]"/>
                <xsl:element name="p">
                    <xsl:attribute name="x">
                        <xsl:value-of select="$x"/>
                    </xsl:attribute>
                    <xsl:attribute name="y">
                        <xsl:value-of select="$y"/>
                    </xsl:attribute>
                    <xsl:if test="$errorBarElements!='no'">
                        <xsl:for-each select="exslt:node-set($errorBarElements)/*[position()=$i]">
                            <xsl:attribute name="lowErrorBar">
                                <xsl:value-of select="$y - . div 2"/>
                            </xsl:attribute>
                            <xsl:attribute name="highErrorBar">
                                <xsl:value-of select="$y + . div 2"/>
                            </xsl:attribute>
                        </xsl:for-each>
                    </xsl:if>
                </xsl:element>
            </xsl:for-each>
        </xsl:element>
    </xsl:template>

    <xsl:template name="plotBaselines">
        <plot>
            <title>Baselines plot</title>
            <xLabel>Ucoord (meters)</xLabel>
            <yLabel>Vcoord (meters)</yLabel>

            <!-- we should follow crlst list according yorick code ->
            //_modeler/dataset//crlst
            -->
            <xsl:for-each select="//_modeler/dataset//CR1">
                <!-- read ucoord and vcoord array and plot u,v and -u -v -->
                <dataset connected="no" marks="various">
                    <xsl:variable name="ucoord" select="ucoord"/>
                    <xsl:variable name="vcoord" select="vcoord"/>
                    <xsl:for-each select="ucoord//td">
                        <xsl:variable name="i" select="position()"/>
                        <xsl:variable name="u" select="."/>
                        <xsl:variable name="v" select="$vcoord//td[position()=$i]"/>
                        <xsl:element name="p">
                            <xsl:attribute name="x">
                                <xsl:value-of select="$u"/>
                            </xsl:attribute>
                            <xsl:attribute name="y">
                                <xsl:value-of select="$v"/>
                            </xsl:attribute>
                        </xsl:element>
                        <xsl:element name="p">
                            <xsl:attribute name="x">
                                <xsl:value-of select="-$u"/>
                            </xsl:attribute>
                            <xsl:attribute name="y">
                                <xsl:value-of select="-$v"/>
                            </xsl:attribute>
                        </xsl:element>
                    </xsl:for-each>
                </dataset>
            </xsl:for-each>
        </plot>
    </xsl:template>

    <xsl:template name="plotUVCoverage">
        <plot>
            <title>UV coverage plot</title>
            <xLabel>Ucoord (1/rad)</xLabel>
            <yLabel>Vcoord (1/rad)</yLabel>
            <!-- we should follow crlst list according yorick code ->
            //_modeler/dataset//crlst
            -->
            <xsl:for-each select="//_modeler/dataset//CR1">
                <!-- read ucoord and vcoord array and plot u,v and -u -v -->
                <dataset connected="no" marks="various">
                    <xsl:variable name="pair">
                        <xsl:copy-of select="vfreq//td"/>
                    </xsl:variable>
                    <xsl:for-each select="ufreq//td">
                        <xsl:variable name="i" select="position()"/>
                        <xsl:variable name="u" select="."/>
                        <!--  take care, vfreqand ufreq are not array(1,n) but one matrix (n,1) -->
                        <xsl:variable name="v" select="exslt:node-set($pair)/td[position()=$i]"/>
                        <xsl:element name="p">
                            <xsl:attribute name="x">
                                <xsl:value-of select="$u"/>
                            </xsl:attribute>
                            <xsl:attribute name="y">
                                <xsl:value-of select="$v"/>
                            </xsl:attribute>
                        </xsl:element>
                        <xsl:element name="p">
                            <xsl:attribute name="x">
                                <xsl:value-of select="-$u"/>
                            </xsl:attribute>
                            <xsl:attribute name="y">
                                <xsl:value-of select="-$v"/>
                            </xsl:attribute>
                        </xsl:element>
                    </xsl:for-each>
                </dataset>
            </xsl:for-each>
        </plot>
    </xsl:template>    
</xsl:stylesheet>
