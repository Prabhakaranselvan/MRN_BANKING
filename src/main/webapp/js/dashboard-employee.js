function initEmployeesScript() {
  const container = document.getElementById("employeesList");
  const prevBtn = document.getElementById("prevPage");
  const nextBtn = document.getElementById("nextPage");
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
	  roleFilter.innerHTML = '<option value="EMPLOYEE">Employee</option>';
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
	  .then(res => res.json())
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
      .then(res => res.json())
      .then(data => {
        console.log("Employee list:", data);
        if (data.employees && data.employees.length > 0) {
          renderEmployees(data.employees);
          prevBtn.disabled = currentPage === 1;
          nextBtn.disabled = data.employees.length < limit;
        } else {
          container.innerHTML = `<p>No employees found.</p>`;
          prevBtn.disabled = true;
          nextBtn.disabled = true;
        }
      })
      .catch(err => {
        console.error("Error loading employees:", err);
        container.innerHTML = `<p class="error">Failed to load employees.</p>`;
      });
  }

  function renderEmployees(employees) {
    const rows = employees.map(e => `
      <div class="client-row">
        <div class="client-id">${e.userId}</div>
        <div class="client-name"><strong>${e.name}</strong></div>
        <div class="client-email">${e.email}</div>
        <div class="client-status ${e.status === 1 ? 'active' : e.status === 0 ? 'inactive' : 'closed'}">
          ${e.status === 1 ? 'Active' : e.status === 0 ? 'Inactive' : 'Closed'}
        </div>
        <div class="client-actions">
          <button onclick="viewEmployee(${e.userId}, ${e.userCategory})" class="action-btn" title="View Profile">
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

  if (roleFilter) roleFilter.addEventListener("change", () => loadEmployees(1));
  if (branchFilter) branchFilter.addEventListener("change", () => loadEmployees(1));

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

  window.viewEmployee = function(userId, userCategory) {
    loadContent(`dashboard-profile.jsp?targetId=${userId}&targetRole=${userCategory}`);
  };

  loadEmployees(currentPage);
}
