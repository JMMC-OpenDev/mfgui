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
    <xsl:output method="text" indent="no"/>
    <xsl:param name="workingDirectory">LIT_MODEL</xsl:param>

    <!--                                                                   -->
    <!--                               MAIN TEMPLATE                       -->
    <!--                                                                   -->
    <!--                                                                   -->
    <xsl:template match="/">
        <xsl:value-of select="'_LPP_MODEL_SETTINGS = h_new(&#10;'"/>

        <xsl:call-template name="targetSection"/>
        <xsl:value-of select="',&#10;'"/>
        <xsl:call-template name="paramSection"/>
        <xsl:value-of select="',&#10;'"/>
        <xsl:call-template name="fitterSection"/>
        <xsl:value-of select="'&#10;'"/>

        <xsl:value-of select="');'"/>


        <!-- create one .b64 file per hrefed data file or plot
        <xsl:for-each select="//files/file[@href]">
            <xsl:document href="{./@id}.b64" method="text"
                xml:space="preserve"><xsl:value-of select="substring-after(./@href,'base64,')"/></xsl:document>
        </xsl:for-each>
        <xsl:for-each select="//resultFile[@href]">
            <xsl:document href="{./@name}.b64" method="text"
                xml:space="preserve"><xsl:value-of select="substring-after(./@href,'base64,')"/></xsl:document>
        </xsl:for-each>
        -->
        
    </xsl:template>

    <!-- targetSection                                                  -->
    <xsl:template name="targetSection">
        <xsl:value-of select="'  target = h_new(&#10;'"/>
        <xsl:for-each select="//targets/target">
            <!-- target definition -->
            <xsl:value-of select="'    TG'"/>
            <xsl:value-of select="position()"/>
            <xsl:value-of select="' = h_new(&#10;'"/>

            <!-- ident definition -->
            <xsl:value-of select="'      ident = &quot;'"/>
            <xsl:value-of select="./ident"/>
            <xsl:value-of select="'&quot;,&#10;'"/>

            <!-- file definition 
            FIXME add multi file support files = ["f1", ..., "f2"],
            <xsl:value-of select="'      files = &quot;./&quot;+'"/>
            -->
            <xsl:value-of select="'      files = ['"/>
            <xsl:for-each select=".//fileLink">
                <xsl:value-of select="'&quot;'"/>
                <xsl:variable name="fileRef"><xsl:value-of select="./@fileRef"/></xsl:variable>
                <xsl:value-of select="//file[./@id=$fileRef]/@id"/>
                <xsl:value-of select="'&quot;'"/>
                <xsl:if test="not(position() = last())">
                    <xsl:value-of select="','"/>
                </xsl:if>
            </xsl:for-each>
            <xsl:value-of select="'],&#10;'"/>
            <xsl:value-of select="'    /* Indicates files corresponding to given ids &#10;'"/>
            <xsl:for-each select=".//fileLink">
                <xsl:variable name="fileRef"><xsl:value-of select="./@fileRef"/></xsl:variable>
                <xsl:value-of select="'     * - '"/>
                <xsl:value-of select="//file[./@id=$fileRef]/@id"/> 
                <xsl:value-of select="' file is '"/>
                <xsl:value-of select="//file[./@id=$fileRef]/@name"/> 
                <xsl:value-of select="'&#10;'"/>
            </xsl:for-each>
            <xsl:value-of select="'     */&#10;'"/>
            <!-- object definition -->
            <xsl:for-each select=".//model">
                <xsl:variable name="modelPosition" select="position()"/>
                <xsl:value-of select="'      OBJ'"/>
                <xsl:value-of select="$modelPosition"/>
                <xsl:value-of select="' = h_new(&#10;'"/>
                <!-- name definition -->
                <xsl:value-of select="'        name = &quot;'"/>
                <xsl:value-of select="./@type"/>
                <xsl:value-of select="'&quot;,&#10;'"/>
                <!-- params definition -->
                <xsl:value-of select="'        params = h_new('"/>
                <xsl:variable name="mySharedParameters">
                    <xsl:for-each select=".//parameterLink">
                        <xsl:variable name="paramId" select="./@parameterRef"/>
                        <xsl:variable name="sharedParam" select="//parameters/parameter[@id=$paramId]"/>
                        <xsl:value-of select="./@type"/>
                        <xsl:value-of select="' = &quot;'"/>
                        <xsl:value-of select="$sharedParam/@name"/>
                        <xsl:value-of select="'&quot;'"/>
                        <xsl:if test="not(position() = last()) or ../parameter">
                            <xsl:value-of select="', '"/>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:variable>
                <xsl:value-of select="$mySharedParameters"/>
                <xsl:for-each select=".//parameter">
                    <xsl:value-of select="./@type"/>
                    <xsl:value-of select="' = &quot;'"/>
                    <xsl:value-of select="./@name"/>
                    <xsl:value-of select="'&quot;'"/>
                    <xsl:if test="not(position() = last())">
                        <xsl:value-of select="', '"/>
                    </xsl:if>
                </xsl:for-each>
                <xsl:value-of select="')&#10;'"/>
                <xsl:value-of select="'      ),&#10;'"/>
            </xsl:for-each>

            <!-- residual definition -->
            <xsl:value-of select="'      residuals = h_new(&#10;'"/>
            <xsl:for-each select=".//residual">
                <xsl:value-of select="'          '"/>
                <xsl:value-of select="@name"/>
                <xsl:value-of select="' = &quot;'"/>
                <xsl:value-of select="@type"/>
                <xsl:value-of select="'&quot;,&#10;'"/>
            </xsl:for-each>



            <xsl:choose>
                <xsl:when test="contains(./normalize,'false') or
                    contains(./normalize,'0')">
                    <xsl:value-of select="'        NORMALIZE = FALSE&#10;'"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="'        NORMALIZE = TRUE&#10;'"/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:value-of select="'      )&#10;'"/>


            <xsl:value-of select="'    )&#10;'"/>
            <!-- add , if target is not the last -->
            <xsl:if test="not(position() = last())">
                <xsl:value-of select="', '"/>
            </xsl:if>
        </xsl:for-each>
        <xsl:value-of select="'  )'"/>
    </xsl:template>

    <!-- paramSection                                                  -->
    <xsl:template name="paramSection">
        <xsl:value-of select="'  param = h_new(&#10;'"/>
        <xsl:variable name="paramList" select="//model/parameter"/>
        <xsl:variable name="sharedParamList" select="//parameters/parameter"/>
        <!-- List parameters -->
        <xsl:for-each select="$paramList | $sharedParamList">
            <xsl:apply-templates select="." mode="forParamSection"/>
            <xsl:if test="not(position() = last())"><xsl:value-of select="','"/></xsl:if>
            <xsl:value-of select="'&#10;'"/>
        </xsl:for-each>
        <xsl:value-of select="'  )'"/>
    </xsl:template>

    <xsl:template match="parameter" mode="forParamSection">
        <xsl:param name="parameter" select="."/>
        <xsl:value-of select="'    '"/>
        <xsl:value-of select="$parameter/@name"/>
        <xsl:value-of select="' = h_new('"/>
        <xsl:value-of select="'value='"/>
        <xsl:value-of select="$parameter/value"/>
        <xsl:value-of select="', '"/>
        <xsl:value-of select="'vmin='"/>
        <xsl:value-of select="$parameter/minValue"/>
        <xsl:value-of select="', '"/>
        <xsl:value-of select="'vmax='"/>
        <xsl:value-of select="$parameter/maxValue"/>
        <xsl:value-of select="', '"/>
        <xsl:value-of select="'units=&quot;'"/>
        <xsl:value-of select="$parameter/units"/>
        <xsl:value-of select="'&quot;'"/>
        <xsl:if test="contains($parameter/hasFixedValue,'true') or contains($parameter/hasFixedValue,'1')">
        <xsl:value-of select="', flags=FIXED'"/>
        </xsl:if>
        <xsl:value-of select="')'"/>
    </xsl:template>


    <!-- fitterSection                                                  -->
    <xsl:template name="fitterSection">
        <xsl:value-of select="'  fitter = &quot;'"/>
        <xsl:value-of select="//fitter"/>
        <xsl:value-of select="'&quot;'"/>
    </xsl:template>
</xsl:stylesheet>
