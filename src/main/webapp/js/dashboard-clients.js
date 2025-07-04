function initClientsScript() {
    const container = document.getElementById("clientsList");
	const prevBtn = document.getElementById("prevPage");
   const nextBtn = document.getElementById("nextPage");

   let currentPage = 1;
   const limit = 10;

   function loadClients(page) {
    fetch(`/MRN_BANKING/MRNBank/client?page=${page}&limit=${limit}`, {
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
                prevBtn.disabled = currentPage === 1;
                nextBtn.disabled = data.clients.length < limit; // Disable if less than limit
            } else {
                container.innerHTML = `<p>No clients found.</p>`;
                prevBtn.disabled = true;
                nextBtn.disabled = true;
            }
        })
        .catch(err => {
            console.error("Error loading clients:", err);
            container.innerHTML = `<p class="error">Failed to load clients.</p>`;
            handleResponse({ error: "Failed to fetch clients." });
        });
		}

		function renderClients(clients) {
		  const rows = clients.map(c => {
		    const statusClass = c.status === 1 ? 'active' : c.status === 0 ? 'inactive' : 'closed';
		    const statusText = c.status === 1 ? 'Active' : c.status === 0 ? 'Inactive' : 'Closed';

		    const actions = c.status !== 2
		      ? `<button onclick="viewClient(${c.userId}, ${c.userCategory})" class="action-btn" title="View Profile">
		           <span class="material-icons">account_circle</span>
		         </button>`
		      : `<button class="lock-btn" title="Locked">
		           <span class="material-icons" style="color: #bbb;">lock</span>
		         </button>`;

		    return `
		      <div class="client-row">
		        <div class="client-id">${c.userId}</div>
		        <div class="client-name"><strong>${c.name}</strong></div>
		        <div class="client-email">${c.email}</div>
		        <div class="client-status ${statusClass}">${statusText}</div>
		        <div class="client-actions">${actions}</div>
		      </div>
		    `;
		  }).join("");

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

		
		prevBtn.addEventListener("click", () => {
	       if (currentPage > 1) {
	           currentPage--;
	           loadClients(currentPage);
	       }
	   });

	   nextBtn.addEventListener("click", () => {
	       currentPage++;
	       loadClients(currentPage);
	   });

	   // Initial Load
	   loadClients(currentPage);

}

function viewClient(targetId, targetRole) {
     openProfileModal(targetId, targetRole);
}
