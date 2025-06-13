document.addEventListener("DOMContentLoaded", () => {
    const forms = document.querySelectorAll('.json-form');
    const dialog = document.getElementById('dialog');
    const dialogMessage = document.getElementById('dialog-message');

    forms.forEach(form => {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();

            const endpoint = form.getAttribute('data-endpoint');
            const customMethod = form.getAttribute('data-method') || 'POST';

            const formData = new FormData(form);
            const jsonObject = {};
            formData.forEach((value, key) => {
				const input = form.querySelector(`[name="${key}"]`);
                const dataType = input?.getAttribute("data-type");

                if (dataType === "int") {
                    jsonObject[key] = parseInt(value, 10);
					console.log(`Converted '${key}' to integer:`, jsonObject[key]);
                } else {
                    jsonObject[key] = value;
                }
            });

            try {
                const response = await fetch(endpoint, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Method': customMethod
                    },
                    body: JSON.stringify(jsonObject)
                });
				
                const result = await response.json();
                let message = "";

                if (response.ok && result.message) {
                    message = result.message;
                } else if (result.error) {
                    message = result.error;
                } else {
                    message = "Unexpected response from server.";
                }

                if (dialog && dialogMessage) {
                    dialogMessage.textContent = message;
                    dialog.showModal();
                }

            } catch (error) {
                console.error("Fetch error:", error);
                if (dialog && dialogMessage) {
                    dialogMessage.textContent = "An error occurred while submitting the form.";
                    dialog.showModal();
                }
            }
        });
    });
});