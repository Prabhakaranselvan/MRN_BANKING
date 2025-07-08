function initRequestScript() {
  const container = document.getElementById("accountRequestList");
  const prevBtn = document.getElementById("prevPage");
  const nextBtn = document.getElementById("nextPage");
  const paginationWrapper = document.getElementById("paginationWrapper");
  paginationWrapper.style.display = "none";
  const statusFilter = document.getElementById("statusFilter");
  const branchFilter = document.getElementById("branchFilter");

  let currentPage = 1;
  const limit = 10;
  const userRole = parseInt(document.body.getAttribute("data-user-role"));
  const userBranchId = parseInt(document.body.getAttribute("data-branch-id"));
  const isGM = userRole === 3;

  if (isGM) {
    fetchBranches();
  } else {
    fetchBranch();
  }

  function fetchBranch() {
    fetch("/MRN_BANKING/MRNBank/branch", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Method": "GET"
      },
      body: JSON.stringify({ branchId: userBranchId })
    })
      .then(res => res.json())
      .then(data => {
        const b = data.branch;
        branchFilter.innerHTML = "";
        const option = document.createElement("option");
        option.value = b.branchId;
        option.textContent = `${b.branchName} (${b.branchLocation})`;
        branchFilter.appendChild(option);
        branchFilter.disabled = true;
      })
      .catch(err => {
        console.error("Failed to fetch branch info for current user:", err);
      });
  }

  function fetchBranches() {
    fetch("/MRN_BANKING/MRNBank/branch", {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        "Method": "GET"
      }
    })
      .then(res => res.json())
      .then(data => {
        branchFilter.innerHTML = '<option value="">All Branches</option>';
        data.Branches.forEach(branch => {
          const option = document.createElement("option");
          option.value = branch.branchId;
          option.textContent = `${branch.branchName} (${branch.branchLocation})`;
          branchFilter.appendChild(option);
        });
      })
      .catch(err => {
        console.error("Failed to load branches:", err);
      });
  }

  function loadRequests(page) {
    const params = new URLSearchParams();
    params.append("page", page);
    params.append("limit", limit);

    if (statusFilter.value) params.append("status", statusFilter.value);
    if (isGM && branchFilter.value) params.append("branchId", branchFilter.value);

    fetch(`/MRN_BANKING/MRNBank/accountrequest?${params.toString()}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        "Method": "GET"
      }
    })
      .then(res => res.json())
      .then(data => {
        const requests = data.AccountRequests || [];
		if (requests.length > 0) {
            renderRequests(requests);
	        paginationWrapper.style.display = "flex";
	        prevBtn.disabled = currentPage === 1;
	        nextBtn.disabled = requests.length < limit;
	    } else {
	        container.innerHTML = `<p>No account requests found.</p>`;
	        if (currentPage === 1) {
	            paginationWrapper.style.display = "none";
	        } else {
	            paginationWrapper.style.display = "flex";
	            prevBtn.disabled = false;
	            nextBtn.disabled = true;
	        }
	    }
      })
      .catch(err => {
        console.error("Error loading account requests:", err);
        container.innerHTML = `<p class="error">Failed to load requests.</p>`;
      });
  }

  function renderRequests(requests) {
    const rows = requests.map(req => {
      const statusClass = req.status === 0 ? "pending" :
                          req.status === 1 ? "approved" : "rejected";
      const statusText = req.status === 0 ? "Pending" :
                         req.status === 1 ? "Approved" : "Rejected";

      const actions = req.status === 0
        ? `
        <button class="action-btn" onclick="approveRequest(${req.requestId}, 1)" title="Approve">
          <span class="material-icons">check_circle</span>
        </button>
        <button class="action-btn" onclick="approveRequest(${req.requestId}, 2)" title="Reject">
          <span class="material-icons">cancel</span>
        </button >`
        : `	<button class="lock-btn" title="Locked">
	          <span class="material-icons" style="color: #bbb;">lock</span>
	        </button >`;

      return `
        <div class="request-row">
          <div class="request-id">#${req.requestId}</div>
          <div class="request-client">${req.clientId}</div>
          <div class="request-type">${formatAccountType(req.accountType)}</div>
          <div class="request-dates">${new Date(req.requestedTime * 1000).toLocaleString()}</div>
		  <div class="request-status ${statusClass}">${statusText}</div>
          <div class="request-actions">${actions}</div>
        </div>`;
    }).join("");

    container.innerHTML = `
      <div class="request-table">
        <div class="request-header">
          <div class="request-id">ID</div>
          <div class="request-client">Client ID</div>
          <div class="request-type">Type</div>
		  <div class="request-dates">Requested On</div>
          <div class="request-status">Status</div>
          <div class="request-actions">Action</div>
        </div>
        ${rows}
      </div>
    `;
  }

  function formatAccountType(type) {
    switch (type) {
      case 1: return "Savings";
      case 2: return "Current";
      case 3: return "Fixed Deposit";
      default: return "Unknown";
    }
  }

  window.approveRequest = function (requestId, status) {
    fetch("/MRN_BANKING/MRNBank/accountapproval", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
		"METHOD": "POST"
      },
      body: JSON.stringify({ requestId, status })
    })
      .then(res => res.json())
      .then(data => {
        handleResponse(data);
        loadRequests(currentPage);
      })
      .catch(err => {
        console.error("Approval error:", err);
      });
  };

  if (statusFilter) statusFilter.addEventListener("change", () => loadRequests(1));
  if (branchFilter) branchFilter.addEventListener("change", () => loadRequests(1));
  prevBtn.addEventListener("click", () => {
    if (currentPage > 1) {
      currentPage--;
      loadRequests(currentPage);
    }
  });
  nextBtn.addEventListener("click", () => {
    currentPage++;
    loadRequests(currentPage);
  });

  loadRequests(currentPage);
}
