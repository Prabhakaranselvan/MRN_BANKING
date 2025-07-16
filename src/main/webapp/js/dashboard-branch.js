// === dashboard-branch.js ===

function initBranchScript() {
  const branchCardsContainer = document.getElementById("branchCardsContainer");
  const branchInfo = document.getElementById("branchInfo");

  const userRole = parseInt(document.body.getAttribute("data-user-role"));
  const isGM = userRole === 3;

  // Initialize modal only if GM and required DOM elements exist
  if (isGM) initBranchModal();

  fetchBranches();

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
        const branches = data.Branches || [];
        branchCardsContainer.innerHTML = "";

        branches.forEach(branch => {
          const card = document.createElement("div");
          card.className = "branch-card";
          card.dataset.branchId = branch.branchId;
          card.innerHTML = `
            <h4>${branch.branchName}</h4>
            <div class="branch-location">${branch.branchLocation}</div>
            <div class="branch-contact">📞 ${branch.contactNo}</div>
          `;

          card.addEventListener("click", () => loadBranchDetails(branch.branchId));

          if (isGM) {
            card.addEventListener("dblclick", () => openBranchModal(branch.branchId));
          }

          branchCardsContainer.appendChild(card);
        });
      })
	  .catch(err => {
  	    if (err !== "handled") {
  	      handleResponse({ error: "Something went wrong.<br/>Please check your network or try refreshing." });
  	    }
  	  });
  }

  function loadBranchDetails(branchId) {
    fetch("/MRN_BANKING/MRNBank/branch", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Method": "GET"
      },
      body: JSON.stringify({ branchId })
    })
      .then(handleFetchResponse)
      .then(data => {
        const b = data.branch;
        branchInfo.innerHTML = `
          <h3>Branch Details</h3>
          <div class="detail-row"><span>Branch ID:</span><strong>${b.branchId}</strong></div>
          <div class="detail-row"><span>Name:</span><strong>${b.branchName}</strong></div>
          <div class="detail-row"><span>Location:</span><strong>${b.branchLocation}</strong></div>
          <div class="detail-row"><span>Contact:</span><strong>${b.contactNo}</strong></div>
          <div class="detail-row"><span>IFSC Code:</span><strong>${b.ifscCode}</strong></div>
          <div class="detail-row"><span>Created:</span><strong>${new Date(b.createdTime * 1000).toLocaleString()}</strong></div>
          <div class="detail-row"><span>Modified:</span><strong>${new Date(b.modifiedTime * 1000).toLocaleString()}</strong></div>
        `;
      })
	  .catch(err => {
	    if (err !== "handled") {
	      handleResponse({ error: "Something went wrong.<br/>Please check your network or try refreshing." });
	    }
	  });
  }

  function initBranchModal() {
    const openBtn = document.getElementById("openBranchModal");
    const modal = document.getElementById("branchModal");
    const closeBtn = document.getElementById("closeBranchModal");
    const form = document.getElementById("branchForm");

    openBtn.addEventListener("click", () => openBranchModal());

    closeBtn.addEventListener("click", () => {
      modal.style.display = "none";
      form.reset();
      document.getElementById("branchId").value = "";
    });

    form.addEventListener("submit", (e) => {
      e.preventDefault();
      const branchId = document.getElementById("branchId").value;
      const branchName = document.getElementById("branchName").value.trim();
      const branchLocation = document.getElementById("branchLocation").value.trim();
      const contactNo = document.getElementById("contactNo").value.trim();

      const methodHeader = branchId ? "PUT" : "POST";
      const payload = {
        branchName,
        branchLocation,
        contactNo
      };
      if (branchId) payload.branchId = parseInt(branchId);

      fetch("/MRN_BANKING/MRNBank/branch", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Method": methodHeader
        },
        body: JSON.stringify(payload)
      })
        .then(handleFetchResponse)
        .then(data => {
          handleResponse(data);
          modal.style.display = "none";
          form.reset();
          fetchBranches();
        })
		.catch(err => {
		  if (err !== "handled") {
		    handleResponse({ error: "Something went wrong.<br/>Please check your network or try refreshing." });
		  }
		});
    });
  }

  function openBranchModal(branchId = null) {
    const modal = document.getElementById("branchModal");
    const title = document.getElementById("branchModalTitle");
    const form = document.getElementById("branchForm");

    if (!branchId) {
      title.textContent = "New Branch";
      form.reset();
      document.getElementById("branchId").value = "";
      modal.style.display = "flex";
      return;
    }

    // Fetch existing branch info to prefill
    fetch("/MRN_BANKING/MRNBank/branch", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Method": "GET"
      },
      body: JSON.stringify({ branchId })
    })
      .then(handleFetchResponse)
      .then(data => {
        const b = data.branch;
        title.textContent = "Edit Branch";
        document.getElementById("branchId").value = b.branchId;
        document.getElementById("branchName").value = b.branchName;
        document.getElementById("branchLocation").value = b.branchLocation;
        document.getElementById("contactNo").value = b.contactNo;
        modal.style.display = "flex";
      })
	  .catch(err => {
	    if (err !== "handled") {
	      handleResponse({ error: "Something went wrong.<br/>Please check your network or try refreshing." });
	    }
	  });
  }
}
