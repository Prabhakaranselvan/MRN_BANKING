function initClientsScript() {
    const container = document.getElementById("clientsList");

    fetch("/MRN_BANKING/MRNBank/client", {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            "Method": "GET"
        }
    })
        .then(res => res.json())
        .then(data => {
            console.log("Clients list:", data);
            if (data.clients && data.clients.length > 0) {
                renderClients(data.clients);
                handleResponse({ message: data.message });
            } else {
                container.innerHTML = `<p>No clients found in your branch.</p>`;
            }
        })
        .catch(err => {
            console.error("Error loading clients:", err);
            container.innerHTML = `<p class="error">Failed to load clients.</p>`;
            handleResponse({ error: "Failed to fetch clients." });
        });

		function renderClients(clients) {
		    const rows = clients.map(c => `
		        <div class="client-row">
		            <div class="client-id">${c.userId}</div>
		            <div class="client-name"><strong>${c.name}</strong></div>
		            <div class="client-email">${c.email}</div>
		            <div class="client-status ${c.status === 1 ? 'active' : c.status === 0 ? 'inactive' : 'closed'}">
		                ${c.status === 1 ? 'Active' : c.status === 0 ? 'Inactive' : 'Closed'}
		            </div>
		            <div class="client-actions">
		                <button onclick="viewClient(${c.userId})" class="action-btn" title="View Profile">
		                    <span class="material-icons">account_circle</span>
		                </button>
		            </div>
		        </div>
		    `).join("");

		    container.innerHTML = `
		        <div class="client-table">
		            <div class="client-header">
		                <div class="client-id">USER ID</div>
		                <div class="client-name">NAME</div>
		                <div class="client-email">EMAIL</div>
		                <div class="client-status">STATUS</div>
		                <div class="client-actions">ACTION</div>
		            </div>
		            ${rows}
		        </div>
		    `;
		}

}

function viewClient(userId) {
    loadContent(`dashboard-profile.jsp?clientId=${userId}`);
}