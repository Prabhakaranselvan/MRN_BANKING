<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8" session="false" %>

<!-- Dialog Modal -->
<dialog id="dialog" class="custom-toast">
<div class="toast-content">
	<div class="toast-icon"></div> <!-- Vertical color bar -->
	<div class="toast-text">
		<strong id="toast-title"></strong>
		<p id="toast-message"></p>
	</div>
	<button class="toast-close"
		onclick="document.getElementById('dialog').close()">×</button>
</div>
</dialog>

<script>
    function handleResponse(responseJson, redirectUrl = null) {
        const dialog = document.getElementById("dialog");
        const title = document.getElementById("toast-title");
        const message = document.getElementById("toast-message");

        // Reset previous classes
        dialog.classList.remove("toast-success", "toast-error");
        
        // Remove any existing outside-click listener to prevent duplicates
        document.removeEventListener("click", outsideClickListener);

        let isSuccess = false;

        if (responseJson.message) {
            title.textContent = "Success";
            message.textContent = responseJson.message;
            dialog.classList.add("toast-success");
            isSuccess = true;
        } else if (responseJson.error) {
            title.textContent = "Error";
            message.innerHTML = responseJson.error;
            dialog.classList.add("toast-error");
        } else {
            title.textContent = "Notice";
            message.textContent = "Unexpected response.";
            dialog.classList.add("toast-error");
        }

        // ✅ Attach redirect only if redirectUrl is provided
        if (redirectUrl) {
            dialog.addEventListener("close", () => {
                window.location.replace(redirectUrl);
            }, { once: true });
        }

        dialog.showModal();

        // ✅ Auto-close only for success
        if (isSuccess) {
            setTimeout(() => {
                if (dialog.open) dialog.close();
            }, 1500);
        }
        
     // Attach listener for outside click
        document.addEventListener("click", outsideClickListener);

        function outsideClickListener(event) {
            if (dialog.open && !dialog.querySelector(".toast-content").contains(event.target)) {
                dialog.close();
                document.removeEventListener("click", outsideClickListener);
            }
        }
    }
</script>




