<%--$Id$--%>
<!--
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib prefix="rdc" uri="http://jakarta.apache.org/taglibs/rdc-1.0" %>

<%@ tag body-content="empty" %>

<%@ attribute name="id" required="true" rtexprvalue="false" %>
<%@ attribute name="submit" required="false" %>
<%@ attribute name="config" required="false" %>
<%@ attribute name="initial" required="false" %>
<%@ attribute name="confirm" required="false" %>
<%@ attribute name="echo" required="false" %>
<%@ attribute name="minLength" required="false" %>
<%@ attribute name="maxLength" required="false" %>
<%@ attribute name="pattern" required="false" %>
<%@ attribute name="minConfidence" required="false" %>
<%@ attribute name="numNBest" required="false" %>
<%@ variable name-from-attribute="id" alias="retVal" scope="AT_END"%>
-->

<rdc:comment>
  Description: Collect, validate and confirm an alpha.
  Attributes:
  id: unique identifier for the atom
  submit: the submit location
  config: the configuration file location 
  initial: initial value
  confirm: boolean value indicating whether the input should be confirmed 
  echo: boolean value indicating whether to echo back the result on completion
  minLength: minimum allowed length of alpha input
  maxLength: maximum allowed length of alpha input
  pattern: the pattern of the alpha

  </rdc:comment>

<rdc:peek var="stateMap" stack="${requestScope.rdcStack}"/>
<jsp:useBean id="constants" class="org.apache.taglibs.rdc.core.Constants" />

<rdc:comment>
  Bean model holds the private data model of this component.
  It is created when the component is called the first time,
  and is found in subsequent requests  in stateMap[id].
  stateMap is in session scope and holds (directly or indirectly)
  the transitive closure
  of all components.
</rdc:comment>

<c:choose>
  <c:when test="${empty stateMap[id]}">
    <rdc:comment>This instance is being called for the first time in 
    this session</rdc:comment>
    <jsp:useBean id="model" class="org.apache.taglibs.rdc.Alpha" >
      <c:set target="${model}" property="state"
      value="${stateMap.initOnlyFlag == true ? constants.FSM_INITONLY : constants.FSM_INPUT}"/>
      <rdc:comment>initialize bean from our attributes</rdc:comment>
      <c:set target="${model}" property="id" value="${id}"/>
      <c:set target ="${model}" property="pattern" value="${pattern}"/>
      <c:set target="${model}" property="confirm" value="${confirm}"/>
      <c:set target ="${model}" property="maxLength" value="${maxLength}"/>
      <c:set target ="${model}" property="minLength" value="${minLength}"/>
      <c:set target ="${model}" property="initial" value="${initial}"/>
      <c:set target ="${model}" property="submit" value="${submit}"/>
      <c:set target ="${model}" property="echo" value="${echo}"/>
      <c:set target="${model}" property="grammar"
       value="${pageContext.request.contextPath}/.grammar/alpha.grxml"/>
      <rdc:configure model="${model}" config="${config}" 
        defaultConfig="META-INF/tags/rdc/config/alpha.xml" />
      <rdc:setup-results model="${model}" submit="${submit}" 
        minConfidence="${minConfidence}" numNBest="${numNBest}" />
    </jsp:useBean>
    <rdc:comment>cache away this instance for future requests in this 
    session</rdc:comment>
    <c:set target="${stateMap}" property="${id}" value="${model}"/>
  </c:when>
  <c:otherwise>
    <rdc:comment>retrieve cached bean for this instance</rdc:comment>
    <c:set var="model" value="${stateMap[id]}"/>
  </c:otherwise>
</c:choose>

<rdc:extract-params target="${model}" parameters="${model.paramsMap}"/>

<rdc:fsm-run  model="${model}"/>
              
<c:if test="${model.state == constants.FSM_DONE}">
  <c:set var="retVal" value="${model.value}"/>
</c:if>
