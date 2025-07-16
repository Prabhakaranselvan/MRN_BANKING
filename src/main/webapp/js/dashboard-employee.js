function initEmployeesScript() {
  const container = document.getElementById("employeesList");
  const prevBtn = document.getElementById("prevPage");
  const nextBtn = document.getElementById("nextPage");
  const paginationWrapper = document.getElementById("paginationWrapper");
  paginationWrapper.style.display = "none";
  const roleFilter = document.getElementById("roleFilter");
  const branchFilter = document.getElementById("branchFilter");

  let currentPage = 1;
  const limit = 10;
  const userRole = parseInt(document.body.getAttribute("data-user-role"));
  const userBranchId = parseInt(document.body.getAttribute("data-branch-id"));
  const isGM = userRole === 3;

  if (isGM) {
      fetchBranches();
  } else {
	  roleFilter.innerHTML = '<option value="1">Employee</option>';
      roleFilter.disabled = true;
  	  fetchBranch();
  }

      
	function fetchBranch() {
		// Fetch and show only their branch name in the dropdown
	 fetch("/MRN_BANKING/MRNBank/branch", {
	    method: "POST",
	    headers: {
	      "Content-Type": "application/json",
	      "Method": "GET"
	    },
	    body: JSON.stringify({ "branchId": userBranchId })
	  })
	  .then(handleFetchResponse)
	  .then(data => {
	    const b = data.branch;
	    branchFilter.innerHTML = "";
	    const option = document.createElement("option");
	    option.value = b.branchId;
	    option.textContent = `${b.branchName} (${b.branchLocation})`;
	    branchFilter.appendChild(option);
	    branchFilter.disabled = true; // Lock it to their branch
	  })
	  .catch(err => {
	    if (err !== "handled") {
	      handleResponse({ error: "Something went wrong.<br/>Please check your network or try refreshing." });
	    }
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
      .then(handleFetchResponse)
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
	    if (err !== "handled") {
	      handleResponse({ error: "Something went wrong.<br/>Please check your network or try refreshing." });
	    }
	  });
  }

  function loadEmployees(page) {
    const params = new URLSearchParams();
    params.append("page", page);
    params.append("limit", limit);

    if (isGM) {
      if (roleFilter.value) params.append("role", roleFilter.value);
      if (branchFilter.value) params.append("branchId", branchFilter.value);
    }

    fetch(`/MRN_BANKING/MRNBank/employee?${params.toString()}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        "Method": "GET"
      }
    })
      .then(handleFetchResponse)
      .then(data => {
        if (data.employees && data.employees.length > 0) {
          renderEmployees(data.employees);
		  paginationWrapper.style.display = "flex";
          prevBtn.disabled = currentPage === 1;
          nextBtn.disabled = data.employees.length < limit;
        } else {
          container.innerHTML = `<p>No employees found.</p>`;
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
	    if (err !== "handled") {
	      handleResponse({ error: "Something went wrong.<br/>Please check your network or try refreshing." });
	    }
	  });
  }

  function renderEmployees(employees) {
    const rows = employees.map(e => {
      const statusClass = e.status === 1 ? 'active' : e.status === 0 ? 'inactive' : 'closed';
      const statusText = e.status === 1 ? 'Active' : e.status === 0 ? 'Inactive' : 'Closed';

      const actions = e.status !== 2
        ? `<button onclick="viewEmployee(${e.userId}, ${e.userCategory})" class="action-btn" title="View Profile">
             <span class="material-icons">account_circle</span>
           </button>`
        : `<button class="lock-btn" title="Locked">
             <span class="material-icons" style="color: #bbb;">lock</span>
           </button>`;

      return `
        <div class="client-row">
          <div class="client-id">${e.userId}</div>
          <div class="client-name"><strong>${e.name}</strong></div>
          <div class="client-email">${e.email}</div>
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


  if (roleFilter) roleFilter.addEventListener("change", () => {
     currentPage = 1;
     loadEmployees(currentPage);
   });
  if (branchFilter) branchFilter.addEventListener("change", () => {
      currentPage = 1;
      loadEmployees(currentPage);
    });

  prevBtn.addEventListener("click", () => {
    if (currentPage > 1) {
      currentPage--;
      loadEmployees(currentPage);
    }
  });

  nextBtn.addEventListener("click", () => {
    currentPage++;
    loadEmployees(currentPage);
  });

  
  loadEmployees(currentPage);
  
  window.refreshEmployees = function () {
  	       loadEmployees(currentPage);
  	   };
}

function viewEmployee(targetId, targetRole) {
     openProfileModal(targetId, targetRole);
}
