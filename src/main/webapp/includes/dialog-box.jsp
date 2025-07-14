<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

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


        if (responseJson.message) {
            title.textContent = "Success";
            message.textContent = responseJson.message;
            dialog.classList.add("toast-success");

            dialog.addEventListener("close", () => {
                if (redirectUrl) {
                	window.location.replace(redirectUrl);
                }
            }, { once: true }); // Ensures it's removed after one execution
            
            dialog.showModal();
            setTimeout(() => {
                dialog.close();
            }, 1500);
            
        } else if (responseJson.error) {
            title.textContent = "Error";
            message.innerHTML = responseJson.error;
            dialog.classList.add("toast-error");

            dialog.showModal();
        } else {
            title.textContent = "Notice";
            message.textContent = "Unexpected response.";
            dialog.classList.add("toast-error");

            dialog.showModal();
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




