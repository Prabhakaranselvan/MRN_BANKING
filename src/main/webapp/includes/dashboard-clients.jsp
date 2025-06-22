<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
	session="false"%>
<%@ include file="/includes/dashboard-sessionguard.jsp"%>

<div class="container">
	<div class="clients-container">
		<div class="clients-header">
			<h2 class="form-header">CLIENTS LIST</h2>			
				<div class="pagination">
					<button id="addClientBtn" class="add-client-btn" onclick="loadContent('dashboard-add-client.jsp')">
						<span class="material-icons">person_add</span>
					</button>
					<button id="prevPage" class="page-btn" title="Previous">
						<span class="material-icons">chevron_left</span>
					</button>
					<button id="nextPage" class="page-btn" title="Next">
						<span class="material-icons">chevron_right</span>
					</button>
				</div>
			</div>

			<div id="clientsList" class="clients-list">
				<p>Loading clients...</p>
			</div>
		</div>
	</div>


	<script
		src="${pageContext.request.contextPath}/js/dashboard-clients.js"></script>