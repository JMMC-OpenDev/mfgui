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
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="xml" standalone="yes" doctype-public= "-//UC Berkeley//DTD PlotML 1//EN"
                doctype-system="http://ptolemy.eecs.berkeley.edu/archive/plotml.dtd"/>

    <xsl:param name="plotName">plotBaselines</xsl:param>

    <!-- copy of definition collected from yoga/OIlib/yorick/oidata.i -->
    <xsl:variable name="OI_CLASS_NONE" select="'0'"/>
    <xsl:variable name="OI_CLASS_TARGET" select="'1'"/>
    <xsl:variable name="OI_CLASS_INSTRUMENT" select="'2'"/>
    <xsl:variable name="OI_CLASS_ARRAY" select="'3'"/>
    <xsl:variable name="OI_CLASS_VIS" select="'4'"/>
    <xsl:variable name="OI_CLASS_VIS2" select="'5'"/>
    <xsl:variable name="OI_CLASS_T3" select="'6'"/>
    <xsl:variable name="OI_CLASS_SED" select="'7'"/>
   
    <xsl:template match="/">
        <xsl:choose>
            <xsl:when test="$plotName='plotBaselines'">
                <xsl:call-template name="plotBaselines"/>
            </xsl:when>
            <xsl:when test="$plotName='plotUVCoverage'">
                <xsl:call-template name="plotUVCoverage"/>
            </xsl:when>
            <xsl:when test="$plotName='plotRadial'">
                <xsl:call-template name="plotRadial"/>
            </xsl:when>
            <xsl:otherwise>
                <plot>
                    <title><xsl:value-of select="$plotName"/> plot not yet supported</title>
                </plot>        
            </xsl:otherwise>
        </xsl:choose>                    
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
                        <xsl:attribute name="x"><xsl:value-of select="$u"/></xsl:attribute>
                        <xsl:attribute name="y"><xsl:value-of select="$v"/></xsl:attribute>
                    </xsl:element>
                    <xsl:element name="p">                            
                        <xsl:attribute name="x"><xsl:value-of select="-$u"/></xsl:attribute>
                        <xsl:attribute name="y"><xsl:value-of select="-$v"/></xsl:attribute>
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
                <xsl:variable name="pair" select="vfreq"/>
                <xsl:for-each select="ufreq//td">
                    <xsl:variable name="i" select="position()"/>
                    <xsl:variable name="u" select="."/>
                    <!--  take care, vfreqand ufreq are not array(1,n) but one matrix (n,1) -->                                                  
                    <xsl:variable name="v" select="$pair//tr[position()=$i]/td"/>                            
                    <xsl:element name="p">
                        <xsl:attribute name="x"><xsl:value-of select="$u"/></xsl:attribute>
                        <xsl:attribute name="y"><xsl:value-of select="$v"/></xsl:attribute>
                    </xsl:element>
                    <xsl:element name="p">                            
                        <xsl:attribute name="x"><xsl:value-of select="-$u"/></xsl:attribute>
                        <xsl:attribute name="y"><xsl:value-of select="-$v"/></xsl:attribute>
                    </xsl:element>                    
                </xsl:for-each>
                </dataset>
            </xsl:for-each>                                        
        </plot>
    </xsl:template>

    <xsl:template name="plotRadial">    
        <plot>
            <title>Plot versus radial distance</title>
            <xLabel>spatial frequency (1/rad)</xLabel>
            <yLabel>Squared visibility (VIS2)</yLabel>            
            <!-- we should follow crlst list according yorick code -> 
            //_modeler/dataset//crlst            
            -->
            <xsl:for-each select="//_modeler/dataset//*[starts-with(name(),'DB')]">
                <xsl:element name="dataset">
                    <xsl:attribute name="name"><xsl:call-template name="getClassName"/></xsl:attribute>
                </xsl:element>
            </xsl:for-each>
        </plot>
    </xsl:template>
    
    <xsl:template name="plotImage">    
        <plot>
            <title>Plot an image of the model</title>
            <xLabel>spatial frequency (1/rad)</xLabel>
            <yLabel>Squared visibility (VIS2)</yLabel>            
            <!-- we should follow crlst list according yorick code -> 
            //_modeler/dataset//crlst            
            -->
            <xsl:for-each select="//_modeler/dataset//*[starts-with(name(),'DB')]">
                <xsl:element name="dataset">
                    <xsl:attribute name="name"><xsl:call-template name="getClassName"/></xsl:attribute>
                </xsl:element>
            </xsl:for-each>
        </plot>
    </xsl:template>   
    
    
    
    
    <!-- Returns class name associated to .//class element -->
    <xsl:template name="getClassName">
        <xsl:param name="class" select=".//class"/>
        <xsl:choose>
            <xsl:when test="$class=$OI_CLASS_NONE">OI_CLASS</xsl:when>
            <xsl:when test="$class=$OI_CLASS_TARGET">OI_CLASS_TARGET</xsl:when>
            <xsl:when test="$class=$OI_CLASS_INSTRUMENT">OI_CLASS_INSTRUMENT</xsl:when>
            <xsl:when test="$class=$OI_CLASS_ARRAY">OI_CLASS_ARRAY</xsl:when>
            <xsl:when test="$class=$OI_CLASS_VIS">OI_CLASS_VIS</xsl:when>
            <xsl:when test="$class=$OI_CLASS_VIS2">OI_CLASS_VIS2</xsl:when>
            <xsl:when test="$class=$OI_CLASS_T3">OI_CLASS_T3</xsl:when>
            <xsl:when test="$class=$OI_CLASS_SED">OI_CLASS_SED</xsl:when>           
            <xsl:otherwise>Unsupported class <xsl:value-of select="$class"/></xsl:otherwise>
        </xsl:choose>                            
    </xsl:template>
    
</xsl:stylesheet>