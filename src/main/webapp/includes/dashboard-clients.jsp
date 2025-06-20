<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	session="false"%>
<%@ include file="/includes/dashboard-sessionguard.jsp"%>

<div class="container">
	<div class="clients-container">
		<h2 class="form-header">CLIENTS LIST</h2>
		<div id="clientsList" class="clients-list">
			<p>Loading clients...</p>
		</div>
	</div>
</div>

<script src="${pageContext.request.contextPath}/js/dashboard-clients.js"></script>
